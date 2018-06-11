package project.ing.soft;

import org.junit.Test;

import project.ing.soft.model.cards.Card;
import project.ing.soft.model.StringBoxBuilder;
import project.ing.soft.model.cards.toolcards.CopperFoilBurnisher;
import project.ing.soft.model.cards.toolcards.ToolCard;
import project.ing.soft.model.gamemodel.GameModelFactory;


public class cardTest {
    @Test
    public void toTestString(){
        StringBoxBuilder aBuilder = new StringBoxBuilder(new StringBoxBuilder.DOUBLELINESQUAREANGLE(),20, 8);
        aBuilder.appendInAboxToTop("Hi, it's me Mario");

        aBuilder.prependInAboxToBottom("3");

        aBuilder.appendToTop("contenuto molto molto molto molto molto molto lungo");
        System.out.println(aBuilder);
    }

    @Test
    public void toTestFillACard(){
        ToolCard aCard = new CopperFoilBurnisher();

        StringBoxBuilder aBuilder = new StringBoxBuilder(new StringBoxBuilder.DOUBLELINESQUAREANGLE(),20, 8);
        aBuilder.appendInAboxToTop(aCard.getTitle());
        aBuilder.appendToTop(aCard.getDescription());
        aBuilder.prependInAboxToBottom("Costo: 2");


        System.out.println(aBuilder.toString());
    }

    @Test
    public void toTestFillTwoCards(){
        StringBoxBuilder aBuilder = new StringBoxBuilder(new StringBoxBuilder.SINGLELINEROUNDEDCORNER(),20, 8);
        aBuilder.appendInAboxToTop("Hi, it's me Mario");

        aBuilder.prependInAboxToBottom("3");

        aBuilder.appendToTop("contenuto molto molto molto molto molto molto lungo");
        System.out.println(aBuilder);

        ToolCard otherCard = new CopperFoilBurnisher();

        StringBoxBuilder aBuilder2 = new StringBoxBuilder(new StringBoxBuilder.DOUBLELINESQUAREANGLE(),20, 8);
        aBuilder2.appendInAboxToTop(otherCard.getTitle());
        aBuilder2.appendToTop(otherCard.getDescription());
        aBuilder2.prependInAboxToBottom("Costo: 2");
        System.out.println(aBuilder2);

        System.out.println(StringBoxBuilder.drawNear( aBuilder, aBuilder2));
    }

    @Test
    public void testACardToString(){
        for(Card aCard: GameModelFactory.getPrivateObjCards()) {
            System.out.println(aCard);
        }
        for(Card aCard: GameModelFactory.getPublicObjCards()) {
            System.out.println(aCard);
        }
        for(Card aCard: GameModelFactory.getToolCards()) {
            System.out.println(aCard);
        }


    }
    @Test
    public void TestMultiline(){
        System.out.println(Card.drawNear(0,2, GameModelFactory.getPrivateObjCards().toArray())); }
}
