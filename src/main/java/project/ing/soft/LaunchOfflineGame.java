package project.ing.soft;

import project.ing.soft.controller.GameController;
import project.ing.soft.view.ClientViewCLI;
import project.ing.soft.view.IView;
import project.ing.soft.controller.IController;

import java.util.UUID;

public class LaunchOfflineGame
{


    public static void main(String[] args) throws Exception {

        // Create the controller which contains the game
        IController fantasticController = new GameController(3, UUID.randomUUID().toString());

        // Create two views (that correspond to clients in RMI context)
        IView viewMatteo = new ClientViewCLI("Matteo");
        IView viewDaniele = new ClientViewCLI("Daniele");
        IView viewKris = new ClientViewCLI("Kris");

        // give each view its controller (that correspond to a remote obj in RMI context)
        viewMatteo.attachController(fantasticController);
        viewDaniele.attachController(fantasticController);
        viewKris.attachController(fantasticController);

        // let the view add themselves to the game through a method call to the controller
        //fantasticController.joinTheGame("Matteo", viewMatteo);
        //fantasticController.joinTheGame("Daniele", viewDaniele);
        //fantasticController.joinTheGame("Kris", viewKris);

    System.out.println("This is the end of the game. Hope you enjoyed");

    }
}
