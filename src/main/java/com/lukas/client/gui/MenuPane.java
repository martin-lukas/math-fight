
package com.lukas.client.gui;

import com.lukas.client.logic.ClientLogic;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * Class for menu bar.
 *
 * @author Jan Novosad
 */
public class MenuPane extends MenuBar implements IInitializable {
    private final Gui GUI;

    public MenuPane(Gui gui) {
        this.GUI = gui;
    }

    @Override
    public void init() {
        Menu fileMenu = new Menu("Game");
        MenuItem konecItem = new MenuItem("Quit");
        konecItem.setOnAction(e -> {
            ClientLogic logic = GUI.getLogic();
            GUI.getPrimaryStage().close();
            logic.userQuit();
        });

        fileMenu.getItems().addAll(konecItem);

        Menu helpMenu = new Menu("Help");

        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(p -> {
            if (GUI.getAboutWindow().isShowing()) {
                GUI.getAboutWindow().close();
            } else {
                GUI.getAboutWindow().show();
            }
        });

        helpMenu.getItems().addAll(aboutItem);

        this.getMenus().addAll(fileMenu, helpMenu);
    }
}

/**
 * Class for "About program" window.
 *
 * @author Jan Novosad
 */
class AboutWindow extends Stage {
    private final Gui GUI;

    public AboutWindow(Gui gui) {
        this.GUI = gui;

        Label nameLabel = new Label("MathFight");
        nameLabel.setStyle("-fx-font-size:25;");
        Label infoLabel = new Label("MathFight is a multiplayer game in which the players " +
                "compete against each other to answer the solutions to simple math equations " +
                "as quickly as possible. This game was created for the course 4IT363 " +
                "at University of Economics in Prague.");
        infoLabel.setStyle("-fx-font-size:12;");
        infoLabel.setWrapText(true);
        infoLabel.setTextAlignment(TextAlignment.JUSTIFY);

        Label autorsLabel = new Label("Authors: Martin Lukáš, Jan Novosad");

        VBox box = new VBox(nameLabel, new Separator(Orientation.HORIZONTAL),
                infoLabel, new Separator(Orientation.HORIZONTAL),
                autorsLabel);
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(350);
        box.setSpacing(10);
        box.setPadding(new Insets(15));

        this.setScene(new Scene(box));
        this.setResizable(false);

        this.setOnShown(e -> {
            Stage stage = GUI.getCurrentStage();
            double x = stage.getX() + stage.getWidth() / 2 - this.getWidth() / 2;
            double y = stage.getY() + stage.getHeight() / 2 - this.getHeight() / 2;
            this.setX(x);
            this.setY(y);
        });
    }
}
