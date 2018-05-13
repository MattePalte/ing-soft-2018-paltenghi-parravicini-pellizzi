package project.ing.soft.model.gamemanager;

import project.ing.soft.model.cards.objectives.privates.*;
import project.ing.soft.model.cards.objectives.publics.*;
import project.ing.soft.model.cards.toolcards.*;
import project.ing.soft.model.cards.WindowPatternCard;
import project.ing.soft.model.Game;
import project.ing.soft.exceptions.GameInvalidException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class GameManagerFactory {
    private static ArrayList<PublicObjective>   publicObjCards;
    private static ArrayList<PrivateObjective>  privateObjCards;
    private static ArrayList<ToolCard>          toolCards;
    private static ArrayList<WindowPatternCard> windowPatternCard;

    private GameManagerFactory(){

    }

    public static List<PublicObjective> getPublicObjCards() {
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

    public static List<PrivateObjective> getPrivateObjCards() {
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

    public static List<ToolCard> getToolCards() {
        if(toolCards == null){

            toolCards = new ArrayList<>();

            /*toolCards.add( new PinzaSgrossatrice());
            toolCards.add( new PennelloPerEglomise());*/
            toolCards.add( new AlesatoreLaminaRame());
            toolCards.add( new Lathekin());
            toolCards.add( new TaglierinaCircolare());
            /*toolCards.add( new PennelloPastaSalda());
            toolCards.add( new DiluentePastaSalda());
            toolCards.add( new Martelletto());
            toolCards.add( new RigaSughero());
            toolCards.add( new TaglierinaManuale());
            toolCards.add( new TamponeDiamantato());
            toolCards.add( new TenagliaRotelle());*/


            //toolCards.add( new StripCutter());
        }
        return toolCards;
    }

    public static List<WindowPatternCard> getWindowPatternCard() {
        URL path = GameManagerFactory.class.getClassLoader().getResource("patterns.txt");
        if(windowPatternCard == null && path != null){
            windowPatternCard = new ArrayList<>(WindowPatternCard.loadFromFile(path));
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
