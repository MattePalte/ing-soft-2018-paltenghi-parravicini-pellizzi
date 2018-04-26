package projectIngSoft.GameManager;


import javafx.util.Pair;
import projectIngSoft.Cards.Card;
import projectIngSoft.Cards.Objectives.ObjectiveCard;
import projectIngSoft.Cards.ToolCards.*;
import projectIngSoft.Cards.Objectives.Publics.*;
import projectIngSoft.Cards.Objectives.Privates.*;
import projectIngSoft.Cards.WindowPatternCard;

import projectIngSoft.*;
import projectIngSoft.events.*;
import projectIngSoft.events.Event;
import projectIngSoft.exceptions.GameInvalidException;
import projectIngSoft.exceptions.RuleViolatedException;


import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;
import java.util.List;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public class GameManagerMulti implements IGameManager, Serializable {


    private Game                        currentGame ;

    private ArrayList<Die>              diceBag;
    private ArrayList<Die>              draftPool;
    private RoundTracker                rounds;

    private ArrayList<PublicObjective > publicObjectives;

    private ArrayList<ToolCard>         toolCards;
    private ArrayList<Player>           currentTurnList;
    private Map<Player, Integer>        favours;
    private List<Pair<Player, Integer>> rank;
    private Map<ToolCard, Integer>      toolCardCost;

    private boolean isFinished;

    //@Signals Exception aGame.isValid() || aGame.numOfPlayers() <= 1 or aGame.numOfPlayers()> 4
    public GameManagerMulti(Game aGame,
                            List<PublicObjective> availablePublicObjectives,
                            List<PrivateObjective> availablePrivateObjectives,
                            List<ToolCard> availableToolCards,
                            List<WindowPatternCard> availableWindowPatternCards) throws GameInvalidException {

        isFinished = false;
        if (!aGame.isValid() || aGame.getNumberOfPlayers() <= 1  || aGame.getNumberOfPlayers() > 4  ) {
            throw new GameInvalidException("Game is not valid!");
        }
        currentGame = new Game(aGame);
        // initialize empty draft pool
        draftPool = new ArrayList<>();
        // initialize Round Tracker obj
        rounds = new RoundTracker();
        // create dies and populate Die Bag
        diceBag = createDice();
        //initialize hashMap favours
        favours = new HashMap<>();
        //initialize hashMap rank
        rank = new ArrayList<>();
        //initialize toolCards cost
        toolCardCost = new HashMap<>();

        // Shuffle everything
        Collections.shuffle(diceBag);
        Collections.shuffle(availablePublicObjectives);
        Collections.shuffle(availablePrivateObjectives);
        Collections.shuffle(availableWindowPatternCards);
        Collections.shuffle(availableToolCards);

        // extract in a random fashion 3 toolCard
        this.toolCards = availableToolCards.stream().limit(3).collect(Collectors.toCollection(ArrayList::new));
        for(ToolCard card : toolCards){
            toolCardCost.put(card, 1);
        }
        // remove cards and leave only 3 publicObjective card for the game
        this.publicObjectives = availablePublicObjectives.stream().limit(3).collect(Collectors.toCollection(ArrayList::new));
        // leave privateObjective equals the number of players in the game
        ArrayList<PrivateObjective> privateObjectives = availablePrivateObjectives.stream().
                limit(currentGame.getNumberOfPlayers()).collect(Collectors.toCollection(ArrayList::new));
        // leave windowPatterns equals the 2*number of players in the game
        ArrayList<WindowPatternCard> windowPatterns   = availableWindowPatternCards.stream().
                limit(currentGame.getNumberOfPlayers()*2).collect(Collectors.toCollection(ArrayList::new));

        this.currentTurnList = createTurns(currentGame.getPlayers());

        // do 1, 2 operation for each player
        for (Player p : currentGame.getPlayers()) {
            // 1 - randomly distribute PrivateObjectiveCards
            PrivateObjective randomPrivateObjective = privateObjectives.remove(0);
            p.setPrivateObjective(randomPrivateObjective);

            ArrayList<WindowPatternCard> selectedPatternCards = new ArrayList<>();
            //2 -extract windowPatternCard and let them choose according to their will
            for(int i = 0; i < 2; i++){
                selectedPatternCards.add(windowPatterns.remove(0));
            }
            p.givePossiblePatternCard(new ArrayList<>(selectedPatternCards));
        }
    }

    @Override
    public void removeFromDraft(Die aDie) {

        draftPool.remove(aDie);

    }

    @Override
    public void addToDraft(Die aDie) {
        draftPool.add(new Die(aDie));
    }

    private GameManagerMulti(GameManagerMulti gameManagerMulti){
        this.currentGame        = new Game(gameManagerMulti.getGameInfo());
        this.diceBag            = new ArrayList<> (gameManagerMulti.diceBag);
        this.draftPool          = new ArrayList<> (gameManagerMulti.draftPool);
        this.rounds             = new RoundTracker(gameManagerMulti.rounds);
        this.publicObjectives   = new ArrayList<> (gameManagerMulti.publicObjectives);
        this.toolCards          = new ArrayList<> (gameManagerMulti.toolCards);
        this.currentTurnList    = new ArrayList<> (gameManagerMulti.currentTurnList);
        this.rank               = new ArrayList<> (gameManagerMulti.rank);
        this.toolCardCost       = new HashMap<>   (gameManagerMulti.toolCardCost);
        this.favours            = new HashMap<>   (gameManagerMulti.favours);
        this.isFinished         = gameManagerMulti.isFinished;
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
    public void start() throws Exception {
        drawDice();
        getCurrentPlayer().resetDieFlag();
        deliverNewStatus(new FinishedSetupEvent());
        deliverNewStatus(new ModelChangedEvent(new GameManagerMulti(this)));
        getCurrentPlayer().update(new myTurnStartedEvent());

    }

    @Override
    public void setupPhase() throws RemoteException{
        //distribute event for selecting a WindowPatternCard

        for(Player p : currentGame.getPlayers()){
            new Thread(() -> {
                try {
                    p.update(new ModelChangedEvent(new GameManagerMulti(this)));
                    p.update(new PatternCardDistributedEvent(p.getPossiblePatternCard().get(0), p.getPossiblePatternCard().get(1)));
                } catch(RemoteException e){
                    e.printStackTrace();
                }
            }).start();

        }
    }

    @Override
    public void bindPatternAndPlayer(String nickname, WindowPatternCard windowCard, Boolean side) throws GameInvalidException {
        for (Player p : getPlayerList()){
            if (p.getName().equals(nickname)){
                p.setPatternCard(windowCard);
                p.setPatternFlipped(side);
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
    public List<Pair<Player, Integer>> countPlayersPoints() {


        for (Player p : getPlayerList()){
            int sum = 0;
            sum += p.countPrivateObjectivesPoints();

            for (ObjectiveCard pubObj : getPublicObjective()){
                sum += pubObj.countPoints(p);
            }

            sum += favours.get(p);
            sum -= getEmptyCells(p.getPlacedDice());


            rank.add( new Pair<>(p, sum));
        }

        rank.sort(Comparator
                .comparingInt((ToIntFunction<Pair<Player, Integer>>) Pair::getValue)
                .thenComparingInt((Pair<Player, Integer> p)-> p.getKey().countPrivateObjectivesPoints() )
                .thenComparingInt((Pair<Player, Integer> p)-> favours.get(p.getKey()))
                .thenComparingInt( (Pair<Player, Integer> p) -> currentGame.getPlayers().indexOf(p.getKey()))
        );

        return rank;
    }



    private int getEmptyCells(Die[][] placedDice){
        int ret = 0;

        for(Die[] row : placedDice)
            for(Die die : row)
                if(die == null)
                    ret++;
        return ret;
    }

    @Override
    public void requestUpdate() throws RemoteException{

        deliverNewStatus( new ModelChangedEvent( new GameManagerMulti(this)));

    }

    @Override
    public void deliverNewStatus(Event event) throws RemoteException{
        for (Player subscriber : currentGame.getPlayers()) {
            subscriber.update( event);
        }
    }

    @Override
    public Player getWinner() throws Exception {
        return rank.get(0).getKey();
    }

    @Override
    public void playToolCard(ToolCard aToolCard) throws Exception {
        //Because apply effect embed some test of the fields passed with the toolcard itself

        if(favours.get(getCurrentPlayer()) < toolCardCost.get(aToolCard))
            throw new RuleViolatedException("Ehi! You don't have enough favours to do that, poor man!!");

        aToolCard.applyEffect(getCurrentPlayer(), this);

        int actualFavours = favours.get(getCurrentPlayer());
        favours.replace(getCurrentPlayer(), actualFavours - toolCardCost.get(aToolCard));
        toolCardCost.replace(aToolCard, 2);
        getCurrentPlayer().update( new ModelChangedEvent(new GameManagerMulti(this)));
    }

    @Override
    public void placeDie(Die aDie, int rowIndex, int colIndex) throws Exception {
        getCurrentPlayer().placeDie(aDie,rowIndex,colIndex);
        draftPool.remove(aDie);

        getCurrentPlayer().update( new ModelChangedEvent(new GameManagerMulti(this)));
    }


    @Override
    public void endTurn() throws Exception {
        if (isFinished) return;

        currentTurnList.remove(0);

        if(currentTurnList.isEmpty() && rounds.getCurrentRound() == 3) {
            //TODO: keep 3 round during debugging procedue, switch to 10 only in final version
            isFinished = true;
            countPlayersPoints();
            deliverNewStatus(new GameFinishedEvent(new ArrayList<>(rank)));
            return;
        }else if(currentTurnList.isEmpty()){
            System.out.println("End of round " + rounds.getCurrentRound());

            currentGame.leftShiftPlayers();
            currentTurnList = createTurns(currentGame.getPlayers());
            rounds.addDiceLeft(draftPool);
            rounds.nextRound();
            draftPool.clear();
            System.out.println("Round " + rounds.getCurrentRound() + " is beginning");
            drawDice();
        }
        getCurrentPlayer().resetDieFlag();
        deliverNewStatus( new ModelChangedEvent(new GameManagerMulti(this)));
        getCurrentPlayer().update(new myTurnStartedEvent());

    }
    @Override
    public Map<Player, Integer> getFavours(){
        return favours;
    }

    @Override
    public RoundTracker getRoundTracker() {
        return new RoundTracker(rounds);
    }

    @Override
    public void swapWithRoundTracker(Die toRemove, Die toAdd) {
        rounds.swapDie(toRemove, toAdd);
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

        draftPool = new ArrayList<> (diceBag.subList(0, (2 * currentGame.getNumberOfPlayers()) + 1));
        diceBag = new ArrayList<> (diceBag.subList((2*currentGame.getNumberOfPlayers()) +1, diceBag.size()));

    }

    private ArrayList<Die> createDice() {
        ArrayList tmp = new ArrayList<Die>();

        for (Colour c : Colour.validColours()) {
            //because aDie it's an immutable class.
            Die aDie = new Die( c);
            // 3 times
            for(int i = 0; i < 18; i++){


                tmp.add(aDie.rollDie());
            }
        }
        return tmp;
    }





}
