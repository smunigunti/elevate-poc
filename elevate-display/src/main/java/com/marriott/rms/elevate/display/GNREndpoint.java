package com.marriott.rms.elevate.display;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mariott.rms.elevate.display.domain.GNRCount;

@ServerEndpoint("/gnr")
public class GNREndpoint {

	private static final org.slf4j.Logger LOG = LoggerFactory
			.getLogger(GNREndpoint.class);

	private static final int UPDATE_INTERVAL = 30;

	static ScheduledExecutorService timer = Executors
			.newSingleThreadScheduledExecutor();

	DBService dbService = new DBService();
	
	private final GsonBuilder gsonBuilder = new GsonBuilder();

	@OnOpen
	public void init(Session session) throws IOException {
		LOG.info("New Request Received from " + session.getId());
		//session.getBasicRemote().sendText("Connection Established");
		timer.scheduleAtFixedRate(() -> update(session), 10, UPDATE_INTERVAL,
				TimeUnit.SECONDS);
	}

	private void update(Session session) {

		List<GNRCount> data = dbService.readMessages();
		//LOG.info("GNR Property/Count: " + data);
		
		Gson gson = gsonBuilder.setPrettyPrinting().create();
		
		Set<Session> allSessions = session.getOpenSessions();

		allSessions.stream().forEach(
			openSession -> {
				try {
					if (data!=null && data.size()>0){
						LOG.info("Sending data to browser");
						openSession.getBasicRemote().sendText(gson.toJson(data));
					}
				} catch (IOException e) {
					LOG.info("Error While Sending text to client: "
							+ e.getMessage(), e);
				}

			});
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		LOG.info("Message from " + session.getId() + ": " + message);
		try {
			session.getBasicRemote().sendText(message);
		} catch (IOException ex) {
			LOG.info("Error While Sending text to client: " + ex.getMessage(), ex);
		}
	}

	@OnClose
	public void onClose(Session session) {
		LOG.info("Session " + session.getId() + " has ended");
	}

}
