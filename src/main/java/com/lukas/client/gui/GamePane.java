package com.lukas.client.gui;

import com.lukas.client.logic.Player;
import com.lukas.protocol.EMessageType;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Main game play area class.
 *
 * @author Jan Novosad
 */
public class GamePane extends Pane implements IInitializable {
    private final Gui GUI;
    private final double width = 500;
    private final double height = 500;

    private TextField answerField;

    public GamePane(Gui gui) {
        this.GUI = gui;
    }

    @Override
    public void init() {
        this.setPrefWidth(width);
        this.setPrefHeight(height);

        drawWaitingScreen();
    }

    void drawQuestionScreen() {
        this.cleanup();

        VBox gameBox = new VBox();
        gameBox.setAlignment(Pos.CENTER);
        gameBox.setSpacing(7);

        Label questionLabel = new Label(GUI.getLogic().getQuestion().getText());
        questionLabel.setStyle("-fx-font-size:20;");
        questionLabel.setPadding(new Insets(0, 0, 25, 0));

        answerField = new TextField();
        answerField.requestFocus();

        Button answerButton = new Button("Answer");
        answerButton.setOnAction(e -> {
            if (answerField.getText().matches("^-?\\d+$")) {
                GUI.getLogic().sendCommand(EMessageType.ANSWER, answerField.getText());
            } else {
                cleanField();
                Gui.showError("Invalid answer", "Use only numbers!", false);
            }
        });

        gameBox.getChildren().addAll(questionLabel, new Separator(Orientation.HORIZONTAL), answerField, answerButton);
        center(gameBox, -50);
        gameBox.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                answerButton.fire();
            }
        });

        this.getChildren().add(gameBox);
    }

    void drawEndOfRoundScreen(String winningPlayer) {
        this.cleanup();

        int playerScore = GUI.getLogic().getPlayerByName(winningPlayer).getScore();

        Label infoLabel = new Label("Question answered by: ");
        infoLabel.setStyle("-fx-font-size:25; -fx-underline:true");
        Label nameLabel = new Label(winningPlayer);
        nameLabel.setStyle("-fx-font-size:20;");
        Label pointLabel = new Label("who now has: " + playerScore + " points");
        pointLabel.setStyle("-fx-font-size:20;");
        Label waitLabel = new Label("\n\n Wait for the next question...");
        waitLabel.setStyle("-fx-font-size:16;");

        VBox infoBox = new VBox(infoLabel, nameLabel, pointLabel, waitLabel);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setSpacing(7);

        center(infoBox, -120);

        this.getChildren().addAll(infoBox);
    }

    void drawEndOfGameScreen() {
        this.cleanup();

        List<Player> allPlayers = GUI.getLogic().getPlayerList();
        List<Player> bestPlayers = new ArrayList<>();

        int bestScore = 0;

        for (Player player : allPlayers) {
            if (player.getScore() > bestScore) {
                bestScore = player.getScore();
            }
        }

        for (Player player : allPlayers) {
            if (player.getScore() == bestScore) {
                bestPlayers.add(player);
            }
        }

        VBox contentBox = new VBox();
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setSpacing(7);

        Label headerLabel = new Label("The End");
        headerLabel.setStyle("-fx-font-size:25; -fx-underline:true");
        contentBox.getChildren().add(headerLabel);

        if (bestScore == 0) {
            Label infoLabel = new Label("Nobody scored any points.");
            infoLabel.setStyle("-fx-font-size:16;");
            contentBox.getChildren().add(infoLabel);
        } else if (bestPlayers.size() == 1) {
            Label infoLabel = new Label("Winner: " + bestPlayers.get(0).getName() +
                    "\n with score: " + bestPlayers.get(0).getScore());
            infoLabel.setStyle("-fx-font-size:16;");
            contentBox.getChildren().add(infoLabel);
        } else if (bestPlayers.size() > 1) {
            String labelText = "Draw: ";
            for (Player player : bestPlayers) {
                labelText += "\n        " + player.getName();
            }
            Label infoLabel = new Label(labelText);
            infoLabel.setStyle("-fx-font-size:16;");
            contentBox.getChildren().add(infoLabel);
        }

        center(contentBox, -150);
        this.getChildren().addAll(contentBox);
    }

    void drawWaitingScreen() {
        this.cleanup();

        Label infoLabel = new Label("Waiting for more players...");
        infoLabel.setStyle("-fx-font-size:20;");
        infoLabel.setLayoutX(width / 2 - 100);
        infoLabel.setLayoutY(height / 2 - 50);

        this.getChildren().addAll(infoLabel);
    }

    void cleanField() {
        if (answerField != null) {
            answerField.setText("");
        }
    }

    private void cleanup() {
        this.getChildren().clear();
        answerField = null;
    }

    private void center(Region region, int horizontalShift) {
        region.widthProperty().addListener((ChangeListener) (o, oldVal, newVal) -> {
            region.setLayoutX(width / 2 - region.getWidth() / 2);
            region.setLayoutY(height / 2 + horizontalShift);
        });
    }
}
