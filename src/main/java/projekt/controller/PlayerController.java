package projekt.controller;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import org.tudalgo.algoutils.student.annotation.DoNotTouch;
import org.tudalgo.algoutils.student.annotation.StudentImplementationRequired;
import projekt.Config;
import projekt.controller.actions.IllegalActionException;
import projekt.controller.actions.PlayerAction;
import projekt.model.DevelopmentCardType;
import projekt.model.Intersection;
import projekt.model.Player;
import projekt.model.PlayerState;
import projekt.model.ResourceType;
import projekt.model.TilePosition;
import projekt.model.TradePayload;
import projekt.model.buildings.Edge;
import projekt.model.buildings.Port;
import projekt.model.buildings.Settlement;
import projekt.model.tiles.Tile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The PlayerController class represents a controller for a {@link Player} in
 * the game.
 * It manages the player's state, objectives, actions and all methods the UI
 * needs to interact with.
 * It receives objectives the player wants to achieve and waits for the UI or AI
 * to trigger any allowed actions. It then executes the actions and updates the
 * player's state.
 */
public class PlayerController {
    private final Player player;

    private final GameController gameController;

    private final BlockingDeque<PlayerAction> actions = new LinkedBlockingDeque<>();

    private final Property<PlayerState> playerStateProperty = new SimpleObjectProperty<>();

    private final Property<PlayerObjective> playerObjectiveProperty = new SimpleObjectProperty<>(PlayerObjective.IDLE);

    private Player tradingPlayer;

    private Map<ResourceType, Integer> playerTradingOffer;

    private Map<ResourceType, Integer> playerTradingRequest;

    private Map<ResourceType, Integer> selectedResources = new HashMap<>();

    private Map<ResourceType, Integer> oldResources = new HashMap<>();

    private int cardsToSelect = 0;

    /**
     * Creates a new {@link PlayerController} with the given {@link GameController}
     * and {@link Player}.
     *
     * @param gameController the {@link GameController} that manages the game logic
     *                       and this controller is part of.
     * @param player         the {@link Player} this controller belongs to. It is
     *                       assumed that the player is valid.
     */
    @DoNotTouch
    public PlayerController(final GameController gameController, final Player player) {
        this.gameController = gameController;
        this.player = player;
        this.playerObjectiveProperty.addListener((observable, oldValue, newValue) -> {
            updatePlayerState();
        });
    }

    /**
     * Returns the {@link Player}.
     *
     * @return the {@link Player}.
     */
    @DoNotTouch
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns a {@link Property} with the current {@link PlayerState}.
     *
     * @return a {@link Property} with the current {@link PlayerState}.
     */
    @DoNotTouch
    public Property<PlayerState> getPlayerStateProperty() {
        return playerStateProperty;
    }

    /**
     * Returns the current {@link PlayerState}.
     *
     * @return the current {@link PlayerState}.
     */
    public PlayerState getPlayerState() {
        return playerStateProperty.getValue();
    }

    /**
     * Returns a {@link Property} with the current {@link PlayerObjective}
     *
     * @return a {@link Property} with the current {@link PlayerObjective}
     */
    @DoNotTouch
    public Property<PlayerObjective> getPlayerObjectiveProperty() {
        return playerObjectiveProperty;
    }

    /**
     * Sets the value of the {@link #playerObjectiveProperty} to the given
     * objective.
     *
     * @param nextObjective the objective to set
     */
    @DoNotTouch
    public void setPlayerObjective(final PlayerObjective nextObjective) {
        playerObjectiveProperty.setValue(nextObjective);
    }

    /**
     * Returns true if it is the first round of the game.
     * The first round is defined as the round where the round counter is 0.
     *
     * @return true if it is the first round of the game
     */
    private boolean isFirstRound() {
        return gameController.getRoundCounterProperty().get() == 0;
    }

