package project.ing.soft;

import project.ing.soft.accesspoint.APProxySocket;
import project.ing.soft.accesspoint.IAccessPoint;
import project.ing.soft.controller.IController;

import project.ing.soft.exceptions.NickNameAlreadyTakenException;
import project.ing.soft.model.Colour;
import project.ing.soft.view.ClientViewCLI;
import project.ing.soft.view.IView;
import project.ing.soft.view.LocalViewCli;

import java.io.PrintStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
import java.util.Scanner;

public class LaunchClient {


    public static void main(String[] args) throws Exception {
        PrintStream out = new PrintStream(System.out);
        Random rndGen = new Random();

        Colour foreground;
        Colour[] coloursAvailable = new Colour[]{Colour.RED, Colour.VIOLET};
        foreground = coloursAvailable[rndGen.nextInt(coloursAvailable.length)];
        out.println(foreground.colourForeground(
                "                                                        ___           \n" +
                        "                                                       (   )          \n" +
                        "    .--.      .---.    .--.    ___ .-.      .---.    .-.| |    .---.  \n" +
                        "  /  _  \\    / .-, \\  /    \\  (   )   \\    / .-, \\  /   \\ |   / .-, \\ \n" +
                        " . .' `. ;  (__) ; | ;  ,-. '  | ' .-. ;  (__) ; | |  .-. |  (__) ; | \n" +
                        " | '   | |    .'`  | | |  | |  |  / (___)   .'`  | | |  | |    .'`  | \n" +
                        " _\\_`.(___)  / .'| | | |  | |  | |         / .'| | | |  | |   / .'| | \n" +
                        "(   ). '.   | /  | | | |  | |  | |        | /  | | | |  | |  | /  | | \n" +
                        " | |  `\\ |  ; |  ; | | '  | |  | |        ; |  ; | | '  | |  ; |  ; | \n" +
                        " ; '._,' '  ' `-'  | '  `-' |  | |        ' `-'  | ' `-'  /  ' `-'  | \n" +
                        "  '.___.'   `.__.'_.  `.__. | (___)       `.__.'_.  `.__,'   `.__.'_. \n" +
                        "                      ( `-' ;                                         \n" +
                        "                       `.__.                                         "
        ));
        out.printf("\t\t\tBe the best stained glass artist%n%n");
        // ask for user name
        Scanner scan = new Scanner(System.in);
        out.println("Enter your name:");
        String name = args.length > 0 ? args[0] : scan.next();
        int actionSel;
        String code = null;

        do{
            out.println("Select an action: ");
            out.println("[0] to connect to a new game");
            out.println("[1] to reconnect to a previous game");
            actionSel = scan.nextInt();
            //Go to nextline to clear scanner
            scan.nextLine();
        }while(actionSel != 0 && actionSel != 1);
        // ask for RMI/Socket
        out.println("Which type of connection do you want to use to communicate with the server:");
        out.println("[0] RMI");
        out.println("[1] Socket");

        IAccessPoint accessPoint = null;
        switch (args.length > 1 ? args[1] : scan.nextLine()) {
            case "0":

                //args[0] should be the ip address of the machine running the registry
                Registry registry = LocateRegistry.getRegistry( Settings.instance().getDefaultIpForRMI());
                out.println("Objects currently registered in the registry");
                String[] registryList = registry.list();
                for(String s : registryList)
                    out.println(s);
                accessPoint = (IAccessPoint) registry.lookup("accesspoint");
                break;
            case "1":
                try {
                    accessPoint = new APProxySocket(Settings.instance().getHost(), Settings.instance().getPort());
                }catch (Exception ex){
                    out.println("Error "+ex);
                    ex.printStackTrace(out);
                }
                break;
            case "q":
                return;
            default:
                out .println("no action");
        }

        if (accessPoint != null) {
            // create the CLI view
            // launch it
            // and attach the chosen controller (Rmi or Socket) to it
            IView view = new ClientViewCLI(name);
            out.println("View created successfully");
            IController controller = null;
            if(actionSel == 0) {
                boolean nicknameAlreadyTaken = false;
                do {
                    try {
                        nicknameAlreadyTaken = false;
                        controller = accessPoint.connect(name, view);
                    } catch (NickNameAlreadyTakenException e) {
                        out.println(e.getMessage());
                        out.println("Please, select another nickname");
                        name = scan.nextLine();
                        out.println("Your nickname is " + name);
                        // Create a new view with the up-to-date name. The view is started after this method ends
                        view = new LocalViewCli(name);
                        nicknameAlreadyTaken = true;
                    }
                } while (nicknameAlreadyTaken);
            }
            else{
                // N.B: con RMI per evitare che dopo una reconnection il vecchio client possa
                // inviare richieste è necessario salvare nel playerController una reference alla view e
                // controllare che la richiesta arrivi da quella corrente
                out.println("Insert the 32 characters long code you received when you connected to the game:");
                code = scan.nextLine();
                controller = accessPoint.reconnect(name, code, view);
            }


            out.println("Controller retrieved from AccessPoint");
            view.attachController(controller);
            out.println("Controller attached to the view");
        }
    }
}
