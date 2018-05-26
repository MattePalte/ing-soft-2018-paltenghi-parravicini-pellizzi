package project.ing.soft.model.cards;

import project.ing.soft.model.StringBoxBuilder;

import java.io.Serializable;

public interface Card extends Serializable{
    int WIDTH_CARD = 29;
    int HEIGHT_CARD = 12;

    String getTitle();

    String getImgPath();

    String getDescription();

    static String drawNear( int first, int last, Object ... others ){
        return StringBoxBuilder.drawNear(first, last, others);
    }

    static String drawNear( Object... others ){
        return StringBoxBuilder.drawNear(0, others.length-1, others);
    }


}
