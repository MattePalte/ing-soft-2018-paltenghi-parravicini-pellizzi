package project.ing.soft;

import org.junit.Assert;
import project.ing.soft.exceptions.ToolCardApplicationException;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.*;
import project.ing.soft.model.cards.toolcards.*;
import project.ing.soft.model.cards.WindowPattern;
import org.junit.Before;
import org.junit.Test;
import project.ing.soft.model.gamemanager.GameManagerMulti;
import project.ing.soft.exceptions.MalformedToolCardException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ToolCardCheckParametersTest {

    private Player stubPlayer;
    private GameManagerMulti stubModel;
    private WindowPattern stubPattern;
    private RoundTracker stubRoundTracker;

    private PinzaSgrossatrice pinzaSgrossatrice;
    private PennelloPerEglomise pennelloPerEglomise;
    private AlesatoreLaminaRame alesatoreLaminaRame;
    private Lathekin lathekin;
    private TaglierinaCircolare taglierinaCircolare;
    private PennelloPastaSalda pennelloPastaSalda;

    @Before
    public void createStubThings(){
        stubPlayer = mock(Player.class);
        stubModel = mock(GameManagerMulti.class);
        stubPattern = mock(WindowPattern.class);
        stubRoundTracker = mock(RoundTracker.class);
        when(stubPlayer.getPattern()).thenReturn(stubPattern);
        when(stubPattern.getHeight()).thenReturn(4);
        when(stubPattern.getWidth()).thenReturn(5);
    }

    @Test
    // check if a well formed PinzaSgrossatrice toolcard doesn't throw the exception
    public void pinzaSgrossatriceOkTest() throws UserInterruptActionException, InterruptedException {
        Die rndDie = randomDie();
        boolean isExHappend = false;
        pinzaSgrossatrice = new PinzaSgrossatrice();

        IToolCardParametersAcquirer param = mock(IToolCardParametersAcquirer.class);

        when(param.getValue(any(String.class), anyInt(), anyInt() ))
                .then(args -> randomBoolean() ? -1 : 1 );

        when(param.getDieFromDraft(any(String.class)))
                .then(args -> rndDie);

        when(stubModel.getDraftPool()).thenReturn(List.of(rndDie));
        try {
            pinzaSgrossatrice.fill(param);
            pinzaSgrossatrice.checkParameters(stubPlayer,stubModel);
        } catch (MalformedToolCardException e) {
            isExHappend = true;
        }
        Assert.assertFalse( isExHappend);
    }


    @Test
    // MalformedToolCardException thrown for one of them:
    // - not setted die
    // - no die in draft
    public void pinzaSgrossatriceKoTest() throws UserInterruptActionException, InterruptedException {
        Die rndDie = randomDie();

        boolean isExHappend = false;
        pinzaSgrossatrice = new PinzaSgrossatrice();

        IToolCardParametersAcquirer param = mock(IToolCardParametersAcquirer.class);
        when(param.getValue(anyString(), anyInt(), anyInt()))
                .then(args-> randomBoolean() ? +1 : -1);

        when(stubModel.getDraftPool()).thenReturn(List.of(rndDie));
        try {
            pinzaSgrossatrice.fill(param);
            pinzaSgrossatrice.checkParameters(stubPlayer,stubModel);
        } catch (MalformedToolCardException e) {
            isExHappend = true;
        }
        Assert.assertTrue( isExHappend);

        isExHappend = false;
        pinzaSgrossatrice = new PinzaSgrossatrice();

        param = mock(IToolCardParametersAcquirer.class);
        when(param.getValue(anyString(), anyInt(), anyInt()))
                .then(args-> randomBoolean() ? 1 : -1);

        when(param.getDieFromDraft(anyString()))
                .then(args-> rndDie);

        when(stubModel.getDraftPool()).thenReturn(List.of());
        try {


            pinzaSgrossatrice.checkParameters(stubPlayer,stubModel);
        } catch (MalformedToolCardException e) {
            isExHappend = true;
        }
        Assert.assertTrue( isExHappend);
    }

    @Test
    // check if a well formed PennelloPerEglomise toolcard doesn't throw the exception
    public void pennelloPerEglomiseOkTest() throws UserInterruptActionException, InterruptedException {
        boolean isExHappend = false;
        pennelloPerEglomise = new PennelloPerEglomise();

        IToolCardParametersAcquirer param = mock(IToolCardParametersAcquirer.class);
        when(param.getCoordinate(anyString()))
                .then(args-> randomValidCoordinate())
                .then(args-> randomValidCoordinate());

        try {
            pennelloPerEglomise.fill(param);
            pennelloPerEglomise.checkParameters(stubPlayer,stubModel);
        } catch (MalformedToolCardException e) {
            isExHappend = true;
        }
        assertFalse( isExHappend);
    }

    @Test
    // MalformedToolCardException thrown for one of them:
    // - coordinate out of bound
    public void pennelloPerEglomiseKoTest() throws UserInterruptActionException, InterruptedException {
        boolean isExHappend = false;
        pennelloPerEglomise = new PennelloPerEglomise();
        for(int i = 0; i < 60; i++){
            isExHappend = false;
            Coordinate c1 = listOfInvalidCoordinates().get(new Random().nextInt(listOfInvalidCoordinates().size()));
            Coordinate c2 = listOfInvalidCoordinates().get(new Random().nextInt(listOfInvalidCoordinates().size()));

            IToolCardParametersAcquirer param = mock(IToolCardParametersAcquirer.class);
            when(param.getCoordinate(anyString()))
                    .then(args-> c1)
                    .then(args-> c2);

            try {
                pennelloPerEglomise.fill(param);
                pennelloPerEglomise.checkParameters(stubPlayer,stubModel);
            } catch (MalformedToolCardException e) {
                isExHappend = true;
            }
            assertTrue(isExHappend);
        }
    }

    @Test
    // check if a well formed alesatoreLaminaRame toolcard doesn't throw the exception
    public void alesatoreLaminaRameOkTest() throws UserInterruptActionException, InterruptedException {
        boolean isExHappend = false;
        alesatoreLaminaRame = new AlesatoreLaminaRame();

        IToolCardParametersAcquirer param = mock(IToolCardParametersAcquirer.class);
        when(param.getCoordinate(anyString()))
                .then(args-> randomValidCoordinate())
                .then(args-> randomValidCoordinate());

        try {
            alesatoreLaminaRame.fill(param);
            alesatoreLaminaRame.checkParameters(stubPlayer,stubModel);
        } catch (MalformedToolCardException e) {
            isExHappend = true;
        }
        assertFalse( isExHappend);
    }

    @Test
    // MalformedToolCardException thrown for one of them:
    // - coordinate out of bound
    public void alesatoreLaminaRameKoTest() throws UserInterruptActionException, InterruptedException {
        boolean isExHappend = false;
        alesatoreLaminaRame = new AlesatoreLaminaRame();
        for(int i = 0; i < 60; i++){
            isExHappend = false;
            Coordinate c1 = listOfInvalidCoordinates().get(new Random().nextInt(listOfInvalidCoordinates().size()));
            Coordinate c2 = listOfInvalidCoordinates().get(new Random().nextInt(listOfInvalidCoordinates().size()));

            IToolCardParametersAcquirer param = mock(IToolCardParametersAcquirer.class);
            when(param.getCoordinate(anyString()))
                    .then(args-> c1)
                    .then(args-> c2);


            try {
                alesatoreLaminaRame.fill(param);
                alesatoreLaminaRame.checkParameters(stubPlayer,stubModel);
            } catch (MalformedToolCardException e) {
                isExHappend = true;
            }
            assertTrue(isExHappend);
        }
    }

    @Test
    // check if a well formed lathekin toolcard doesn't throw the exception
    public void lathekinTest() throws UserInterruptActionException, InterruptedException {
        boolean isExHappend = false;
        lathekin = new Lathekin();

        IToolCardParametersAcquirer param = mock(IToolCardParametersAcquirer.class);

        doAnswer((invocation) -> randomValidCoordinate())
                .when(param).getCoordinate(anyString());

        try {
            lathekin.fill(param);
            lathekin.checkParameters(stubPlayer,stubModel);
        } catch (MalformedToolCardException e) {
            isExHappend = true;
        }
        Assert.assertFalse(isExHappend);
    }

    @Test
    // MalformedToolCardException thrown for one of them:
    // - coordinate out of bound
    public void lathekinKoTest() throws UserInterruptActionException, InterruptedException {
        boolean isExHappend = false;
        lathekin = new Lathekin();
        for(int i = 0; i < 60; i++){
            isExHappend = false;
            Coordinate c1 = listOfInvalidCoordinates().get(new Random().nextInt(listOfInvalidCoordinates().size()));
            Coordinate c2 = listOfInvalidCoordinates().get(new Random().nextInt(listOfInvalidCoordinates().size()));
            Coordinate c3 = listOfInvalidCoordinates().get(new Random().nextInt(listOfInvalidCoordinates().size()));
            Coordinate c4 = listOfInvalidCoordinates().get(new Random().nextInt(listOfInvalidCoordinates().size()));

            IToolCardParametersAcquirer param = mock(IToolCardParametersAcquirer.class);

            when(param.getCoordinate(any(String.class)))
                    .then( (args)-> c1)
                    .then( (args)-> c2)
                    .then( (args)-> c3)
                    .then( (args)-> c4);


            try {
                lathekin.fill(param);
                lathekin.checkParameters(stubPlayer,stubModel);
            } catch (MalformedToolCardException e) {
                isExHappend = true;
            }
            assertTrue(isExHappend);
        }
    }

    @Test
    // check if a well formed taglierinaCircolare toolcard doesn't throw the exception
    public void taglierinaCircolareOkTest() throws InterruptedException, UserInterruptActionException {
        Die rndDieRoundTracker = randomDie();
        Die rndDieDraftPool = randomDie();
        boolean isExHappend = false;
        taglierinaCircolare = new TaglierinaCircolare();


        when(stubModel.getRoundTracker()).thenReturn(stubRoundTracker);
        ArrayList<Die> myDiceLeft = new ArrayList<>();
        myDiceLeft.add(rndDieRoundTracker);
        when(stubRoundTracker.getDiceLeftFromRound()).thenReturn(myDiceLeft);

        when(stubModel.getDraftPool()).thenReturn(List.of(rndDieDraftPool));

        IToolCardParametersAcquirer param = mock(IToolCardParametersAcquirer.class);
        when(param.getDieFromRound(anyString()))
                .then( args -> rndDieRoundTracker);
        when(param.getDieFromDraft(anyString()))
                .then( args -> rndDieDraftPool);


        try {
            taglierinaCircolare.fill(param);
            taglierinaCircolare.checkParameters(stubPlayer,stubModel);
        } catch (MalformedToolCardException e) {
            isExHappend = true;
        }
        assertFalse(isExHappend);
    }


    @Test
    // MalformedToolCardException thrown for one of them:
    // - the die selected is not in the draft pool
    // - the die selected is not on the roundtracker
    public void taglierinaCircolareKoTest() throws UserInterruptActionException, InterruptedException {
        Die rndDieRoundTracker = randomDie();
        Die rndDieDraftPool = randomDie();
        boolean isExHappend = false;
        taglierinaCircolare = new TaglierinaCircolare();
        // - the die selected is not in the draft pool
        when(stubModel.getRoundTracker()).thenReturn(stubRoundTracker);
        ArrayList<Die> myDiceLeft = new ArrayList<>();
        myDiceLeft.add(rndDieRoundTracker);
        when(stubRoundTracker.getDiceLeftFromRound()).thenReturn(myDiceLeft);
        when(stubModel.getDraftPool()).thenReturn(List.of());

        IToolCardParametersAcquirer param = mock(IToolCardParametersAcquirer.class);
        when(param.getDieFromDraft(anyString()))
                .then( args -> rndDieDraftPool);
        when(param.getDieFromRound(anyString()))
                .then( args -> rndDieRoundTracker);

        try {
            taglierinaCircolare.fill(param);
            taglierinaCircolare.checkParameters(stubPlayer,stubModel);
        } catch (MalformedToolCardException e) {
            isExHappend = true;
        }
        assertTrue( isExHappend);

        // - the die selected is not on the roundtracker
        when(stubModel.getRoundTracker()).thenReturn(stubRoundTracker);
        when(stubRoundTracker.getDiceLeftFromRound()).thenReturn(myDiceLeft);
        Die aDie ;
        do {
         aDie= randomDie();
        } while (myDiceLeft.contains(rndDieDraftPool));


        param = mock(IToolCardParametersAcquirer.class);

        Die aDieThatDoesNotBelongToRoundTracker = aDie;
        when(param.getDieFromRound(anyString()))
                .then( args -> aDieThatDoesNotBelongToRoundTracker);

        Die aDieFromDraft = randomDie();
        when(param.getDieFromDraft(anyString()))
                .then( args -> aDieFromDraft);

        when(stubModel.getDraftPool()).thenReturn(List.of(aDieFromDraft));

        try {
            taglierinaCircolare.fill(param);
            taglierinaCircolare.checkParameters(stubPlayer,stubModel);
        } catch (MalformedToolCardException e) {
            isExHappend = true;
        }
        assertTrue( isExHappend);
    }

    @Test
    public void martellettoKoTest(){
        when(stubModel.getCurrentTurnList()).thenReturn(List.of(stubPlayer, stubPlayer));
        when(stubModel.getPlayerList()).thenReturn(List.of(stubPlayer));
        try{
            Martelletto martelletto = new Martelletto();
            martelletto.fill(mock(IToolCardParametersAcquirer.class));
            martelletto.checkParameters(stubPlayer, stubModel);
            Assert.fail();
        }catch (MalformedToolCardException e) {
            Assert.assertEquals("This is not your second turn in this round",e.getMessage()  );
        }

    }

    @Test
    // check if a well formed pennelloPastaSalda toolcard doesn't throw the exception
    public void pennelloPastaSaldaOkTest() throws UserInterruptActionException, InterruptedException {
        Die rndDie = randomDie();
        boolean isExHappend = false;
        pennelloPastaSalda = new PennelloPastaSalda();

        when(stubModel.getDraftPool()).thenReturn(List.of(rndDie));

        IToolCardParametersAcquirer param = mock(IToolCardParametersAcquirer.class);
        when(param.getDieFromDraft(anyString()))
                .then( args -> rndDie);

        try {
            pennelloPastaSalda.fill(param);
            pennelloPastaSalda.checkParameters(stubPlayer,stubModel);
        } catch (MalformedToolCardException e) {
            isExHappend = true;
        }
        assertFalse(isExHappend);
    }

    @Test
    // MalformedToolCardException thrown for one of them:
    // - the die selected is not in the draft pool
    public void pennelloPastaSaldaKoTest() throws UserInterruptActionException, InterruptedException {
        Die rndDie = randomDie();
        boolean isExHappend = false;
        pennelloPastaSalda = new PennelloPastaSalda();

        IToolCardParametersAcquirer param = mock(IToolCardParametersAcquirer.class);
        when(param.getDieFromDraft(anyString()))
                .then( args -> rndDie);


        when(stubModel.getDraftPool()).thenReturn(List.of());
        try {
            pennelloPastaSalda.fill(param);
            pennelloPastaSalda.checkParameters(stubPlayer,stubModel);
        } catch (MalformedToolCardException e) {
            isExHappend = true;
        }
        Assert.assertTrue( isExHappend);
    }


    private Die randomDie(){
        return new Die(new Random().nextInt(5) + 1, randomValidColour());
    }

    private Colour randomValidColour(){
        return Colour.validColours().get(new Random().nextInt(Colour.validColours().size()));
    }

    private Coordinate randomValidCoordinate(){
        return new Coordinate(new Random().nextInt(4), new Random().nextInt(5));
    }

    private List<Coordinate> listOfInvalidCoordinates(){
        List<Coordinate> list = new ArrayList<>();
        list.add(new Coordinate(new Random().nextInt(40) + 4, new Random().nextInt(40) + 5));
        list.add(new Coordinate(new Random().nextInt(40) + 4, new Random().nextInt(5)));
        list.add(new Coordinate(new Random().nextInt(4), new Random().nextInt(40) + 5));
        list.add(new Coordinate(new Random().nextInt(40) - 40, new Random().nextInt(40) - 40));
        list.add(new Coordinate(new Random().nextInt(40) - 40, new Random().nextInt(5)));
        list.add(new Coordinate(new Random().nextInt(4), new Random().nextInt(40) - 40));
        list.add(new Coordinate(new Random().nextInt(40) - 40, new Random().nextInt(40) +5));
        list.add(new Coordinate(new Random().nextInt(40) + 4, new Random().nextInt(40) - 40));
        return list;
    }

    private Die randomDieFrom(List<Die> listOfDie){
        return listOfDie.get(new Random().nextInt(listOfDie.size()));
    }

    private boolean randomBoolean(){
        return List.of(true, false). get (new Random().nextInt(1));
    }

}

