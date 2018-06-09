package project.ing.soft.model.gamemanager;

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


public class GameManagerFactory {
    private static ArrayList<PublicObjective>   publicObjCards;
    private static ArrayList<PrivateObjective>  privateObjCards;
    private static ArrayList<ToolCard>          toolCards;
    private static ArrayList<WindowPatternCard> windowPatternCard;
    private static ArrayList<Die>               dice;

    private GameManagerFactory(){

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

            /*toolCards.add( new PinzaSgrossatrice());
            toolCards.add( new PennelloPerEglomise());
            toolCards.add( new AlesatoreLaminaRame());
            toolCards.add( new Lathekin());
            toolCards.add( new TaglierinaCircolare());*/
            toolCards.add( new PennelloPastaSalda());
            toolCards.add( new DiluentePerPastaSalda());
            toolCards.add( new Martelletto());/*
            toolCards.add( new RigaSughero());
            toolCards.add( new TaglierinaManuale());
            toolCards.add( new TamponeDiamantato());
            toolCards.add( new TenagliaRotelle());*/


        }
        return toolCards;
    }


    public static List<WindowPatternCard> getWindowPatternCard() {
        if(windowPatternCard == null ){
            windowPatternCard = new ArrayList<>(WindowPatternCard.loadFromFile("/patterns.txt"));
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


    public static IGameManager factory(Game aGame) {
        if (aGame.getNumberOfPlayers() == 1) {
            //instantiate a single player GameManager

            return null;
        } else if (aGame.getNumberOfPlayers() <= 4) {
            try {
                return new GameManagerMulti(aGame, getPublicObjCards(), getPrivateObjCards(), getToolCards(), getWindowPatternCard(), getDice());
            } catch (GameInvalidException e) {
                return null;
            }
        } else {
            return null;
        }

    }
}
