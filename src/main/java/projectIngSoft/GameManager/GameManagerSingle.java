package projectIngSoft.GameManager;

import projectIngSoft.*;
import projectIngSoft.Cards.Card;
import projectIngSoft.Cards.Objectives.ObjectiveCard;
import projectIngSoft.Cards.Objectives.Privates.*;
import projectIngSoft.Cards.Objectives.Publics.*;
import projectIngSoft.Cards.ToolCards.*;
import projectIngSoft.Cards.WindowPatternCard;
import projectIngSoft.Controller.IController;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class GameManagerSingle {
    private Game currentGame ;

    private ArrayList<Die> diceBag;
    private ArrayList<Die> draftPool;
    private RoundTracker rounds;
    private ArrayList<Card> privateObjectives;
    private ArrayList<Card> publicObjectives;
    private ArrayList<Card> windowPatterns;
    private ArrayList<Card> toolCards;
    private ArrayList<Player> currentTurn;
    private final int difficulty;

    //@ Signals Exception aGame.isValid() || aGame.numOfPlayers() != 1;
    public GameManagerSingle(Game aSinglePlayerGame) throws Exception {

        if (!aSinglePlayerGame.isValid() || aSinglePlayerGame.getNumberOfPlayers() != 1 )
            throw  new Exception("Game is not valid!");
        //TODO clone object and avoid using the same reference
        currentGame = aSinglePlayerGame;
        //TODO: ask for difficulty
        difficulty = 1;

        if( difficulty <= 0 || difficulty > 5)
            throw  new Exception("Difficulty must be set between 1 and 5");

        setupPhase();


    }

    @Override
    public GameManagerSingle clone(){
        return new GameManagerSingle(this);
    }

    private GameManagerSingle(GameManagerSingle gameManagerSingle){
        this.currentGame = new Game(gameManagerSingle.getGameInfo());
        this.diceBag = new ArrayList<>(gameManagerSingle.diceBag);
        this.draftPool = new ArrayList<>(gameManagerSingle.draftPool);
        this.rounds = new RoundTracker(rounds);
        this.privateObjectives = new ArrayList<>(gameManagerSingle.privateObjectives);
        this.publicObjectives = new ArrayList<>(gameManagerSingle.publicObjectives);
        this.windowPatterns = new ArrayList<>(gameManagerSingle.windowPatterns);
        this.toolCards = new ArrayList<>(gameManagerSingle.toolCards);
        this.currentTurn = new ArrayList<>(gameManagerSingle.currentTurn);
        this.difficulty = gameManagerSingle.difficulty;
    }


    //ensures /result.equals(List.of( game.getPlayers().get(0), game.getPlayers().get(0)))
    public List<Player> getRoundTurns(){
        // return list with two times the same player
        Player p =currentGame.getPlayers().get(0);
        ArrayList<Player> resultList = new ArrayList<>();
        resultList.add(p);
        resultList.add(p);
        return resultList;
    }

    //nsures /result.equals(List.of( game.getPlayers().get(0))

    public List<Player> getPlayerList() {
        return new ArrayList<>(currentGame.getPlayers());
    }


    public List<ToolCard> getToolCards() {
        return toolCards.stream().map(card -> (ToolCard)card).collect(Collectors.toList());
    }


    public List<Die> getDraftPool() {
        return new ArrayList<>(draftPool);
    }


    public Game getGameInfo() {
        return new Game(currentGame);
    }


    public Player getCurrentPlayer() {
        return currentTurn.get(0);
    }


    public List<PublicObjective> getPublicObjective() {
        return null;
    }


    public List<Die> getDiceBag() {
        //TODO: get rid of the method!
        return null;
    }




    public void countPlayersPoints() throws Exception {

    }


    public void requestUpdate() {

    }


    public void deliverNewStatus(IGameManager newStatus) {

    }


    public Player getWinner() throws Exception {
        return null;
    }


    public void playToolCard(ToolCard aToolCard) throws Exception {

    }


    public void placeDie(Die aDie, int rowIndex, int colIndex) throws Exception {

    }


    public List<PrivateObjective> getPrivateObjective() {
        return null;
    }


    public List<Card> getPublicCards() {
        return null;
    }


    public void endTurn() throws Exception{

    }


    public void start() {

    }

    public void setupPhase() throws FileNotFoundException, Colour.ColorNotFoundException  {


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

        // extract in a random fashion 2 toolCard
        toolCards = toolCards.stream().limit(difficulty).collect(Collectors.toCollection(ArrayList::new));
        // remove cards and leave only 2 publicObjective card for the game
        publicObjectives = publicObjectives.stream().limit(2).collect(Collectors.toCollection(ArrayList::new));
        //extract from deck 2 Private Objectives cards
        privateObjectives = privateObjectives.stream().limit(2).collect(Collectors.toCollection(ArrayList::new));

    }




    public Map<Player, Integer> getFavours(){
        return new HashMap<>();
    }


    public RoundTracker getRoundTracker() {
        return null;
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
