package com.lukas.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lukas.protocol.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Game class for managing the players and providing them with questions to answer.
 *
 * @author Martin Lukáš
 */
public class Game {
    private Equation currentEquation;
    private HashMap<String, Integer> status;
    private boolean running = true;
	private boolean paused = false;
	private boolean started = false;
    private int round = 0;
	private final int id;
	private final HashSet<Handler> handlers;
	
	public static final Logger LOGGER = LoggerFactory.getLogger(Game.class);

	public Game(int id) {
		this.id = id;
        handlers = new HashSet<>();
    }

	public synchronized void startGame() {
		if (!started) {
			status = new HashMap<>();
			for (Handler h : handlers) {
				status.put(h.getUsername(), 0);
			}
			started = true;
			LOGGER.info("Game " + id + ": Starting the game.");
			sendStatusMessage();
			sendNewQuestion();
		} else {
			LOGGER.info("Game " + id + ": Trying to start an already " + "started game.");
		}
    }

	public synchronized boolean addHandler(Handler handler) {
		if (!containsUsername(handler.getUsername())) {
			Server.getOutputs().add(handler.getOutput());
			LOGGER.info("Game " + id + ": " + handler.getUsername()
					+ " allowed.");
			handlers.add(handler);
			LOGGER.info("Game " + id + ": Adding " + handler.getUsername()
					+ " to the game. " + "Free spaces: " + freeSpots());
			LOGGER.info("Game " + id + ": Sending: CONNECTED");
			sendMessageTo(handler, new Message(EMessageType.CONNECTED, 
					String.valueOf(id)));
			if (handlers.size() == Server.capacity) {
				startGame();
			}
			return true;
		}
		return false;
    }

	public void processAnswerFromHandler(Handler handler, String answer) {
		if (!answer.equals("") && 
				Integer.parseInt(answer) == currentEquation.getResult()) {
			addPointToHandler(handler.getUsername());
			sendStatusMessage();
			if (round != Server.numberOfRounds) {
				LOGGER.info("Game " + id + ": Sending to all: END_OF_ROUND, "
					+ handler.getUsername());
				sendMessageToAll(new Message(EMessageType.END_OF_ROUND, 
						handler.getUsername()));
				paused = true;
				try {
					Thread.sleep(Server.WAIT_TIME);
				} catch (InterruptedException ex) {
					LOGGER.error("Game " + id + ": Could not put game "
							+ "thread to sleep: " + ex);
				}
				paused = false;
				sendNewQuestion();
			} else {
				endGame();
			}
		} else {
			LOGGER.info("Game " + id + ": Sending to "	+ handler.getUsername()
					+ ": WRONG_ANSWER" );
			sendMessageTo(handler, new Message(EMessageType.WRONG_ANSWER));
		}
	}

	public synchronized void endGame() {
		LOGGER.info("Game " + id + ": Sending to all: END_OF_GAME");
		sendMessageToAll(new Message(EMessageType.END_OF_GAME));
		running = false;
		Server.removeGame(this);
	}

    public synchronized void addPointToHandler(String name) {
		int score = status.get(name);
        status.replace(name, score + 1);
    }

	public synchronized void sendNewQuestion() {
		updateEquation();
		LOGGER.info("Game " + id + ": Sending to all: QUESTION");
		sendMessageToAll(new Message(EMessageType.QUESTION, 
				currentEquation.toString()));
    }

    public synchronized void updateEquation() {
        currentEquation = new Equation();
        round++;
    }

	public synchronized void sendMessageTo(Handler handler, Message m) {
		if (handlers.contains(handler)) {
			try {
				handler.getOutput().writeObject(m);
			} catch (IOException e) {
				LOGGER.error("Game " + id + ": ERROR in sending to " + handler.getUsername() + ": "
						+ m.getType() + ". Ex: " + e);
			}
		}
	}

    public synchronized void sendMessageToAll(Message message) {
        try {
            for (Handler h : handlers) {
                h.getOutput().writeObject(message);
            }
        } catch (IOException e) {
            LOGGER.error("Game " + id + ": Error sending " + message.getType()
					+ " message to all: " + e);
        }
    }

	public synchronized void sendStatusMessage() {
		LOGGER.info("Game " + id + ": Sending to all: STATUS");
		try {
            for (Handler h : handlers) {
                h.getOutput().writeObject(new MessageStatus(null, new HashMap<>(status)));
            }
        } catch (IOException e) {
            LOGGER.error("Game " + id + ": Error sending STATUS to all: " + e);
        }
    }

	public boolean containsUsername(String username) {
		for (Handler h : handlers) {
			if (username.equals(h.getUsername())) {
				return true;
			}
		}
		return false;
	}

	public synchronized void removeHandler(Handler handler) {
		if (handlers.contains(handler)) {
			handlers.remove(handler);
		}
	}

	public synchronized int freeSpots() {
        return Server.capacity - handlers.size();
    }

	public boolean isStarted() {
		return started;
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isPaused() {
		return paused;
	}
	
	public int getId() {
		return id;
	}
}