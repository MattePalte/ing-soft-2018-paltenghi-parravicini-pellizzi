package project.ing.soft.events;

import java.io.Serializable;

public interface Event extends Serializable {
    void accept(IEventHandler eventHandler);
}
