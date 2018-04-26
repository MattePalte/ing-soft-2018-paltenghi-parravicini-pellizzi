package projectIngSoft.testSocket;

import projectIngSoft.Controller.IController;
import projectIngSoft.Game;
import projectIngSoft.testSocket.request.*;
import projectIngSoft.testSocket.response.CreationGameResponse;
import projectIngSoft.testSocket.response.InformationResponse;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.*;


public class SocketHandler implements IRequestHandler, Runnable{
    private Socket aSocket;
    private ObjectOutputStream out;
    private ObjectInputStream  in;
    private HashMap<Game, IController> hostedGames;
    private Logger log;


    public SocketHandler(Socket aSocket,HashMap<Game, IController> hostedGames){
        this.aSocket    = aSocket;
        this.in         = null;
        this.out        = null;
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

        try{
            out = new ObjectOutputStream(aSocket.getOutputStream());
            in = new ObjectInputStream(aSocket.getInputStream());

            while(!aSocket.isClosed() && !Thread.currentThread().isInterrupted()){

                IRequest aRequest = (IRequest) in.readObject();
                this.visit(aRequest);

            }

        }catch(EOFException ignored){

        }catch (ClassNotFoundException ex) {
            log.log( Level.SEVERE,"A class wasn't found {0}", ex );
        } catch (Exception ex){
            log.log(Level.SEVERE, "An error occurred while writing/reading objects {0}", ex);
        }finally {
            log.log(Level.INFO, "disconnected");
        }

        try {
            out.close();
        }catch (Exception ignored){

        }

        try {
            in.close();
        }catch (Exception ignored){

        }


    }



    @Override
    public void handle(ParticipationRequest aRequest) throws Exception{


    }

    @Override
    public void handle(InformationRequest aRequest) throws Exception {

        out.writeObject(new InformationResponse(getAvailableGames()));
        out.flush();
    }

    @Override
    public void handle(CreationGameRequest aRequest) throws Exception {
        hostedGames.put(new Game(aRequest.getNumberOfPlayer()), null);
        out.writeObject(new CreationGameResponse());
        out.flush();

    }

    private ArrayList<Game> getAvailableGames() {
        return new ArrayList<>(hostedGames.keySet());
    }

    @Override
    public void visit(IRequest aRequest) throws Exception {
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

        public String getHead(Handler h) {
            return super.getHead(h);
        }

        public String getTail(Handler h) {
            return super.getTail(h);
        }
    }
}
