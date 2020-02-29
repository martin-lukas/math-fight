package com.lukas.client.logic;

import com.lukas.client.gui.Gui;
import javafx.application.Platform;
import com.lukas.protocol.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketException;
import java.util.Arrays;

/**
 * Class for the listening thread.
 *
 * @author Jan Novosad
 */
public class ListeningThread implements Runnable {
    private final ObjectInputStream input;
    private final Gui GUI;
    private final ClientLogic logic;

    public ListeningThread(ObjectInputStream input, Gui gui, ClientLogic logic) {
        this.input = input;
        this.GUI = gui;
        this.logic = logic;
    }

    @Override
    public void run() {
        ClientLogic.logicLogger.info("Starting listening thread");

        while (!Thread.interrupted()) {
            Message message = null;

            try {
                message = (Message) input.readObject();

                ClientLogic.logicLogger.info("Received message from the server: "
                        + message.getType() + ", parameters: "
                        + Arrays.toString(message.getContent()));

                switch (message.getType()) {
                    case CONNECTED:
                        callGui(EMessageType.CONNECTED, message.getContent()[0]);
                        break;

                    case QUESTION:
                        logic.setQuestion(new Question(message.getContent()[0]));
                        callGui(EMessageType.QUESTION);
                        break;

                    case STATUS:
                        MessageStatus messageS = (MessageStatus) message;
                        logic.updatePlayers(messageS.getPlayers());
                        callGui(EMessageType.STATUS);
                        break;

                    case END_OF_ROUND:
                        callGui(EMessageType.END_OF_ROUND, message.getContent()[0]);
                        break;

                    case END_OF_GAME:
                        Thread.currentThread().interrupt();
                        callGui(EMessageType.END_OF_GAME);
                        break;

                    case WRONG_ANSWER:
                        callGui(EMessageType.WRONG_ANSWER);
                        break;

                    case QUIT:
                        callGui(EMessageType.QUIT);
                        break;

                    case ERROR:
                        callGui(EMessageType.ERROR, message.getContent()[0]);
                        break;
                }
            } catch (SocketException e) {
                if (logic.isUserClosing())  //chybu jsme úmyslně způsobily my zavřením streamů
                {
                    ClientLogic.logicLogger.info("User closed the session.");
                } else {
                    Thread.currentThread().interrupt();
                    Gui.showError("Connection failed", "Connection failed, closing the game.", true);
                }
            } catch (IOException e) {
                Thread.currentThread().interrupt();
                Gui.showError("Connection failed", "Connection failed, closing the game.", true);
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                ClientLogic.logicLogger.error("Error during serialization of the message object.");
                e.printStackTrace();
            }
        }

        ClientLogic.logicLogger.debug("Closing the listening thread.");

    }

    private void callGui(EMessageType messageType, String... content) {
        Platform.runLater(() -> GUI.reactToCommand(messageType, content));
    }
}
