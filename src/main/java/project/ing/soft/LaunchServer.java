package project.ing.soft;

import project.ing.soft.accesspoint.APointRMI;
import project.ing.soft.accesspoint.IAccessPoint;
import project.ing.soft.controller.GameController;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.Coordinate;
import project.ing.soft.socket.SimpleSocketConnectionListener;

import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;

public class LaunchServer extends Thread {

    private final HashMap<String, GameController> hostedGames;
    private final HashMap<String, GameController> playersInGame;


    public LaunchServer(HashMap<String, GameController> hostedGames, HashMap<String, GameController> playersInGame) {
        this.hostedGames = hostedGames;
        this.playersInGame = playersInGame;
    }

    @Override
    public void run() {

        APointRMI uniqueRmiAP = null;
        try {
            uniqueRmiAP = new APointRMI(hostedGames);
            APointRMI.export(uniqueRmiAP);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {
        LogManager manager = LogManager.getLogManager();

        HashMap<String, GameController> hostedGames = new HashMap<>();
        HashMap<String, GameController> playersInGame = new HashMap<>();
        //map of commands



        //Start socket
        SimpleSocketConnectionListener socketConnectionListener = new SimpleSocketConnectionListener(3000, hostedGames, playersInGame);
        socketConnectionListener.start();
        //Start RMI
        LaunchServer rmiConnectionListener = new LaunchServer(hostedGames, playersInGame);
        rmiConnectionListener.start();

        Map<String, Runnable> commands = new HashMap<>();
        // Populate commands map
        commands.put("logAvailable", () -> {
            System.out.println("Logger available:");
            for (Enumeration<String> e = manager.getLoggerNames(); e.hasMoreElements();)
                System.out.println(e.nextElement());
        });
        commands.put("logEnable", () -> {
            Scanner input = new Scanner(System.in);
            System.out.println("Enter name of the logger to be enabled");
            try {
                manager.getLogger(input.nextLine()).setLevel(Level.ALL);
            }catch(Exception ex){
                System.out.println(ex.getMessage());
            }
        });
        commands.put("logDisable", () -> {
            Scanner input = new Scanner(System.in);
            System.out.println("Enter name of the logger to be disabled");
            try {
                manager.getLogger(input.nextLine()).setLevel(Level.OFF);
            }catch(Exception ex){
                System.out.println(ex.getMessage());
            }
        });
        commands.put("quit", () ->{
            socketConnectionListener.interrupt();
            rmiConnectionListener.interrupt();
        });


        Scanner input = new Scanner(System.in);
        String cmd ;
        do {
            System.out.println("Commands available:");
            for(String s : commands.keySet()){
                System.out.println(s);
            }
            cmd = input.nextLine();
            // Invoke some command
            commands.get(cmd).run();


        } while (!cmd.startsWith("quit"));


    }
}

