package com.lukas.client.gui;

import com.lukas.client.logic.ClientLogic;
import com.lukas.client.logic.Player;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Class for player panel.
 *
 * @author Jan Novosad
 */
public class PlayersPane extends VBox implements IInitializable {
    private final Gui GUI;
    private final GridPane playersTable = new GridPane();

    public PlayersPane(Gui gui) {
        this.GUI = gui;
    }

    @Override
    public void init() {
        ClientLogic logic = GUI.getLogic();

        Label label = new Label("Players:");
        label.setStyle("-fx-font-size: 12pt;");

        playersTable.setHgap(15);
        playersTable.setVgap(5);

        refresh();

        //playersTable.setGridLinesVisible(true);

        ColumnConstraints column0 = new ColumnConstraints();
        column0.setPrefWidth(125);

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPrefWidth(25);

        playersTable.getColumnConstraints().addAll(column0, column1);

        this.setPadding(new Insets(5, 5, 5, 5));
        this.setPadding(new Insets(10, 10, 0, 10));
        this.setAlignment(Pos.TOP_CENTER);
        this.getChildren().addAll(label, new Separator(Orientation.HORIZONTAL), playersTable);
    }

    public void refresh() {
        playersTable.getChildren().clear();

        List<Player> playerList = GUI.getLogic().getPlayerList();

        for (int i = 0; i < playerList.size(); i++) {
            String name = playerList.get(i).getName();

            Label nameL = new Label(name);
            nameL.setWrapText(true);
            Label scoreL = new Label(String.valueOf(playerList.get(i).getScore()));
            scoreL.setWrapText(true);

            if (name.equalsIgnoreCase(GUI.getLogic().getUserName())) {
                nameL.setStyle("-fx-font-weight:bold;");
                scoreL.setStyle("-fx-font-weight:bold;");
            }

            playersTable.add(nameL, 0, i);
            playersTable.add(scoreL, 1, i);
        }
    }
}
