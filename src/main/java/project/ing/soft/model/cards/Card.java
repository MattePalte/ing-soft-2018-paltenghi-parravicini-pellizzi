package project.ing.soft.model.cards;

import project.ing.soft.model.StringBoxBuilder;

import java.io.Serializable;

public abstract class Card implements Serializable{
    public static final int WIDTH_CARD = 29;
    public static final int HEIGHT_CARD = 12;

    protected String title;
    protected String description;
    protected String imgPath;

    public Card(String title, String description, String imgPath){
        this.title = title;
        this.description = description;
        this.imgPath = imgPath;
    }

    public Card(String title, String description){
        this.title = title;
        this.description = description;
    }

    public String getTitle(){
        return title;
    }

    public String getImgPath() {
        return imgPath;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString(){
        StringBoxBuilder aBuilder = new StringBoxBuilder(new StringBoxBuilder.SINGLELINEROUNDEDCORNER(),Card.WIDTH_CARD, Card.HEIGHT_CARD);
        aBuilder.appendInAboxToTop(getTitle());
        aBuilder.appendToTop(getDescription());
        return aBuilder.toString();
    }

    public static String drawNear( int first, int last, Object ... others ){
        return StringBoxBuilder.drawNear(first, last, others);
    }

    public static String drawNear( Object... others ){
        return StringBoxBuilder.drawNear(0, others.length-1, others);
    }


}
