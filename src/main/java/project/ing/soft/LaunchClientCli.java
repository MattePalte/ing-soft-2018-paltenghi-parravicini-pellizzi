package project.ing.soft;

import project.ing.soft.exceptions.CodeInvalidException;
import project.ing.soft.socket.APProxySocket;
import project.ing.soft.accesspoint.IAccessPoint;
import project.ing.soft.controller.IController;

import project.ing.soft.exceptions.NickNameAlreadyTakenException;
import project.ing.soft.model.Colour;
import project.ing.soft.cli.ClientViewCLI;
import project.ing.soft.cli.Console;
import project.ing.soft.view.IView;

import java.io.PrintStream;
import java.rmi.Naming;
import java.util.Random;
import java.util.Scanner;
import java.util.prefs.Preferences;

/**
 * This class is the entry point of the program to launch a CLI client
 */
public class LaunchClientCli {

    /**
     * main method of the class. This methods asks the user's name and which type of connection he wants
     * to use and tries to connect him to the server.
     * @param args arguments passed from the terminal
     * @throws Exception if anything went wrong during connection to the server
     */
    public static void main(String[] args) throws Exception {
        Console out = new Console(System.out);

        System.setProperty("java.rmi.dgc.leaseValue", "10000");
        printLandingPage(out);
        // ask for user name
        Scanner scan = new Scanner(System.in);
        String name = getPlayerName(args, out, scan);
        IAccessPoint accessPoint = getAccessPoint(args, out, scan);
        if (accessPoint == null) {
            return;
        }

        IController controller = null;
        boolean nicknameAlreadyTaken = false;
        do {
            IView view = new ClientViewCLI(name);
            out.println("View created successfully");
            // create the CLI view
            // launch it
            // and attach the chosen controller (Rmi or Socket) to it
            do {
                try {
                    nicknameAlreadyTaken = false;
                    controller = getController(out, scan, name, accessPoint, view);
                    out.println("Controller retrieved from AccessPoint");
                    view.attachController(controller);
                    out.println("Controller attached to the view");
                } catch (NickNameAlreadyTakenException e) {
                    out.println(e.getMessage());
                    out.println("Please, enter another nickname");
                    name = scan.nextLine();
                    out.println("Your nickname is " + name);
                    // Create a new view with the up-to-date name. The view is started after this method ends
                    nicknameAlreadyTaken = true;
                }catch (Exception ex){
                    out.println(ex.getMessage());
                }
            } while (nicknameAlreadyTaken);
        }while(controller == null);



        out.clear();

    }

    /**
     * Method used to ask the user if he wants to connect to a new game or reconnect to a old one
     * @param out PrintStream on which the method should print information for the user
     * @param scan the scanner used to get user input
     * @return an int value representing the choice of the user
     */

    private static IController getController(PrintStream out, Scanner scan, String name, IAccessPoint accessPoint, IView view) throws Exception {
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


        IController controller = null;
        switch (connectionMethod){
            case 0:
                controller = accessPoint.connect(name, view);
            break;
            case 1:
                Preferences pref = Preferences.userRoot().node(Settings.instance().getProperty("preferences.location"));
                String token = pref.get(Settings.instance().getProperty("preferences.connection.token.location"), "");
                if(!token.equals(""))
                    controller = accessPoint.reconnect(name, token, view);
                else
                    throw new CodeInvalidException("Code does not seems to be saved");
                break;
            case 2:
                out.println("Insert the 32 characters long code you received when you connected to the game:");
                controller = accessPoint.reconnect(name, scan.nextLine(), view);
                break;
            default:

        }
        return controller;
    }

    /**
     * Method called to get a reference to an access point: a object needed to ask connection or reconnection
     * to a game. With socket connection an access point proxy is set and a request to the server is sent.
     * With RMI connection, instead, the client gets a remote reference of the access point from the
     * registry and requests directly for a connection or a reconnection
     * @param args the arguments passed from the terminal
     * @param out PrintStream on which the method should print information for the user
     * @param scan the scanner used to get user input
     * @return a reference to an access point
     */
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
                    accessPoint = (IAccessPoint) Naming.lookup(Settings.instance().getRemoteRmiApName(getIP(out,scan)));
                } catch (Exception ex){
                    ex.printStackTrace(out);
                }
                break;
            case "1":
                try {
                    accessPoint = new APProxySocket(getIP(out,scan), Settings.instance().getPort());
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

    /**
     * Method to get the ip number from the console
     * @return ip number in the right format as a String
     */
    private static String getIP(PrintStream out, Scanner scan){
        String ipInserted = null;
        String ipChosen = null;
        do {
            out.println("Enter the IP where the server is running (q if the server is on this machine)");
            ipInserted = scan.next();
            switch (ipInserted){
                case "q":
                    ipChosen = Settings.instance().getHost();
                    break;
                default:
                    if (validIP(ipInserted)) {
                        ipChosen = ipInserted;
                    }
                    break;
            }
        } while (ipChosen == null);
        return ipChosen;
    }

    /**
     * Method to check that the given IP is valid
     * @param ip to check
     * @return true -> is valid or false -> invalid IP
     */
    private static boolean validIP(String ip) {
        try {
            String[] parts = ip.split( "\\." );
            if ( ip.isEmpty() || parts.length != 4 || ip.endsWith(".")) {
                return false;
            }
            for ( String s : parts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }


    /**
     * Method called to ask user's name
     * @param args arguments passed from the terminal
     * @param out PrintStream on which the method should print information for the user
     * @param scan Scanner used to get user input
     * @return name of the player
     */
    private static String getPlayerName(String[] args, PrintStream out, Scanner scan) {
        out.println("Enter your name:");
        if(args.length > 0 ) {
            return args[0];
        }

        String name = scan.next();
        scan.nextLine();
        return name;
    }

    /**
     * Method which prints a starting page of the game
     * @param out PrintStream on which the method should print information for the user
     */
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
