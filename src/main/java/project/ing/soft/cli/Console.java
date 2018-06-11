package project.ing.soft.cli;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

import java.io.PrintStream;
import java.util.Arrays;

/**
 * This class handles output for the ConsoleUI.
 * Special handling for this is needed as UTF-8 output in Windows is unreliable
 * due to a broken unicode page.
 *
 * @author Håvard Slettvold, D.Parravicini
 */
public class Console extends PrintStream {

    private Kernel32 instance = null;
    private Pointer handle;

    /**
     * Console
     * @param defaultOut as back-up printStream if the attemp to print using
     *                   kernel32.dll endpoints fails
     */
    public Console(PrintStream defaultOut) {
        super(defaultOut);
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("win")) {
            instance = Native.loadLibrary("kernel32", Kernel32.class);

            instance.SetConsoleCP(65001);
            handle = instance.GetStdHandle(-11);
            instance.SetConsoleMode(handle, 7);
        }
    }

    /**
     * Interface to call method from kernel32.dll
     */
    public interface Kernel32 extends StdCallLibrary {
        Pointer GetStdHandle(int nStdHandle);

        boolean WriteConsoleW(Pointer hConsoleOutput, char[] lpBuffer, int nNumberOfCharsToWrite,
                                     IntByReference lpNumberOfCharsWritten, Pointer lpReserved);

        boolean SetConsoleCP(long wCodePageID);

        boolean SetConsoleMode(Pointer hConsoleHandle, int dwMode);
    }




    /**
     * main print method
     * @param message to be printed. It tries to print using Kernel32.dll
     *                if an error is raised it uses the default printstream
     *                passed as an argument to {@link Console}
     */
    @Override
    public void print(String message) {
        if (!attemptWindowsPrint(message)) {
            super.print(message);
        }
    }


    @Override
    public void print(Object obj) {
        print(obj.toString());
    }

    @Override
    public void println(String message) {
        print(message.concat(System.getProperty("line.separator")));

    }

    @Override
    public void println(Object obj) {
        println(obj.toString());

    }


    /**
     * Attempts to print text to the Windows console.
     * @param message Message to print
     * @return True if text was correctly printed to a windows console
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

    /**
     * Main to test utf-8 capabilities
     * @param args used as a print arguments
     */
    public static void main(String[] args){
        Console c = new Console(System.out);
        c.println(Arrays.toString(args));
        c.println( "⚀");
        c.println("\u001B[31mit's me mario\u001B[0m");
        c.print("java èéøÞǽлљΣæča");

    }
}

