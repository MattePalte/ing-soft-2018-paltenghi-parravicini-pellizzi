package project.ing.soft.testsocket.response;

public interface IResponseHandler {
    void handle(InformationResponse aResponse) throws Exception;
    void handle(ParticipationConfirmedResponse aResponse) throws Exception;
    void handle(CreationGameResponse aResponse) throws Exception;
    void visit(IResponse aResponse) throws Exception;
}
