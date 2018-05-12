package project.ing.soft.socket;

import project.ing.soft.model.Game;
import project.ing.soft.socket.request.*;
import project.ing.soft.socket.response.CreationGameResponse;
import project.ing.soft.controller.IController;
import project.ing.soft.socket.response.InformationResponse;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.util.Map;
import java.util.logging.*;


public class SocketHandler implements IRequestHandler, Runnable{
    private Socket aSocket;
    private ObjectOutputStream toClient;
    private ObjectInputStream fromClient;
    private Map<Game, IController> hostedGames;
    private Logger log;


    public SocketHandler(Socket aSocket,Map<Game, IController> hostedGames) throws IOException {
        this.aSocket    = aSocket;

        this.toClient   = new ObjectOutputStream(aSocket.getOutputStream());
        this.toClient.flush();
        this.fromClient = new ObjectInputStream(aSocket.getInputStream());
        this.hostedGames= hostedGames;
        this.log        = Logger.getLogger(aSocket.toString());
        this.log.setUseParentHandlers(false);

        MyFormatter formatter = new MyFormatter();
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(formatter);

        this.log.addHandler(handler);

    }



    @Override
    public void run() {

        try {


            while (!aSocket.isClosed() && !Thread.currentThread().isInterrupted()) {

                AbstractRequest aRequest = (AbstractRequest) fromClient.readObject();
                this.visit(aRequest);

            }
        }catch (EOFException ignored){
            log.log(Level.SEVERE, "EOF exception occured");

        }catch (ClassNotFoundException ex) {
            log.log( Level.SEVERE,"A class wasn't found {0}", ex );
        } catch (Exception ex){
            log.log(Level.SEVERE, "An error occurred while writing/reading objects {0}", ex);
        }
        finally {
            log.log(Level.INFO, "User disconnected");
        }




    }


    @Override
    public void handle(PlaceDieRequest aRequest) {

    }

    @Override
    public void handle(UpdateRequest aRequest) {

    }

    @Override
    public void handle(PlayToolCardRequest aRequest) {

    }

    @Override
    public void handle(EndTurnRequest aRequest) {

    }

    @Override
    public void handle(ChoosePatternRequest aRequest) {

    }

    @Override
    public void handle(JoinTheGameRequest aRequest) {

    }


    @Override
    public void handle(InformationRequest aRequest) throws Exception {

        toClient.writeObject(new InformationResponse(getAvailableGames()));
        toClient.flush();
    }

    @Override
    public void handle(CreationGameRequest aRequest) throws Exception {
        hostedGames.put(new Game(aRequest.getNumberOfPlayer()), null);
        toClient.writeObject(new CreationGameResponse());
        toClient.flush();

    }

    private ArrayList<Game> getAvailableGames() {
        return new ArrayList<>(hostedGames.keySet());
    }

    @Override
    public void visit(AbstractRequest aRequest) throws Exception {
        aRequest.accept(this);
    }


    private class MyFormatter extends Formatter {
        // Create a DateFormat to format the logger timestamp.
        private final DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");

        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder(1000);
            builder.append(df.format(new Date(record.getMillis()))).append(" - ");
            builder.append("[").append(aSocket.getRemoteSocketAddress()).append("] - ");
            builder.append("[").append(record.getLevel()).append("] - ");
            builder.append(formatMessage(record));
            builder.append("\n");
            return builder.toString();
        }
    }
}
