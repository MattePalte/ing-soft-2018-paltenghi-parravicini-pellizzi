package projectIngSoft.GameManager;


import javafx.util.Pair;
import projectIngSoft.Cards.Card;
import projectIngSoft.Cards.ToolCards.*;
import projectIngSoft.Cards.Objectives.Publics.*;
import projectIngSoft.Cards.Objectives.Privates.*;
import projectIngSoft.Cards.WindowPatternCard;

import projectIngSoft.*;
import projectIngSoft.events.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class GameManagerMulti implements IGameManager {


    private Game currentGame ;

    private ArrayList<Die> diceBag;
    private ArrayList<Die> draftPool;
    private RoundTracker rounds;
    private ArrayList<Card> privateObjectives;
    private ArrayList<Card> publicObjectives;
    private ArrayList<Card> windowPatterns;
    private ArrayList<Card> toolCards;
    private ArrayList<Player> currentTurnList;
    private Map<Player, Integer> favours;

    //@ Signals Exception aGame.isValid() || aGame.numOfPlayers() <= 1 or aGame.numOfPlayers()> 4;
    public GameManagerMulti(Game aGame) throws Exception {

        if (!aGame.isValid() || aGame.getNumberOfPlayers() <= 1  || aGame.getNumberOfPlayers() > 4  )
            //TODO: create a more specific Exception
            throw  new Exception("Game is not valid!");
        currentGame = new Game(aGame);
        }

    public GameManagerMulti clone() {
        return new GameManagerMulti(this);
    }

    private GameManagerMulti(GameManagerMulti gameManagerMulti){
        this.currentGame = new Game(gameManagerMulti.getGameInfo());
        this.diceBag = new ArrayList<>(gameManagerMulti.diceBag);
        this.draftPool = new ArrayList<>(gameManagerMulti.draftPool);
        this.rounds = new RoundTracker(gameManagerMulti.rounds); //TODO: why before it was -> new RoundTracker(rounds)
        this.privateObjectives = new ArrayList<>(gameManagerMulti.privateObjectives);
        this.publicObjectives = new ArrayList<>(gameManagerMulti.publicObjectives);
        this.windowPatterns = new ArrayList<>(gameManagerMulti.windowPatterns);
        this.toolCards = new ArrayList<>(gameManagerMulti.toolCards);
        this.currentTurnList = new ArrayList<>(gameManagerMulti.currentTurnList);
    }

    public List<Player> getRoundTurns(){
        return new ArrayList<>(currentTurnList);
    }

    @Override
    public List<Player> getPlayerList() {
        return currentGame.getPlayers().stream().sorted((p1,p2) -> p1.getName().compareTo(p2.getName())).collect(Collectors.toCollection(ArrayList :: new));
    }

    @Override
    public List<ToolCard> getToolCards() {
        return toolCards.stream().map(card -> (ToolCard)card).collect(Collectors.toList());
    }

    @Override
    public List<Die> getDraftPool() {
        return new ArrayList<>(draftPool);
    }

    @Override
    public Game getGameInfo() {
        return new Game(currentGame);
    }

    @Override
    public Player getCurrentPlayer() {
        return currentTurnList.get(0);
    }

    @Override
    public List<PublicObjective> getPublicObjective() {
        return new ArrayList<PublicObjective>(publicObjectives.stream().map(card -> (PublicObjective)card).collect(Collectors.toList()));
    }

    @Override
    public List<Die> getDiceBag() {
        return new ArrayList(diceBag);
    }

    @Override
    public void start() throws Exception {
        drawDice();
        getCurrentPlayer().prepareDieFlag();
        deliverNewStatus(this, new FinishedSetupEvent());
    }

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

        //initialize hashMap favours
        favours = new HashMap<>();

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

        // do 1, 2 operation for each player
        for (Player p : currentGame.getPlayers()) {
            // 1 - randomly distribute PrivateObjectiveCards
            PrivateObjective randomPrivateObjective = (PrivateObjective)privateObjectives.remove(0);
            p.setPrivateObjective(randomPrivateObjective);

            ArrayList<WindowPatternCard> selectedPatternCards = new ArrayList<>();
            for(int i = 0; i < 2; i++){
                selectedPatternCards.add((WindowPatternCard)windowPatterns.remove(0));
            }

            // TODO: se una pattern card è data tra le possibili scelte ad un giocatore ma non viene selezionata, va reinserita nelle possibilita?
            p.givePossiblePatternCard(new ArrayList<>(selectedPatternCards));
        }

        currentTurnList = createTurns(currentGame.getPlayers());

        deliverNewStatus(this, new PatternCardDistributedEvent());


    }

    @Override
    public void bindPatternAndPlayer(String nickname, Pair<WindowPatternCard, Boolean> chosenPattern){
        for (Player p : getPlayerList()){
            if (p.getName().equals(nickname)){
                p.setPatternCard(chosenPattern.getKey());
                p.setPatternFlipped(chosenPattern.getValue());
                favours.put(p, p.getPattern().getDifficulty());
            }
        }
        // check if all players have chosen their card
        for (Player p : getPlayerList()) {
            if (p.getPatternCard() == null) return;
        }
        // if all have chosen their card start the match
        try {
            start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void countPlayersPoints() throws Exception {

    }

    @Override
    public void requestUpdate() {

    }

    @Override
    public void deliverNewStatus(IGameManager newStatus, Event event) {
        for (Player subscriber : currentGame.getPlayers()) {
            subscriber.update(this.clone(), event);
        }
    }

    @Override
    public Player getWinner() throws Exception {
        return null;
    }

    @Override
    public void playToolCard(ToolCard aToolCard) throws Exception {

    }

    @Override
    public void placeDie(Die aDie, int rowIndex, int colIndex) throws Exception {
        if(getCurrentPlayer().placeDie(aDie,rowIndex,colIndex)) {
            draftPool.remove(aDie);

        }
    }

    @Override
    public List<PrivateObjective> getPrivateObjective() {
        return new ArrayList<PrivateObjective>(privateObjectives.stream().map(obj -> (PrivateObjective)obj).collect(Collectors.toList()));
    }

    @Override
    public List<Card> getPublicCards() {
        return new ArrayList<>(publicObjectives);
    }

    @Override
    public void endTurn() throws Exception {
        currentTurnList.remove(0);
        if(currentTurnList.size() == 0){
            System.out.println("End of round " + rounds.getCurrentRound());
            if(rounds.getCurrentRound() == 10)
                return;
            currentGame.leftShiftPlayers();
            currentTurnList = createTurns(currentGame.getPlayers());
            rounds.addDiceLeft(draftPool);
            rounds.nextRound();
            draftPool.removeAll(draftPool);
            System.out.println("Round " + rounds.getCurrentRound() + " is beginning");
            drawDice();
        }
        getCurrentPlayer().prepareDieFlag();
        if (currentTurnList.size() > 0 ) {
            deliverNewStatus(this, new CurrentPlayerChangedEvent());
        } else {
            deliverNewStatus(this, new GameFinishedEvent());
        }
    }
    @Override
    public Map<Player, Integer> getFavours(){
        return new HashMap<>(favours);
    }

    @Override
    public RoundTracker getRoundTracker() {
        return new RoundTracker(rounds);
    }


    //TODO: check if we can do this in another way
    /*private List<Player> nextRoundTurn(ArrayList<Player> actualTurn){
        List<Player> ret = new ArrayList<>();
        for(int i = 0; i < actualTurn.size(); i++){
            ret.add(i, actualTurn.get((i + 1) % actualTurn.size()));
        }
        return ret;
    }*/

    private ArrayList<Player> createTurns(List<Player> players){
        // create p1 p2 p3
        ArrayList<Player> turn = new ArrayList<>(players);
        // add p3 p2 p1
        Collections.reverse(players);
        turn.addAll(new ArrayList<>(players));
        //turn.addAll(players.stream().sorted((p1, p2) -> players.indexOf(p1) >= players.indexOf(p2) ? 1 : -1).collect(Collectors.toList()));
        return turn; // result: p1 p2 p3 p3 p2 p1
    }

    private void drawDice(){
        ArrayList<Die> dice = new ArrayList<>(diceBag.subList(0, (2 * currentGame.getNumberOfPlayers()) + 1));
        draftPool.addAll(dice);
        //diceBag.removeAll(dice);
        for(int i = 0; i < 2 * currentGame.getNumberOfPlayers() + 1; i++){
            diceBag.remove(i);
        }
    }

    private ArrayList<Die> createDice() {
        ArrayList tmp = new ArrayList<Die>();
        ArrayList<Colour> diceColoursAvailable = new ArrayList<>();
        diceColoursAvailable.add(Colour.BLUE);
        diceColoursAvailable.add(Colour.YELLOW);
        diceColoursAvailable.add(Colour.RED);
        diceColoursAvailable.add(Colour.GREEN);
        diceColoursAvailable.add(Colour.VIOLET);
        Random rndGen = new Random();
        for (Colour c : diceColoursAvailable) {
            // 3 times
            for(int i = 0; i < 18; i++){
                Die aDie = new Die(rndGen.nextInt(6) + 1, c);
                tmp.add(aDie);
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
