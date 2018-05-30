package project.ing.soft;

import project.ing.soft.accesspoint.APProxySocket;
import project.ing.soft.accesspoint.IAccessPoint;
import project.ing.soft.controller.IController;

import project.ing.soft.view.ClientViewCLI;
import project.ing.soft.view.IView;
import project.ing.soft.view.LocalViewCli;

import java.io.PrintStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import static java.lang.Thread.getDefaultUncaughtExceptionHandler;
import static java.lang.Thread.sleep;

public class LaunchClient {


    public static void main(String[] args) throws Exception {
        PrintStream out = new PrintStream(System.out);

        // ask for user name
        Scanner scan = new Scanner(System.in);
        out.println("Enter your name:");
        String name = args.length > 0 ? args[0] : scan.next();
        // ask for RMI/Socket
        out.println("Which type of connection do you want to use to communicate with the server:");
        out.println("[0] RMI");
        out.println("[1] Socket");

        IAccessPoint accessPoint = null;
        switch (args.length > 1 ? args[1] : scan.next()) {
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
            IController controller = accessPoint.connect(name, view);
            out.println("Controller retrieved from AccessPoint");
            view.attachController(controller);
            out.println("Controller attached to the view");
        }

    }
}
