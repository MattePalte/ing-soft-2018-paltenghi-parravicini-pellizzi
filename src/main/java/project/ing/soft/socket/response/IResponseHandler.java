package project.ing.soft.socket.response;

public interface IResponseHandler {
    void visit(IResponse aResponse) throws Exception;

    void handle(InformationResponse aResponse) throws Exception;

    void handle(CreationGameResponse aResponse) throws Exception;

    void handle(ExceptionalResponse aResponse) throws Exception;

    void handle(AllRightResponse aResponse) throws Exception;

    void handle(EventResponse aResponse) throws Exception;




}
