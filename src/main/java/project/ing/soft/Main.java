package project.ing.soft;

import javafx.application.Application;
import project.ing.soft.gui.GuiBackgroundApp;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    static PrintStream out = System.out;

    public static void main(String[] args) throws Exception {
        HashMap<Integer, String> messages = new HashMap<>();
        messages.put(1, "Server");
        messages.put(2, "Client on Command Line (CLI)");
        messages.put(3, "Client with graphical Interface (GUI)");
        Scanner sc = new Scanner(System.in);

        int choice = 0;
        do {
            out.println("What do you want to start?");
            messages.keySet().forEach(i ->
                out.println(i + " - " + messages.get(i))
            );
            choice = sc.nextInt();
            if (!messages.keySet().contains(choice)) {
                 choice = 0;
                out.println("Invalid choice");
            }
        } while (choice == 0);

        switch (choice){
            case 1:
                new LaunchServer().run();
                break;
            case 2:
                LaunchClientCli.main(args);
                break;
            case 3:
                Application.launch(GuiBackgroundApp.class, args);
                break;
            default:
                out.println("Invalid choice, restart the program");
        }
    }
}
