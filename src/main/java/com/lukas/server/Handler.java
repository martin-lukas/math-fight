package com.lukas.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lukas.protocol.EMessageType;
import com.lukas.protocol.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Handler for managing client socket and thread.
 *
 * @author Martin Lukáš
 */
public class Handler extends Thread {
	public static final Logger LOGGER = LoggerFactory.getLogger(Handler.class);

	private final Socket socket;

	private ObjectInputStream in;
	private ObjectOutputStream out;

	private String username;
	private Game game;
	private int gameId;

	public Handler(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());

			while (true) {
				Message loginMessage = null;
				try {
					loginMessage = (Message) in.readObject();
				} catch (ClassNotFoundException ex) {
					LOGGER.error("Class Message was not serializable "
							+ "enough: " + ex);
					return;
				}
				if (loginMessage == null) {
					LOGGER.info("Receiving (login): NULL");
					return;
				}
				if (loginMessage.getType().equals(EMessageType.LOGIN)) {
					LOGGER.info("Receiving: LOGIN");
					username = loginMessage.getContent()[0];
				}
				gameId = Server.addToGame(this);
				if (gameId != 0) {
					break;
				}
				LOGGER.error("Sending to " + username + ": ERROR - login");
				out.writeObject(new Message(EMessageType.ERROR, "login"));
			}

			game = Server.getGameById(gameId);

			// Listening to client and reacting.
			while (game.isRunning()) {
				if (game.isPaused()) {
					continue;
				}
				Message input = null;
				try {
					input = (Message) in.readObject();
				} catch (ClassNotFoundException ex) {
					LOGGER.error(username + ": Cannot cast received object " + "to Message: " + ex);
				}
				if (input == null) {
					game.LOGGER.error("Game " + gameId + ": Receiving from " + username + ": NULL");
					return;
				}
				switch (input.getType()) {
					case QUIT:
						game.LOGGER.info("Game " + gameId + ": Receiving from " + username + ": QUIT");
						game.removeHandler(this);
						if (game.isStarted()) {
							game.endGame();
						}
						return;
					case ANSWER:
						game.LOGGER.info("Game " + gameId + ": Receiving from " + username
								+ ": ANSWER, " + input.getContent()[0]);
						game.processAnswerFromHandler(this, 
								input.getContent()[0]);
				}
			}
		} catch (IOException e) {
			LOGGER.error("Client closed. Disconnecting...: " + e);
		} catch (Exception e) {
			LOGGER.error("Unexpected ending: " + e);
		}
		finally {
			if (out != null) {
				Server.getOutputs().remove(out);
			}
			try {
				socket.close();
			} catch (IOException e) {
                LOGGER.error("Client socket could not be closed:" + e);
            }
		}
	}

	public String getUsername() {
		return username;
	}

	public ObjectOutputStream getOutput() {
		return out;
	}
}