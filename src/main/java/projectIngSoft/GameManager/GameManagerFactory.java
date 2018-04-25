package projectIngSoft.GameManager;

import jdk.jshell.spi.ExecutionControl;
import projectIngSoft.Cards.Objectives.Privates.*;
import projectIngSoft.Cards.Objectives.Publics.*;
import projectIngSoft.Cards.ToolCards.*;
import projectIngSoft.Cards.WindowPatternCard;
import projectIngSoft.Game;
import projectIngSoft.exceptions.GameInvalidException;
import java.util.ArrayList;


public class GameManagerFactory {
    private static ArrayList<PublicObjective>   publicObjCards;
    private static ArrayList<PrivateObjective>  privateObjCards;
    private static ArrayList<ToolCard>          toolCards;
    private static ArrayList<WindowPatternCard> windowPatternCard;

    private static ArrayList<PublicObjective> getPublicObjCards() {
        if(publicObjCards == null){
            publicObjCards = new ArrayList<>();
            publicObjCards.add(new ColoriDiversiColonna());
            publicObjCards.add(new ColoriDiversiRiga());
            publicObjCards.add(new DiagonaliColorate());
            publicObjCards.add(new SfumatureChiare());
            publicObjCards.add(new SfumatureDiverse());
            publicObjCards.add(new SfumatureDiverseColonna());
            publicObjCards.add(new SfumatureDiverseRiga());
            publicObjCards.add(new SfumatureMedie());
            publicObjCards.add(new SfumatureScure());
            publicObjCards.add(new VarietaColore());
        }
        return publicObjCards;
    }

    private static ArrayList<PrivateObjective> getPrivateObjCards() {
        if(privateObjCards == null){
            privateObjCards = new ArrayList<>();
            privateObjCards.add(new SfumatureBlu());
            privateObjCards.add(new SfumatureGialle());
            privateObjCards.add(new SfumatureRosse());
            privateObjCards.add(new SfumatureVerdi());
            privateObjCards.add(new SfumatureViola());
        }

        return privateObjCards;
    }

    private static ArrayList<ToolCard> getToolCards() {
        if(toolCards == null){
            toolCards = new ArrayList<>();
            toolCards.add( new PinzaSgrossatrice());
            toolCards.add( new PennelloPerEglomise());
            toolCards.add( new AlesatoreLaminaRame());
            toolCards.add( new Lathekin());
            toolCards.add( new TaglierinaCircolare());
            toolCards.add( new PennelloPastaSalda());
            /*
            toolCards.add( new DiluentePastaSalda());
            toolCards.add( new Martelletto());
            toolCards.add( new RigaSughero());
            toolCards.add( new StripCutter());
            toolCards.add( new TaglierinaManuale());
            toolCards.add( new TamponeDiamantato());
            toolCards.add( new TenagliaRotelle());
            */
        }
        return toolCards;
    }

    private static ArrayList<WindowPatternCard> getWindowPatternCard() {
        if(windowPatternCard == null){
            windowPatternCard = WindowPatternCard.loadFromFile("src/main/patterns.txt");
        }
        return windowPatternCard;
    }


    public static IGameManager factory(Game aGame) {
        if (aGame.getNumberOfPlayers() == 1) {
            //instantiate a single player GameManager

            return null;
        } else if (aGame.getNumberOfPlayers() <= 4) {
            try {
                return new GameManagerMulti(aGame, getPublicObjCards(), getPrivateObjCards(), getToolCards(), getWindowPatternCard());
            } catch (GameInvalidException e) {
                return null;
            }
        } else {
            return null;
        }

    }
}
