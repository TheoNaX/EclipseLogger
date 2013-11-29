package eclipselogger.events.actions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.eclipse.core.resources.IResource;

import eclipselogger.db.ActionDB;
import eclipselogger.db.DynamicQuery;

public abstract class EclipseAction {
	
	protected long timeSinceLastAction;
	protected int previousAction;
	protected int action_id;
	private boolean contextChange;
	protected Date timestamp;
	protected String recentActions;
	protected int recentSameActionsCount;
	
	protected int packageDistanceFromLastAction;
	protected IResource resource;
	
	public static final String TABLE_NAME = "eclipse_action";
	
	public static final int SEND_STATUS_UNSENT = 1;
	public static final int SEND_STATUS_SENT = 2;
	
	public EclipseAction(final long timeSinceLastAction, final EclipseAction previousAction, final String recentActions, final int recentSameActionsCount, final int packageDistance) {
		this.timeSinceLastAction = timeSinceLastAction;
		this.previousAction = (previousAction != null) ? previousAction.getActionType().getValue() : 0;
		this.timestamp = new Date();
		this.recentActions = recentActions;
		this.recentSameActionsCount = recentSameActionsCount;
		this.packageDistanceFromLastAction = packageDistance;
	}
	
	public EclipseAction(final ResultSet rs) throws SQLException {
		this.timeSinceLastAction = rs.getLong(ActionDB.TIME_SINCE_LAST);
		this.previousAction = rs.getInt(ActionDB.LAST_ACTION);
		this.action_id = rs.getInt(ActionDB.ECLIPSE_ACTION_ID);
		this.contextChange = rs.getBoolean(ActionDB.CONTEXT);
		this.timestamp = rs.getDate(ActionDB.TIMESTAMP);
		this.recentActions = rs.getString(ActionDB.RECENT_ACTIONS);
		this.recentSameActionsCount = rs.getInt(ActionDB.RECENT_SAME_ACTIONS_COUNT);
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
	
	public boolean getContextChange() {
		return this.contextChange;
	}
	
	public String getRecentActions() {
		return this.recentActions;
	}
	
	public int getRecentSameActionsCount() {
		return this.recentSameActionsCount;
	}
	
	public static DynamicQuery createQuery() {
		final DynamicQuery query = new DynamicQuery(TABLE_NAME);
		query.addColumnToSelect(ActionDB.ECLIPSE_ACTION_ID);
		query.addColumnToSelect(ActionDB.TIME_SINCE_LAST);
		query.addColumnToSelect(ActionDB.ACTION_TYPE);
		query.addColumnToSelect(ActionDB.CONTEXT);
		query.addColumnToSelect(ActionDB.TIMESTAMP);
		query.addColumnToSelect(ActionDB.LAST_ACTION);
		query.addColumnToSelect(ActionDB.RECENT_ACTIONS);
		query.addColumnToSelect(ActionDB.RECENT_SAME_ACTIONS_COUNT);
		
		return query;
	}
	
	public Date getTimestamp() {
		return this.timestamp;
	}
	
	public IResource getResource() {
		return this.resource;
	}
	
	public void setResource(final IResource resource) {
		this.resource = resource;
	}
	
	public int getPackageDistance() {
		return this.packageDistanceFromLastAction;
	}
}
