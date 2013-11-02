package eclipselogger.events.actions;

import java.sql.ResultSet;
import java.sql.SQLException;

import eclipselogger.db.ActionDB;
import eclipselogger.db.DynamicQuery;

public abstract class EclipseAction {
	
	protected long timeSinceLastAction;
	protected int previousAction;
	protected int action_id;
	
	public static final String TABLE_NAME = "eclipse_action";
	
	public static final int SEND_STATUS_UNSENT = 1;
	public static final int SEND_STATUS_SENT = 2;
	
	public EclipseAction(final long timeSinceLastAction, final EclipseAction previousAction) {
		this.timeSinceLastAction = timeSinceLastAction;
		this.previousAction = previousAction.getActionType().getValue();
	}
	
	public EclipseAction(final ResultSet rs) throws SQLException {
		this.timeSinceLastAction = rs.getLong(ActionDB.TIME_SINCE_LAST);
		this.previousAction = rs.getInt(ActionDB.LAST_ACTION);
		this.action_id = rs.getInt(ActionDB.ECLIPSE_ACTION_ID);
	}
	
	public abstract ActionType getActionType(); 
	
	public long getTimeSinceLastAction() {
		return this.timeSinceLastAction;
	}
	
	public int getPreviousAction() {
		return this.previousAction;
	}
	
	public int getActionId() {
		return this.action_id;
	}
	
	public static DynamicQuery createQuery() {
		final DynamicQuery query = new DynamicQuery(TABLE_NAME);
		query.addColumnToSelect(ActionDB.ECLIPSE_ACTION_ID);
		query.addColumnToSelect(ActionDB.LAST_ACTION);
		query.addColumnToSelect(ActionDB.TIME_SINCE_LAST);
		query.addColumnToSelect(ActionDB.ACTION_TYPE);
		query.addColumnToSelect(ActionDB.CONTEXT);
		query.addColumnToSelect(ActionDB.TIMESTAMP);
		query.addColumnToSelect(ActionDB.LAST_ACTION);
		
		return query;
	}
}