    /**
     * Updates the {@link #playerStateProperty} with the current
     * {@link PlayerState}.
     */
    @DoNotTouch
    private void updatePlayerState() {
        playerStateProperty
            .setValue(new PlayerState(getBuildableVillageIntersections(), getUpgradeableVillageIntersections(),
                                      getBuildableRoadEdges(), getPlayersToStealFrom(), getPlayerTradingPayload(),
                                      getCardsToSelect(), getChangedResources()
            ));
    }

    /**
     * Returns which resources and how many have changed since the last action.
     *
     * @return a map of the changed resources
     */
    private Map<ResourceType, Integer> getChangedResources() {
        final Map<ResourceType, Integer> changedResources = new HashMap<>();
        for (final ResourceType resourceType : ResourceType.values()) {
            final int oldAmount = oldResources.getOrDefault(resourceType, 0);
            final int newAmount = player.getResources().getOrDefault(resourceType, 0);
            if (oldAmount != newAmount) {
                changedResources.put(resourceType, newAmount - oldAmount);
            }
        }
        return changedResources;
    }

    /**
     * Returns the amount of cards the player has to select
     *
     * @return the amount of cards to select
     */
    private int getCardsToSelect() {
        return cardsToSelect;
    }

    /**
     * Sets the amount of cards the player has to select
     *
     * @param cardsToSelect the amount of cards to select
     */
    public void setCardsToSelect(final int cardsToSelect) {
        this.cardsToSelect = cardsToSelect;
    }

    /**
     * Returns a list of all other players.
     *
     * @return a list of all other players.
     */
    public List<Player> getOtherPlayers() {
        return gameController.getState()
            .getPlayers()
            .stream()
            .filter(p -> p != player)
            .toList();
    }

    /**
     * Rolls the dice.
     */
    public void rollDice() {
        gameController.castDice();
    }

    /**
     * Processes the selected resources for the current objective.
     * If the current objective is {@link PlayerObjective#DROP_CARDS}, the
     * selected resources are removed. Otherwise, the selected resources are stored.
     *
     * @param selectedResources the selected resources
     * @throws IllegalActionException if the selected resources are invalid
     */
    public void processSelectedResources(final Map<ResourceType, Integer> selectedResources)
    throws IllegalActionException {
        if (selectedResources.values().stream().mapToInt(Integer::intValue).sum() != getCardsToSelect()) {
            throw new IllegalActionException("Wrong amount of cards selected");
        }
        if (PlayerObjective.DROP_CARDS.equals(playerObjectiveProperty.getValue())) {
            dropSelectedResources(selectedResources);
        }
        this.selectedResources = selectedResources;
    }

    // Process Actions

    /**
     * Gets called from viewer thread to trigger an Action. This action will then be
     * waited for using the method {@link #waitForNextAction()}.
     *
     * @param action The Action that should be triggered next
     */
    @DoNotTouch
    public void triggerAction(final PlayerAction action) {
        actions.add(action);
    }

    /**
     * Takes the next action from the queue. This method blocks until an action is
     * in the queue.
     *
     * @return The next action
     * @throws InterruptedException if the thread is interrupted while waiting for
     *                              the next action
     */
    @DoNotTouch
    public PlayerAction blockingGetNextAction() throws InterruptedException {
        return actions.take();
    }

    /**
     * Waits for the next action and executes it.
     *
     * @param nextObjective the objective to set before the action is awaited
     * @return the executed action
     */
    @DoNotTouch
    public PlayerAction waitForNextAction(final PlayerObjective nextObjective) {
        setPlayerObjective(nextObjective);
        return waitForNextAction();
    }

