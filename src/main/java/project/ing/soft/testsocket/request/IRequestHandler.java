package project.ing.soft.testsocket.request;

public interface IRequestHandler {
    void visit(IRequest aRequest) throws  Exception;

    void handle(ParticipationRequest aRequest) throws Exception;

    void handle(InformationRequest aRequest) throws Exception;

    void handle(CreationGameRequest aRequest) throws Exception;

    void handle(PlaceDieRequest aRequest) throws Exception;

    void handle(UpdateRequest aRequest) throws Exception;

    void handle(PlayToolCardRequest aRequest) throws Exception;

    void handle(EndTurnRequest aRequest) throws Exception;

    void handle(choosePatternRequest aRequest) throws Exception;

    void handle(joinTheGameRequest aRequest) throws Exception;

}
