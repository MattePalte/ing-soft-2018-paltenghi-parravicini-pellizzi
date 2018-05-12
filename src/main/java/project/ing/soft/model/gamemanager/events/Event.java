package project.ing.soft.model.gamemanager.events;

import java.io.Serializable;

public interface Event extends Serializable {
    void accept(IEventHandler eventHandler);
}
