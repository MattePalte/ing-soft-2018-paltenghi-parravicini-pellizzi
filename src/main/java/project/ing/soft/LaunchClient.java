package project.ing.soft;

import project.ing.soft.controller.IController;
import project.ing.soft.view.IView;
import project.ing.soft.view.LocalViewCli;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class LaunchClient {
    public static void main(String[] args) throws Exception {
        // MULTI MACHINE INTERNAL LAN
        // where 192.168.x.x is the internal ip of the machine hosting the registry
        // decomment this ->Registry registry = LocateRegistry.getRegistry("192.168.x.x");
        // and comment the statement below
        Registry registry = LocateRegistry.getRegistry();

        /* Use this if you want to list the bound objects
        for (String name : registry.list()) {
            System.out.println(name);
        }
        */
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter your name:");
        String name = scan.next();

        // gets a reference for the remote controller
        IController controller = (IController) registry.lookup("controller");

        // creates and launches the view
        IView myView = new LocalViewCli(name);

        myView.attachController(controller);
        myView.run();

        controller.joinTheGame(name, myView);


        while (true) {
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
