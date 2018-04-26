package projectIngSoft;

import projectIngSoft.Controller.IController;
import projectIngSoft.View.IView;
import projectIngSoft.View.LocalViewCli;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class LaunchClient {
    public static void main(String[] args) throws Exception {
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

        controller.addPlayer(name, myView);


        while (true) {
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
