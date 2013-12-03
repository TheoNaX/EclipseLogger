package eclipselogger.sender;

import java.util.List;

import org.apache.log4j.Logger;

import eclipselogger.db.ActionLoader;
import eclipselogger.events.actions.EclipseAction;
import eclipselogger.utils.ConfigReader;

public class ActionFileSender implements Runnable {
	
	private final static Logger logger = Logger.getLogger(ActionFileSender.class);
	
	public static final int UNSENT = 1;
	public static final int SENT = 2;
	public static final int ERROR = 3;
	
	private final Thread runner;
	
	private boolean shouldStop = false;
	
	private final int SEND_INTERVAL = ConfigReader.getFilesSendIntervalSeconds() * 1000; // default 5 minutes

	private final ActionLoader dbActionLoader = new ActionLoader();
	// private final ActionUploaderIF sender = new RESTActionUploader();
	private final ActionUploaderIF sender;
	private final ActionFormatterIF formatter = new XMLActionFormatter();
	
	public ActionFileSender() throws Exception {
		this.sender = createSender();
		this.runner = new Thread(this, "ACTION_FILE_SENDER");
		this.runner.start();
	}
	
	private static ActionUploaderIF createSender() throws Exception {
		ActionUploaderIF sender = null;
		logger.debug("Going to create sender...");
		logger.debug("Configured sender type: " + ConfigReader.getFileUploaderType());
		final String senderType = ConfigReader.getFileUploaderType();
		if (senderType.equalsIgnoreCase(ActionUploaderIF.SFTP_SENDER)) {
			sender = new SFTPActionUploader();
			logger.debug("SFTP sender created");
		} else if (senderType.equalsIgnoreCase(ActionUploaderIF.LOCAL_SENDER)) {
			logger.debug("Before local sender creating");
			sender = new DummyLocalUploader();
			logger.debug("Local sender created");
		} else if (senderType.equalsIgnoreCase(ActionUploaderIF.REST_SENDER)) {
			sender = new RESTActionUploader();
			logger.debug("REST sender created");
		}
		
		return sender;
		
	}
	
	@Override
	public void run() {
		while (!this.shouldStop) {
			try {
				Thread.sleep(this.SEND_INTERVAL);
			} catch (final Exception ignore) {
				this.shouldStop = true;
				return;
			} 
			
			// process all unsent eclipse actions
			processUnsentEclipseActions();
		}
		
	}
	
	private synchronized void processUnsentEclipseActions() {
		List<EclipseAction> unsentActions = null;
		
		try {
			unsentActions = this.dbActionLoader.loadNotSentEclipseActions();
			if (unsentActions != null && !unsentActions.isEmpty()) {
				for (final EclipseAction action : unsentActions) {
					final String formattedAction = this.formatter.formatEclipseAction(action);
					try {
						logger.info("Going to upload action " + action.getActionType() + ", ID: " + action.getActionId());
						this.sender.uploadEclipseActionToServer(action.getActionId(), action.getTimestamp(), formattedAction);
						this.dbActionLoader.updateActionSendStatus(action.getActionId(), SENT);
						logger.info("Action uploaded successfully, ID: " + action.getActionId());
					} catch (final Exception e) {
						logger.error("Sending of action failed, ID: " + action.getActionId(), e);
					}
				}
			}
		} catch (final Exception e) {
			logger.error("Failed to load unsent actions", e);
		}
	}
	
	public synchronized void stopSenderThread() {
		try {
			this.shouldStop = true;
			this.runner.interrupt();
			this.runner.join(5000);
		} catch (final InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
