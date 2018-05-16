package project.ing.soft;

import project.ing.soft.controller.IController;

import project.ing.soft.socket.ControllerProxyOverSocket;
import project.ing.soft.view.IView;
import project.ing.soft.view.LocalViewCli;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class LaunchClient {

    private static final String DEFAULT_IP = "127.0.0.1";

    public static void main(String[] args) throws Exception {

        //args[0] should be the ip address of the machine running the registry
        Registry registry = LocateRegistry.getRegistry( args.length > 0 ? args[0] : DEFAULT_IP);

        // default configuration for sockets
        String host = "localhost";
        int port    = 3000;

        /* Use this if you want to list the bound objects
        for (String name : registry.list()) {
            System.out.println(name);
        }
        */
        String[] registryList = registry.list();
        for(String s : registryList)
            System.out.println(s);

        // ask for user name
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter your name:");
        String name = scan.next();
        // ask for RMI/Socket
        System.out.println("Which type of connection do you want to use to communicate with the server:");
        System.out.println("[0] RMI");
        System.out.println("[1] Socket");
        IController controller = null;
        switch (scan.next()) {
            case "0":
                // gets a reference for the remote controller
                controller = (IController) registry.lookup("controller" + registryList.length);
                break;
            case "1":
                try {
                    ControllerProxyOverSocket controllerProxy = new ControllerProxyOverSocket(host, port);
                    controllerProxy.start();
                    // from now on we will use the controllerProxy as a real IController
                    controller = (IController) controllerProxy;
                }catch (Exception ex){
                    System.out.println("Error "+ex);
                    ex.printStackTrace(System.out);
                }
                break;
            case "q":
                return;
        }

        if (controller != null) {
            // create the CLI view
            // launch it
            // and attach the chosen controller (Rmi or Socket) to it
            IView view = new LocalViewCli(name);
            System.out.println("View created successfully");
            view.attachController(controller);
            controller.joinTheGame(name , view);
        }

    }
}
