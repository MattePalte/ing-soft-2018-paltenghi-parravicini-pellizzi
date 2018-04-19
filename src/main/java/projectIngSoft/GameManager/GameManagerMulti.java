package projectIngSoft.GameManager;


import javafx.util.Pair;
import projectIngSoft.Cards.Card;
import projectIngSoft.Cards.ToolCards.*;
import projectIngSoft.Cards.Objectives.Publics.*;
import projectIngSoft.Cards.Objectives.Privates.*;
import projectIngSoft.Cards.WindowPatternCard;

import projectIngSoft.*;
import projectIngSoft.events.*;
import projectIngSoft.events.Event;
import projectIngSoft.exceptions.GameInvalidException;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class GameManagerMulti implements IGameManager, Cloneable {


    private Game                        currentGame ;

    private ArrayList<Die>              diceBag;
    private ArrayList<Die>              draftPool;
    private RoundTracker                rounds;

    private ArrayList<PublicObjective > publicObjectives;

    private ArrayList<ToolCard>         toolCards;
    private ArrayList<Player>           currentTurnList;
    private Map<Player, Integer>        favours;

    //@Signals Exception aGame.isValid() || aGame.numOfPlayers() <= 1 or aGame.numOfPlayers()> 4
    public GameManagerMulti(Game aGame) throws GameInvalidException {

        if (!aGame.isValid() || aGame.getNumberOfPlayers() <= 1  || aGame.getNumberOfPlayers() > 4  )

            throw  new GameInvalidException("Game is not valid!");
        currentGame = new Game(aGame);
    }

    private GameManagerMulti(GameManagerMulti gameManagerMulti){
        this.currentGame        = new Game(gameManagerMulti.getGameInfo());
        this.diceBag            = new ArrayList<> (gameManagerMulti.diceBag);
        this.draftPool          = new ArrayList<> (gameManagerMulti.draftPool);
        this.rounds             = new RoundTracker(gameManagerMulti.rounds);
        this.publicObjectives   = new ArrayList<> (gameManagerMulti.publicObjectives);
        this.toolCards          = new ArrayList<> (gameManagerMulti.toolCards);
        this.currentTurnList    = new ArrayList<> (gameManagerMulti.currentTurnList);
    }

    public GameManagerMulti clone() {
        return new GameManagerMulti(this);
    }


    @Override
    public Game getGameInfo()       {
        return new Game(currentGame);
    }
    @Override
    public List<Die> getDraftPool() {
        return new ArrayList<>(draftPool);
    }
    @Override
    public List<PublicObjective> getPublicObjective() {
        return new ArrayList<>(publicObjectives);
    }
    @Override
    public List<ToolCard> getToolCards()     {
        return new ArrayList<>(toolCards);
    }

    @Override
    public List<Card> getPublicCards() {
        ArrayList<Card> ret = new ArrayList<>();
        ret.addAll(publicObjectives);
        ret.addAll(toolCards);
        return ret;
    }

    public List<Player> getCurrentTurnList(){
        return new ArrayList<>(currentTurnList);
    }

    @Override
    public List<Player> getPlayerList() {
        return currentGame.getPlayers().stream().sorted((p1,p2) -> p1.getName().compareTo(p2.getName())).collect(Collectors.toCollection(ArrayList :: new));
    }
    @Override
    public Player getCurrentPlayer() {
        return currentTurnList.get(0);
    }

    @Override
    public void start() throws Exception, GameInvalidException {
        drawDice();
        getCurrentPlayer().resetDieFlag();
        deliverNewStatus(this, new FinishedSetupEvent());
    }

    public void setupPhase() throws GameInvalidException {
        //TODO: move inizialization into a static method, where just copying pre-initialized elements.
        //TODO: get class contained in projectIngSoft.Cards.Objectives.public.* instead of using a method in every referee. in order to use something like Cards.max4Players.deckOfPublicObjectives .

        // initialize empty draft pool
        draftPool = new ArrayList<>();

        // initialize Round Tracker obj
        rounds = new RoundTracker();

        // create dies and populate Die Bag
        diceBag = createDice();

        // initialize private Objective cards
        ArrayList<PrivateObjective> privateObjectives = createPrivateObjectives();

        // initialize public Objective cards
        publicObjectives =  createPublicObjectives();

        // initialize window pattern cards
        ArrayList<WindowPatternCard> windowPatterns = createPatternCards();

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

            p.givePossiblePatternCard(new ArrayList<>(selectedPatternCards));
            p.update(this, new PatternCardDistributedEvent(selectedPatternCards.get(0), selectedPatternCards.get(1)));
        }

        currentTurnList = createTurns(currentGame.getPlayers());



    }

    @Override
    public void bindPatternAndPlayer(String nickname, Pair<WindowPatternCard, Boolean> chosenPattern) throws GameInvalidException {
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
        getCurrentPlayer().placeDie(aDie,rowIndex,colIndex);
        draftPool.remove(aDie);
    }


    @Override
    public void endTurn() throws Exception, GameInvalidException {
        currentTurnList.remove(0);

        if(currentTurnList.isEmpty()){
            System.out.println("End of round " + rounds.getCurrentRound());

            if(rounds.getCurrentRound() == 10){
                deliverNewStatus(this, new GameFinishedEvent());
                return;
            }

            currentGame.leftShiftPlayers();
            currentTurnList = createTurns(currentGame.getPlayers());
            rounds.addDiceLeft(draftPool);
            rounds.nextRound();
            draftPool.clear();
            System.out.println("Round " + rounds.getCurrentRound() + " is beginning");
            drawDice();
        }
        getCurrentPlayer().resetDieFlag();

        deliverNewStatus(this, new CurrentPlayerChangedEvent());
    }
    @Override
    public Map<Player, Integer> getFavours(){
        return new HashMap<>(favours);
    }

    @Override
    public RoundTracker getRoundTracker() {
        return new RoundTracker(rounds);
    }

    private ArrayList<Player> createTurns(List<Player> players){
        // create p1 p2 p3
        ArrayList<Player> turn = new ArrayList<>(players);
        // add p3 p2 p1
        Collections.reverse(players);
        turn.addAll(new ArrayList<>(players));

        return turn; // result: p1 p2 p3 p3 p2 p1
    }

    private void drawDice() throws GameInvalidException{
        if(!draftPool.isEmpty())
            throw new GameInvalidException("Panic");

        draftPool = (ArrayList<Die>) diceBag.subList(0, (2 * currentGame.getNumberOfPlayers()) + 1);
        diceBag = (ArrayList<Die>) diceBag.subList((2*currentGame.getNumberOfPlayers()) +1, diceBag.size());

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

    private ArrayList<PrivateObjective> createPrivateObjectives() {
        ArrayList<PrivateObjective> tmp = new ArrayList<>();

        tmp.add(new SfumatureBlu());
        tmp.add(new SfumatureGialle());
        tmp.add(new SfumatureRosse());
        tmp.add(new SfumatureVerdi());
        tmp.add(new SfumatureViola());

        return tmp;
    }

    private ArrayList<PublicObjective> createPublicObjectives() {
        ArrayList<PublicObjective> tmp = new ArrayList<>();

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

    private ArrayList<ToolCard> createToolCards() {
        ArrayList<ToolCard> tmp = new ArrayList<>();

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

    private static  ArrayList<WindowPatternCard> createPatternCards() throws GameInvalidException{

        ArrayList<WindowPatternCard> patterns = new ArrayList<>();
        try( Scanner input = new Scanner(new File("src/main/patterns.txt"))) {
            for (int i = 0; i < 12; i++) {
                patterns.add(WindowPatternCard.loadFromScanner(input));
                input.nextLine();
            }

        } catch(FileNotFoundException ex){
            throw new GameInvalidException("Error while loading cards from file. Aborting...");
        } catch (Colour.ColorNotFoundException ex){
            throw new GameInvalidException("Error while loading cards from file. color error Aborting...");
        }

        return patterns;
    }



}
