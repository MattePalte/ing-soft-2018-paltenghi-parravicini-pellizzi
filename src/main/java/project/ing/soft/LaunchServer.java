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

public class LaunchServer {

    private final HashMap<String, GameController> hostedGames;
    private final HashMap<String, GameController> playersInGame;
    private final LogManager manager;
    private final PrintStream out;
    private final Scanner in;
    private final Map<String, Runnable> commands ;
    private SocketListener socketListener;
    private  APointRMI uniqueRmiAP;

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
    private void logAvailable(){

        out.println("Logger available:");
        for (Enumeration<String> e = manager.getLoggerNames(); e.hasMoreElements();)
            out.println(e.nextElement());

    }

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

    private Object chooseFrom(List objects) throws UserInterruptActionException {
        return objects.get(chooseIndexFrom(objects));
    }

    private int chooseIndexFrom(List objects) throws UserInterruptActionException {

        out.println(String.format("Enter a number between 0 and %d to select:", objects.size()-1));
        for (int i = 0; i < objects.size() ; i++) {
            out.println(String.format("[%d] for %s", i, objects.get(i).toString()));
        }
        return waitForUserInput(0, objects.size()-1);

    }
    //endregion

    /**
     * Launch Server class is responsible for handling construction/destruction of the server
     * @param args no parameter is required
     */
    public static void main(String[] args) {
        LaunchServer ls = new LaunchServer();
        ls.run();

    }


}

