package project.ing.soft.socket;


import project.ing.soft.view.ClientViewCLI;


public class ClientExample extends Thread{
    private String name;
    private String host;
    private int port;

    public ClientExample(String name, String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        try {


            ControllerProxyOverSocket controller = new ControllerProxyOverSocket(host, port);

            controller.start();
            ClientViewCLI view = new ClientViewCLI(name);
            view.attachController(controller);
            controller.joinTheGame(name , view);


        }catch (Exception ex){
            System.out.println("Error "+ex);
            ex.printStackTrace(System.out);
        }

    }


    public static void main(String[] args) {
        String host = "localhost";
        int port    = 3000;
        new ClientExample(args[0]  , host, port ).start();
        //new ClientExample("gianpaolo",host, port ).start()
        //new ClientExample("affo"     , host, port ).start()
        //new ClientExample("baffo"     , host, port ).start()





    }


}
