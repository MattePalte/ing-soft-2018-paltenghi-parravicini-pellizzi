package project.ing.soft.socket.response;

import java.io.IOException;

public interface IResponseHandler {
    void visit(IResponse aResponse) ;

    void handle(InformationResponse aResponse) ;

    void handle(CreationGameResponse aResponse) ;

    void handle(ExceptionalResponse aResponse) ;

    void handle(AllRightResponse aResponse) ;

    void handle(EventResponse aResponse);




}
