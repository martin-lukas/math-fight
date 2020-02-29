package com.lukas.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lukas.protocol.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.HashSet;

/**
 * Main server class managing all the incoming connections, and assigning rooms to players.
 *
 * @author Martin Lukáš
 */
public class Server {
    public static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
    public static final int WAIT_TIME = 3000; // in ms

    public static int port = 9001;
    public static int capacity = 3;
    public static int numberOfRounds = 3;

    private static final HashSet<ObjectOutputStream> OUTPUTS = new HashSet<>();
    private static final HashSet<Game> GAMES = new HashSet<>();

    private static int gameCounter = 1;

    public static void main(String[] args) {
        if (args.length > 0) {
            for (String arg : args) {
                if (!arg.matches("\\d+")) {
                    LOGGER.error("The given arguments are invalid.");
                    LOGGER.info("\nFirst: port number\nSecond: capacity of "
                            + "rooms\nThird: number of rounds in a game\n");
                    LOGGER.info("You can put in 1, 2 or 3 arguments in the order above.");
                    return;
                }
            }
            switch (args.length) {
                case 1:
                    port = Integer.valueOf(args[0]);
                    break;
                case 2:
                    port = Integer.valueOf(args[0]);
                    capacity = Integer.valueOf(args[1]);
                    break;
                case 3:
                    port = Integer.valueOf(args[0]);
                    capacity = Integer.valueOf(args[1]);
                    numberOfRounds = Integer.valueOf(args[2]);
                    break;
            }
        }
        LOGGER.info("The MathFight game server is running");
        LOGGER.info("\nConfiguration: \nPort: " + port + "\nCapacity "
                + "of rooms: " + capacity
                + "\nNumber of rounds: " + numberOfRounds);

        try (ServerSocket listener = new ServerSocket(port)) {
            while (true) {
                try {
                    new Handler(listener.accept()).start();
                    LOGGER.info("Connection accepted");
                } catch (IOException ex) {
                    LOGGER.error("Connection denied: " + ex);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Server socket fail: " + e);
        } catch (Exception e) {
            LOGGER.error("Critical server error: " + e);
            for (ObjectOutputStream out : OUTPUTS) {
                try {
                    out.writeObject(new Message(EMessageType.ERROR,
                            "Critical server error: " + e));
                } catch (IOException ex) {
                    LOGGER.info("Error sending ERROR - clients unavailable: " + ex);
                }
            }
        }
    }

    public synchronized static int addToGame(Handler handler) {
        if (GAMES.size() > 0) {
            for (Game game : GAMES) {
                if (game.isRunning() && !game.isStarted()) {
                    if (game.addHandler(handler)) {
                        return game.getId();
                    } else {
                        game.LOGGER.error("Game " + game.getId() + ": "
                                + handler.getUsername() + " denied");
                        return 0;
                    }
                }
            }
        }
        LOGGER.info("Creating Game " + gameCounter);
        GAMES.add(new Game(gameCounter));
        getGameById(gameCounter).addHandler(handler);
        return gameCounter++;
    }

    public static void removeGame(Game game) {
        LOGGER.info("Removing Game " + game.getId());
        GAMES.remove(game);
    }

    public static Game getGameById(int id) {
        for (Game g : GAMES) {
            if (g.getId() == id) {
                return g;
            }
        }
        return null;
    }

    public static HashSet<ObjectOutputStream> getOutputs() {
        return OUTPUTS;
    }
}