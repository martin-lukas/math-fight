package com.lukas.client.logic;

import com.lukas.client.gui.Gui;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lukas.protocol.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Game logic class.
 *
 * @author Jan Novosad
 */
public class ClientLogic {
    public final static Logger logicLogger = LoggerFactory.getLogger(ClientLogic.class);

    private final Set<Player> playerSet;

    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private String userName;
    private Thread listeningThread;
    private Gui gui;
    private Question question;

    private boolean connected = false;
    private boolean userClosing;

    public ClientLogic() {
        this.playerSet = new HashSet<>();
    }

    public List<Player> getPlayerList() {
        return Collections.unmodifiableList(new ArrayList<Player>(playerSet));
    }

    public Player getPlayerByName(String name) {
        for (Player player : playerSet) {
            if (player.getName().equals(name)) {
                return player;
            }
        }
        return null;
    }

    public synchronized Question getQuestion() {
        return question;
    }

    synchronized void setQuestion(Question question) {
        this.question = question;
    }

    public String getUserName() {
        return userName;
    }

    synchronized boolean isUserClosing() {
        return userClosing;
    }

    public void updatePlayers(Map<String, Integer> playerMap) {
        this.playerSet.clear();
        List<Player> playersList = new ArrayList<>();

        for (String name : playerMap.keySet()) {
            playersList.add(new Player(name, playerMap.get(name)));
        }

        this.playerSet.addAll(playersList);
    }

    public boolean connect(String login, String serverAdress, int port) {
        userName = login;

        try {
            InetAddress adress = InetAddress.getByName(serverAdress);
            socket = new Socket(adress, port);

            connected = true;
            logicLogger.info("Connection successfully established.");

            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());

            listeningThread = new Thread(new ListeningThread(input, gui, this), "ServerListener");
            listeningThread.start();

            sendCommand(EMessageType.LOGIN, login);

            return true;
        } catch (UnknownHostException ex) {
            logicLogger.error("Unknown host");
            return false;
        } catch (IOException ex) {
            logicLogger.error("Error during the connection");
            return false;
        }
    }

    public void userQuit() {
        if (!connected) {
            return;
        }

        userClosing = true;
        sendCommand(EMessageType.QUIT);
        stop();
    }

    public void stop() {
        listeningThread.interrupt();

        connected = false;

        try {
            input.close();
            output.close();
            socket.close();
        } catch (IOException ex) {
            logicLogger.error("Error during closing of the network sockets.");
        }

        Platform.exit();
    }

    public void sendCommand(EMessageType messageType, String... params) {
        if (!listeningThread.isAlive()) {
            logicLogger.error("Failed to send message, because the communication thread is closed. " +
                    "Message: " + messageType);
            return;
        }

        logicLogger.info("Sending message: " + messageType);
        try {
            Message messageToSend = new Message(messageType, params);
            output.writeObject(messageToSend);
        } catch (IOException ex) {
            logicLogger.error("Error during sending of the message: " + messageType);
            Gui.showError("Connection lost", "Can't connect to the server.", true);

        }
    }

    public void registerGui(Gui gui) {
        this.gui = gui;
    }
}
