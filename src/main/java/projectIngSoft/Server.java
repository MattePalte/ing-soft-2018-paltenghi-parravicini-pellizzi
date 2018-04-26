package projectIngSoft;

import projectIngSoft.Controller.Controller;
import projectIngSoft.Controller.IController;

import projectIngSoft.View.IView;
import projectIngSoft.View.LocalViewCli;

import java.io.FileNotFoundException;

public class Server
{


    public static void main(String[] args) throws FileNotFoundException, Colour.ColorNotFoundException, Exception {

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

        // let the view add themself to the game through a method call to the controller
        fantasticController.joinTheGame("Matteo", viewMatteo);
        fantasticController.joinTheGame("Daniele", viewDaniele);
        fantasticController.joinTheGame("Kris", viewKris);

    System.out.println("This is the end of the game. Hope you enjoyed");

    }
}
