package project.ing.soft;


import project.ing.soft.model.Colour;
import project.ing.soft.model.Game;
import project.ing.soft.model.Player;
import project.ing.soft.view.LocalViewCli;

import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Random;


import org.junit.*;

public class GameTest {

    private Random rndGen = new Random();

    @Before
    public void CreateGameAndAddPlayers() throws FileNotFoundException, Colour.ColorNotFoundException, RemoteException {
        Game aMultiplePlayerGame = new Game(3);
        aMultiplePlayerGame.add(new Player("Matteo", new LocalViewCli("Matteo")));
        aMultiplePlayerGame.add(new Player("Daniele", new LocalViewCli("Daniele")));
        Assert.assertEquals(2, aMultiplePlayerGame.getNumberOfPlayers());
        Assert.assertTrue(aMultiplePlayerGame.isValid());

        aMultiplePlayerGame.add(new Player("Kris", new LocalViewCli("Kris")));
        Assert.assertEquals(3, aMultiplePlayerGame.getNumberOfPlayers());
        Assert.assertTrue(aMultiplePlayerGame.isValid());

        aMultiplePlayerGame.add(new Player("TestPlayer", new LocalViewCli("TestPlayer")));
        Assert.assertEquals(aMultiplePlayerGame.getMaxNumPlayers(), aMultiplePlayerGame.getNumberOfPlayers());
        Assert.assertTrue(aMultiplePlayerGame.isValid());
    }

    @Before
    public void CreateGameAndAddPlayer() throws FileNotFoundException, Colour.ColorNotFoundException, RemoteException {
        Game aSinglePlayerGame = new Game(1);
        aSinglePlayerGame.add(new Player("Matteo",new LocalViewCli("Matteo")));
    }

    @Test
    public void productorTest() throws RemoteException {
        int players = rndGen.nextInt(4) + 1;
        Game toBeCopied = new Game(players);
        for(int i = 0; i < players; i++){
            toBeCopied.add(new Player("TestPlayer", new LocalViewCli("TestPlayer")));
        }
        Game copy = new Game(toBeCopied);
        Assert.assertFalse(copy == toBeCopied);
        Assert.assertEquals(players, copy.getMaxNumPlayers());
        Assert.assertEquals(players, copy.getNumberOfPlayers());
        Assert.assertEquals(toBeCopied.getPlayers(), copy.getPlayers());
        Assert.assertEquals(toBeCopied, copy);
    }

    @Test
    public void shiftTest() throws RemoteException {
        int players = rndGen.nextInt(4) + 1;
        Game toBeTested = new Game(players);
        for(int i = 0; i < players; i++){
            toBeTested.add(new Player("Test", new LocalViewCli("Test")));
        }
        Player firstPlayer = toBeTested.getPlayers().get(0);
        //Repeat shift operation until the result is equal to the first list
        do {
            List<Player> playersInGame = toBeTested.getPlayers();
            toBeTested.leftShiftPlayers();
            for (int i = 0; i < players; i++) {
                //checking if the list has been correctly shifted
                if (i < players - 1)
                    Assert.assertEquals(playersInGame.get(i + 1), toBeTested.getPlayers().get(i));
                else
                    Assert.assertEquals(playersInGame.get(0), toBeTested.getPlayers().get(players - 1));
            }
        } while(!toBeTested.getPlayers().get(0).equals(firstPlayer));
    }

    @Test
    public void reconnectTest() throws RemoteException {
        int players = rndGen.nextInt(4) + 1;
        Game toBeTested = new Game(players);
        for(int i = 0; i < players - 1; i++){
            toBeTested.add(new Player("Test", new LocalViewCli("Test")));
        }
        toBeTested.add(new Player("Kris", new LocalViewCli("Kris")));
        Game gameBackup = new Game(toBeTested);

        toBeTested.reconnect("Kris", new LocalViewCli("Kris"));
        Assert.assertEquals(gameBackup.getMaxNumPlayers(), toBeTested.getMaxNumPlayers());
        Assert.assertEquals(gameBackup.getNumberOfPlayers(), toBeTested.getNumberOfPlayers());
        Assert.assertNotEquals(gameBackup.getPlayers(), toBeTested.getPlayers());
        for(int i = 0; i < players; i++){
            Player fromBackup = gameBackup.getPlayers().get(i);
            Player fromGame = toBeTested.getPlayers().get(i);
            if(!fromBackup.getName().equals("Kris"))
                Assert.assertEquals(fromBackup, fromGame);
            else
                Assert.assertEquals("Kris", fromGame.getName());
        }

        gameBackup = new Game(toBeTested);
        toBeTested.reconnect("Johnny", new LocalViewCli("Johnny"));
        Assert.assertEquals(gameBackup, toBeTested);
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
