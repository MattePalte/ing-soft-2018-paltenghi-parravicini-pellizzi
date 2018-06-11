package project.ing.soft.model.gamemodel.events;

import java.io.Serializable;

public interface Event extends Serializable {
    void accept(IEventHandler eventHandler);
}
