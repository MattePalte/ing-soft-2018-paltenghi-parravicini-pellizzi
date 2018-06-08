package project.ing.soft.model.gamemanager.events;

import java.io.Serializable;
import java.sql.Timestamp;

public class MyTurnStartedEvent implements Event, Serializable {

    private Timestamp endTurnTimeStamp;

    public MyTurnStartedEvent(){
        endTurnTimeStamp = null;
    }

    public MyTurnStartedEvent(Timestamp endTurnTimeStamp){
        this.endTurnTimeStamp = endTurnTimeStamp;
    }

    public Timestamp getEndTurnTimeStamp(){
        return endTurnTimeStamp;
    }

    @Override
    public void accept(IEventHandler eventHandler) {
        eventHandler.respondTo(this);
    }
}
