package project.ing.soft;


import project.ing.soft.model.Colour;
import project.ing.soft.model.Game;
import project.ing.soft.model.Player;
import project.ing.soft.model.gamemanager.GameManagerFactory;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.view.LocalViewCli;

import java.io.FileNotFoundException;
import java.rmi.RemoteException;


import org.junit.*;

public class GameTest {

    private Game aMultiplePlayerGame;
    private Game aSinglePlayerGame;

    @Before
    public void CreateGameAndAddPlayers() throws FileNotFoundException, Colour.ColorNotFoundException, RemoteException {
        aMultiplePlayerGame = new Game(3);
        aMultiplePlayerGame.add(new Player("Matteo", new LocalViewCli("Matteo")));
        aMultiplePlayerGame.add(new Player("Daniele", new LocalViewCli("Daniele")));
        aMultiplePlayerGame.add(new Player("Kris", new LocalViewCli("Kris")));
    }

    @Before
    public void CreateGameAndAddPlayer() throws FileNotFoundException, Colour.ColorNotFoundException, RemoteException {
        aSinglePlayerGame = new Game(1);
        aSinglePlayerGame.add(new Player("Matteo",new LocalViewCli("Matteo")));
    }

    @Test //to avoid suggestion
    public void noTest(){

    }

/*
    @Test
    public void testMultiplayer() throws Exception {
        IGameManager myModel = GameManagerFactory.factory(aMultiplePlayerGame);

        myModel.countPlayersPoints();
        Player p = myModel.getWinner();
        Assert.assertTrue(p.equals(new Player("Kris", new LocalViewCli("Kris"))));
        System.out.println("Player "+ p +" wins!");
    }

    @Test
    public void testSinglePlayer() throws Exception {

        IGameManager referee = GameManagerFactory.factory(aSinglePlayerGame);
        referee.countPlayersPoints();
        Player p = referee.getWinner();
        Assert.assertTrue(p.equals(new Player("Kris", new LocalViewCli("Kris"))));
        System.out.println("Player "+ p +" wins!");

    }*/
}
