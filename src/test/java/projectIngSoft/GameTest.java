package projectIngSoft;


import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.GameManager.GameManagerMulti;

import java.io.FileNotFoundException;


import org.junit.*;
import projectIngSoft.GameManager.GameManagerSingle;

public class GameTest {

    private Game aMultiplePlayerGame;
    private Game aSinglePlayerGame;

    @Before
    public void CreateGameAndAddPlayers() throws FileNotFoundException, Colour.ColorNotFoundException {
        aMultiplePlayerGame = new Game(3);
        aMultiplePlayerGame.add(new Player("Matteo"));
        aMultiplePlayerGame.add(new Player("Daniele"));
        aMultiplePlayerGame.add(new Player("Kris"));
    }

    @Before
    public void CreateGameAndAddPlayer() throws FileNotFoundException, Colour.ColorNotFoundException {
        aSinglePlayerGame = new Game(1);
        aSinglePlayerGame.add(new Player("Matteo"));
    }


    @Test
    public void testMultiplayer() throws Exception {
        IGameManager referee = new GameManagerMulti(aMultiplePlayerGame);
        referee.setupPhase();
        referee.countPlayersPoints();
        Player p = referee.getWinner();
        Assert.assertTrue(p.equals(new Player("Kris")));
        System.out.println("Player "+ p +" wins!");
    }

    @Test
    public void testSinglePlayer() throws Exception {
        IGameManager referee = new GameManagerSingle(aSinglePlayerGame);
        referee.setupPhase();
        referee.countPlayersPoints();
        Player p = referee.getWinner();
        Assert.assertTrue(p.equals(new Player("Kris")));
        System.out.println("Player "+ p +" wins!");
    }
}