    /**
     * Waits for a action to be triggered, checks if the action is allowed and then
     * executes it.
     * If a {@link IllegalActionException} is thrown, the action is ignored and the
     * next action is awaited. This is done to ensure only allowed actions are
     * executed.
     *
     * @return the executed action
     */
    @DoNotTouch
    public PlayerAction waitForNextAction() {
        try {
            oldResources = new HashMap<>(player.getResources());
            // blocking, waiting for viewing thread
            final PlayerAction action = blockingGetNextAction();

            System.out.println("TRIGGER " + action + " [" + player.getName() + "]");

            if (!playerObjectiveProperty.getValue().allowedActions.contains(action.getClass())) {
                throw new IllegalActionException(String.format("Illegal Action %s performed. Allowed Actions: %s",
                                                               action, playerObjectiveProperty.getValue().getAllowedActions()
                ));
            }
            action.execute(this);
            updatePlayerState();
            return action;
        } catch (final IllegalActionException e) {
            // Ignore and keep going
            e.printStackTrace();
            return waitForNextAction();
        } catch (final InterruptedException e) {
            throw new RuntimeException("Main thread was interrupted!", e);
        }
    }

    // -- Building methods --

    /**
     * Returns all intersections where a village can be built.
     * During a regular turn, a village can only be built next to an existing road.
     * In the first round the village can be built anywhere.
     *
     * <b>A village can never be built on an intersection that is adjacent to
     * another settlement or already has a settlement.</b>
     *
     * @return all intersections where a village can be built.
     */
    private Set<Intersection> getBuildableVillageIntersections() {
        if (!canBuildVillage()) {
            return Set.of();
        }
        Stream<Intersection> intersections = gameController.getState().getGrid().getIntersections().values().stream()
            .filter(intersection -> intersection.getSettlement() == null).filter(intersection -> intersection
                .getAdjacentIntersections().stream().noneMatch(Intersection::hasSettlement));
        if (!isFirstRound()) {
            intersections = intersections.filter(intersection -> intersection.getConnectedEdges().stream()
                .anyMatch(edge -> edge.hasRoad() && edge.getRoadOwner().equals(player)));
        }
        return intersections.collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Checks whether the {@link Player} can build a village.
     * To build a village, the {@link Player} must have enough resources or the
     * current objective must be {@link PlayerObjective#PLACE_VILLAGE}. The
     * {@link Player} must also have enough villages left.
     *
     * @return whether the {@link Player} can build a village.
     */
    @StudentImplementationRequired("H2.5")
    public boolean canBuildVillage() {
        // TODO: H2.5
        return org.tudalgo.algoutils.student.Student.crash("H2.5 - Remove if implemented");
    }

    /**
     * Tries to build a village at the given intersection.
     * Validates whether the {@link Player} has enough resources and whether the
     * village can be built at the given intersection.
     * Also removes the resources from the {@link Player} if the village was built
     * and the current objective is not {@link PlayerObjective#PLACE_VILLAGE}.
     *
     * @param intersection the intersection to build the village at
     * @throws IllegalActionException if the village cannot be built
     */
    @StudentImplementationRequired("H2.5")
    public void buildVillage(final Intersection intersection) throws IllegalActionException {
        // TODO: H2.5
        org.tudalgo.algoutils.student.Student.crash("H2.5 - Remove if implemented");
    }

    /**
     * Returns all intersections where a village can be upgraded to a city.
     *
     * @return all intersections where a village can be upgraded to a city.
     */
    private Set<Intersection> getUpgradeableVillageIntersections() {
        if (!canUpgradeVillage()) {
            return Set.of();
        }
        return player.getSettlements().stream().filter(settlement -> settlement.type() == Settlement.Type.VILLAGE)
            .map(Settlement::intersection).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Checks whether the {@link Player} can upgrade a village to a city.
     * To upgrade a village to a city, the {@link Player} must have enough
     * resources, at least one village and enough cities left.
     *
     * @return whether the {@link Player} can upgrade a village to a city.
     */
    public boolean canUpgradeVillage() {
        final var requiredResources = Config.SETTLEMENT_BUILDING_COST.get(Settlement.Type.CITY);
        return player.hasResources(requiredResources) && player.getSettlements().stream()
            .anyMatch(settlement -> settlement.type() == Settlement.Type.VILLAGE)
            && player.getRemainingCities() > 0;
    }

    /**
     * Tries to upgrade a village at the given intersection to a city.
     * Validates whether the {@link Player} has enough resources and whether the
     * village can be upgraded.
     * Also removes the resources from the {@link Player} if the village was
     * upgraded.
     *
     * @param intersection the intersection to upgrade the village at
     * @throws IllegalActionException if the village cannot be upgraded
     */
    @StudentImplementationRequired("H2.6")
    public void upgradeVillage(final Intersection intersection) throws IllegalActionException {
        // TODO: H2.6
        org.tudalgo.algoutils.student.Student.crash("H2.6 - Remove if implemented");
    }

    /**
     * Returns all edges where a road can be built.
     * During a regular turn, a road can only be built next to an existing road.
     * In the first round the road can only be built next to a village with no
     * adjacent roads.
     *
     * <b>A road can never be built on an edge that already has a road.</b>
     *
     * @return all edges where a road can be built.
     */
    private Set<Edge> getBuildableRoadEdges() {
        if (!canBuildRoad()) {
            return Set.of();
        }
        Stream<Edge> edges = gameController.getState().getGrid().getEdges().values().stream()
            .filter(edge -> !edge.hasRoad());
        if (isFirstRound()) {
            edges = edges.filter(edge -> edge.getIntersections().stream()
                .anyMatch(intersection -> intersection.playerHasSettlement(player)
                    && intersection.getConnectedEdges().stream().noneMatch(Edge::hasRoad)));
        } else {
            edges = edges.filter(edge -> edge.getConnectedRoads(player).size() < 4)
                .filter(edge -> !edge.getConnectedRoads(player).isEmpty());
        }
        return edges.collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Checks whether the {@link Player} can build a road.
     * To build a road, the {@link Player} must have enough resources or the current
     * objective must be {@link PlayerObjective#PLACE_ROAD}. The {@link Player} must
     * also have enough roads left.
     *
     * @return whether the {@link Player} can build a road.
     */
    @StudentImplementationRequired("H2.5")
    public boolean canBuildRoad() {
        // TODO: H2.5
        return org.tudalgo.algoutils.student.Student.crash("H2.5 - Remove if implemented");
    }

    /**
     * Tries to build a road at the given edge.
     *
     * @param tile          the tile to build the road at
     * @param edgeDirection the direction of the edge to build the road at
     * @throws IllegalActionException if the road cannot be built
     * @see #buildRoad(TilePosition, TilePosition)
     */
    public void buildRoad(final Tile tile, final TilePosition.EdgeDirection edgeDirection)
    throws IllegalActionException {
        buildRoad(tile.getPosition(), TilePosition.neighbour(tile.getPosition(), edgeDirection));
    }

    /**
     * Tries to build a road between the given positions.
     * Validates whether the {@link Player} has enough resources and whether the
     * road can be built between the given positions.
     * Also removes the resources from the {@link Player} if the road was built and
     * the current objective is not {@link PlayerObjective#PLACE_ROAD}.
     *
     * @param position0 the first position to build the road between
     * @param position1 the second position to build the road between
     * @throws IllegalActionException if the road cannot be built
     */
    @StudentImplementationRequired("H2.5")
    public void buildRoad(final TilePosition position0, final TilePosition position1) throws IllegalActionException {
        // TODO: H2.5
        org.tudalgo.algoutils.student.Student.crash("H2.5 - Remove if implemented");
    }

    // Development card methods

    /**
     * Checks whether the {@link Player} can buy a development card.
     * To buy a development card, the {@link Player} must have enough resources.
     *
     * @return whether the {@link Player} can buy a development card.
     */
    public boolean canBuyDevelopmentCard() {
        return player.hasResources(Config.DEVELOPMENT_CARD_COST);
    }

    /**
     * Tries to buy a development card.
     * Validates whether the {@link Player} has enough resources.
     * Also removes the resources from the {@link Player} if the development card
     * was bought.
     *
     * @throws IllegalActionException if the development card cannot be bought
     */
    public void buyDevelopmentCard() throws IllegalActionException {
        if (!canBuyDevelopmentCard()) {
            throw new IllegalActionException("Cannot buy development card");
        }

        final var requiredResources = Config.DEVELOPMENT_CARD_COST;
        player.addDevelopmentCard(gameController.drawDevelopmentCard());
        player.removeResources(requiredResources);
    }

    /**
     * Plays the given development card.
     * The development card is removed from the {@link Player} after it is played.
     * After a development card is played, the {@link Player} continues his regular
     * turn.
     *
     * @param developmentCard the development card to play
     * @throws IllegalActionException if the player does not have the selected
     *                                development card
     * @see DevelopmentCardType
     */
    public void playDevelopmentCard(final DevelopmentCardType developmentCard) throws IllegalActionException {
        if (!getPlayer().removeDevelopmentCard(developmentCard)) {
            throw new IllegalActionException("Player does not have the selected development card");
        }
        switch (developmentCard) {
            case KNIGHT -> {
                waitForNextAction(PlayerObjective.SELECT_ROBBER_TILE);
                waitForNextAction(PlayerObjective.SELECT_CARD_TO_STEAL);
            }
            case ROAD_BUILDING -> {
                waitForNextAction(PlayerObjective.PLACE_ROAD);
                waitForNextAction(PlayerObjective.PLACE_ROAD);
            }
            case INVENTION -> {
                cardsToSelect = 2;
                waitForNextAction(PlayerObjective.SELECT_CARDS);
                player.addResources(selectedResources);
            }
            case MONOPOLY -> {
                cardsToSelect = 1;
                waitForNextAction(PlayerObjective.SELECT_CARDS);
                final ResourceType resourceType = selectedResources.keySet().iterator().next();
                for (final Player player : getOtherPlayers()) {
                    final int amount = player.getResources().getOrDefault(resourceType, 0);
                    player.removeResource(resourceType, amount);
                    getPlayer().addResource(resourceType, amount);
                }
            }
            default -> {
                System.out.printf("No action for development card type %s registered%n", developmentCard);
                return;
            }
        }
        waitForNextAction(PlayerObjective.REGULAR_TURN);
    }

    // -- Trading methods --

    /**
     * Trades the given resources with the bank.
     * The {@link Player} must have enough resources to trade.
     * The trade ratio is determined by {@link Player#getTradeRatio(ResourceType)}.
     *
     * @param offerType   the type of resource to offer
     * @param offerAmount the amount of resources to offer
     * @param request     the type of resource to request
     * @throws IllegalActionException if the trade cannot be made
     * @see Port
     */
    @StudentImplementationRequired("H2.3")
    public void tradeWithBank(final ResourceType offerType, final int offerAmount, final ResourceType request)
    throws IllegalActionException {
        // TODO: H2.3
        org.tudalgo.algoutils.student.Student.crash("H2.3 - Remove if implemented");
    }

    /**
     * Offers the trade to all other players that can accept the trade.
     *
     * @param offer   the offered resources
     * @param request the requested resources
     */
    public void offerTrade(final Map<ResourceType, Integer> offer, final Map<ResourceType, Integer> request) {
        gameController.offerTrade(player, offer, request);
    }

    /**
     * Checks whether this player can accept a trade offer from the other player.
     * A player cannot accept a trade offer from himself or if he does not have
     * enough resources.
     *
     * @param otherPlayer the player to trade with
     * @param request     the resources to request
     * @return whether this player can accept a trade offer from the other player
     */
    public boolean canAcceptTradeOffer(final Player otherPlayer, final Map<ResourceType, Integer> request) {
        return !player.equals(otherPlayer) && player.hasResources(request);
    }

    /**
     * Sets the trade offer from the other player.
     * This method is called by the {@link GameController} when a trade offer is
     * made by another player.
     *
     * @param player  the player who made the trade offer
     * @param offer   the offered resources
     * @param request the requested resources
     */
    protected void setPlayerTradeOffer(
        final Player player, final Map<ResourceType, Integer> offer,
        final Map<ResourceType, Integer> request
    ) {
        this.tradingPlayer = player;
        this.playerTradingOffer = offer;
        this.playerTradingRequest = request;
    }

    /**
     * Resets the trade offer
     */
    protected void resetPlayerTradeOffer() {
        this.tradingPlayer = null;
        this.playerTradingOffer = null;
        this.playerTradingRequest = null;
    }

    /**
     * Returns a {@link TradePayload} with the current trade offer from the other
     * player.
     *
     * @return a {@link TradePayload} with the current trade offer from the other
     * player.
     */
    private TradePayload getPlayerTradingPayload() {
        if (tradingPlayer == null || playerTradingOffer == null || playerTradingRequest == null) {
            return null;
        }
        return new TradePayload(playerTradingOffer, playerTradingRequest, false, tradingPlayer);
    }

    /**
     * Accepts the trade offer from the other player if one exists.
     * Both {@link Player}s must have the required resources.
     *
     * @param accepted whether the trade offer is accepted
     * @throws IllegalActionException if no trade offer exists or if one
     *                                {@link Player} does not have the required
     *                                resources
     */
    @StudentImplementationRequired("H2.3")
    public void acceptTradeOffer(final boolean accepted) throws IllegalActionException {
        // TODO: H2.3
        org.tudalgo.algoutils.student.Student.crash("H2.3 - Remove if implemented");
    }

    // Robber methods

    /**
     * Drops the selected resources from the {@link Player}s inventory.
     * Validates that the {@link Player} must have the selected resources.
     * The resources are removed from the {@link Player}s inventory.
     *
     * @param resourcesToDrop a map containing the type and quantity of resources to
     *                        drop
     */
    public void dropSelectedResources(final Map<ResourceType, Integer> resourcesToDrop) {
        if (!player.hasResources(resourcesToDrop)) {
            return;
        }
        playerObjectiveProperty.setValue(PlayerObjective.IDLE);
        // remove resources from player
        player.removeResources(resourcesToDrop);
        cardsToSelect = 0;
    }

    /**
     * Selects the player and resource to steal from when a 7 is rolled.
     *
     * @param playerToStealFrom the player to steal from
     * @param resourceToSteal   the resource to steal
     * @throws IllegalActionException if the player does not have the selected
     *                                resource
     */
    public void selectPlayerAndResourceToSteal(final Player playerToStealFrom, final ResourceType resourceToSteal)
    throws IllegalActionException {
        if (!playerToStealFrom.removeResource(resourceToSteal, 1)) {
            throw new IllegalActionException("Player does not have the selected resource");
        }
        playerObjectiveProperty.setValue(PlayerObjective.IDLE);
        // add resource to player
        player.addResource(resourceToSteal, 1);
    }

    /**
     * Sets the robber position.
     *
     * @param position the position to set the robber to
     */
    public void setRobberPosition(final TilePosition position) {
        gameController.getState().getGrid().setRobberPosition(position);
    }

    /**
     * Returns all players that are next to the robber and not the current player.
     *
     * @return all players that are next to the robber and not the current player.
     */
    public List<Player> getPlayersToStealFrom() {
        return gameController.getState().getGrid().getTileAt(gameController.getState().getGrid().getRobberPosition())
            .getIntersections().stream()
            .filter(Intersection::hasSettlement)
            .map(i -> i.getSettlement().owner())
            .filter(Predicate.not(player::equals))
            .filter(otherPlayer -> !otherPlayer.getResources().isEmpty())
            .collect(Collectors.toUnmodifiableList());
    }
}
