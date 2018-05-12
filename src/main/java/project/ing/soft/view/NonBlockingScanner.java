package project.ing.soft.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;




public class NonBlockingScanner {
   private BufferedReader br;

    public NonBlockingScanner(InputStream in) {
        this.br = new BufferedReader(new InputStreamReader(in));

    }
    private void waitForInputAvailable() throws IOException, InterruptedException {
        while (!br.ready()) {
            Thread.sleep(100);
            if(Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
        }
    }

    public int read() throws InterruptedException{

        try {
            // wait until we have data to complete a readLine()
            waitForInputAvailable();
            return br.read();
        }catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

    }

    public String readLine() throws InterruptedException {

        String input = "";

        do {

            try {
                // wait until we have data to complete a readLine()
                waitForInputAvailable();
                input = br.readLine();
            }catch (IOException e) {
                e.printStackTrace();
            }
        } while ("".equals(input));

        return  input;
    }


}

