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
        aMultiplePlayerGame.add(new Player("Matteo", new LocalViewCli("Matteo")));
        aMultiplePlayerGame.add(new Player("Daniele", new LocalViewCli("Daniele")));
        aMultiplePlayerGame.add(new Player("Kris", new LocalViewCli("Kris")));
        // Create related model
        IGameManager myModel = GameManagerFactory.factory(aMultiplePlayerGame);
        // Create a unique controller for every player of this match
        IController fantasticController = new Controller(myModel);
        // give a reference of the controller to every view
        // player.giveControllerToTheView(x) - call -> view.attachController(x)
        for (Player p : myModel.getPlayerList())
            p.giveControllerToTheView(fantasticController);

        myModel.setupPhase();

        System.out.println("This is the end of the game. Hope you enjoyed");

    }
}
