package projectIngSoft;


import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.GameManager.GameManagerMulti;

import java.io.FileNotFoundException;


import org.junit.*;
import projectIngSoft.GameManager.GameManagerSingle;
import projectIngSoft.View.ClientViewCLI;
import projectIngSoft.View.LocalViewCli;

public class GameTest {

    private Game aMultiplePlayerGame;
    private Game aSinglePlayerGame;

    @Before
    public void CreateGameAndAddPlayers() throws FileNotFoundException, Colour.ColorNotFoundException {
        aMultiplePlayerGame = new Game(3);
        aMultiplePlayerGame.add(new Player("Matteo", new LocalViewCli("Matteo")));
        aMultiplePlayerGame.add(new Player("Daniele", new LocalViewCli("Daniele")));
        aMultiplePlayerGame.add(new Player("Kris", new LocalViewCli("Kris")));
    }

    @Before
    public void CreateGameAndAddPlayer() throws FileNotFoundException, Colour.ColorNotFoundException {
        aSinglePlayerGame = new Game(1);
        aSinglePlayerGame.add(new Player("Matteo",new LocalViewCli("Matteo")));
    }


    @Test
    public void testMultiplayer() throws Exception {
        IGameManager myModel = new GameManagerMulti(aMultiplePlayerGame);
        myModel.start();

        myModel.countPlayersPoints();
        Player p = myModel.getWinner();
        Assert.assertTrue(p.equals(new Player("Kris", new LocalViewCli("Kris"))));
        System.out.println("Player "+ p +" wins!");
    }

    @Test
    public void testSinglePlayer() throws Exception {
        /*
        IGameManager referee = new GameManagerSingle(aSinglePlayerGame);
        referee.countPlayersPoints();
        Player p = referee.getWinner();
        Assert.assertTrue(p.equals(new Player("Kris", new LocalViewCli())));
        System.out.println("Player "+ p +" wins!");
        */
    }
}
