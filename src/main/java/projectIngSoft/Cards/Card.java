package projectIngSoft.Cards;

public abstract class Card {
    protected String title;
    protected String description;
    protected String imgPath;

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
        return getTitle() +": " +getDescription();
    }
}
