package com.lukas.client;

import com.lukas.client.gui.Gui;
import com.lukas.client.logic.ClientLogic;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main Client class.
 *
 * @author Jan Novosad
 */
public class Client extends Application {
    private static Gui gui;

    private ClientLogic logic;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        logic = new ClientLogic();
        gui = new Gui(logic, primaryStage);
    }

    @Override
    public void stop() {
//        logic.stop();
        gui.closeAllDialogs();
        ClientLogic.logicLogger.info("Aplikace ukončena");
    }
}
