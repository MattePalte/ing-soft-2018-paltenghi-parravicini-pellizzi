package project.ing.soft;


import project.ing.soft.accesspoint.IAccessPoint;
import project.ing.soft.controller.IController;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Game;
import project.ing.soft.model.Player;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.util.logging.Level;


import org.junit.*;
import project.ing.soft.cli.ClientViewCLI;
import project.ing.soft.model.Round;
import project.ing.soft.model.gamemodel.IGameModel;
import project.ing.soft.model.gamemodel.events.Event;
import project.ing.soft.model.gamemodel.events.ModelChangedEvent;
import project.ing.soft.model.gamemodel.events.PatternCardDistributedEvent;
import project.ing.soft.model.gamemodel.events.SetTokenEvent;
import project.ing.soft.socket.APProxySocket;
import project.ing.soft.view.IView;

public class GameTest {

    private Random rndGen = new Random();

    @Before
    public void CreateGameAndAddPlayers() throws FileNotFoundException, Colour.ColorNotFoundException, RemoteException {
        Game aMultiplePlayerGame = new Game(3);
        aMultiplePlayerGame.add(new Player("Matteo", new ClientViewCLI("Matteo")));
        aMultiplePlayerGame.add(new Player("Daniele", new ClientViewCLI("Daniele")));
        Assert.assertEquals(2, aMultiplePlayerGame.getNumberOfPlayers());
        Assert.assertTrue(aMultiplePlayerGame.isValid());

        aMultiplePlayerGame.add(new Player("Kris", new ClientViewCLI("Kris")));
        Assert.assertEquals(3, aMultiplePlayerGame.getNumberOfPlayers());
        Assert.assertTrue(aMultiplePlayerGame.isValid());

        aMultiplePlayerGame.add(new Player("TestPlayer", new ClientViewCLI("TestPlayer")));
        Assert.assertEquals(aMultiplePlayerGame.getMaxNumPlayers(), aMultiplePlayerGame.getNumberOfPlayers());
        Assert.assertTrue(aMultiplePlayerGame.isValid());
    }

    @Before
    public void CreateGameAndAddPlayer() throws FileNotFoundException, Colour.ColorNotFoundException, RemoteException {
        Game aSinglePlayerGame = new Game(1);
        aSinglePlayerGame.add(new Player("Matteo",new ClientViewCLI("Matteo")));
    }

    @Test
    public void productorTest() throws RemoteException {
        int players = rndGen.nextInt(4) + 1;
        Game toBeCopied = new Game(players);
        for(int i = 0; i < players; i++){
            toBeCopied.add(new Player("TestPlayer", new ClientViewCLI("TestPlayer")));
        }
        Game copy = new Game(toBeCopied);
        Assert.assertNotSame(copy , toBeCopied);
        Assert.assertEquals(players, copy.getMaxNumPlayers());
        Assert.assertEquals(players, copy.getNumberOfPlayers());
        Assert.assertEquals(toBeCopied.getPlayers(), copy.getPlayers());
        Assert.assertEquals(toBeCopied, copy);
    }

    @Test
    public void shiftTest() throws RemoteException {
        for(int players = 1; players <6; players++) {
            Game toBeTested = new Game(players);
            for (int i = 0; i < players; i++) {
                toBeTested.add(new Player("Test" + i, new ClientViewCLI("Test" + i)));
            }

            //Repeat shift operation until the result is equal to the first list
            Round r = new Round(0,toBeTested);
            Round rPlus = r.nextRound();
            for (int j = 0; j < players; j++) {
                Player first = r.getCurrent();
                for (int i = 0; i < players; i++, rPlus.next()) {

                    //checking if the list has been correctly shifted
                    if (i < players - 1)
                        Assert.assertEquals(r.next(), rPlus.getCurrent());
                    else
                        Assert.assertEquals(first, rPlus.getCurrent());
                }
                r = r.nextRound();
                rPlus = rPlus.nextRound();
            }

            for (int i = 0; i < players; i++,r.next()) {
                Assert.assertEquals(r.getCurrent(), toBeTested.getPlayers().get(i));
            }


        }
    }

    @Test
    public void testTurn() throws RemoteException {
        Game g = new Game(3);
        g.add(new Player("a", new ClientViewCLI("a")));
        g.add(new Player("b", new ClientViewCLI("b")));
        g.add(new Player("c", new ClientViewCLI("c")));
        Round round;
        int i;
        for (i = 0,  round = new Round(0,g); i < 10; i++, round = round.nextRound()) {
            System.out.println("Round: "+i);


            Player p = round.getCurrent();
            System.out.println(p.getName());
            while(round.hasNext()){
                p = round.next();
                System.out.println(p.getName());
            }

        }
    }

