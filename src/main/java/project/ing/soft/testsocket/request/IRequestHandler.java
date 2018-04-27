package project.ing.soft.testsocket.request;

public interface IRequestHandler {
    void handle(ParticipationRequest aRequest) throws Exception;
    void handle(InformationRequest aRequest) throws Exception;
    void handle(CreationGameRequest aRequest) throws Exception;
    void handle(PlaceDieRequest aRequest);
    void visit(IRequest aRequest) throws  Exception;


}
