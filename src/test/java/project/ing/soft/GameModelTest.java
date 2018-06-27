package project.ing.soft;

import org.junit.*;
import project.ing.soft.controller.IController;
import project.ing.soft.exceptions.GameInvalidException;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Die;
import project.ing.soft.model.Game;
import project.ing.soft.model.Player;
import project.ing.soft.model.gamemodel.GameModelFactory;
import project.ing.soft.model.gamemodel.IGameModel;
import project.ing.soft.model.gamemodel.events.Event;
import project.ing.soft.model.gamemodel.events.PatternCardDistributedEvent;
import project.ing.soft.view.IView;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class GameModelTest {
    private IGameModel model;
    private int nrPlayers;

    /**
     * Stub view to enable set up phase
     */
    class CustomView implements IView{
        String owner;

        CustomView(String nick) {
            owner = nick;
        }

        @Override
        public void update(Event event) throws IOException {
            if (event instanceof PatternCardDistributedEvent) {
                try {
                    model.bindPatternAndPlayer(owner, ((PatternCardDistributedEvent) event).getOne(), true);
                } catch (GameInvalidException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void run() throws IOException {

        }

        @Override
        public void attachController(IController gameController) throws IOException {

        }
    }

    /**
     * Create a model
     */
    @Before
    public void setUp()  {
        Game game = new Game(2);
        game.add(new Player("Alice", new CustomView("Alice")));
        game.add(new Player("Bob", new CustomView("Bob")));
        model = GameModelFactory.factory(game);
        nrPlayers = game.getNumberOfPlayers();
        model.setupPhase();
    }


    /**
     * Test that draw dice extract exactly 2*n+1 dice
     */
    @Test
    public void testDraftPoolExtraction() {
        Assert.assertTrue((model.getDraftPool().size() == (nrPlayers * 2 + 1)));
    }

    /**
     * Test draft pool methods by extracting dice and inserting them
     */
    @Test
    public void testDraftPoolRemove() {
        List<Die> draftBefore;
        List<Die> draftAfter;
        draftBefore = model.getDraftPool();
        Die dieToRemove = model.getDraftPool().get(0);

        model.removeFromDraft(dieToRemove);
        draftAfter = model.getDraftPool();
        for (int i = 0; i < draftAfter.size(); i++) {
            draftBefore.remove(draftAfter.get(i));
        }

        Assert.assertTrue(draftBefore.size() ==  1);
        Assert.assertTrue(draftBefore.get(0).getColour() == dieToRemove.getColour());
        Assert.assertTrue(draftBefore.get(0).getValue() == dieToRemove.getValue());

    }

    /**
     * Test draftpool by inserting one die
     */
    @Test
    public void testAddToDraft() throws Exception {
        List<Die> draftBefore;
        List<Die> draftAfter;
        draftBefore = model.getDraftPool();
        Die dieToAdd = new Die(5, Colour.GREEN);

        model.addToDraft(dieToAdd);
        draftAfter = model.getDraftPool();
        Assert.assertTrue(draftAfter.size() == (draftBefore.size() + 1));

        for (int i = 0; i < draftBefore.size(); i++) {
            draftAfter.remove(draftBefore.get(i));
        }

        Assert.assertTrue(draftAfter.size() ==  1);
        Assert.assertTrue(draftAfter.get(0).getColour() == dieToAdd.getColour());
        Assert.assertTrue(draftAfter.get(0).getValue() == dieToAdd.getValue());
    }



}
