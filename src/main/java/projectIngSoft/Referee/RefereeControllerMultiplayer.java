package projectIngSoft.Referee;


import projectIngSoft.Cards.Card;
import projectIngSoft.Cards.ToolCards.*;
import projectIngSoft.Cards.Objectives.Publics.*;
import projectIngSoft.Cards.Objectives.Privates.*;
import projectIngSoft.Cards.WindowPatternCard;

import projectIngSoft.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class RefereeControllerMultiplayer implements RefereeController {


    private Game currentGame ;

    private ArrayList<Die> diceBag;
    private ArrayList<Die> draftPool;
    private RoundTracker rounds;
    private ArrayList<Card> privateObjectives;
    private ArrayList<Card> publicObjectives;
    private ArrayList<Card> windowPatterns;
    private ArrayList<Card> toolCards;
    private ArrayList<Player> currentTurn;
    private Map<Player, Integer> favours;

    //@ Signals Exception aGame.isValid() || aGame.numOfPlayers() <= 0 or aGame.numOfPlayers()> 4;
    public RefereeControllerMultiplayer(Game aGame) throws Exception {

        if (!aGame.isValid())
            throw  new Exception("Game is not valid!");
        currentGame = new Game(aGame);
        currentTurn = getTurn();
        //TODO favours whould be distributed after setup phase, otherwise players don't own any pattern
        favours = new HashMap<>();
        for(Player player : currentGame.getPlayers())
            favours.put(player, player.getVisiblePattern().getDifficulty());
    }

    public List<Player> getRoundTurns(){
        return new ArrayList<>(currentTurn);
    }

    @Override
    public List<ToolCard> getToolCardAvailable() {
        return toolCards.stream().map(card -> (ToolCard)card).collect(Collectors.toList());
    }

    @Override
    public List<Die> getDraftPool() {
        return new ArrayList<>(draftPool);
    }

    @Override
    public Player getCurrentPlayer() {
        return currentTurn.get(0);
    }

    @Override
    public void setupPhase() throws FileNotFoundException, Colour.ColorNotFoundException  {



        //Package pk = projectIngSoft.Cards.Objectives Package().getAnnotations();

        // initialize empty draft pool
        draftPool = new ArrayList<Die>();

        // initialize Round Tracker obj
        rounds = new RoundTracker();
        //TODO: move inizialization into a static method, where just copying pre-initialized elements.
        //TODO: get class contained in projectIngSoft.Cards.Objectives.public.* instead of using a method in every referee. in order to use something like Cards.max4Players.deckOfPublicObjectives .

        // create dies and populate Die Bag
        diceBag = createDice();

        // initialize private Objective cards
        privateObjectives = createPrivateObjectives();

        // initialize public Objective cards
        publicObjectives =  createPublicObjectives();

        // initialize window pattern cards
        windowPatterns = createPatternCards();

        //initialize toolcards
        toolCards = createToolCards();


        // Shuffle everything
        Collections.shuffle(diceBag);
        Collections.shuffle(publicObjectives);
        Collections.shuffle(privateObjectives);
        Collections.shuffle(windowPatterns);
        Collections.shuffle(toolCards);

        // extract in a random fashion 3 toolCard
        toolCards = toolCards.stream().limit(3).collect(Collectors.toCollection(ArrayList::new));

        // remove cards and leave only 3 publicObjective card for the game
        publicObjectives = publicObjectives.stream().limit(3).collect(Collectors.toCollection(ArrayList::new));

        //randomly distribute PrivateObjectiveCards
        for (Player p : currentGame.getPlayers()) {

            PrivateObjective randomPrivateObjective = (PrivateObjective)privateObjectives.remove(0);

            p.setPrivateObjective(randomPrivateObjective);
            WindowPatternCard aPatternCard = (WindowPatternCard) windowPatterns.remove(0);
            if(new Random().nextBoolean())
                aPatternCard.flip();
            p.setPatternCard(aPatternCard);

        }
    }

    @Override
    public void watchTheGame() throws Exception {
        Scanner userInput = new Scanner(System.in);
        String input;
        for(Player player : currentTurn){
            String playerName = getCurrentPlayer().getName();


        }


    }

    @Override
    public void countPlayersPoints() throws Exception {

    }

    @Override
    public Player getWinner() throws Exception {
        return null;
    }

    @Override
    public List<Card> getObjectives() throws Exception {
        return new ArrayList<>(publicObjectives);
    }

    @Override
    public void playToolCard(ToolCard aToolCard) throws Exception {

    }

    @Override
    public void placeDie(Die aDie, int rowIndex, int colIndex) throws Exception {

    }

    private Map<Player, Integer> getFavours(){
        return new HashMap<>(favours);
    }

    private List<Player> turnLeftShift(ArrayList<Player> actualTurn){
        List<Player> ret = new ArrayList<>();
        for(int i = 0; i < actualTurn.size(); i++){
            ret.add((i) % 3, actualTurn.get((i + 1) % 3));
        }
        return ret;
    }

    private ArrayList<Player> getTurn(){
        ArrayList<Player> players = currentGame.getPlayers();

        ArrayList<Player> turn = new ArrayList<>(players);
        turn.addAll(players.stream().sorted((p1, p2) -> players.indexOf(p1) >= players.indexOf(p2) ? 1 : -1).collect(Collectors.toList()));
        return turn;
    }

    private void drawDice(){
        ArrayList<Die> dice = new ArrayList<>(diceBag.subList(0, (2 * currentGame.getNumberOfPlayers()) + 1));
        draftPool.addAll(dice);
        diceBag.removeAll(dice);
    }

    private ArrayList<Die> createDice() {
        ArrayList tmp = new ArrayList<Die>();
        for (Colour c : Colour.values()) {
            for (int i = 1; i <= 18; i++) {
                Die newDie = new Die(c);
                tmp.add(newDie);
            }
        }
        return tmp;
    }

    private ArrayList<Card> createPrivateObjectives() {
        ArrayList<Card> tmp = new ArrayList<Card>();

        tmp.add(new SfumatureBlu());
        tmp.add(new SfumatureGialle());
        tmp.add(new SfumatureRosse());
        tmp.add(new SfumatureVerdi());
        tmp.add(new SfumatureViola());

        return tmp;
    }

    private ArrayList<Card> createPublicObjectives() {
        ArrayList<Card> tmp = new ArrayList<Card>();

        tmp.add(new ColoriDiversiColonna());
        tmp.add(new ColoriDiversiRiga());
        tmp.add(new DiagonaliColorate());
        tmp.add(new SfumatureChiare());
        tmp.add(new SfumatureDiverse());
        tmp.add(new SfumatureDiverseColonna());
        tmp.add(new SfumatureDiverseRiga());
        tmp.add(new SfumatureMedie());
        tmp.add(new SfumatureScure());
        tmp.add(new VarietaColore());

        return tmp;
    }

    private ArrayList<Card> createToolCards() {
        ArrayList<Card> tmp = new ArrayList<>();

        tmp.add( new AlesatoreLaminaRame());
        tmp.add( new DiluentePastaSalda());
        tmp.add( new Lathekin());
        tmp.add( new Martelletto());
        tmp.add( new PennelloPastaSalda());
        tmp.add( new PennelloPerEglomise());
        tmp.add( new PinzaSgrossatrice());
        tmp.add( new RigaSughero());
        tmp.add( new StripCutter());
        tmp.add( new TaglierinaCircolare());
        tmp.add( new TaglierinaManuale());
        tmp.add( new TamponeDiamantato());
        tmp.add( new TenagliaRotelle());

        return tmp;
    }

    private static  ArrayList<Card> createPatternCards() throws FileNotFoundException, Colour.ColorNotFoundException {
        File file = new File("src/main/patterns.txt");
        Scanner input = new Scanner(file);

        ArrayList<Card> patterns = new ArrayList<Card>();

        for(int i = 0; i < 12; i++) {
            patterns.add(WindowPatternCard.loadFromScanner(input));
            input.nextLine();
        }

        return patterns;
    }



}
