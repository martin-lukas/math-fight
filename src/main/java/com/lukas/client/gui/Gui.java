
package com.lukas.client.gui;

import com.lukas.client.logic.ClientLogic;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lukas.protocol.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main GUI class.
 *
 * @author Jan Novosad
 */
public class Gui implements IInitializable {
    public final static Logger guiLogger = LoggerFactory.getLogger(Gui.class);

    private static final List<Dialog> openedDialogs = new ArrayList<>();

    private final ClientLogic logic;
    private final Stage primaryStage;
    private final LoginWindow loginWindow;
    private final GamePane gamePane;
    private final PlayersPane playersPane;
    private final MenuPane menuPane;
    private final Stage aboutWindow;
    private Stage currentMainStage;

    public Gui(ClientLogic logic, Stage primaryStage) {
        this.logic = logic;
        this.primaryStage = primaryStage;

        this.loginWindow = new LoginWindow(this);

        this.gamePane = new GamePane(this);
        this.playersPane = new PlayersPane(this);
        this.menuPane = new MenuPane(this);
        this.aboutWindow = new AboutWindow(this);

        logic.registerGui(this);
        DialogCreator.setGui(this);
    }

    public static void showError(String title, String text, boolean exitApp) {
        Platform.runLater(() -> DialogCreator.createAlertDialog(title, text, exitApp));
    }

    static void RegisterClosableDialog(Dialog dialog) {
        openedDialogs.add(dialog);
    }

    ClientLogic getLogic() {
        return logic;
    }

    Stage getAboutWindow() {
        return aboutWindow;
    }

    Stage getPrimaryStage() {
        return primaryStage;
    }

    Stage getCurrentStage() {
        return currentMainStage;
    }

    void setCurrentStage(Stage stage) {
        currentMainStage = stage;
    }

    @Override
    public void init() {
        gamePane.init();
        playersPane.init();
        menuPane.init();

        //primaryStage.sizeToScene();
        createMainWindow();
    }

    public void reactToCommand(EMessageType commandType, String... params) {
        guiLogger.info("GUI reacts to the command: " + commandType
                + ", parameters: " + Arrays.toString(params));
        switch (commandType) {
            case CONNECTED:
                loginWindow.close();
                this.init();
                primaryStage.setTitle("MathFight - player: " + logic.getUserName() + ", room no. " + params[0]);
                break;

            case STATUS:
                playersPane.refresh();
                break;

            case QUESTION:
                closeAllDialogs();
                gamePane.drawQuestionScreen();
                break;

            case END_OF_ROUND:
                closeAllDialogs();
                gamePane.drawEndOfRoundScreen(params[0]);
                break;

            case END_OF_GAME:
                closeAllDialogs();
                gamePane.drawEndOfGameScreen();
                break;

            case WRONG_ANSWER:
                showError("wrong answer", "Your answer is incorrect, please try again.", false);
                gamePane.cleanField();
                break;

            case QUIT:
                showError("Disconnected.", "Disconnected from the server.", true);
                break;

            case ERROR:
                if (params[0].equals("login")) {
                    showError("incorrect login", "Provided username is already in use!!", false);
                    loginWindow.resetLogin();
                } else {
                    showError(params[0], params[0], false);
                }

                break;
        }
    }

    private void createMainWindow() {
        HBox playersWrapBox = new HBox(playersPane, new Separator(Orientation.VERTICAL));

        BorderPane root = new BorderPane(gamePane, menuPane, null, null, playersWrapBox);
        Scene mainScene = new Scene(root);
        this.primaryStage.setScene(mainScene);
        this.primaryStage.setResizable(false);
        this.primaryStage.setOnCloseRequest(e -> {
            aboutWindow.close();
            closeAllDialogs();
            logic.userQuit();
        });
        this.primaryStage.show();
        setCurrentStage(primaryStage);

    }

    public void closeAllDialogs() {
        for (Dialog dialog : openedDialogs) {
            dialog.close();
        }
    }
}