package projectIngSoft.Referee;


import projectIngSoft.Cards.Card;
import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.Cards.Objectives.Publics.*;
import projectIngSoft.Cards.Objectives.Privates.*;
import projectIngSoft.Cards.WindowPatternCard;

import projectIngSoft.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class RefereeControllerMultiplayer implements RefereeController {
    private Game currrentGame ;
    private ArrayList<Die> diceBag;
    private ArrayList<Die> draftPool;
    private RoundTracker rounds;
    private ArrayList<Card> privateObjectives;
    private ArrayList<Card> publicObjectives;
    private ArrayList<WindowPatternCard> windowPatterns;

    public RefereeControllerMultiplayer(Game theGame) throws Exception {
        if (!currrentGame.isValid())
            throw  new Exception("Game is not valid!");
    }

    @Override
    public List<ToolCard> getToolCardAvailable() {
        return null;
    }

    @Override
    public List<Die> getDraftPool() {
        return null;
    }

    @Override
    public Player getCurrentPlayer() {
        return null;
    }

    @Override
    public void startGame() throws FileNotFoundException, Colour.ColorNotFoundException {


            // create dies and populate Die Bag
            diceBag = new ArrayList<Die>();
            for (Colour c : Colour.values()) {
                for (int i = 1; i <= 18; i++) {
                    Die newDie = new Die(c);
                    this.diceBag.add(newDie);
                }
            }
            // initialize empty draft pool
            draftPool = new ArrayList<Die>();
            // initialize Round Tracker obj
            rounds = new RoundTracker();
            // initialize private Objective cards
            privateObjectives = new ArrayList<Card>();
            privateObjectives.add(new SfumatureBlu());
            privateObjectives.add(new SfumatureGialle());
            privateObjectives.add(new SfumatureRosse());
            privateObjectives.add(new SfumatureVerdi());
            privateObjectives.add(new SfumatureViola());
            // initialize public Objective cards
            publicObjectives = new ArrayList<Card>();
            publicObjectives.add(new ColoriDiversiColonna());
            publicObjectives.add(new ColoriDiversiRiga());
            publicObjectives.add(new DiagonaliColorate());
            publicObjectives.add(new SfumatureChiare());
            publicObjectives.add(new SfumatureDiverse());
            publicObjectives.add(new SfumatureDiverseColonna());
            publicObjectives.add(new SfumatureDiverseRiga());
            publicObjectives.add(new SfumatureMedie());
            publicObjectives.add(new SfumatureScure());
            publicObjectives.add(new VarietaColore());
            // initialize window pattern cards
            windowPatterns = createPatternCards();

            // Shuffle everything
            Collections.shuffle(diceBag);
            Collections.shuffle(publicObjectives);
            Collections.shuffle(privateObjectives);
            Collections.shuffle(windowPatterns);
            // remove cards and leave only 3 publicObjective card for the game
            publicObjectives = publicObjectives.stream().limit(3).collect(Collectors.toCollection(ArrayList::new));
            //distribute cards
            for (Player p : currrentGame.getPlayers()) {

                PrivateObjective randomPrivateObjective = (PrivateObjective) privateObjectives.remove(0);

                p.setPrivateObjective(randomPrivateObjective);
                WindowPatternCard aPatternCard = windowPatterns.remove(0);
                if(new Random().nextBoolean())
                    aPatternCard.flip();
                p.setPattern(aPatternCard.getCurrentPattern());

                System.out.println("To " + p.getName() + " has been given : " + p.getPrivateObjective()+
                        "He chose this pattern: \n"+ p.getPattern().toString());

            }
    }

    private static  ArrayList<WindowPatternCard> createPatternCards() throws FileNotFoundException, Colour.ColorNotFoundException {
        File file = new File("src/main/patterns.txt");
        Scanner input = new Scanner(file);

        ArrayList<WindowPatternCard> patterns = new ArrayList<WindowPatternCard>();


        for(int i = 0; i < 12; i++) {
            patterns.add(WindowPatternCard.loadFromScanner(input));
            input.nextLine();
        }
        return patterns;
    }



}
