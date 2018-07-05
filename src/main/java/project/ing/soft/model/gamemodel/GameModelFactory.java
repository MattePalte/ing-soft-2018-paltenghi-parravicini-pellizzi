package project.ing.soft.model.gamemodel;

import project.ing.soft.Settings;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Die;
import project.ing.soft.model.cards.objectives.privates.*;
import project.ing.soft.model.cards.objectives.publics.*;
import project.ing.soft.model.cards.toolcards.*;
import project.ing.soft.model.cards.WindowPatternCard;
import project.ing.soft.model.Game;
import project.ing.soft.exceptions.GameInvalidException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Object to create new GameModel through a Factory design pattern
 * Each private attribute is created only once then it is shared among different
 * GameModels during their creation phase.
 */
public class GameModelFactory {
    private static ArrayList<PublicObjective>   publicObjCards;
    private static ArrayList<PrivateObjective>  privateObjCards;
    private static ArrayList<ToolCard>          toolCards;
    private static ArrayList<WindowPatternCard> windowPatternCard;
    private static ArrayList<Die>               dice;

    private GameModelFactory(){

    }

    public static List<PublicObjective> getPublicObjCards() {
        if(publicObjCards == null){
            publicObjCards = new ArrayList<>();
            publicObjCards.add(new ColumnColourVariety());
            publicObjCards.add(new RowColourVariety());
            publicObjCards.add(new Diagonals());
            publicObjCards.add(new LightShades());
            publicObjCards.add(new ShadeVariety());
            publicObjCards.add(new ColumnShadeVariety());
            publicObjCards.add(new RowShadeVariety());
            publicObjCards.add(new MediumShades());
            publicObjCards.add(new DarkShades());
            publicObjCards.add(new ColourVariety());
        }
        return publicObjCards;
    }

    public static List<PrivateObjective> getPrivateObjCards() {
        if(privateObjCards == null){
            privateObjCards = new ArrayList<>();
            privateObjCards.add(new ShadesOfBlue());
            privateObjCards.add(new ShadesOfYellow());
            privateObjCards.add(new ShadesOfRed());
            privateObjCards.add(new ShadesOfGreen());
            privateObjCards.add(new ShadesOfPurple());
        }

        return privateObjCards;
    }

    public static List<ToolCard> getToolCards() {
        if(toolCards == null){

            toolCards = new ArrayList<>();

            toolCards.add( new CopperFoilBurnisher());
            toolCards.add( new CorkBackedStraightedge());
            toolCards.add( new EglomiseBrush());
            toolCards.add( new FluxBrush());
            toolCards.add( new FluxRemover());
            toolCards.add( new GlazingHammer());
            toolCards.add( new GrindingStone());
            toolCards.add( new GrozingPliers());
            toolCards.add( new Lathekin());
            toolCards.add( new LensCutter());
            toolCards.add( new RunningPliers());
            toolCards.add( new TapWheel());


        }
        return toolCards;
    }


    public static List<WindowPatternCard> getWindowPatternCard() {
        if(windowPatternCard == null ){

            try {
                windowPatternCard = new ArrayList<>(WindowPatternCard.loadFromFile(Settings.instance().getLocationOfWindowPatternFile()));
            } catch (Exception ex){
                Logger log = Logger.getAnonymousLogger();
                log.log(Level.SEVERE, "Window pattern cards couldn't be initialized, the server can't continue", ex);
                System.exit(1);
            }
        }
        return windowPatternCard;
    }

    public static List<Die> getDice() {
        if(dice == null) {
            dice = new ArrayList<>();
            for (Colour c : Colour.validColours()) {
                for (int i = 0; i < 18; i++) {
                    dice.add(new Die(c));
                }
            }
        }
        return dice;
    }


    public static IGameModel factory(Game aGame) {
        if (aGame.getNumberOfPlayers() == 1) {
            //instantiate a single player GameModel

            return null;
        } else if (aGame.getNumberOfPlayers() <= 4) {
            try {
                return new GameModel(aGame, getPublicObjCards(), getPrivateObjCards(), getToolCards(), getWindowPatternCard(), getDice());
            } catch (GameInvalidException e) {
                return null;
            }
        } else {
            return null;
        }

    }
}
