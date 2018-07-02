Client - Server interaction
---------------------------

To let client and server communicate each other, we implemented, as requested, a MVC pattern, with some differences from the plain one.

On the client there is player's view, which contains a reference to the remote controller (if the client connected through RMI) or to a controller proxy (if the client connected through socket). If a player wants to perform any action which involves a change on the model, he must ask for it to the controller. Actually, the client has a copy of the model available, but, even if he is able to modify it using a custom client, the change won't affect the game, because it has effect only on the local copy of the player and the shared model (which is only on the server) is unchanged.

The model is divided into more classes, to let every class represent a single object and to separate methods and attributes according to their context. A central class, called GameModel, is used to integrate every class of the model and let them interact each other. This class is also responsible for the game flow proceed: it sends events to every player connected to the match. In particular, every time the model state changes, it sends a ModelChangedEvent, to let players save an up-to-date copy of the model.

The controller is divided into 2 different entities: there is a GameController, which is responsible for checking that the action has been asked from the player who must play its turn and, before delegate the request to the Model, this controller checks if the timeout is already expired. In these cases the controller won't ask the model to perform any action and will return to the client an exception, explaining what happened.

The second part of the controller is called PlayerController. Each player is associated with its own PlayerController. This controller is used to avoid spoofing by other players: every request to the server is done by passing as a parameter also the name of the player itself. If a player A (using a custom client) tries to perform any action pretending to be player B, player A's PlayerController, who knows the name of the player associated, delegates the request to the GameController with the real name of the player. This way, if the player who asked for an action is not the current player, the GameController will return to him an Exception.

Here is a graphical representation of client-server interaction:
![client-server interaction](https://github.com/MattePalte/ing-soft-2018-paltenghi-parravicini-pellizzi/blob/master/UML/Client-Server%20interaction.PNG?raw=true)

Design Pattern used
-------------------
In addition to MVC pattern, already explained, we implemented:
- Command Pattern to implement ToolCards: there is a ToolCard interface, which contains all the methods needed to let players use them. Since every ToolCard applies a different effect, there is a specific implementeation for every single existing ToolCard in the game, but, since some ToolCards have quite similar effects, the operations to be executed above the GameModel are coded into the GameModel itself, and the ToolCards call them in the order they need. Here is a scheme of the pattern: 
<div align=CENTER><img src="https://github.com/MattePalte/ing-soft-2018-paltenghi-parravicini-pellizzi/blob/master/UML/ToolCards.png?raw=true" width=400/></div>

### ToolCards limitations
All the ToolCards implying the player to move one or more dice have been implemented by taking the dice chosen by the player from its board and, one by one, in the same order the player chose them, he is asked to place them again. This implementation allow us to permit players to swap 2 dice in their board. However, it does not allow players to move dice in cascade: let's pretend a player placed only 2 dice. If he plays a toolcard to move both of them, he can't choose a die, place it near the other one and move the second one. The dice are taken at the same time and then placed one by one. If taking the 2 dice chosen leaves the board clear (no dice are left on the board) then the player must place the first die on the edge of the board, just as if it were its first die in the game.
The toolcards affected by this limitation are:

	-Lathekin
    -Tap Wheel

- Strategy Pattern to implement public objectives: there is a PublicObjective interface, which contains the methods needed to count points on players' boards. Since the way to count points changes according to the specific public objective, there is a specific implementation for every one of them. Here is a scheme of the pattern: 
<div align=CENTER><img src="https://github.com/MattePalte/ing-soft-2018-paltenghi-parravicini-pellizzi/blob/master/UML/PublicObjectives.png?raw=true" width=300/></div>

- Proxy Pattern to implement communication through socket: if a player connects to the game through socket, since its view can't have a reference to the remote object, as it happens with RMI, a ControllerProxy is created. The view can't distinguish through a real controller or a proxy, so, it asks for an operation normally. The controller proxy, creates a Request object, which will be sent to the server, and received by a ViewProxy. The ViewProxy can understand the request, and asks to the real Controller to execute the method asked by the player. In a similar way, the communication goes also from server to client: when something is needed to be sent from server to client, such as GameModel's events, it is incapsulated in a Response object by the ViewProxy and sent to the ControllerProxy on the player's client. The ControllerProxy can understand the response, and sends it to the view in a format he can manage, typically an Event. Here is an example scheme of the pattern:
<div align=CENTER><img src="https://github.com/MattePalte/ing-soft-2018-paltenghi-parravicini-pellizzi/blob/master/UML/Proxy.png?raw=true" height=75 /></div>

- Visitor Pattern to let ControllerProxy and ViewProxy manage, respectively, Response and Request objects and to let the View manage Event objects: the behavior of the proxy pattern implemented in the socket communication has already been explained. When a ViewProxy receives a Request, it recognizes and manages the specific Request object through a Visitor pattern: for every kind of request, a specific implementation and a method in the RequestHandler interface exist. The RequestHandler interface is implemented by the ViewProxy. In a similar way is implemented the visitor Pattern in the ControllerProxy: for every kind of Response, a specific implementation and a method in the ResponseHandler interface exist. The ResponseHandler interface is implemented by the ControllerProxy. As said at the beginning, the visitor Pattern is also implemented in the View objects to let them handle the events received from the model: for every Event, a specific implementation and a method in the EventHandler interface exist. As suggested, the EventHandler interface is implemented by the View. Here is an example scheme of the pattern:
<div align=CENTER><img src="https://github.com/MattePalte/ing-soft-2018-paltenghi-parravicini-pellizzi/blob/master/UML/Visitor.png?raw=true" width=500/></div>

- State Pattern to distinguish the different states of the game: for every different state the game can assume, a specific implementation exists. In every state, some operations are available, while others are unavailable, according to the state itself. For example: if the game is in the state of waiting for players to choose their pattern cards, a method to bind player and pattern card is available, but this will be unavailable in every other state, during which a player can't choose a pattern card again. There are 3 different states for the game: WAITING_FOR_PATTERNCARD, ON_GOING and ENDED.
