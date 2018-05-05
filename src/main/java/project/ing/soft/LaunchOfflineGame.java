package project.ing.soft;

import project.ing.soft.controller.Controller;
import project.ing.soft.view.IView;
import project.ing.soft.view.LocalViewCli;
import project.ing.soft.controller.IController;

public class LaunchOfflineGame
{


    public static void main(String[] args) throws Exception {

        // Create the controller which contains the game
        IController fantasticController = new Controller(3);

        // Create two views (that correspond to clients in RMI context)
        IView viewMatteo = new LocalViewCli("Matteo");
        IView viewDaniele = new LocalViewCli("Daniele");
        IView viewKris = new LocalViewCli("Kris");

        // give each view its controller (that correspond to a remote obj in RMI context)
        viewMatteo.attachController(fantasticController);
        viewDaniele.attachController(fantasticController);
        viewKris.attachController(fantasticController);

        // let the view add themselves to the game through a method call to the controller
        fantasticController.joinTheGame("Matteo", viewMatteo);
        fantasticController.joinTheGame("Daniele", viewDaniele);
        fantasticController.joinTheGame("Kris", viewKris);

    System.out.println("This is the end of the game. Hope you enjoyed");

    }
}