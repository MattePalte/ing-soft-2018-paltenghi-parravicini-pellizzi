package project.ing.soft;

import javafx.application.Application;
import project.ing.soft.gui.GuiBackgroundApp;

/**
 * This class is the entry point of the program to launch a GUI client
 */
public class LaunchClientGui {
    /**
     * Method which launches javafx Application which renders the GUI window
     * @param args arguments passed from the terminal
     */
    public static void main(String[] args) {
        Application.launch(GuiBackgroundApp.class, args);
    }
}
