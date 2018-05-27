package project.ing.soft;

import project.ing.soft.accesspoint.APointRMI;
import project.ing.soft.controller.GameController;
import project.ing.soft.socket.SimpleSocketConnectionListener;

import java.io.IOException;
import java.io.PrintStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

public class LaunchServer {

    private final HashMap<String, GameController> hostedGames;
    private final HashMap<String, GameController> playersInGame;
    private final LogManager manager;
    private final PrintStream out;
    private final Scanner in;
    private final Map<String, Runnable> commands ;
    private SimpleSocketConnectionListener socketConnectionListener;
    private  APointRMI uniqueRmiAP;

    public LaunchServer() {
        hostedGames = new HashMap<>();
        playersInGame = new HashMap<>();

        manager  = LogManager.getLogManager();

        out = new PrintStream(System.out);
        in  = new Scanner(System.in);

        //map of commands
        commands = new HashMap<>();
        // Populate commands map
        commands.put("logAvailable", this::logAvailable);
        commands.put("logEnable", this::logEnable);
        commands.put("logDisable", this::logDisable);
        commands.put("quit", this::quit);
    }

    private void logAvailable(){

        out.println("Logger available:");
        for (Enumeration<String> e = manager.getLoggerNames(); e.hasMoreElements();)
            out.println(e.nextElement());

    }

    private void logEnable(){

        out.println("Enter name of the logger to be enabled");
        try {
            manager.getLogger(in.nextLine()).setLevel(Level.ALL);
        }catch(Exception ex){
            out.println(ex.getMessage());
        }
    }

    private void logDisable(){

        out.println("Enter name of the logger to be disabled");
        try {
            manager.getLogger(in.nextLine()).setLevel(Level.OFF);
        }catch(Exception ex){
            out.println(ex.getMessage());
        }
    }

    private void quit(){
        socketConnectionListener.interrupt();
        try {
            APointRMI.unbind(uniqueRmiAP);
        } catch (RemoteException|NotBoundException e) {
            out.println(e);
        }
    }

    public void run() {
        //Start socket
        socketConnectionListener = new SimpleSocketConnectionListener(3000, hostedGames, playersInGame);
        socketConnectionListener.start();
        //Start RMI
        try {
            uniqueRmiAP = new APointRMI(hostedGames);
            APointRMI.bind(uniqueRmiAP);
        } catch (IOException | InterruptedException e ) {
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
            commands.get(cmd).run();


        } while (!cmd.startsWith("quit"));

    }

    public static void main(String[] args) {
        LaunchServer ls = new LaunchServer();
        ls.run();

    }
}

