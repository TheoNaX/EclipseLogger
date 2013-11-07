package eclipselogger.sender;

import java.util.List;

import org.apache.log4j.Logger;

import eclipselogger.db.ActionLoader;
import eclipselogger.events.actions.EclipseAction;
import eclipselogger.utils.ConfigReader;

public class ActionFileSender implements Runnable {
	
	private final Logger logger = Logger.getLogger(ActionFileSender.class);
	
	public static final int UNSENT = 1;
	public static final int SENT = 2;
	public static final int ERROR = 3;
	
	private final Thread runner;
	
	private boolean shouldStop = false;
	
	private final int SEND_INTERVAL = 1 * 60 * 1000; // 10 minutes

	private final ActionLoader dbActionLoader = new ActionLoader();
	// private final ActionUploaderIF sender = new RESTActionUploader();
	private final ActionUploaderIF sender;
	private final ActionFormatterIF formatter = new XMLActionFormatter();
	
	public ActionFileSender() {
		this.sender = createSender();
		this.runner = new Thread(this, "ACTION_FILE_SENDER");
		this.runner.start();
	}
	
	private static ActionUploaderIF createSender() {
		ActionUploaderIF sender = null;
		if (ConfigReader.getFileUploaderType().equals(ActionUploaderIF.SFTP_SENDER)) {
			sender = new SFTPActionUploader();
		} else if (ConfigReader.getFileUploaderType().equals(ActionUploaderIF.LOCAL_SENDER)) {
			sender = new DummyLocalUploader();
		} else if (ConfigReader.getFileUploaderType().equals(ActionUploaderIF.REST_SENDER)) {
			sender = new RESTActionUploader();
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
						this.logger.info("Going to upload action " + action.getActionType() + ", ID: " + action.getActionId());
						this.sender.uploadEclipseActionToServer(formattedAction);
						this.dbActionLoader.updateActionSendStatus(action.getActionId(), SENT);
						this.logger.info("Action uploaded successfully, ID: " + action.getActionId());
					} catch (final Exception e) {
						this.logger.error("Sending of action failed, ID: " + action.getActionId(), e);
					}
				}
			}
		} catch (final Exception e) {
			this.logger.error("Failed to load unsent actions", e);
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
