package project.ing.soft;

import project.ing.soft.controller.IController;
import project.ing.soft.socket.ControllerProxy;
import project.ing.soft.view.IView;
import project.ing.soft.view.LocalViewCli;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class LaunchClient {
    public static void main(String[] args) throws Exception {

        //args[0] should be the ip address of the machine running the registry
        Registry registry = LocateRegistry.getRegistry( args.length > 0 ? args[0] : "127.0.0.1");

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
                    ControllerProxy controllerProxy = new ControllerProxy(host, port);
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
        } else {
            return;
        }

        while (true) {
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
