package eclipselogger.events.actions;

public abstract class EclipseAction {
	protected long timeSinceLastAction;
	protected EclipseAction previousAction;
	
	public static final String TABLE_NAME = "eclipse_action";
	
	public EclipseAction(long timeSinceLastAction, EclipseAction previousAction) {
		this.timeSinceLastAction = timeSinceLastAction;
		this.previousAction = previousAction;
	}
	
	public abstract ActionType getActionType(); 
	
	public long getTimeSinceLastAction() {
		return this.timeSinceLastAction;
	}
	
	public EclipseAction getPreviousAction() {
		return this.getPreviousAction();
	}
}
