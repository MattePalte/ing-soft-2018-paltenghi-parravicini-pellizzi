package project.ing.soft.socket;


import project.ing.soft.exceptions.NickNameAlreadyTakenException;
import project.ing.soft.view.ClientViewCLI;

import java.util.Scanner;


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
            System.out.println("[0] Connect to a new game");
            System.out.println("[1] Reconnect to a previous game (token needed)");
            Scanner in = new Scanner(System.in);
            int choice = in.nextInt();
            in.nextLine();
            ClientViewCLI view = new ClientViewCLI(name);
            APProxySocket accessPointProxy = new APProxySocket(host, port);
            boolean nicknameAlreadyTaken;
            if(choice == 0)
                // Looping in case of nickname collision
                do {
                    try {
                        nicknameAlreadyTaken = false;
                        accessPointProxy.connect(name, view);
                    } catch (NickNameAlreadyTakenException e) {
                        System.out.println(e.getMessage());
                        System.out.println("Please, select another nickname");
                        name = in.nextLine();
                        System.out.println("Your nickname is " + name);
                        // Create a new view with the up-to-date name. The view is started after this method ends
                        view = new ClientViewCLI(name);
                        nicknameAlreadyTaken = true;
                    }
                } while(nicknameAlreadyTaken);
            else {
                System.out.println("Insert the 32 chars code to connect to the game: ");
                String code = in.nextLine();
                accessPointProxy.reconnect(name, code, view);
            }

        } catch (Exception ex){
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
