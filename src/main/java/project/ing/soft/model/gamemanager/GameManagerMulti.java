package project.ing.soft.model.gamemanager;


import project.ing.soft.exceptions.ToolCardApplicationException;
import project.ing.soft.model.*;
import project.ing.soft.model.cards.objectives.ObjectiveCard;
import project.ing.soft.model.cards.objectives.privates.PrivateObjective;
import project.ing.soft.model.cards.objectives.publics.PublicObjective;
import project.ing.soft.model.cards.toolcards.ToolCard;
import project.ing.soft.model.gamemanager.events.*;
import project.ing.soft.exceptions.GameInvalidException;
import project.ing.soft.exceptions.RuleViolatedException;
import javafx.util.Pair;
import project.ing.soft.model.cards.Card;
import project.ing.soft.model.cards.WindowPatternCard;

import project.ing.soft.model.gamemanager.events.Event;
import project.ing.soft.view.IView;


import java.io.Serializable;
import java.util.*;
import java.util.List;
import java.util.function.ToIntFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GameManagerMulti implements IGameManager, Serializable {

    //TODO: keep 3 round during debugging procedure, switch to 10 only in final version
    private static final int ROUNDS_NUMBER = 10;

    private Game                currentGame ;
    private GAME_MANAGER_STATUS status;

    private RoundTracker                rounds;
    private ArrayList<Player>           currentTurnList;
    private List<Pair<Player, Integer>> rank;

    private ArrayList<Die>              diceBag;
    private ArrayList<Die>              draftPool;

    private ArrayList<PublicObjective> publicObjectives;

    private ArrayList<ToolCard>         toolCards;
    private Map<String, Integer>        toolCardCost;

    private Map<String, Integer>        favours;
    private Map<String, String>         pointDescription;
    private transient Logger logger;
    //Constructor
    //@Signals Exception aGame.isValid() || aGame.numOfPlayers() <= 1 or aGame.numOfPlayers()> 4
    public GameManagerMulti(Game aGame,
                            List<PublicObjective> availablePublicObjectives,
                            List<PrivateObjective> availablePrivateObjectives,
                            List<ToolCard> availableToolCards,
                            List<WindowPatternCard> availableWindowPatternCards,
                            List<Die> dice
    ) throws GameInvalidException {
        logger = Logger.getLogger(this.getClass().getCanonicalName()+aGame.getPlayers().stream().map(Player::getName).collect(Collectors.toList()).toString());
        logger.setLevel(Level.OFF);

        if (!aGame.isValid() || aGame.getNumberOfPlayers() <= 1  || aGame.getNumberOfPlayers() > 4  ) {
            setStatus(GAME_MANAGER_STATUS.ENDED);
            throw new GameInvalidException("Game is not valid!");
        }

        setStatus(GAME_MANAGER_STATUS.WAITING_FOR_PATTERNCARD);

        currentGame = new Game(aGame);
        // initialize empty draft pool
        draftPool = new ArrayList<>();


        // initialize Round Tracker obj
        rounds = new RoundTracker();
        // create dies and populate Die Bag
        diceBag = new ArrayList<>(dice);
        //initialize hashMap favours
        favours = new HashMap<>();
        //initialize hashMap rank
        rank = new ArrayList<>();
        // initialize hash map points description
        pointDescription = new HashMap<>();
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
        logger.log(Level.INFO, "distributed cards");
    }

    private void setStatus(GAME_MANAGER_STATUS status) {
        logger.log(Level.INFO, "Game manager changed state from {0} to {1} ", new Object[]{this.status != null ? this.status.name(): "---", status.name()});
        this.status = status;
    }

    //Copy constructor
    private GameManagerMulti(GameManagerMulti from){
        from.logger.log(Level.INFO, "A game manager was cloned from this");
        this.logger             = Logger.getAnonymousLogger();
        this.logger.setLevel(Level.OFF);
        this.currentGame        = new Game(from.currentGame);
        this.diceBag            = new ArrayList<> (from.diceBag);
        this.draftPool          = new ArrayList<> (from.draftPool);
        this.rounds             = new RoundTracker(from.rounds);
        this.publicObjectives   = new ArrayList<> (from.publicObjectives);
        this.toolCards          = new ArrayList<> (from.toolCards);
        this.currentTurnList = new ArrayList<>();
        for(Player p : from.currentTurnList){
            this.currentTurnList.add(new Player(p));
        }
        this.rank               = new ArrayList<> (from.rank);
        this.toolCardCost       = new HashMap<>   (from.toolCardCost);
        this.favours            = new HashMap<>   (from.favours);
        this.pointDescription   = from.pointDescription;
        this.setStatus(from.status);

    }
    //
    public IGameManager copy(){
        return new GameManagerMulti(this);
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
        return currentGame
                .getPlayers()
                .stream()
                .sorted(Comparator.comparing(Player::getName))
                .collect(Collectors.toCollection(ArrayList :: new));
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
        logger.log(Level.INFO, "Setup phase started");
        //distribute event for selecting a WindowPatternCard
        for(Player p : getPlayerList()) {
            deliverEvent(p, new ModelChangedEvent(new GameManagerMulti(this)),
                    new PatternCardDistributedEvent(p.getPrivateObjective(), p.getPossiblePatternCard().get(0), p.getPossiblePatternCard().get(1)));

        }
    }

    @Override
    public void bindPatternAndPlayer(String nickname, WindowPatternCard windowCard, Boolean side) throws GameInvalidException {
        logger.log(Level.INFO, "Player {0} chosen a pattern card", nickname);
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
        setStatus(GAME_MANAGER_STATUS.ONGOING);

        drawDice();
        broadcastEvents(new FinishedSetupEvent(), new ModelChangedEvent(new GameManagerMulti(this)));
        deliverEvent(getCurrentPlayer(), new MyTurnStartedEvent());
    }

    @Override
    public List<Pair<Player, Integer>> countPlayersPoints() {


        for (Player p : getPlayerList()){
            int tmpCount;
            StringBuilder sb = new StringBuilder(p.getName());
            sb.append(" gained points due to\n");
            int sum = 0;
            sum += p.countPrivateObjectivesPoints();
            sb.append(p.getPrivateObjective().getTitle());
            sb.append(": ");
            sb.append(sum);
            sb.append("\n");

            for (ObjectiveCard pubObj : getPublicObjective()){
                tmpCount = pubObj.countPoints(p);
                sb.append(pubObj.getTitle());
                sb.append(": ");
                sb.append(tmpCount);
                sb.append("\n");
                sum += tmpCount;
            }

            tmpCount = favours.get(p.getName());
            sb.append("Favours: ");
            sb.append(tmpCount);
            sb.append("\n");
            sum += tmpCount;
            tmpCount = p.getEmptyCells();
            sb.append("Empty cells: ");
            sb.append(tmpCount);
            sb.append("\n");
            sum -= tmpCount;

            pointDescription.put(p.getName(), new String(sb));
            rank.add( new Pair<>(p, sum));
        }

        rank.sort(Comparator
                .comparingInt((ToIntFunction<Pair<Player, Integer>>) Pair::getValue)
                .thenComparingInt((Pair<Player, Integer> p)-> p.getKey().countPrivateObjectivesPoints() )
                .thenComparingInt((Pair<Player, Integer> p)-> favours.get(p.getKey().getName())).reversed()
                .thenComparingInt( (Pair<Player, Integer> p) -> currentGame.getPlayers().indexOf(p.getKey()))
        );

        return rank;
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
        if (!p.isConnected())
            return;
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
        logger.log(Level.INFO, "Player {0} would like to place die {1} on ({2}, {3})", new Object[]{getCurrentPlayer().getName(),aDie,rowIndex, colIndex });
        if(aDie.getValue() == 0)
            throw new RuleViolatedException("This die can't be placed!");

        getCurrentPlayer().placeDie(aDie,rowIndex,colIndex, true);
        removeFromDraft(aDie);

        deliverEvent(getCurrentPlayer(), new ModelChangedEvent(new GameManagerMulti(this)), new MyTurnStartedEvent());

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
        if (favours.get(getCurrentPlayer().getName()) < toolCardCost.get(aToolCard.getTitle()))
            throw new RuleViolatedException("Ehi! You don't have enough favours to do that, poor man!!");
    }

    @Override
    public void playToolCard(ToolCard aToolCard) throws Exception{
        logger.log(Level.INFO, "Player {0} would like to use the ToolCard: {1}", new Object[]{getCurrentPlayer().getName(),aToolCard.getTitle()});
        if(!toolCardCost.containsKey(aToolCard.getTitle())){
            logger.log(Level.INFO, "Player {0} would like to use the ToolCard: {1}. REFUSED", new Object[]{getCurrentPlayer().getName(),aToolCard.getTitle()});
            throw new ToolCardApplicationException("ToolCard not permitted");
        }

        aToolCard.play(getCurrentPlayer(), this);
    }


    private void endGame(){
        setStatus(GAME_MANAGER_STATUS.ENDED);
        countPlayersPoints();
        broadcastEvents(new GameFinishedEvent(new ArrayList<>(rank), new HashMap<>(pointDescription)));
    }

    @Override
    public void endTurn(boolean timeoutOccurred) throws GameInvalidException {
        if (status == GAME_MANAGER_STATUS.ENDED) return;

        logger.log(Level.INFO, "Player {0} ended the turn",getCurrentPlayer().getName());
        Player current = getCurrentPlayer();
        if(timeoutOccurred)
            deliverEvent(current, new MyTurnEndedEvent());
        current.endTurn();
        currentTurnList.remove(0);
        // if a single player is online, end the game due to insufficiency of players
        if(getPlayerList().stream().filter(Player :: isConnected).count() == 1){
            endGame();
            return;
        }
        // Making all disconnected players jump its turn
        while(!currentTurnList.isEmpty() && !currentTurnList.get(0).isConnected()){
            currentTurnList.remove(0);
        }

        if(currentTurnList.isEmpty() && rounds.getCurrentRound() == ROUNDS_NUMBER) {
            endGame();
            return;
        }else if(currentTurnList.isEmpty()){
            logger.log(Level.INFO, "Round {0} finished.", rounds.getCurrentRound());

            currentGame.leftShiftPlayers();
            currentTurnList = createTurns(currentGame.getPlayers());
            rounds.addDiceLeft(draftPool);
            rounds.nextRound();
            draftPool.clear();
            logger.log(Level.INFO, "Round {0} just started.", rounds.getCurrentRound());

            drawDice();
        }


        Player next = getCurrentPlayer();

        broadcastEvents(new ModelChangedEvent(new GameManagerMulti(this)));
        deliverEvent(next, new MyTurnStartedEvent());
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
        Die ret;

        ret = diceBag.remove(new Random().nextInt(diceBag.size())).rollDie();
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

    public void reconnectPlayer(String nickname, IView view){
        Player player = currentGame.getPlayers()
                .stream()
                .filter(p-> p.getName().equals(nickname)).findFirst().orElse(null);

        if(player == null){
            return;
        }

        player.disconnectView();
        player.reconnectView(view);
        player.update(new ModelChangedEvent(this));
        if(status == GAME_MANAGER_STATUS.WAITING_FOR_PATTERNCARD)
            player.update(new PatternCardDistributedEvent(player.getPrivateObjective(), player.getPossiblePatternCard().get(0), player.getPossiblePatternCard().get(1)));
        else if(status == GAME_MANAGER_STATUS.ONGOING && getCurrentPlayer().equals(player))
            player.update(new MyTurnStartedEvent());

    }

    @Override
    public void disconnectPlayer(String playerToDisconnect) {
        for (Player p : getPlayerList()) {
            if (p.getName().equals(playerToDisconnect) ) {
                p.disconnectView();
                logger.log(Level.INFO, "Player {0} disconnected", playerToDisconnect);
            }
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

    private void drawDice() throws GameInvalidException {
        if(!draftPool.isEmpty())
            throw new GameInvalidException("Panic");

        draftPool = (diceBag.subList(0, (2 * currentGame.getNumberOfPlayers()) + 1)).stream().map(Die::rollDie).collect(Collectors.toCollection(ArrayList::new));

        diceBag = new ArrayList<> (diceBag.subList((2*currentGame.getNumberOfPlayers()) +1, diceBag.size()));

    }



}
