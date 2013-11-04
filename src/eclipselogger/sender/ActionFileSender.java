package eclipselogger.sender;

import java.util.List;

import eclipselogger.db.ActionLoader;
import eclipselogger.events.actions.EclipseAction;

public class ActionFileSender implements Runnable {
	
	public static final int UNSENT = 1;
	public static final int SENT = 2;
	public static final int ERROR = 3;
	
	private final Thread runner;
	
	private boolean shouldStop = false;
	
	private final int SEND_INTERVAL = 10 * 60 * 1000; // 10 minutes

	private final ActionLoader dbActionLoader = new ActionLoader();
	private final ActionUploaderIF sender = new RESTActionUploader();
	private final ActionFormatterIF formatter = new XMLActionFormatter();
	
	public ActionFileSender() {
		this.runner = new Thread(this, "ACTION_FILE_SENDER");
		this.runner.start();
	}
	
	@Override
	public void run() {
		while (!this.shouldStop) {
			try {
				Thread.sleep(this.SEND_INTERVAL);
			} catch (final Exception ignore) {} // TODO maybe some check of last execution??
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
						System.out.println(">>>>>> Sending action: " + action);
						this.sender.uploadEclipseActionToServer(formattedAction);
						this.dbActionLoader.updateActionSendStatus(action.getActionId(), SENT);
					} catch (final Exception e) {
						System.err.println("Eclipse action with ID: " + action.getActionId() + " sending failed!!!");
						e.printStackTrace();
					}
				}
			}
		} catch (final Exception e) {
			// TODO exception handling
			e.printStackTrace();
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
