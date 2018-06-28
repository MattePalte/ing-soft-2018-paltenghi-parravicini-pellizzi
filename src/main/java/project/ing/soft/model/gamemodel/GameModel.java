package project.ing.soft.model.gamemodel;

import project.ing.soft.Settings;
import project.ing.soft.exceptions.*;
import project.ing.soft.model.*;
import project.ing.soft.model.cards.objectives.ObjectiveCard;
import project.ing.soft.model.cards.objectives.privates.PrivateObjective;
import project.ing.soft.model.cards.objectives.publics.PublicObjective;
import project.ing.soft.model.cards.toolcards.ToolCard;
import project.ing.soft.model.gamemodel.events.*;
import project.ing.soft.model.cards.WindowPatternCard;

import project.ing.soft.view.IView;


import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.ToIntFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;



public class GameModel implements IGameModel, Serializable {
    private Game                        currentGame ;
    private GAME_MANAGER_STATUS         status;

    private RoundTracker                roundTracker;
    private ArrayList<Die>              diceBag;
    private ArrayList<Die>              draftPool;

    private Round                       currentRound;

    private ArrayList<PublicObjective>  publicObjectives;
    private ArrayList<ToolCard>         toolCards;
    private Map<String, Integer>        toolCardCost;
    private Map<String, Integer>        favours;
    private boolean aToolcardUsedDuringThisTurn;

    private transient Timestamp currentPlayerEndTime;
    private transient Logger logger;
    //Constructor
    //@Signals Exception aGame.isValid() || aGame.numOfPlayers() <= 1 or aGame.numOfPlayers()> 4
    GameModel(Game aGame,
              List<PublicObjective> availablePublicObjectives,
              List<PrivateObjective> availablePrivateObjectives,
              List<ToolCard> availableToolCards,
              List<WindowPatternCard> availableWindowPatternCards,
              List<Die> dice
    ) throws GameInvalidException {
        logger = Logger.getLogger(this.getClass().getCanonicalName()+aGame.getPlayers().stream().map(Player::getName).collect(Collectors.toList()).toString());
        logger.setLevel(Settings.instance().getDefaultLoggingLevel());


        if (!aGame.isValid() || aGame.getNumberOfPlayers() <= 1  || aGame.getNumberOfPlayers() > 4  ) {
            setStatus(GAME_MANAGER_STATUS.ENDED);
            throw new GameInvalidException("Game is not valid!");
        }

        setStatus(GAME_MANAGER_STATUS.WAITING_FOR_PATTERNCARD);

        currentGame = aGame;
        // initialize empty draft pool
        draftPool = new ArrayList<>();
        // initialize Round Tracker obj
        roundTracker = new RoundTracker();
        // create dies and populate Die Bag
        diceBag = new ArrayList<>(dice);
        //initialize hashMap favours
        favours = new HashMap<>();
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
        this.toolCards.forEach( (ToolCard t) -> toolCardCost.put(t.getTitle(), 1));
        // remove cards and leave only 3 publicObjective card for the game
        this.publicObjectives = availablePublicObjectives.stream().limit(3).collect(Collectors.toCollection(ArrayList::new));
        // leave privateObjective equals the number of players in the game
        ArrayList<PrivateObjective> privateObjectives = availablePrivateObjectives.stream().
                limit(currentGame.getNumberOfPlayers()).collect(Collectors.toCollection(ArrayList::new));
        // leave windowPatterns equals the 2*number of players in the game
        ArrayList<WindowPatternCard> windowPatterns   = availableWindowPatternCards.stream().
                limit((long)currentGame.getNumberOfPlayers()*2).collect(Collectors.toCollection(ArrayList::new));

        this.currentRound = new Round(0,currentGame);
        logger.log(Level.INFO, "created turns");
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
        aToolcardUsedDuringThisTurn = false;
        logger.log(Level.INFO, "distributed cards");
    }

