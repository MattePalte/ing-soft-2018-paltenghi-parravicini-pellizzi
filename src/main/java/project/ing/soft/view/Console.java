package project.ing.soft.view;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

import java.io.IOException;

/**
 * This class handles output for the ConsoleUI.
 * Special handling for this is needed as UTF-8 output in Windows is unreliable
 * due to a broken unicode page.
 *
 * @author Håvard Slettvold
 */
public class Console {

    private Kernel32 instance = null;
    private Pointer handle;

    public Console() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("win")) {
            instance = Native.loadLibrary("kernel32", Kernel32.class);

            instance.SetConsoleCP(65001);
            handle = instance.GetStdHandle(-11);
            instance.SetConsoleMode(handle, 7);
        }

    }

    public interface Kernel32 extends StdCallLibrary {
        Pointer GetStdHandle(int nStdHandle);

        boolean WriteConsoleW(Pointer hConsoleOutput, char[] lpBuffer, int nNumberOfCharsToWrite,
                                     IntByReference lpNumberOfCharsWritten, Pointer lpReserved);

        boolean SetConsoleCP(long wCodePageID);

        boolean SetConsoleMode(Pointer hConsoleHandle, int dwMode);
    }

    public void print(String message) {
        if (!attemptWindowsPrint(message)) {
            System.out.print(message);
        }
    }

    public void println(String message) {
        if (!attemptWindowsPrint(message.concat(System.getProperty("line.separator")))) {
            System.out.println(message);
        }
    }

    /**
     * Attempts to print text to the Windows console.
     *
     * @param message Message to print
     * @return True if text was printed to a windows console
     */
    private boolean attemptWindowsPrint(String message) {
        boolean successful = false;


        if (instance != null) {

            char[] buffer = message.toCharArray();

            IntByReference lpNumberOfCharsWritten = new IntByReference();
            successful = instance.WriteConsoleW(handle, buffer, buffer.length, lpNumberOfCharsWritten, null);

        }


        return successful;
    }

    public static void main(String[] args){
        Console c = new Console();
        String str = "⚀";
        c.println( str);
        c.println("\u001B[31mit's me mario\u001B[0m");
        c.print("java èéøÞǽлљΣæča");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

