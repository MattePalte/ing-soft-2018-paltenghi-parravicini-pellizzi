package project.ing.soft;

import project.ing.soft.socket.APProxySocket;
import project.ing.soft.accesspoint.IAccessPoint;
import project.ing.soft.controller.IController;

import project.ing.soft.exceptions.NickNameAlreadyTakenException;
import project.ing.soft.model.Colour;
import project.ing.soft.view.ClientViewCLI;
import project.ing.soft.view.Console;
import project.ing.soft.view.IView;

import java.io.PrintStream;
import java.rmi.Naming;
import java.util.Random;
import java.util.Scanner;

public class LaunchClient {


    public static void main(String[] args) throws Exception {
        PrintStream out = new Console(System.out);

        System.setProperty("java.rmi.dgc.leaseValue", "10000");
        printLandingPage(out);
        // ask for user name
        Scanner scan = new Scanner(System.in);
        String name = getPlayerName(args, out, scan);
        IAccessPoint accessPoint = getAccessPoint(args, out, scan);
        if (accessPoint == null) {
            return;
        }
        int connectionMethod = getConnectionMethod(args, out, scan);

        // create the CLI view
        // launch it
        // and attach the chosen controller (Rmi or Socket) to it
        IView view = new ClientViewCLI(name);
        out.println("View created successfully");
        IController controller = null;
        switch (connectionMethod){
            case 0:
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
                        view = new ClientViewCLI(name);
                        nicknameAlreadyTaken = true;
                    }
                } while (nicknameAlreadyTaken);
            break;
            case 1:
                // N.B: con RMI per evitare che dopo una reconnection il vecchio client possa
                // inviare richieste è necessario salvare nel playerController una reference alla view e
                // controllare che la richiesta arrivi da quella corrente
                controller = accessPoint.reconnect(name, System.getProperty(Settings.instance().tokenProperty()), view);
                break;
            case 2:
                out.println("Insert the 32 characters long code you received when you connected to the game:");
                controller = accessPoint.reconnect(name, scan.nextLine(), view);
                break;
            default:

        }


        out.println("Controller retrieved from AccessPoint");
        view.attachController(controller);
        out.println("Controller attached to the view");

    }

    private static IAccessPoint getAccessPoint(String[] args, PrintStream out, Scanner scan) {
        // ask for RMI/Socket
        out.println("Which type of connection do you want to use to communicate with the server:");
        out.println("[0] RMI");
        out.println("[1] Socket");
        IAccessPoint accessPoint = null;
        switch (args.length > 1 ? args[1] : scan.nextLine()) {
            case "0":

                //args[0] should be the ip address of the machine running the registry
                try {
                    accessPoint = (IAccessPoint) Naming.lookup(Settings.instance().getRmiApName());
                } catch (Exception ex){
                    ex.printStackTrace(out);
                }
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

            default:
                out.println("no action");
        }
        return accessPoint;
    }


    private static int getConnectionMethod(String[] args, PrintStream out, Scanner scan) {
        int connectionMethod;

        do{
            out.println("Select an action: ");
            out.println("[0] to connect to a new game");
            out.println("[1] to reconnect to the previous game started here");
            out.println("[2] to reconnect to the previous game from code");
            connectionMethod = scan.nextInt();
            //Go to next line to clear scanner
            scan.nextLine();
        }while(connectionMethod < 0 || connectionMethod > 2);
        return connectionMethod;
    }

    private static String getPlayerName(String[] args, PrintStream out, Scanner scan) {
        out.println("Enter your name:");
        String name = args.length > 0 ? args[0] : scan.next();
        scan.nextLine();
        return name;
    }

    private static void printLandingPage(PrintStream out) {
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
    }
}
