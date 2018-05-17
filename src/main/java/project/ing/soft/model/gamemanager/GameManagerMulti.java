package project.ing.soft.model.gamemanager;


import project.ing.soft.model.*;
import project.ing.soft.model.cards.objectives.ObjectiveCard;
import project.ing.soft.model.cards.objectives.privates.PrivateObjective;
import project.ing.soft.model.cards.objectives.publics.PublicObjective;
import project.ing.soft.model.cards.toolcards.MultipleInteractionToolcard;
import project.ing.soft.model.cards.toolcards.ToolCard;
import project.ing.soft.model.gamemanager.events.*;
import project.ing.soft.exceptions.GameInvalidException;
import project.ing.soft.exceptions.RuleViolatedException;
import javafx.util.Pair;
import project.ing.soft.model.cards.Card;
import project.ing.soft.model.cards.WindowPatternCard;

import project.ing.soft.model.gamemanager.events.Event;


import java.io.Serializable;
import java.util.*;
import java.util.List;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public class GameManagerMulti implements IGameManager, Serializable {

    //TODO: keep 3 round during debugging procedue, switch to 10 only in final version
    private static final int ROUNDS_NUMBER = 10;

    private Game                currentGame ;
    private GAME_MANAGER_STATUS status;

    private RoundTracker                rounds;
    private ArrayList<Player>           currentTurnList;
    private List<Pair<Player, Integer>> rank;

    private ArrayList<Die>              diceBag;
    private ArrayList<Die>              draftPool;
    private transient Die unrolledDie;

    private ArrayList<PublicObjective> publicObjectives;

    private ArrayList<ToolCard>         toolCards;
    private Map<String, Integer>        toolCardCost;

    private Map<String, Integer>        favours;

    //Constructor
    //@Signals Exception aGame.isValid() || aGame.numOfPlayers() <= 1 or aGame.numOfPlayers()> 4
    public GameManagerMulti(Game aGame,
                            List<PublicObjective> availablePublicObjectives,
                            List<PrivateObjective> availablePrivateObjectives,
                            List<ToolCard> availableToolCards,
                            List<WindowPatternCard> availableWindowPatternCards,
                            List<Die> dice
    ) throws GameInvalidException {



        if (!aGame.isValid() || aGame.getNumberOfPlayers() <= 1  || aGame.getNumberOfPlayers() > 4  ) {
            status = GAME_MANAGER_STATUS.ENDED;
            throw new GameInvalidException("Game is not valid!");
        }
        status = GAME_MANAGER_STATUS.WAITING_FOR_PATTERNCARD;

        currentGame = new Game(aGame);
        // initialize empty draft pool
        draftPool = new ArrayList<>();

        unrolledDie = null;
        // initialize Round Tracker obj
        rounds = new RoundTracker();
        // create dies and populate Die Bag
        diceBag = new ArrayList<>(dice);
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
            toolCardCost.put(card.getTitle(), 1);
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
    //Copy constructor
    private GameManagerMulti(GameManagerMulti gameManagerMulti){
        this.currentGame        = new Game(gameManagerMulti.currentGame);
        this.diceBag            = new ArrayList<> (gameManagerMulti.diceBag);
        this.draftPool          = new ArrayList<> (gameManagerMulti.draftPool);
        this.rounds             = new RoundTracker(gameManagerMulti.rounds);
        this.publicObjectives   = new ArrayList<> (gameManagerMulti.publicObjectives);
        this.toolCards          = new ArrayList<> (gameManagerMulti.toolCards);
        this.currentTurnList = new ArrayList<>();
        for(Player p : gameManagerMulti.currentTurnList){
            this.currentTurnList.add(new Player(p));
        }
        this.rank               = new ArrayList<> (gameManagerMulti.rank);
        this.toolCardCost       = new HashMap<>   (gameManagerMulti.toolCardCost);
        this.favours            = new HashMap<>   (gameManagerMulti.favours);
        this.status             = gameManagerMulti.status;
    }

    @Override
    public Game getGameInfo()       {
        return new Game(currentGame);
    }
    @Override
    public GAME_MANAGER_STATUS getStatus() {
        return status;
    }
    @Override
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
    public List<Die> getDraftPool() {
        return new ArrayList<>(draftPool);
    }
    @Override
    public void addToDraft(Die aDie) {
        draftPool.add(new Die(aDie));
    }
    @Override
    public void removeFromDraft(Die aDie) {
        draftPool.remove(aDie);
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

    @Override
    public void setupPhase() {
        //distribute event for selecting a WindowPatternCard
        for(Player p : getPlayerList()) {
            deliverEvent(p, new ModelChangedEvent(new GameManagerMulti(this)),
                    new PatternCardDistributedEvent(p.getPrivateObjective(), p.getPossiblePatternCard().get(0), p.getPossiblePatternCard().get(1)));

        }
    }

    @Override
    public void bindPatternAndPlayer(String nickname, WindowPatternCard windowCard, Boolean side) throws GameInvalidException {
        for (Player p : getPlayerList()){
            if (p.getName().equals(nickname)){
                p.setPatternCard(windowCard);
                p.setPatternFlipped(side);
                favours.put(p.getName(), p.getPattern().getDifficulty());
            }
        }
        // check if all players have chosen their card
        for (Player p : getPlayerList()) {
            if (p.getPatternCard() == null) return;
        }
        // if all have chosen their card start the match
        status = GAME_MANAGER_STATUS.ONGOING;

        drawDice();
        broadcastEvents(new FinishedSetupEvent(), new ModelChangedEvent(new GameManagerMulti(this)));
        getCurrentPlayer().update(new MyTurnStartedEvent());
    }

    @Override
    public List<Pair<Player, Integer>> countPlayersPoints() {


        for (Player p : getPlayerList()){
            int sum = 0;
            sum += p.countPrivateObjectivesPoints();

            for (ObjectiveCard pubObj : getPublicObjective()){
                sum += pubObj.countPoints(p);
            }

            sum += favours.get(p.getName());
            sum -= p.getEmptyCells();


            rank.add( new Pair<>(p, sum));
        }

        rank.sort(Comparator
                .comparingInt((ToIntFunction<Pair<Player, Integer>>) Pair::getValue)
                .thenComparingInt((Pair<Player, Integer> p)-> p.getKey().countPrivateObjectivesPoints() )
                .thenComparingInt((Pair<Player, Integer> p)-> favours.get(p.getKey().getName()))
                .thenComparingInt( (Pair<Player, Integer> p) -> currentGame.getPlayers().indexOf(p.getKey()))
                .reversed()
        );

        return rank;
    }


    @Override
    public void setUnrolledDie(Die aDie){
        unrolledDie = aDie;
    }

    @Override
    public void requestUpdate() {

        broadcastEvents( new ModelChangedEvent( new GameManagerMulti(this)));

    }

    private void broadcastEvents(Event ... events){
        for (Player subscriber : currentGame.getPlayers()) {
            deliverEvent(subscriber, events);
        }

    }

    private void deliverEvent(Player p, Event ...events ){
        for(Event event: events) {
            p.update(event);
        }
    }


    @Override
    public Player getWinner() {
        return rank.get(0).getKey();
    }

    @Override
    public void placeDie(Die aDie, int rowIndex, int colIndex) throws Exception {
        if(!draftPool.contains(aDie) && !aDie.equals(unrolledDie))
            throw new RuleViolatedException("The die you want to place does not exist in the current turn");
        getCurrentPlayer().placeDie(aDie,rowIndex,colIndex, true);
        if(unrolledDie == null)
            draftPool.remove(aDie);
        else {
            draftPool.remove(unrolledDie);
            unrolledDie = null;
        }

        deliverEvent(getCurrentPlayer(), new ModelChangedEvent(new GameManagerMulti(this)), new MyTurnStartedEvent());

    }

    @Override
    public void playToolCard(ToolCard aToolCard) throws Exception{
        //Because apply effect embed some test of the fields passed with the toolcard itself
        int actualFavours = favours.get(getCurrentPlayer().getName());

        if(actualFavours < toolCardCost.get(aToolCard.getTitle()))
            throw new RuleViolatedException("Ehi! You don't have enough favours to do that, poor man!!");

        aToolCard.applyEffect(getCurrentPlayer(), this);

        favours.replace(getCurrentPlayer().getName(), actualFavours - toolCardCost.get(aToolCard.getTitle()));
        toolCardCost.replace(aToolCard.getTitle(), 2);


        getCurrentPlayer().update(new ModelChangedEvent(new GameManagerMulti(this)));
        if(!(aToolCard instanceof MultipleInteractionToolcard)) {
            // If a single interaction is needed, send new TurnStartedEvent, otherwise, the toolcard itself will call it due to operation requests
            getCurrentPlayer().update(new MyTurnStartedEvent());
        }
    }


    @Override
    public void endTurn(boolean timeoutOccurred) throws GameInvalidException {
        if (status == GAME_MANAGER_STATUS.ENDED) return;

        Player current = getCurrentPlayer();
        if(timeoutOccurred)
            current.update(new MyTurnEndedEvent());
        current.endTurn();
        currentTurnList.remove(0);

        if(currentTurnList.isEmpty() && rounds.getCurrentRound() == ROUNDS_NUMBER) {

            status = GAME_MANAGER_STATUS.ENDED;
            countPlayersPoints();
            broadcastEvents(new GameFinishedEvent(new ArrayList<>(rank)));
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


        Player next = getCurrentPlayer();
        if(unrolledDie != null)
            unrolledDie = null;
        broadcastEvents(new ModelChangedEvent(new GameManagerMulti(this)));
        next.update(new MyTurnStartedEvent());
    }
    @Override
    public Map<String, Integer> getFavours(){
        return favours;
    }

    @Override
    public RoundTracker getRoundTracker() {
        return new RoundTracker(rounds);
    }

    @Override
    public void swapWithRoundTracker(Die toAdd, Die toRemove) {
        rounds.swapDie(toAdd, toRemove);
    }

    @Override
    public void rollDraftPool(){
        for(int i = 0; i < draftPool.size(); i++){
            draftPool.add(i, draftPool.remove(i).rollDie());
        }
    }

    @Override
    public void addToDicebag(Die aDie){
        diceBag.add(aDie.rollDie());
        Collections.shuffle(diceBag);
    }

    @Override
    public Die drawFromDicebag(){
        Die ret;

        ret = diceBag.remove(new Random().nextInt(diceBag.size()));
        return ret;
    }

    @Override
    public void samePlayerAgain(){
        for(int i = currentTurnList.size() - 1; i >= 0; i--){
            if(currentTurnList.get(i).getName().equals(getCurrentPlayer().getName())) {
                currentTurnList.remove(i);
                break;
            }
        }
        currentTurnList.add(1, getCurrentPlayer());

    }

    @Override
    public void chooseDie(Die aDie) {
        // Whenever this method is called with a null parameter, unset unrolledDie
        if(aDie == null)
            unrolledDie = null;
        // If gamemanager didn't set an unrolledDie, so there's no need for the player to change a die, it return doing nothing
        if(unrolledDie == null)
            return;
        // if the player chose a die with the same colour of unrolledDie, then change it and add it to the draftpool
        if(aDie.getColour().equals(unrolledDie.getColour())) {
            unrolledDie = new Die(aDie);
            draftPool.add(unrolledDie);
            getCurrentPlayer().update(new ModelChangedEvent(new GameManagerMulti(this)));
        }
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

        draftPool = (diceBag.subList(0, (2 * currentGame.getNumberOfPlayers()) + 1)).stream().map(Die::rollDie).collect(Collectors.toCollection(ArrayList::new));

        diceBag = new ArrayList<> (diceBag.subList((2*currentGame.getNumberOfPlayers()) +1, diceBag.size()));

    }

}
