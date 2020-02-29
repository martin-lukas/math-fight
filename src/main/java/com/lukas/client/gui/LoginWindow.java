package com.lukas.client.gui;

import com.lukas.client.logic.ClientLogic;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Class for building the login window.
 *
 * @author Jan Novosad
 */
class LoginWindow extends Stage {
    private final Gui GUI;
    private final TextField serverField;
    private final TextField loginField;
    private final Scene loginScene;
    private final Scene connectingScene;

    public LoginWindow(Gui gui) {
        this.GUI = gui;
        this.setTitle("MathFight - login");
        this.serverField = new TextField();
        this.loginField = new TextField();

        GridPane gridPane = new GridPane();

        Label serverLabel = new Label("Server: ");
        //serverLabel.setStyle("-fx-font-size: 12px; ");
        serverField.setText("localhost:9000");
        // serverField.setPrefWidth(50);

        Label loginLabel = new Label("Username: ");
        //loginLabel.setStyle("-fx-font-size: 25px; ");
        loginField.setText("");

        Button loginBut = new Button("Connect");
        loginBut.setOnAction(e -> startLogging());

        gridPane.add(serverLabel, 0, 0, 1, 1);
        gridPane.add(serverField, 1, 0, 1, 1);
        gridPane.add(loginLabel, 0, 1, 1, 1);
        gridPane.add(loginField, 1, 1, 2, 1);
        gridPane.add(loginBut, 0, 2, 2, 1);

        //gridPane.setGridLinesVisible(true);

        ColumnConstraints column0 = new ColumnConstraints();
        column0.setHalignment(HPos.CENTER);
        gridPane.getColumnConstraints().addAll(column0);

        // gridPane.setHgap(15);
        gridPane.setVgap(5);

        gridPane.setPadding(new Insets(10, 5, 5, 5));


        loginScene = new Scene(gridPane);

        Label connectingLabel = new Label("Connecting...");
        connectingLabel.setPadding(new Insets(40));
        connectingLabel.setAlignment(Pos.CENTER);

        connectingScene = new Scene(connectingLabel, 350, 100);

        this.setScene(loginScene);
        this.setResizable(false);
        this.setOnCloseRequest(e -> gui.getLogic().userQuit());

        loginField.requestFocus();
        loginScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                loginBut.fire();
            }
        });

        this.show();
        GUI.setCurrentStage(this);
    }

    void resetLogin() {
        this.setScene(loginScene);
    }

    private void startLogging() {
        ClientLogic logic = GUI.getLogic();

        String serverAdress = serverField.getText();
        String login = loginField.getText();

        if (serverAdress.matches("((^(?:[0-9]{1,3}\\.){3}[0-9]{1,3})|^localhost):\\d{2,4}$")
                && !login.equals("")) {
            String[] adress = serverAdress.split(":");

            this.setScene(connectingScene);
            if (!logic.connect(login, adress[0], Integer.valueOf(adress[1]))) {
                Gui.showError("Error - Connection failed", "Connection failed.", false);
                resetLogin();
            }
        } else {
            Gui.showError("Error - Wrong username!", "Wrong username!", false);
        }
    }
}
