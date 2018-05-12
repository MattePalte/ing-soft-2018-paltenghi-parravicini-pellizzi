package project.ing.soft;

import project.ing.soft.controller.IController;
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


        /* Use this if you want to list the bound objects
        for (String name : registry.list()) {
            System.out.println(name);
        }
        */
        String[] registryList = registry.list();
        for(String s : registryList)
            System.out.println(s);

        Scanner scan = new Scanner(System.in);
        System.out.println("Enter your name:");
        String name = scan.next();

        // gets a reference for the remote controller
        IController controller = (IController) registry.lookup("controller" + registryList.length);

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
