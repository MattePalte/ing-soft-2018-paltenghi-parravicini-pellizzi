Index
-----
1) [Group Info](#Info)
2) [Coverage](#Coverage)
3) [UML](#UML)
    - [Global UML class diagram](#Global_UML)
4) [Advanced Functionalities](#FA)
    - [Dynamic pattern cards creation from file](#Personalized_Cards)
    - [Multi game support](#Multi_game)
5) [Limitations](#Limitations)
    - [ToolCards limitations](#ToolCards_limitations)
6) [Design Choices](#Design_choices)
    - [Client - Server interaction](#Client-Server_Interaction)
    - [Design Patterns used](#Design_Patterns)
        - [Command Pattern](#Command)
        - [Strategy Pattern](#Strategy)
        - [Proxy Pattern](#Proxy)
        - [Visitor Pattern](#Visitor)
        - [State Pattern](#State)
        - [Singleton](#Singleton)
        - [Factory](#Factory)
        - [Template](#Template)
    - [Connection and Reconnection](#Conn-Reconn)


<a name="Info"></a>1. Group's Info
----------
| Cognome         | Nome            	 | Matricola        	 | Codice Persona  	|
| :-------------: | :-------------: 	 | :--------------: 	 | :--------------:	|
| Paltenghi 	  | Matteo 		 | 847354 	 	 | 10523358 		|
| Parravicini 	  | Daniele		 | 847911   		 | 10527346		|
| Pellizzi  	  | Kristopher Francesco | 847198 		 | 10522862 		|

<a name="Coverage"></a>2. Coverage
----------
Here below there are the coverage metrics of Intellij both of the package and the model.
<div align="CENTER"><img src="https://github.com/MattePalte/ing-soft-2018-paltenghi-parravicini-pellizzi/blob/master/SCREEN/screenTestPackage.png?raw=true"/></div>
<div align="CENTER"><img src="https://github.com/MattePalte/ing-soft-2018-paltenghi-parravicini-pellizzi/blob/master/SCREEN/screenTestModel.png?raw=true"/></div>
Here the metrics of Sonarqube:

<a name="UML"></a>3. UML
---
Here is a complete UML class diagram of the entire project:
<div name="Global_UML" align="CENTER"><img src="https://github.com/MattePalte/ing-soft-2018-paltenghi-parravicini-pellizzi/blob/master/UML/final/GlobalUML.png?raw=true"/></div>

<a name="FA"></a>4. Advanced functionalities
---
<a name="Personalized_Cards"></a>
### Dynamic pattern cards creation from file
Information about window pattern cards can be gathered throught file. 
Since a card has always a front and a back view, window patterns must be declared in pairs. A window pattern contains information about constraints the user has to respect during die placement, a name and a natural positive number, also called as difficulty, that resemble a number of favours a player should be given in a multiplayer game.
The goal was to provide a naive mechanism that could endorse new window pattern cards.

First line of the file should express the number of window pattern cards that can be read.

We decided to use a simple encoding to represent a single pattern card
```
<name that can contain spaces>
<number of favour> <width> <height> <costraint #1, ... ,constraint#width*height>

```

We chose to represent every constraint using a pair of characters which express respectively a value and a color restriction:
1. The first must be a number, between 1 and 6; 
2. the second identifies the colour {W=> WHITE, G=> Green, Y=>Yellow, R=> Red, V => Violet, B=>Blue} .
E.g.
1W => indicates that during ordinary die placement procedures only a die that has value '1' should be accepted; 
0B => indicates that during ordinary die placement procedures only die that of colour BLUE should be accepted.

Here is an example of the pattern encoding:
<div align=CENTER><img src="https://github.com/MattePalte/ing-soft-2018-paltenghi-parravicini-pellizzi/blob/master/SCREEN/text%20pattern.PNG?raw=true"/> <img src="https://github.com/MattePalte/ing-soft-2018-paltenghi-parravicini-pellizzi/blob/master/SCREEN/pattern.PNG?raw=true"/></div>

##### Default window pattern cards file content
```
12 <- Number of Couples to read
Kaleidoscopic Dream
4 4 5 0Y0B0W0W1W0G0W5W0W4W3W0W0R0W0G2W0W0W0B0Y
Virtus
5 4 5 4W0W2W5W0G0W0W6W0G2W0W3W0G4W0W5W0G1W0W0W
Aurorae Magnificus
5 4 5 5W0G0B0V2W0V0W0W0W0Y0Y0W6W0W0V1W0W0W0G4W
Via Lux
4 4 5 0Y0W6W0W0W0W1W5W0W2W3W0Y0R0V0W0W0W4W3W0R
Bellesguard
3 4 5 0B6W0W0W0Y0W3W0B0W0W0W5W6W2W0W0W4W0W1W0G
Sun Catcher
3 4 5 0W0B2W0W0Y0W4W0W0R0W0W0W5W0Y0W0G3W0W0W0V
Firmitas
5 4 5 0V6W0W0W3W5W0V3W0W0W0W2W0V1W0W0W1W5W0V4W
Symphony Of Light
6 4 5 2W0W5W0W1W0Y6W0V2W0R0W0B4W0G0W0W3W0W5W0W
Aurora Sagradis
4 4 5 0R0W0B0W0Y4W0V3W0G2W0W1W0W5W0W0W0W6W0W0W
Industria
5 4 5 1W0R3W0W6W5W4W0R2W0W0W0W5W0R1W0W0W0W3W0R
Batllo
5 4 5 0W0W6W0W0W0W5W0B4W0W3W0G0Y0V2W1W4W0R5W3W
Shadow Thief
5 4 5 6W0V0W0W5W5W0W0V0W0W0R6W0W0V0W0Y0R5W4W3W
Fractal Drops
3 4 5 0W4W0W0Y6W0R0W2W0W0W0W0W0R0V1W0B0Y0W0W0W
Gravitas
5 4 5 1W0W3W0B0W0W2W0B0W0W6W0B0W4W0W0B5W2W0W1W
Chromatic Splendor
4 4 5 0W0W0G0W0W2W0Y5W0B1W0W0R3W0V0W1W0W6W0W4W
Lux Astram
5 4 5 0W1W0G0V4W6W0V2W5W0G1W0G5W3W0V0W0W0W0W0W
Firelight
5 4 5 3W4W1W5W0W0W6W2W0W0Y0W0W0W0Y0R5W0W0Y0R6W
Luz Celestial
3 4 5 0W0W0R5W0W0V4W0W0G3W6W0W0W0B0W0W0Y2W0W0W
Ripples Of Light
5 4 5 0W0W0W0R5W0W0W0V4W0B0W0B3W0Y6W0Y2W0G1W0R
Water Of Life
6 4 5 6W0B0W0W1W0W5W0B0W0W4W0R2W0B0W0G6W0Y3W0V
Comitas
5 4 5 0Y0W2W0W6W0W4W0W5W0Y0W0W0W0Y5W1W2W0Y3W0W
Lux Mundi
6 4 5 0W0W1W0W0W1W0G3W0B2W0B5W4W6W0G0W0B5W0G0W
Fulgor Del Cielo
5 4 5 0W0B0R0W0W0W4W5W0W0B0B2W0W0R5W6W0R3W1W0W
Sun's Glory
6 4 5 1W0V0Y0W4W0V0Y0W0W6W0Y0W0W5W3W0W5W4W2W1W
```
<a name="Multi_game"></a>
### Multi game support
When a player connects to the game, he will be connected to an already existing game waiting for players to start, if this exist, otherwise, a new game is created on the same server. While the games are on going, both of them are managed by the same server and other new games could be added meanwhile. To keep references to every game hosted, a Map exist, which saves both the reference of the GameController and its unique ID. When the game finishes, the game is removed from this Map, and players can connect again to a new game using the same nickname, since that does not exist anymore on the server. Since no registration and login are required to connect, when the game finishes, is possible for another person to join a game using a nickname used by one of the players who just finished, making it impossible to anyone else, also the person who just used it, to join using it. Let's make an example to make it clear: let's suppose a player Alice is playing in a game with the nickname MyNameIsTheBest and that, while the game is on going, Bob tries to connect to a game using the same nickname. In this case, Bob is notified that the nickname already exists on the server and that he must choose another name. But, if the match played by Alice finishes and Bob tries to connect once again using the nickname MyNameIsTheBest, his connection will be accepted and he will connect to a game using that name. This means that, if Alice tries to play again with the same nickname, her connection will be refused and she will be notified that the nickname she has chosen already exists on the server.
Player connection and usage of the game ID will be explained better lately in the section [*Connection and Reconnection*](#Conn-Reconn).

<a name="Limitations"></a>5. Limitations 
---

<a name="ToolCards_limitations"></a>
### ToolCards limitations
- All the ToolCards implying the player to move one or more dice have been implemented by taking the dice chosen by the player from its board and, one by one, in the same order the player chose them, he is asked to place them again. This implementation allow us to permit players to swap 2 dice in their board. However, it does not allow players to move dice in cascade: let's suppose a player placed only 2 dice. If he plays a toolcard to move both of them, he can't choose a die, place it near the other one and move the second one. The dice are taken at the same time and then placed one by one. If taking the 2 dice chosen leaves the board clear (no dice are left on the board) then the player must place the first die on the edge of the board, just as if it were its first die in the game.
The toolcards affected by this limitation are:
	- Lathekin
	- Tap Wheel

- The effect description of the ToolCard called "Running Pliers", says: "After your first turn, immediately draft a die. Skip your next turn this round". From this description, we intended that the player who plays this ToolCard, must take its turn twice in a row. So, when this ToolCard is played, its effect changes the turn sequence of the round and the player who played it can take its turn twice, choosing, for every turn, what to do. This means that even in its second turn, he can choose to place a die, to play a ToolCard, or to do nothing and simply pass its turn.

<a name="Design_choices"></a>6. Design Choices
---------------------------

<a name="Client-Server_Interaction"></a>
### Client - Server interaction

To let client and server communicate each other, we implemented, as requested, a MVC pattern, with some differences from the plain one.

On the client there is player's view, which contains a reference to the remote controller (if the client connected through RMI) or to a controller proxy (if the client connected through socket). If a player wants to perform any action which involves a change on the model, he must ask for it to the controller. Actually, the client has a copy of the model available, but, even if he is able to modify it using a custom client, the change won't affect the game, because it has effect only on the local copy of the player and the shared model (which is only on the server) is unchanged.

The model is divided into more classes, to let every class represent a single object and to separate methods and attributes according to their context. A central class, called GameModel, is used to integrate every class of the model and let them interact each other. This class is also responsible for the game flow proceed: it sends events to every player connected to the match. In particular, every time the model state changes, it sends a ModelChangedEvent, to let players save an up-to-date copy of the model.

The controller is divided into 2 different entities: there is a GameController, which is responsible for checking that the action has been asked from the player who must play its turn and, before delegate the request to the Model, this controller checks if the timeout is already expired. In these cases the controller won't ask the model to perform any action and will return to the client an exception, explaining what happened.

The second part of the controller is called PlayerController. Each player is associated with its own PlayerController. This controller is used to avoid spoofing by other players: every request to the server is done by passing as a parameter also the name of the player itself. If a player A (using a custom client) tries to perform any action pretending to be player B, player A's PlayerController, who knows the name of the player associated, delegates the request to the GameController with the real name of the player. This way, if the player who asked for an action is not the current player, the GameController will return to him an Exception.

Here is a graphical representation of client-server interaction:
![client-server interaction](https://github.com/MattePalte/ing-soft-2018-paltenghi-parravicini-pellizzi/blob/master/UML/Client-Server%20interaction.PNG?raw=true)

<a name="Design_Patterns"></a>
### Design Pattern used
In addition to MVC pattern, already explained, we implemented:
- <a name="Command"></a>**Command Pattern** to implement ToolCards: there is a ToolCard interface, which contains all the methods needed to let players use them. Since every ToolCard applies a different effect, there is a specific implementeation for every single existing ToolCard in the game, but, since some ToolCards have quite similar effects, the operations to be executed above the GameModel are coded into the GameModel itself, and the ToolCards call them in the order they need. Here is a scheme of the pattern: 
<div align=CENTER><img src="https://github.com/MattePalte/ing-soft-2018-paltenghi-parravicini-pellizzi/blob/master/UML/ToolCards.png?raw=true" width=400/></div>

- <a name="Strategy"></a>**Strategy Pattern** to implement public objectives: there is a PublicObjective interface, which contains the methods needed to count points on players' boards. Since the way to count points changes according to the specific public objective, there is a specific implementation for every one of them. Here is a scheme of the pattern: 
<div align=CENTER><img src="https://github.com/MattePalte/ing-soft-2018-paltenghi-parravicini-pellizzi/blob/master/UML/PublicObjectives.png?raw=true" width=300/></div>

- <a name="Proxy"></a>**Proxy Pattern** to implement communication through socket: if a player connects to the game through socket, since its view can't have a reference to the remote object, as it happens with RMI, a ControllerProxy is created. The view can't distinguish through a real controller or a proxy, so, it asks for an operation normally. The controller proxy, creates a Request object, which will be sent to the server, and received by a ViewProxy. The ViewProxy can understand the request, and asks to the real Controller to execute the method asked by the player. In a similar way, the communication goes also from server to client: when something is needed to be sent from server to client, such as GameModel's events, it is incapsulated in a Response object by the ViewProxy and sent to the ControllerProxy on the player's client. The ControllerProxy can understand the response, and sends it to the view in a format he can manage, typically an Event. Here is an example scheme of the pattern:
<div align=CENTER><img src="https://github.com/MattePalte/ing-soft-2018-paltenghi-parravicini-pellizzi/blob/master/UML/Proxy.png?raw=true" height=75 /></div>

- <a name="Visitor"></a>**Visitor Pattern** to let ControllerProxy and ViewProxy manage, respectively, Response and Request objects and to let the View manage Event objects: the behavior of the proxy pattern implemented in the socket communication has already been explained. When a ViewProxy receives a Request, it recognizes and manages the specific Request object through a Visitor pattern: for every kind of request, a specific implementation and a method in the RequestHandler interface exist. The RequestHandler interface is implemented by the ViewProxy. In a similar way is implemented the visitor Pattern in the ControllerProxy: for every kind of Response, a specific implementation and a method in the ResponseHandler interface exist. The ResponseHandler interface is implemented by the ControllerProxy. As said at the beginning, the visitor Pattern is also implemented in the View objects to let them handle the events received from the model: for every Event, a specific implementation and a method in the EventHandler interface exist. As suggested, the EventHandler interface is implemented by the View. Here is an example scheme of the pattern:
<div align=CENTER><img src="https://github.com/MattePalte/ing-soft-2018-paltenghi-parravicini-pellizzi/blob/master/UML/Visitor.png?raw=true" width=500/></div>

- <a name="State"></a>**State Pattern** to distinguish the different states of some ToolCards: some of the ToolCards required more interaction with the player and, after every interaction, the behavior of the ToolCard changes. To implement this kind of ToolCards, we created a specific implementation for every single phase of the ToolCard itself. When the player asks to play it, the first phase is executed and the state changes, notifying the player that he must now play the second phase. The player can't abort the operation, after the first phase is played. The only things he can do are finish all the phases of the ToolCard he asked for or let its timer expires. In that case, the operation is truncated, and the player loses its turn. However, since the first phase has been played and some changes have been made on the model, the player will pay the cost of the ToolCard even if he didn't finish to apply the entire effect. The ToolCards implemented with a state pattern, which require at most 2 interactions, are:

	- Flux Remover
	- Flux Brush

	There are no ToolCards requiring more than 2 interactions with the user, but, if needed, the sequence could be iterated on more phases, creating a specific implementation for every one of them. Here is a scheme of the pattern:
    <div align="CENTER"><img src="https://github.com/MattePalte/ing-soft-2018-paltenghi-parravicini-pellizzi/blob/master/UML/State.png?raw=true"/></div>
- <a name="Singleton"></a>**Singleton** to create a Settings class: since the Settings class contains quite constant properties, a single instance of it is created the first time any class requires a property. If the object has already been created and other settings are needed, a reference to this unique instance is returned.
- <a name="Factory"></a>**Factory** to create the instance of the GameModel: depending on the number of player that made up a game, rules can change. Therefore we have created a class that decouple the decision of which particular game instantiate from the general management of the game. This decision will take place in its factory method where, based on some logic, it can create the appropiate kind of GameModel to instantiate. Furthermore, every match needs common objects to be initialized, so the factory method takes care of creating and initialize ToolCards, Public Objectives, Private Objectives, DiceBag and PatternCards that can be reused to create instances other GameModels. *Note: even if we planned that a single player GameModel could be built, we developed other Advanced Functionalities, explained in the section [Advanced Functionalities](#FA), so,a single player GameModel is never requested and never created.*
- <a name="Template"></a>**Template** to make ToolCards (only those with a single state) have a default behavior: a ToolCard abstract class exists, with a template method implemented. This calls other methods in a particular sequence and it's used to play the ToolCard itself. The sequence of actions performed in the template method is:
	1. Verify if the player who asked to play the ToolCard has enough Favour Points to pay it
	2. Check if the parameters inserted by the player to play the ToolCard are correct
	3. Apply ToolCard effect
	4. If everything went well (no exception have been thrown) pay the ToolCard consuming player's Favour Points
	5. Update player's local copy of the model
 
	Every single state ToolCard extends this abstract class, overriding, eventually, the methods that have a different behavior according to the specific ToolCard. The template method is not changed, so the sequence of action executed will be the same for every ToolCard, but, since some methods have been override, the effect could be slightly different.
    
<a name="Conn-Reconn"></a>
### Connection and Reconnection
One of the functionalities to be implemented requires to manage players connection, disconnection and reconnection to the game. As requested, when a player asks to connect to a game, he will be added to the game which is waiting for players to start, if this exists. If there are no games waiting for players, a new game is created and the player will be added to this one. When a game is created and the first player connects to it, he is notified to wait until the match starts. To make the game start, at least two players must be connected to the game: when there are at least two players connected, a timer is set. If the game reaches the maximum number of players allowed, settable from a settings file, or the timer expires, the game starts, and no other players can be added to this one. If all the players except from one leave the game while it it starting, the timer will be stopped and it will be set again if another player connects to it. During the match, can happen that a player disconnect. In this case, all the other players in the game are notified of the disconnection and the game goes on skipping the disconnected players turn. If all the players disconnect from the game except from one, the match finishes and the last player remained is the winner. As requested, a player who disconnects from the game, has the chance to reconnect to the same game, with the same name, of course. To avoid any person reconnect to a game he didn't join with the name of another player, we implemented a token code computation, that is given and shown to the user when he first connects to the game. This token code is created server side, by appending to the game ID the name of the player and computing an MD5 hash string of the result. Since the game ID is available only on the server, other players can't compute it and, as a property of hash functions, every token code will be different for every player connected to the server, because every player nickname must be different from the ones already existing in a game hosted on the server, so only the player who was really playing the match can reconnect to the game he abandoned. When the player reconnects to the game, all other players in the same match are notified and the player who rejoined can play back again immediately at its first turn of the round.

