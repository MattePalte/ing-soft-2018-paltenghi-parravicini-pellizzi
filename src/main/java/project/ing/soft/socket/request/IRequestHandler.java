package project.ing.soft.socket.request;

public interface IRequestHandler {
    void visit(AbstractRequest aRequest) throws  Exception;

    void handle(InformationRequest aRequest) throws Exception;

    void handle(CreationGameRequest aRequest) throws Exception;

    void handle(PlaceDieRequest aRequest) throws Exception;

    void handle(UpdateRequest aRequest) throws Exception;

    void handle(PlayToolCardRequest aRequest) throws Exception;

    void handle(EndTurnRequest aRequest) throws Exception;

    void handle(ChoosePatternRequest aRequest) throws Exception;

    void handle(JoinTheGameRequest aRequest) throws Exception;

}