    @Test
    public void reconnectTest() throws RemoteException {
        int players = rndGen.nextInt(4) + 1;
        Game toBeTested = new Game(players);
        for(int i = 0; i < players - 1; i++){
            toBeTested.add(new Player("Test", new ClientViewCLI("Test")));
        }
        toBeTested.add(new Player("Kris", new ClientViewCLI("Kris")));
        Game gameBackup = new Game(toBeTested);

        toBeTested.reconnect("Kris", new ClientViewCLI("Kris"));
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
        toBeTested.reconnect("Johnny", new ClientViewCLI("Johnny"));
        Assert.assertEquals(gameBackup, toBeTested);
    }


    /**
     * Stub view to enable set up phase
     */
    class CustomView extends UnicastRemoteObject implements IView , Serializable  {
        String owner;
        String code;
        IController ctrl;
        IGameModel gm;

        CustomView(String nick) throws RemoteException {
            super();
            owner = nick;
        }

        @Override
        public void update(Event event) throws IOException {
            if (event instanceof PatternCardDistributedEvent) {
                try {
                    ctrl.choosePattern(owner, ((PatternCardDistributedEvent) event).getOne(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(event instanceof SetTokenEvent){
                code = ((SetTokenEvent) event).getToken();
            }else if(event instanceof ModelChangedEvent){
                gm = ((ModelChangedEvent) event).getaGameCopy();
            }

        }

        @Override
        public void run() throws IOException {

        }

        @Override
        public void attachController(IController gameController) throws IOException {
            ctrl = gameController;
        }
    }

    @Test
    public void reconnectionTest() throws Exception {
        Settings.instance().setDefaultLoggingLevel(Level.ALL);
        Thread server = new Thread(new LaunchServer()::run);
        server.start();

        IAccessPoint apRmi = (IAccessPoint) Naming.lookup(Settings.instance().getRemoteRmiApName());
        IAccessPoint apSock = new APProxySocket(Settings.instance().getHost(), Settings.instance().getPort());
        CustomView tom = new CustomView("Tom"),
                matt = new CustomView("matt");
        tom.attachController(apRmi.connect(tom.owner, tom ));
        matt.attachController(apSock.connect(matt.owner, matt));

        Thread.sleep(Settings.instance().getGameStartTimeout()+500);
        CustomView matt2 = new CustomView("matt");
        matt2.attachController(apRmi.reconnect(matt2.owner, matt.code, matt2));

        Thread.sleep(500);
        Assert.assertEquals(matt2.code, matt.code);
        apSock = new APProxySocket(Settings.instance().getHost(), Settings.instance().getPort());
        CustomView matt3 = new CustomView("matt");
        matt2.attachController(apSock.reconnect(matt3.owner, matt2.code, matt3));
        Thread.sleep(500);
        Assert.assertEquals(matt3.code, matt3.code);
        for (int i = 0; i < Settings.instance().getNrOfRound(); i++) {
            for (int j = 0; j <4 ; j++) {
                System.out.println(matt3.gm.getPlayerList());
                if (matt3.gm.getCurrentPlayer().getName().equals(matt3.owner)){
                    matt3.ctrl.endTurn(matt3.owner);
                }else {
                    tom.ctrl.endTurn(tom.owner);
                }
                Thread.sleep(1000);
            }
        }
        Assert.assertEquals(IGameModel.GAME_MANAGER_STATUS.ENDED,matt3.gm.getStatus());


    }

/*
    @Test
    public void testMultiplayer() throws Exception {
        IGameModel myModel = GameModelFactory.factory(aMultiplePlayerGame);

        myModel.countPlayersPoints();
        Player p = myModel.getWinner();
        Assert.assertTrue(p.equals(new Player("Kris", new ClientViewCLI("Kris"))));
        System.out.println("Player "+ p +" wins!");
    }

    @Test
    public void testSinglePlayer() throws Exception {

        IGameModel referee = GameModelFactory.factory(aSinglePlayerGame);
        referee.countPlayersPoints();
        Player p = referee.getWinner();
        Assert.assertTrue(p.equals(new Player("Kris", new ClientViewCLI("Kris"))));
        System.out.println("Player "+ p +" wins!");

    }*/
}
