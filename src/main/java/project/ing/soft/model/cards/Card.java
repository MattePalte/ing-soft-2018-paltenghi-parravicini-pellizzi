package project.ing.soft.model.cards;

import project.ing.soft.model.StringBoxBuilder;

import java.io.Serializable;
import java.util.List;

public interface Card extends Serializable{

    String getTitle();

    String getImgPath();

    String getDescription();

    static String drawNear( int first, int last, Object ... others ){
        return StringBoxBuilder.drawNear(first, last, others);
    }

    static String drawNear( Object... others ){
        return StringBoxBuilder.drawNear(0, others.length-1, others);
    }

    static String drawNear( List others ){
        return StringBoxBuilder.drawNear(0, others.size()-1, others.toArray());
    }


}
