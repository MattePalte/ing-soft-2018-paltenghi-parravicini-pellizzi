package projectIngSoft;

import projectIngSoft.Controller.Controller;
import projectIngSoft.Controller.IController;
import projectIngSoft.GameManager.GameManagerFactory;

import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.View.LocalViewCli;

import java.io.FileNotFoundException;

public class Server
{


    public static void main(String[] args) throws FileNotFoundException, Colour.ColorNotFoundException, Exception {
        // Create a game with 3 players
        Game aMultiplePlayerGame;
        aMultiplePlayerGame = new Game(3);

        /*
        IController fantasticController = new Controller(GameManagerFactory.factory(new Game(3)));
        fantasticController.joinAGame("Matteo", new LocalViewCli("Matteo"));
        fantasticController.joinAGame("Daniele", new LocalViewCli("Daniele"));
        fantasticController.joinAGame("Kris", new LocalViewCli("Kris"));

        */
    System.out.println("This is the end of the game. Hope you enjoyed");

    }
}
