package project.ing.soft.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


public class NonBlockingScanner {
    private BufferedReader br;
    private Logger log;

    public NonBlockingScanner(InputStream in) {
        this.br = new BufferedReader(new InputStreamReader(in));
        this.log = Logger.getLogger(Objects.toString(this));

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
            log.log(Level.SEVERE, "error while reading", e);
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
                log.log(Level.SEVERE, "error while reading", e);
            }
        } while ("".equals(input));

        return  input;
    }


}