    //Copy constructor
    private GameModel(GameModel from, Player recipient){
        from.logger.log(Level.INFO, "A game manager was cloned from this");
        this.logger             = Logger.getAnonymousLogger();
        this.logger.setLevel(Settings.instance().getDefaultLoggingLevel());
        this.currentGame        = new Game(from.currentGame);
        for(Player p : this.currentGame.getPlayers())
            if(!p.getName().equals(recipient.getName()))
                p.setPrivateObjective(null);
        this.diceBag            = new ArrayList<> (from.diceBag);
        this.draftPool          = new ArrayList<> (from.draftPool);
        this.roundTracker       = new RoundTracker(from.roundTracker);
        this.publicObjectives   = new ArrayList<> (from.publicObjectives);
        this.toolCards          = new ArrayList<> (from.toolCards);
        this.currentRound       = new Round(from.currentRound, currentGame);
        for(Player p : from.currentGame){
            Player toAdd = new Player(p);
            if(!toAdd.getName().equals(recipient.getName()))
                toAdd.setPrivateObjective(null);
        }
        this.toolCardCost       = new HashMap<>   (from.toolCardCost);
        this.favours            = new HashMap<>   (from.favours);
        this.aToolcardUsedDuringThisTurn = from.aToolcardUsedDuringThisTurn;
        this.setStatus(from.status);

    }
    //
    @Override
    public IGameModel copy(Player recipient){
        return new GameModel(this, recipient);
    }

