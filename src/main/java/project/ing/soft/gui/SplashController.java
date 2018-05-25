package project.ing.soft.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import project.ing.soft.controller.IController;
import project.ing.soft.view.IView;

import java.util.concurrent.atomic.AtomicReference;

public class SplashController {

    @FXML StackPane stackBox;
    @FXML ImageView ivSplash;

    private IController myController;
    private IView myView;
    private AtomicReference<Double> currentHeight = new AtomicReference<>();
    private AtomicReference<Double> currentWidth = new AtomicReference<>();

    private final double startWidth = 500.0;
    private final double startHeight = 600.0;

    @FXML
    protected void initialize(){
        Image img = new Image("gui/sagrada_splash.png");
        ivSplash.setImage(img);
        ivSplash.setFitWidth(startWidth);
        ivSplash.setFitHeight(startHeight);
    }

    public void collectDimension(Scene scene){
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                System.out.println("Width: " + newSceneWidth);
                currentWidth.set(newSceneWidth.doubleValue());
                updateDimension(scene);

            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                System.out.println("Height: " + newSceneHeight);
                currentHeight.set(newSceneHeight.doubleValue());
                updateDimension(scene);
            }
        });
    }

    private synchronized void updateDimension(Scene scene){
        Stage stage = (Stage) scene.getWindow();
        if (currentWidth.get() == null || currentHeight.get() == null) return;
        Double curWidth = currentWidth.get();
        Double curHeight = currentHeight.get();
        Double ratio = startWidth/startHeight;
        Double widthImposedByHeight = curHeight*ratio;
        Double heightImposedBywidth = curWidth/ratio;
        //which is the constrain? height or width?
        Double desiredWidth = Math.min(widthImposedByHeight, curWidth);
        Double desiredHeight = Math.min(heightImposedBywidth, curHeight);
        ivSplash.setFitWidth(desiredWidth);
        ivSplash.setFitHeight(desiredHeight);
    }
}

