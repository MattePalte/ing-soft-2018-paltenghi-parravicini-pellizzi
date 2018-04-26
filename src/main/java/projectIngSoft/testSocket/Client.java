package projectIngSoft.testSocket;

import projectIngSoft.testSocket.request.CreationGameRequest;
import projectIngSoft.testSocket.request.InformationRequest;
import projectIngSoft.testSocket.response.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;


public class Client implements IResponseHandler {
    private int port;
    private String host;
    private Client(String host, int port){
        this.host = host;
        this.port = port;
    }

    private void start(){
        try (Socket aSocket = new Socket()) {
            aSocket.connect( new InetSocketAddress(host, port));

            try(ObjectInputStream in = new ObjectInputStream(aSocket.getInputStream())){
                try(ObjectOutputStream out = new ObjectOutputStream(aSocket.getOutputStream())){

                    out.writeObject(new InformationRequest());
                    IResponse aResponse = (IResponse) in.readObject();
                    this.visit(aResponse);

                    out.writeObject(new CreationGameRequest(4));
                     aResponse = (IResponse) in.readObject();
                    this.visit(aResponse);

                    out.writeObject(new InformationRequest());
                     aResponse = (IResponse) in.readObject();
                    this.visit(aResponse);

                }
            }catch (Exception ex){

            }
        }catch (Exception ex){
            System.out.println("Something went wrong");
            ex.printStackTrace();
        }

    }
    @Override
    public void handle(InformationResponse aResponse) {
        System.out.println("List of all available games");
        System.out.println(aResponse.getGamesAvailable());
    }

    @Override
    public void handle(ParticipationConfirmedResponse aResponse) {

    }

    @Override
    public void handle(CreationGameResponse aResponse) throws Exception {
        System.out.println("A game should have been created");
    }

    @Override
    public void visit(IResponse aResponse) throws Exception{
        aResponse.accept(this);
    }

    public static void main(String[] args) {
        Client aClient = new Client("localhost", 3000);
        aClient.start();
    }
}
