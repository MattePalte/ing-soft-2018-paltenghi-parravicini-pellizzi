package project.ing.soft.socket.request;

public interface IRequestHandler {

    void handle(InformationRequest aRequest);

    void handle(CreationGameRequest aRequest);

    void handle(PlaceDieRequest aRequest) throws Exception;

    void handle(UpdateRequest aRequest);

    void handle(PlayToolCardRequest aRequest) throws Exception;

    void handle(EndTurnRequest aRequest) throws Exception;

    void handle(ChoosePatternRequest aRequest) throws Exception;
}
