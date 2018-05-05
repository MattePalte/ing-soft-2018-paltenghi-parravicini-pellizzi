package project.ing.soft.testsocket;


import project.ing.soft.view.ClientViewCLI;
import project.ing.soft.view.LocalViewCli;


public class SimpleClient extends Thread{
    private String name;
    private String host;
    private int port;

    public SimpleClient(String name, String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        try {


            ControllerProxy controller = new ControllerProxy(host, port);

            controller.start();
            LocalViewCli view = new LocalViewCli(name);
            //ClientViewCLI view = new ClientViewCLI(name);
            view.attachController(controller);
            controller.joinTheGame(name , view);


        }catch (Exception ex){
            System.out.println("Error "+ex);
            ex.printStackTrace(System.out);
        }

    }

    @Override
    public void interrupt() {
        super.interrupt();
    }

    public static void main(String[] args) {
        String host = "localhost";
        int port    = 3000;
        new SimpleClient(args[0]  , host, port ).start();
        //new SimpleClient("gianpaolo",host, port ).start();
        //new SimpleClient("affo"     , host, port ).start();
        //new SimpleClient("baffo"     , host, port ).start();





    }


}
