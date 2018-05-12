package project.ing.soft;

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
    public void pinzaSgrossatriceOkTest() {
        Die rndDie = randomDie();
        boolean isExHappend = false;
        pinzaSgrossatrice = new PinzaSgrossatrice();
        pinzaSgrossatrice.setToBeIncreased(randomBoolean());
        pinzaSgrossatrice.setChoosenDie(rndDie);
        when(stubModel.getDraftPool()).thenReturn(List.of(rndDie));
        try {
            pinzaSgrossatrice.checkParameters(stubPlayer,stubModel);
        } catch (MalformedToolCardException e) {
            isExHappend = true;
        }
        assertEquals(false, isExHappend);
    }

    @Test
    // MalformedToolCardException thrown for one of them:
    // - not setted die
    // - no die in draft
    public void pinzaSgrossatriceKoTest() {
        Die rndDie = randomDie();

        boolean isExHappend = false;
        pinzaSgrossatrice = new PinzaSgrossatrice();
        pinzaSgrossatrice.setToBeIncreased(randomBoolean());
        when(stubModel.getDraftPool()).thenReturn(List.of(rndDie));
        try {
            pinzaSgrossatrice.checkParameters(stubPlayer,stubModel);
        } catch (MalformedToolCardException e) {
            isExHappend = true;
        }
        assertEquals(true, isExHappend);

        isExHappend = false;
        pinzaSgrossatrice = new PinzaSgrossatrice();
        pinzaSgrossatrice.setToBeIncreased(randomBoolean());
        pinzaSgrossatrice.setChoosenDie(rndDie);
        when(stubModel.getDraftPool()).thenReturn(List.of());
        try {
            pinzaSgrossatrice.checkParameters(stubPlayer,stubModel);
        } catch (MalformedToolCardException e) {
            isExHappend = true;
        }
        assertEquals(true, isExHappend);
    }

    @Test
    // check if a well formed PennelloPerEglomise toolcard doesn't throw the exception
    public void pennelloPerEglomiseOkTest() {
        boolean isExHappend = false;
        pennelloPerEglomise = new PennelloPerEglomise();
        pennelloPerEglomise.setStartPosition(randomValidCoordinate());
        pennelloPerEglomise.setEndPosition(randomValidCoordinate());
        try {
            pennelloPerEglomise.checkParameters(stubPlayer,stubModel);
        } catch (MalformedToolCardException e) {
            isExHappend = true;
        }
        assertEquals(false, isExHappend);
    }

    @Test
    // MalformedToolCardException thrown for one of them:
    // - coordinate out of bound
    public void pennelloPerEglomiseKoTest() {
        boolean isExHappend = false;
        pennelloPerEglomise = new PennelloPerEglomise();
        for(int i = 0; i < 60; i++){
            isExHappend = false;
            Coordinate c1 = listOfInvalidCoordinates().get(new Random().nextInt(listOfInvalidCoordinates().size()));
            Coordinate c2 = listOfInvalidCoordinates().get(new Random().nextInt(listOfInvalidCoordinates().size()));
            pennelloPerEglomise.setStartPosition(c1);
            pennelloPerEglomise.setEndPosition(c2);
            try {
                pennelloPerEglomise.checkParameters(stubPlayer,stubModel);
            } catch (MalformedToolCardException e) {
                isExHappend = true;
            }
            assertEquals(true, isExHappend);
        }
    }

    @Test
    // check if a well formed alesatoreLaminaRame toolcard doesn't throw the exception
    public void alesatoreLaminaRameOkTest() {
        boolean isExHappend = false;
        alesatoreLaminaRame = new AlesatoreLaminaRame();
        alesatoreLaminaRame.setStartPosition(randomValidCoordinate());
        alesatoreLaminaRame.setEndPosition(randomValidCoordinate());
        try {
            alesatoreLaminaRame.checkParameters(stubPlayer,stubModel);
        } catch (MalformedToolCardException e) {
            isExHappend = true;
        }
        assertEquals(false, isExHappend);
    }

    @Test
    // MalformedToolCardException thrown for one of them:
    // - coordinate out of bound
    public void alesatoreLaminaRameKoTest() {
        boolean isExHappend = false;
        alesatoreLaminaRame = new AlesatoreLaminaRame();
        for(int i = 0; i < 60; i++){
            isExHappend = false;
            Coordinate c1 = listOfInvalidCoordinates().get(new Random().nextInt(listOfInvalidCoordinates().size()));
            Coordinate c2 = listOfInvalidCoordinates().get(new Random().nextInt(listOfInvalidCoordinates().size()));
            alesatoreLaminaRame.setStartPosition(c1);
            alesatoreLaminaRame.setEndPosition(c2);
            try {
                alesatoreLaminaRame.checkParameters(stubPlayer,stubModel);
            } catch (MalformedToolCardException e) {
                isExHappend = true;
            }
            assertEquals(true, isExHappend);
        }
    }

    @Test
    // check if a well formed lathekin toolcard doesn't throw the exception
    public void lathekinTest() {
        boolean isExHappend = false;
        lathekin = new Lathekin();
        lathekin.setFirstDieStartPosition(randomValidCoordinate());
        lathekin.setFirstDieEndPosition(randomValidCoordinate());
        lathekin.setSecondDieStartPosition(randomValidCoordinate());
        lathekin.setSecondDieEndPosition(randomValidCoordinate());
        try {
            lathekin.checkParameters(stubPlayer,stubModel);
        } catch (MalformedToolCardException e) {
            isExHappend = true;
        }
        assertEquals(false, isExHappend);
    }

    @Test
    // MalformedToolCardException thrown for one of them:
    // - coordinate out of bound
    public void lathekinKoTest() {
        boolean isExHappend = false;
        lathekin = new Lathekin();
        for(int i = 0; i < 60; i++){
            isExHappend = false;
            Coordinate c1 = listOfInvalidCoordinates().get(new Random().nextInt(listOfInvalidCoordinates().size()));
            Coordinate c2 = listOfInvalidCoordinates().get(new Random().nextInt(listOfInvalidCoordinates().size()));
            Coordinate c3 = listOfInvalidCoordinates().get(new Random().nextInt(listOfInvalidCoordinates().size()));
            Coordinate c4 = listOfInvalidCoordinates().get(new Random().nextInt(listOfInvalidCoordinates().size()));

            lathekin.setFirstDieStartPosition(c1);
            lathekin.setFirstDieEndPosition(c2);
            lathekin.setSecondDieStartPosition(c3);
            lathekin.setSecondDieEndPosition(c4);
            try {
                lathekin.checkParameters(stubPlayer,stubModel);
            } catch (MalformedToolCardException e) {
                isExHappend = true;
            }
            assertEquals(true, isExHappend);
        }
    }

    @Test
    // check if a well formed taglierinaCircolare toolcard doesn't throw the exception
    public void taglierinaCircolareOkTest() {
        Die rndDieRoundTracker = randomDie();
        Die rndDieDraftPool = randomDie();
        boolean isExHappend = false;
        taglierinaCircolare = new TaglierinaCircolare();
        taglierinaCircolare.setDieFromRoundTracker(rndDieRoundTracker);
        when(stubModel.getRoundTracker()).thenReturn(stubRoundTracker);
        ArrayList<Die> myDiceLeft = new ArrayList<>();
        myDiceLeft.add(rndDieRoundTracker);
        when(stubRoundTracker.getDiceLeftFromRound()).thenReturn(myDiceLeft);
        taglierinaCircolare.setDieFromDraft(rndDieDraftPool);
        when(stubModel.getDraftPool()).thenReturn(List.of(rndDieDraftPool));
        try {
            taglierinaCircolare.checkParameters(stubPlayer,stubModel);
        } catch (MalformedToolCardException e) {
            isExHappend = true;
        }
        assertEquals(false, isExHappend);
    }

    @Test
    // MalformedToolCardException thrown for one of them:
    // - the die selected is not in the draft pool
    // TODO: - the die selected is not on the roundtracker
    public void taglierinaCircolareKoTest() {
        Die rndDieRoundTracker = randomDie();
        Die rndDieDraftPool = randomDie();
        boolean isExHappend = false;
        taglierinaCircolare = new TaglierinaCircolare();
        taglierinaCircolare.setDieFromRoundTracker(rndDieRoundTracker);
        when(stubModel.getRoundTracker()).thenReturn(stubRoundTracker);
        ArrayList<Die> myDiceLeft = new ArrayList<>();
        myDiceLeft.add(rndDieRoundTracker);
        when(stubRoundTracker.getDiceLeftFromRound()).thenReturn(myDiceLeft);
        taglierinaCircolare.setDieFromDraft(rndDieDraftPool);
        when(stubModel.getDraftPool()).thenReturn(List.of());
        try {
            taglierinaCircolare.checkParameters(stubPlayer,stubModel);
        } catch (MalformedToolCardException e) {
            isExHappend = true;
        }
        assertEquals(true, isExHappend);
    }

    @Test
    // check if a well formed pennelloPastaSalda toolcard doesn't throw the exception
    public void pennelloPastaSaldaOkTest() {
        Die rndDie = randomDie();
        boolean isExHappend = false;
        pennelloPastaSalda = new PennelloPastaSalda();
        pennelloPastaSalda.setToRoll(rndDie);
        when(stubModel.getDraftPool()).thenReturn(List.of(rndDie));
        try {
            pennelloPastaSalda.checkParameters(stubPlayer,stubModel);
        } catch (MalformedToolCardException e) {
            isExHappend = true;
        }
        assertEquals(false, isExHappend);
    }

    @Test
    // MalformedToolCardException thrown for one of them:
    // - the die selected is not in the draft pool
    public void pennelloPastaSaldaKoTest() {
        Die rndDie = randomDie();
        boolean isExHappend = false;
        pennelloPastaSalda = new PennelloPastaSalda();
        pennelloPastaSalda.setToRoll(rndDie);
        when(stubModel.getDraftPool()).thenReturn(List.of());
        try {
            pennelloPastaSalda.checkParameters(stubPlayer,stubModel);
        } catch (MalformedToolCardException e) {
            isExHappend = true;
        }
        assertEquals(true, isExHappend);
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
