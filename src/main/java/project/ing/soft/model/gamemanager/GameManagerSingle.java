package project.ing.soft.model.gamemanager;

import project.ing.soft.model.*;
import project.ing.soft.model.cards.objectives.privates.*;
import project.ing.soft.model.cards.objectives.publics.*;
import project.ing.soft.model.cards.toolcards.*;
import project.ing.soft.model.cards.WindowPatternCard;
import project.ing.soft.exceptions.GameInvalidException;
import project.ing.soft.model.cards.Card;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class GameManagerSingle {
    private Game currentGame ;

    private ArrayList<Die> diceBag;
    private ArrayList<Die> draftPool;
    private RoundTracker rounds;
    private ArrayList<PrivateObjective> privateObjectives;
    private ArrayList<PublicObjective> publicObjectives;

    private ArrayList<ToolCard> toolCards;
    private ArrayList<Player> currentTurn;
    private final int difficulty;

    //@ Signals Exception aGame.isValid() || aGame.numOfPlayers() != 1
    public GameManagerSingle(Game aSinglePlayerGame) throws Exception, GameInvalidException {

        if (!aSinglePlayerGame.isValid() || aSinglePlayerGame.getNumberOfPlayers() != 1 )
            throw  new GameInvalidException("Game is not valid!");
        //TODO clone object and avoid using the same reference
        currentGame = aSinglePlayerGame;
        //TODO: ask for difficulty
        difficulty = 1;

        if( difficulty <= 0 || difficulty > 5)
            throw  new GameInvalidException("Difficulty must be set between 1 and 5");

        setupPhase();


    }

    private GameManagerSingle(GameManagerSingle gameManagerSingle){
        this.currentGame = new Game(gameManagerSingle.getGameInfo());
        this.diceBag = new ArrayList<>(gameManagerSingle.diceBag);
        this.draftPool = new ArrayList<>(gameManagerSingle.draftPool);
        this.rounds = new RoundTracker(rounds);
        this.privateObjectives = new ArrayList<>(gameManagerSingle.privateObjectives);
        this.publicObjectives = new ArrayList<>(gameManagerSingle.publicObjectives);
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

    public void setupPhase() throws GameInvalidException {


        // initialize empty draft pool
        draftPool = new ArrayList<>();
        // initialize Round Tracker obj
        rounds = new RoundTracker();
        //TODO: move inizialization into a static method, where just copying pre-initialized elements.
        //TODO: get class contained in projectIngSoft.cards.objectives.public.* instead of using a method in every referee. in order to use something like cards.max4Players.deckOfPublicObjectives .
        // create dies and populate Die Bag
        diceBag = createDice();
        // initialize private Objective cards
        privateObjectives = createPrivateObjectives();
        // initialize public Objective cards
        publicObjectives =  createPublicObjectives();
        // initialize window pattern cards
        List<WindowPatternCard>windowPatterns = createPatternCards();
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
        //extract from deck 2 Private objectives cards
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
        List<Player> players = currentGame.getPlayers();

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
        ArrayList<Die> tmp = new ArrayList<>();
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

        tmp.add(new ShadesOfBlue());
        tmp.add(new ShadesOfYellow());
        tmp.add(new ShadesOfRed());
        tmp.add(new ShadesOfGreen());
        tmp.add(new ShadesOfPurple());

        return tmp;
    }

    private ArrayList<PublicObjective> createPublicObjectives() {
        ArrayList<PublicObjective> tmp = new ArrayList<>();

        tmp.add(new ColumnColourVariety());
        tmp.add(new RowColourVariety());
        tmp.add(new Diagonals());
        tmp.add(new LightShades());
        tmp.add(new ShadeVariety());
        tmp.add(new ColumnShadeVariety());
        tmp.add(new RowShadeVariety());
        tmp.add(new MediumShades());
        tmp.add(new DarkShades());
        tmp.add(new ColourVariety());

        return tmp;
    }

    private ArrayList<ToolCard> createToolCards() {
        ArrayList<ToolCard> tmp = new ArrayList<>();

        tmp.add( new AlesatoreLaminaRame());
        tmp.add( new DiluentePerPastaSalda());
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
                patterns.add(WindowPatternCard.loadAPatternCardFromScanner(input));
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