    //region Getter
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
        return currentRound.getRemaining();
    }
    @Override
    public List<Player> getPlayerList() { return currentGame.getPlayers(); }
    @Override
    public Player getCurrentPlayer() {
        return currentRound.getCurrent();
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
    public Map<String, Integer> getFavours(){
        return favours;
    }

    @Override
    public RoundTracker getRoundTracker() {
        return new RoundTracker(roundTracker);
    }

    @Override
    public void swapWithRoundTracker(Die toAdd, Die toRemove) {
        roundTracker.swapDie(toAdd, toRemove);
    }
    //endregion

    //region operations exposed for ToolCards
    @Override
    public void addToDraft(Die aDie) {
        draftPool.add(new Die(aDie));
    }
    @Override
    public void removeFromDraft(Die aDie) {
        draftPool.remove(aDie);
    }
    @Override
    public void rollDraftPool(){
        draftPool = draftPool
                .stream()
                .map(Die::rollDie)
                .collect(Collectors.toCollection(ArrayList::new));
    }
    @Override
    public void addToDicebag(Die aDie){
        diceBag.add(aDie);
        Collections.shuffle(diceBag);
    }
    @Override
    public Die drawFromDicebag(){
        return diceBag.remove(new Random().nextInt(diceBag.size())).rollDie();
    }
    @Override
    public void samePlayerAgain(){
        currentRound.repeatCurrentPlayer();
    }

    /**
     * has to pay the ToolCard.
     * @param aToolCard to be
     */
    @Override
    public void payToolCard(ToolCard aToolCard) {
        int actualFavours = favours.get(getCurrentPlayer().getName());
        if  (actualFavours < toolCardCost.get(aToolCard.getTitle()))
            return;

        aToolcardUsedDuringThisTurn = true;
        favours.replace(getCurrentPlayer().getName(), actualFavours - toolCardCost.get(aToolCard.getTitle()));
        toolCardCost.replace(aToolCard.getTitle(), 2);
        logger.log(Level.INFO, "Player {0} paid the ToolCard: {1}", new Object[]{getCurrentPlayer().getName(),aToolCard.getTitle()});
    }

    /**
     * Can play ToolCard method has to ensure you have enough favour to use the ToolCard
     * @param aToolCard that has to be checked
     * @throws RuleViolatedException reason for which the test failed
     */
    @Override
    public void canPayToolCard(ToolCard aToolCard) throws RuleViolatedException {
        if(aToolcardUsedDuringThisTurn)
            throw new RuleViolatedException("Ehi! You can't play more than a ToolCard at turn");
        if (favours.get(getCurrentPlayer().getName()) < toolCardCost.get(aToolCard.getTitle()))
            throw new RuleViolatedException("Ehi! You don't have enough favours to do that, poor man!!");
    }
    //endregion

    private void setStatus(GAME_MANAGER_STATUS status) {
        logger.log(Level.INFO, "Game manager changed state from {0} to {1} ", new Object[]{this.status != null ? this.status.name(): "---", status.name()});
        this.status = status;
    }

    //region operation for gameController
    @Override
    public void setupPhase() {
        logger.log(Level.INFO, "Setup phase started");
        //distribute event for selecting a WindowPatternCard
        for(Player p : currentGame) {
            p.update(   new ModelChangedEvent(new GameModel(this, p)),
                        new PatternCardDistributedEvent(p.getPrivateObjective(), p.getPossiblePatternCard().get(0), p.getPossiblePatternCard().get(1)));

        }
    }

    //region player connection
    @Override
    public void reconnectPlayer(String nickname, IView view){
        Player player = currentGame.getPlayerFromName(nickname);
        if(player == null){
            return;
        }
        currentGame.reconnect(nickname, view);

        player.update(new ModelChangedEvent(this));
        if(status == GAME_MANAGER_STATUS.WAITING_FOR_PATTERNCARD)
            player.update(new PatternCardDistributedEvent(player.getPrivateObjective(), player.getPossiblePatternCard().get(0), player.getPossiblePatternCard().get(1)));
        else if(status == GAME_MANAGER_STATUS.ONGOING && getCurrentPlayer().getName().equals(player.getName()))
            player.update(new MyTurnStartedEvent(currentPlayerEndTime));
        getPlayerList().stream().filter(p -> p.isConnected() && !p.getName().equals(nickname)).forEach(p -> p.update(new ModelChangedEvent(new GameModel(this, p)), new PlayerReconnectedEvent(nickname)));

    }


    @Override
    public void disconnectPlayer(String playerToDisconnect) {
        Player player = currentGame.getPlayerFromName(playerToDisconnect);
        if(player != null) player.disconnectView();

        logger.log(Level.INFO, "Player {0} disconnected", playerToDisconnect);
        getPlayerList().forEach(p -> p.update(new ModelChangedEvent(new GameModel(this, p)), new PlayerDisconnectedEvent(playerToDisconnect)));

        }
    //endregion

    @Override
    public void bindPatternAndPlayer(String nickname, WindowPatternCard windowCard, Boolean side) throws GameInvalidException {
        logger.log(Level.INFO, "Player {0} chosen a pattern card", nickname);
        Player player = currentGame.getPlayerFromName(nickname);

        player.setPatternCard(windowCard);
        player.setPatternFlipped(side);
        favours.put(player.getName(), player.getPattern().getDifficulty());

        // check if all players have chosen their card
        for (Player other : currentGame) {
            if (other.getPatternCard() == null) return;
        }
        // if all have chosen their card start the match
        setStatus(GAME_MANAGER_STATUS.ONGOING);

        drawDice();
        currentGame.forEach(p -> p.update(new FinishedSetupEvent(), new ModelChangedEvent(new GameModel(this, p))));
        currentPlayerEndTime = new Timestamp(System.currentTimeMillis() + Settings.instance().getTurnTimeout());
        getCurrentPlayer().update( new MyTurnStartedEvent(currentPlayerEndTime));
    }

    @Override
    public void requestUpdate() {
        currentGame.forEach(p -> p.update(new ModelChangedEvent(new GameModel(this, p))));
    }

    private void broadcastEvents(Event ... events){
        for (Player subscriber : currentGame.getPlayers()) {
            subscriber.update( events);
        }

    }

    @Override
    public void placeDie(Die aDie, int rowIndex, int colIndex) throws PatternConstraintViolatedException, PositionOccupiedException, RuleViolatedException {
        logger.log(Level.INFO, "Player {0} would like to place die {1} on ({2}, {3})", new Object[]{getCurrentPlayer().getName(),aDie,rowIndex, colIndex });
        if(aDie.getValue() == 0 )
            throw new RuleViolatedException("This die can't be placed!");
        if(!draftPool.contains(aDie))
            throw new RuleViolatedException("This die is not in the draftpool");

        getCurrentPlayer().placeDie(aDie,rowIndex,colIndex, true);
        removeFromDraft(aDie);

        getCurrentPlayer().update(new ModelChangedEvent(new GameModel(this, getCurrentPlayer())), new MyTurnStartedEvent());

    }


    @Override
    public void playToolCard(ToolCard aToolCard) throws ToolCardApplicationException {
        logger.log(Level.INFO, "Player {0} would like to use the ToolCard: {1}", new Object[]{getCurrentPlayer().getName(),aToolCard.getTitle()});
        if(!toolCardCost.containsKey(aToolCard.getTitle())){
            logger.log(Level.INFO, "Player {0} would like to use the ToolCard: {1}. REFUSED", new Object[]{getCurrentPlayer().getName(),aToolCard.getTitle()});
            throw new ToolCardApplicationException("ToolCard not permitted");
        }

        aToolCard.play(getCurrentPlayer(), this);
    }


    private void endGame(){
        setStatus(GAME_MANAGER_STATUS.ENDED);
        currentGame.forEach(p -> p.update( new ModelChangedEvent(new GameModel(this, p))));
        broadcastEvents(buildGameFinishedEvent());
    }

    @Override
    public void endTurn(boolean timeoutOccurred) throws GameInvalidException {
        if (status == GAME_MANAGER_STATUS.ENDED) return;

        logger.log(Level.INFO, "Player {0} ended the turn",getCurrentPlayer().getName());
        Player currentPlayer = getCurrentPlayer();
        if(timeoutOccurred)
            currentPlayer.update(new MyTurnEndedEvent());
        currentPlayer.endTurn();

        // Making all disconnected players jump their turn
        do {
            currentRound.next();
        }while( currentRound.hasNext() && !currentRound.getCurrent().isConnected());

        // if a single player is online, end the game due to insufficiency of players
        if( (currentRound.hasNext() && roundTracker.getCurrentRound() == Settings.instance().getNrOfRound()) ||
            (getPlayerList().stream().filter(Player :: isConnected).count() == 1)) {
            endGame();
            return;
        }else if(!currentRound.hasNext()){
            logger.log(Level.INFO, "Round {0} finished.", roundTracker.getCurrentRound());
            currentRound = currentRound.nextRound();
            roundTracker.addDiceLeft(draftPool);
            roundTracker.nextRound();
            draftPool.clear();
            logger.log(Level.INFO, "Round {0} just started.", roundTracker.getCurrentRound());

            drawDice();
        }

        aToolcardUsedDuringThisTurn = false;
        Player nextPlayer = getCurrentPlayer();

        currentGame.getPlayers().forEach(p -> p.update(new ModelChangedEvent(new GameModel(this, p))));
        currentPlayerEndTime = new Timestamp(System.currentTimeMillis() + Settings.instance().getTurnTimeout());
        nextPlayer.update(new MyTurnStartedEvent(currentPlayerEndTime));
    }

    private GameFinishedEvent buildGameFinishedEvent() {

        LinkedList<Pair<Player, Integer>> rank = new LinkedList<>();
        LinkedHashMap<String, String> pointDescription = new LinkedHashMap<>();


        for (Player p : getPlayerList()){
            StringBuilder sb = new StringBuilder( "List of points:\n");
            int sum = 0;
            int tmpCount = p.countPrivateObjectivesPoints();
            sb.append(String.format("%s gave %d points%n", p.getPrivateObjective().getTitle(), tmpCount));
            sum += tmpCount;

            for (ObjectiveCard pubObj : getPublicObjective()){
                tmpCount = pubObj.countPoints(p);
                sb.append(String.format("%s gave %d points%n", pubObj.getTitle(), tmpCount));
                sum += tmpCount;
            }

            tmpCount = favours.get(p.getName());
            sb.append(String.format("Favours gave %d points%n", tmpCount));
            sum += tmpCount;
            tmpCount = p.getEmptyCells();
            sb.append(String.format("Empty cell subtract %d points%n", tmpCount));
            sum -= tmpCount;

            pointDescription.put(p.getName(), sb.toString());
            rank.add(new Pair<>(p, sum));
        }

        rank = rank.stream().sorted(Comparator
                .comparingInt((ToIntFunction<Pair<Player, Integer>>) Pair::getValue)
                .thenComparingInt( p-> p.getKey().countPrivateObjectivesPoints() )
                .thenComparingInt( p-> favours.get(p.getKey().getName())).reversed()
                .thenComparingInt( p -> currentGame.getPlayers().indexOf(p.getKey()))
        ).collect(LinkedList::new, LinkedList::add,LinkedList::addAll );

        return new GameFinishedEvent(rank, new HashMap<>(pointDescription));

    }

    private void drawDice() throws GameInvalidException {
        if(!draftPool.isEmpty())
            throw new GameInvalidException("Panic");

        draftPool = (diceBag.subList(0, (2 * currentGame.getNumberOfPlayers()) + 1)).stream().map(Die::rollDie).collect(Collectors.toCollection(ArrayList::new));
        diceBag = new ArrayList<> (diceBag.subList((2*currentGame.getNumberOfPlayers()) +1, diceBag.size()));

    }

    //endregion





}
