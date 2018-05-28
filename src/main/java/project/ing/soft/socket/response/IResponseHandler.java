package project.ing.soft.socket.response;


public interface IResponseHandler {

    void handle(InformationResponse aResponse);

    void handle(CreationGameResponse aResponse);

    void handle(ExceptionalResponse aResponse);

    void handle(AllRightResponse aResponse);

    void handle(EventResponse aResponse);
}
