package com.lukas.client.gui;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * Class for creation of error dialogs.
 *
 * @author Jan Novosad
 */
class DialogCreator {
    private static Gui currentGui;

    static void createAlertDialog(String title, String text, boolean exitApp) {
        Alert errorDialog = new Alert(Alert.AlertType.ERROR);
        errorDialog.setTitle(title);
        errorDialog.setHeaderText(null);
        errorDialog.setContentText(text);
        if (exitApp) {
            errorDialog.setOnCloseRequest(e -> currentGui.getLogic().stop());
        } else {
            Gui.RegisterClosableDialog(errorDialog);
        }

        Stage stage = currentGui.getCurrentStage();
        double x = stage.getX() + stage.getWidth() / 2 - 350 / 2;
        double y = stage.getY() + stage.getHeight() / 2 - 50 / 2;
        errorDialog.setX(x);
        errorDialog.setY(y);

        errorDialog.showAndWait();
    }

    static void setGui(Gui gui) {
        currentGui = gui;
    }
}
