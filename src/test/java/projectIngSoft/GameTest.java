package projectIngSoft;


import projectIngSoft.Referee.RefereeController;
import projectIngSoft.Referee.RefereeControllerMultiplayer;

import java.io.FileNotFoundException;


import org.junit.*;
import projectIngSoft.Referee.RefereeControllerSinglePlayer;

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
        RefereeController referee = new RefereeControllerMultiplayer(aMultiplePlayerGame);
        referee.setupPhase();
        referee.watchTheGame();
        referee.attributePoints();
        Player p = referee.getWinner();
        Assert.assertTrue(p.equals(new Player("Kris")));
        System.out.println("Player "+ p +" wins!");
    }

    @Test
    public void testSinglePlayer() throws Exception {
        RefereeController referee = new RefereeControllerSinglePlayer(aSinglePlayerGame);
        referee.setupPhase();
        referee.watchTheGame();
        referee.attributePoints();
        Player p = referee.getWinner();
        Assert.assertTrue(p.equals(new Player("Kris")));
        System.out.println("Player "+ p +" wins!");
    }
}
