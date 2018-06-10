package project.ing.soft;

import project.ing.soft.rmi.APointRMI;
import project.ing.soft.accesspoint.AccessPointReal;
import project.ing.soft.controller.GameController;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.socket.SocketListener;

import java.io.IOException;
import java.io.PrintStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.stream.Collectors;

/**
 * This class is the entry point of the program to launch a server instance
 */
public class LaunchServer {

    private final HashMap<String, GameController> hostedGames;
    private final HashMap<String, GameController> playersInGame;
    private final LogManager manager;
    private final PrintStream out;
    private final Scanner in;
    private final Map<String, Runnable> commands ;
    private SocketListener socketListener;
    private  APointRMI uniqueRmiAP;

    /**
     * Server default constructor. It creates maps to save on going games and players actually playing and
     * exposes commands to enable and disable loggers
     */
    private LaunchServer() {
        hostedGames = new HashMap<>();
        playersInGame = new HashMap<>();

        manager  = LogManager.getLogManager();

        out = new PrintStream(System.out);
        in  = new Scanner(System.in);

        //map of commands
        commands = new TreeMap<>();
        // Populate commands map
        commands.put("logAvailable", this::logAvailable);
        commands.put("logEnable", this::logEnable);
        commands.put("logDisable", this::logDisable);
        commands.put("quit", this::quit);
    }

    //region commands

    /**
     * Method used to print a list of available loggers
     */
    private void logAvailable(){

        out.println("Logger available:");
        for (Enumeration<String> e = manager.getLoggerNames(); e.hasMoreElements();)
            out.println(e.nextElement());

    }

    /**
     * Method used to enable a logger from the available ones
     */
    private void logEnable(){

        out.println("Enter name of the logger to be enabled");
        List<String> list = new ArrayList<>();
        for (Enumeration<String> e = manager.getLoggerNames(); e.hasMoreElements();)
           list.add(e.nextElement());
        list = list.stream().sorted().collect(Collectors.toList());
        try {
            manager.getLogger((String)chooseFrom(list)).setLevel(Level.ALL);

        }catch (UserInterruptActionException ex){
            //no action need to be carried out
        }catch (Exception ex){
            out.println(ex.getMessage());
        }
    }

    /**
     * Method used to disable a logger
     */
    private void logDisable(){

        out.println("Enter name of the logger to be enabled");
        List<String> list = new ArrayList<>();
        for (Enumeration<String> e = manager.getLoggerNames(); e.hasMoreElements();)
            list.add(e.nextElement());
        list = list.stream().sorted().collect(Collectors.toList());
        try {
            manager.getLogger((String)chooseFrom(list)).setLevel(Settings.instance().getDefaultLoggingLevel());
        }catch (UserInterruptActionException ex){
            //no action need to be carried out
        }catch (Exception ex){
            out.println(ex.getMessage());
        }
    }

    /**
     * Method which stops server from listening connection
     */
    private void quit(){
        socketListener.interrupt();
        try {
            APointRMI.unbind(uniqueRmiAP);
        } catch (RemoteException|NotBoundException e) {
            e.printStackTrace(out);
        }
        try {
            APointRMI.stopRegistry();
        } catch (RemoteException e) {
            e.printStackTrace(out);
        }
    }

    /**
     * This method is responsible for the creation of a single access point used to let players connect
     * to the game. It also runs a thread which will accept users socket connection and exports the RMI
     * access point in the registry.
     */
    public void run() {
        // Create real AccessPoint server-side
        AccessPointReal accessPointReal = new AccessPointReal(hostedGames,playersInGame);

        // Create AccessPoint for Socket and start its socket listener
        socketListener = new SocketListener(Settings.instance().getPort(), accessPointReal);
        socketListener.start();
        // Create AccessPoint for RMI and start it
        try {
            uniqueRmiAP = new APointRMI(accessPointReal);
            APointRMI.bind(uniqueRmiAP);
        } catch (IOException e ) {
            e.printStackTrace(out);
        }

        String cmd ;
        do {
            out.println("Commands available:");
            for(String s : commands.keySet().stream().sorted().collect(Collectors.toList())){
                out.println(s);
            }
            cmd = in.nextLine();
            // Invoke some command
            try {
                commands.get(cmd).run();
            }catch (Exception ex){
                ex.printStackTrace(out);
            }

        } while (!cmd.startsWith("quit"));

    }
    //endregion

    //region scanner operation

    /**
     * Method used to get user input
     * @param lowerBound inferior limit of the command numeric identifier a user can choose
     * @param upperBound superior limit of the command numeric identifier a user can choose
     * @return an int value representing user's choice
     * @throws UserInterruptActionException if the user itself chose to interrupt its choice
     */
    private int waitForUserInput(int lowerBound , int upperBound) throws UserInterruptActionException {
        int ret = 0;
        boolean err;
        String str = null;

        do{
            err = false;
            try{
                str = in.nextLine();
                ret = Integer.valueOf(str);
            }
            catch( NumberFormatException e){
                err = true;
            }
            err = err || ret < lowerBound || ret > upperBound;

            if(err){
                if(str != null &&  str.startsWith("q"))
                    throw new UserInterruptActionException();
                out.println("You entered a value that does not fit into the correct interval. Enter q to interrupt the operation");

            }
        }while(err);

        return ret;
    }

    /**
     * Method used to make users choose from a list of objects
     * @param objects the list of objects from which users must make a choice
     * @return the object chosen by the user
     * @throws UserInterruptActionException if the user itself chose to interrupt its choice
     */
    private Object chooseFrom(List objects) throws UserInterruptActionException {
        return objects.get(chooseIndexFrom(objects));
    }

    /**
     * Method which asks the user to choose an object from a list by enumerating them
     * @param objects the list of objects from which the user must make a choice
     * @return an int value representing user's choice
     * @throws UserInterruptActionException if the user itself chose to interrupt its choice
     */
    private int chooseIndexFrom(List objects) throws UserInterruptActionException {

        out.println(String.format("Enter a number between 0 and %d to select:", objects.size()-1));
        for (int i = 0; i < objects.size() ; i++) {
            out.println(String.format("[%d] for %s", i, objects.get(i).toString()));
        }
        return waitForUserInput(0, objects.size()-1);

    }
    //endregion

    /**
     * This method runs an instance of the server
     * @param args no parameter is required
     */
    public static void main(String[] args) {
        LaunchServer ls = new LaunchServer();
        ls.run();

    }


}

