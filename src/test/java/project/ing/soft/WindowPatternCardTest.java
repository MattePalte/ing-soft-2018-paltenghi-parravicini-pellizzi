package project.ing.soft;

import org.junit.Assert;
import org.junit.Test;
import project.ing.soft.model.cards.WindowPatternCard;
import project.ing.soft.model.gamemanager.GameManagerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WindowPatternCardTest {
    @Test
    public void testLoading(){
        URL path = GameManagerFactory.class.getClassLoader().getResource("patterns.txt");
        List<WindowPatternCard> windowPatternCard;
        windowPatternCard = new ArrayList<>(WindowPatternCard.loadFromFile(path));
        Assert.assertNotNull(windowPatternCard);
    }
}
