package eclipselogger.events.actions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;

import eclipselogger.context.TaskContext;
import eclipselogger.db.ActionDB;
import eclipselogger.db.DynamicQuery;
import eclipselogger.events.EclipseResource;
import eclipselogger.utils.FileChanges;

/**
 * Abstract class representing each action executed in Eclipse IDE
 * Contains common parameters for all action types
 * Contains task context specific parameters
 * @author Tomas
 *
 */
public abstract class EclipseAction {
	
	private static Logger logger = Logger.getLogger(EclipseAction.class);
	
	protected long timeSinceLastAction;
	protected int previousAction;
	protected int action_id;
	protected boolean contextChange;
	protected Date timestamp;
	protected String recentActions;
	protected int recentSameActionsCount;
	
	protected int packageDistanceFromLastAction;
	protected EclipseResource resource;
	
	// task context specific features
	protected FileChanges mostRecentFileChanges;
	protected FileChanges averageFileChanges;
	protected double averagePackageDistanceDiff;
	protected double averagePackageDistanceDiffForAction;
	protected int maxPackageDistanceDiff;
	protected int minPackageDistanceDiff;
	
	protected long averageDurationDiffForAction;
	protected long averageDurationDiff;
	protected long maxDurationDiff;
	protected long minDurationDiff;
	
	protected int sameActionsCountInContext;
	protected double sameActionsRatio;
	protected long timeSinceLastSameAction;
	protected int sameActionsTransitionsCount;
	protected double sameActionsTransitionsRatio;
	
	protected int resourcesWorkedBefore;
	protected int packagesWorkedBefore;
	
	protected int packagesUsedCount;
	protected int resourcesUsedCount;
	
	public static final String TABLE_NAME = "eclipse_action";
	
	public static final int SEND_STATUS_UNSENT = 1;
	public static final int SEND_STATUS_SENT = 2;
	
	private EclipseAction lastAction;
	
	/**
	 * Constructor used by Eclipse IDE event listeners when actions are executed 
	 * @param timeSinceLastAction Time since previous action in milliseconds
	 * @param previousAction Previous action
	 * @param recentActions String containing comma separated list of recent actions
	 * @param recentSameActionsCount Count of recent actions of the same type
	 * @param packageDistance Package tree distance from the last action
	 */
	public EclipseAction(final long timeSinceLastAction, final EclipseAction previousAction, final String recentActions, final int recentSameActionsCount, final int packageDistance) {
		this.timeSinceLastAction = timeSinceLastAction;
		this.previousAction = (previousAction != null) ? previousAction.getActionType().getValue() : 0;
		this.timestamp = new Date();
		this.recentActions = recentActions;
		this.recentSameActionsCount = recentSameActionsCount;
		this.packageDistanceFromLastAction = packageDistance;
		this.lastAction = previousAction;
	}
	
	/**
	 * Constructor used by database Eclipse action loader, creates Eclipse action from ResultSet
	 * @param rs ResultSet containing select from eclipse_action table and set to a non null row
	 * @throws SQLException
	 */
	public EclipseAction(final ResultSet rs) throws SQLException {
		this.timeSinceLastAction = rs.getLong(ActionDB.TIME_SINCE_LAST);
		this.previousAction = rs.getInt(ActionDB.LAST_ACTION);
		this.action_id = rs.getInt(ActionDB.ECLIPSE_ACTION_ID);
		this.contextChange = rs.getBoolean(ActionDB.CONTEXT);
		this.timestamp = rs.getTimestamp(ActionDB.TIMESTAMP);
		this.recentActions = rs.getString(ActionDB.RECENT_ACTIONS);
		this.recentSameActionsCount = rs.getInt(ActionDB.RECENT_SAME_ACTIONS_COUNT);
		this.packageDistanceFromLastAction = rs.getInt(ActionDB.PACKAGE_DISTANCE);
		
		// context
		this.mostRecentFileChanges = getRecentFileChangesFromResultSet(rs);
		this.averageFileChanges = getAverageFileChangesFromResultSet(rs);
		
		this.averagePackageDistanceDiff = rs.getDouble(ActionDB.AVERAGE_PACKAGE_DIFF);
		this.averagePackageDistanceDiffForAction = rs.getDouble(ActionDB.AVERAGE_PACKAGE_ACTION_DIFF);
		this.maxPackageDistanceDiff = rs.getInt(ActionDB.MAX_PACKAGE_DIFF);
		this.minPackageDistanceDiff = rs.getInt(ActionDB.MIN_PACKAGE_DIFF);
		
		this.averageDurationDiff = rs.getLong(ActionDB.AVERAGE_DURATION_DIFF);
		this.averageDurationDiffForAction = rs.getLong(ActionDB.AVERAGE_DURATION_ACTION_DIFF);
		this.minDurationDiff = rs.getLong(ActionDB.MIN_DURATION_DIFF);
		this.maxDurationDiff = rs.getLong(ActionDB.MAX_DURATION_DIFF);
		
		this.sameActionsCountInContext = rs.getInt(ActionDB.CONTEXT_SAME_ACTIONS);
		this.sameActionsRatio = rs.getDouble(ActionDB.SAME_ACTIONS_RATIO);
		this.timeSinceLastSameAction = rs.getLong(ActionDB.TIME_SINCE_LAST_SAME);
		this.sameActionsTransitionsCount = rs.getInt(ActionDB.SAME_TRANSITIONS_COUNT);
		this.sameActionsTransitionsRatio = rs.getDouble(ActionDB.SAME_TRANSITIONS_RATIO);
		
		this.packagesUsedCount = rs.getInt(ActionDB.TOTAL_PACKAGES);
		this.resourcesUsedCount = rs.getInt(ActionDB.TOTAL_RESOURCES);
		this.packagesWorkedBefore = rs.getInt(ActionDB.PACKAGE_WORKED_BEFORE);
		this.resourcesWorkedBefore = rs.getInt(ActionDB.RESOURCE_WORKED_BEFORE);
				
	}
	
	private static FileChanges getRecentFileChangesFromResultSet(final ResultSet rs) throws SQLException {
		final int added = rs.getInt(ActionDB.RECENT_LINES_ADDED);
		final int changed = rs.getInt(ActionDB.RECENT_LINES_CHANGED);
		final int deleted = rs.getInt(ActionDB.RECENT_LINES_DELETED);
		
		final FileChanges changes = new FileChanges(changed, deleted, added);
		return changes;
	}
	
	private static FileChanges getAverageFileChangesFromResultSet(final ResultSet rs) throws SQLException {
		final int added = rs.getInt(ActionDB.AVERAGE_LINES_ADDED);
		final int changed = rs.getInt(ActionDB.AVERAGE_LINES_CHANGED);
		final int deleted = rs.getInt(ActionDB.AVERAGE_LINES_DELETED);
		
		final FileChanges changes = new FileChanges(changed, deleted, added);
		return changes;
	}
	
	/**
	 * Must be implemented by all Eclipse actions
	 * @return Type of action
	 */
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
	
	/**
	 * Creates DynamicQuery used for select from database from eclipse_action table
	 * Adds to query all common and context specific parameters
	 * @return DynamicQuery with select of all common and context columns
	 */
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
		query.addColumnToSelect(ActionDB.PACKAGE_DISTANCE);
		
		// context specific
		query.addColumnToSelect(ActionDB.TOTAL_PACKAGES);
		query.addColumnToSelect(ActionDB.TOTAL_RESOURCES);
		query.addColumnToSelect(ActionDB.PACKAGE_WORKED_BEFORE);
		query.addColumnToSelect(ActionDB.RESOURCE_WORKED_BEFORE);
		
		query.addColumnToSelect(ActionDB.RECENT_LINES_CHANGED);
		query.addColumnToSelect(ActionDB.RECENT_LINES_ADDED);
		query.addColumnToSelect(ActionDB.RECENT_LINES_DELETED);
		query.addColumnToSelect(ActionDB.AVERAGE_LINES_ADDED);
		query.addColumnToSelect(ActionDB.AVERAGE_LINES_CHANGED);
		query.addColumnToSelect(ActionDB.AVERAGE_LINES_DELETED);
		
		query.addColumnToSelect(ActionDB.AVERAGE_PACKAGE_DIFF);
		query.addColumnToSelect(ActionDB.AVERAGE_PACKAGE_ACTION_DIFF);
		query.addColumnToSelect(ActionDB.MAX_PACKAGE_DIFF);
		query.addColumnToSelect(ActionDB.MIN_PACKAGE_DIFF);
		
		query.addColumnToSelect(ActionDB.AVERAGE_DURATION_DIFF);
		query.addColumnToSelect(ActionDB.AVERAGE_DURATION_ACTION_DIFF);
		query.addColumnToSelect(ActionDB.MAX_DURATION_DIFF);
		query.addColumnToSelect(ActionDB.MIN_DURATION_DIFF);
		
		query.addColumnToSelect(ActionDB.CONTEXT_SAME_ACTIONS);
		query.addColumnToSelect(ActionDB.SAME_ACTIONS_RATIO);
		query.addColumnToSelect(ActionDB.TIME_SINCE_LAST_SAME);
		query.addColumnToSelect(ActionDB.SAME_TRANSITIONS_COUNT);
		query.addColumnToSelect(ActionDB.SAME_TRANSITIONS_RATIO);
		
		return query;
	}
	
	public Date getTimestamp() {
		return this.timestamp;
	}
	
	public EclipseResource getResource() {
		return this.resource;
	}
	
	public void setResource(final EclipseResource resource) {
		this.resource = resource;
	}
	
	/**
	 * Applies actual task context to Eclipse action
	 * Sets all context specific parameters
	 * @param context Actual task context
	 */
	public void applyContext(final TaskContext context) {
		this.mostRecentFileChanges = context.getLastFileChanges();
		this.averageFileChanges = context.getAverageFileChanges();
		
		applyContextToPackageDistances(context);
		
		applyContextToDurations(context);
		
		applyContextToActionCounts(context);
		
		applyContextToResources(context);
	}
	
	private void applyContextToPackageDistances(final TaskContext context) {
		final double avgDistance = context.getAveragePackageDistance();
		logger.debug(">>>>>> package distance from last action: " + this.packageDistanceFromLastAction);
		logger.debug(">>>>> average package distance: " + avgDistance);
		this.averagePackageDistanceDiff = (avgDistance >= this.packageDistanceFromLastAction) ? (avgDistance - this.packageDistanceFromLastAction) : (this.packageDistanceFromLastAction - avgDistance);
		logger.debug(">>>>>>>> averagePackageDistanceDiff: " + this.averagePackageDistanceDiff);
		
		final double avgForAction = context.getAveragePackageDistanceForAction(getActionType());
		logger.debug(">>>>> average package distance for action: " + avgForAction);
		this.averagePackageDistanceDiffForAction = (avgForAction >= this.packageDistanceFromLastAction) ? (avgForAction - this.packageDistanceFromLastAction) : (this.packageDistanceFromLastAction - avgForAction);
		logger.debug(">>>>>>> averagePackageDistanceDiffForAction: " + this.averagePackageDistanceDiffForAction);
		
		final int maxPackageDistance = context.getMaxPackageDistance();
		this.maxPackageDistanceDiff = (maxPackageDistance >= this.packageDistanceFromLastAction) ? (maxPackageDistance - this.packageDistanceFromLastAction) : (this.packageDistanceFromLastAction - maxPackageDistance);
		
		final int minPackageDistance = context.getMinPackageDistance();
		this.minPackageDistanceDiff = (minPackageDistance >= this.packageDistanceFromLastAction) ? (minPackageDistance - this.packageDistanceFromLastAction) : (this.packageDistanceFromLastAction - minPackageDistance);
	}
	
	
	private void applyContextToDurations(final TaskContext context) {
		final long avgDuration = context.getAverageDuration();
		this.averageDurationDiff = (avgDuration >= this.timeSinceLastAction) ? (avgDuration - this.timeSinceLastAction) : (this.timeSinceLastAction - avgDuration);
		
		final long avgForAction = context.getAverageDurationForAction(getActionType());
		this.averageDurationDiffForAction = (avgForAction >= this.timeSinceLastAction) ? (avgForAction - this.timeSinceLastAction) : (this.timeSinceLastAction - avgForAction);
		
		final long maxDuration = context.getMaxActionDuration();
		this.maxDurationDiff = (maxDuration >= this.timeSinceLastAction) ? (maxDuration - this.timeSinceLastAction) : (this.timeSinceLastAction - maxDuration);
		
		final long minDuration = context.getMinActionDuration();
		this.minDurationDiff = (minDuration >= this.timeSinceLastAction) ? (minDuration - this.timeSinceLastAction) : (this.timeSinceLastAction - minDuration);
	}
	
	
	private void applyContextToActionCounts(final TaskContext context) {
		if (context.getTotalActionsCount() > 0) {
			this.sameActionsCountInContext = context.getSameActionsCount(getActionType());
			this.sameActionsRatio = (double) this.sameActionsCountInContext / context.getTotalActionsCount();
		}

		final EclipseAction lastSameAction = context.getLastSameAction(getActionType());
		if (lastSameAction != null) {
			this.timeSinceLastSameAction = this.timestamp.getTime() - lastSameAction.getTimestamp().getTime();
		}
		
		if (this.lastAction != null) {
			this.sameActionsTransitionsCount = context.getSameActionsTransitions(this.lastAction.getActionType(), getActionType());
			final int totalTransitions = (context.getTotalActionsCount() - 2 > 1) ? context.getTotalActionsCount() - 2 : 1;
			this.sameActionsTransitionsRatio = (double) this.sameActionsTransitionsCount / totalTransitions;
		} 
		
	}
	
	private void applyContextToResources(final TaskContext context) {
		this.resourcesUsedCount = context.getTotalReources();
		this.packagesUsedCount = context.getTotalPackages();
		
		this.packagesWorkedBefore = context.packageWorkedWithAlready(this.resource);
		this.resourcesWorkedBefore = context.resourceWorkedWithAlready(this.resource);
		
		
	}

	public int getPackageDistanceFromLastAction() {
		return this.packageDistanceFromLastAction;
	}

	public FileChanges getMostRecentFileChanges() {
		return this.mostRecentFileChanges;
	}

	public FileChanges getAverageFileChanges() {
		return this.averageFileChanges;
	}

	public double getAveragePackageDistanceDiff() {
		return this.averagePackageDistanceDiff;
	}

	public double getAveragePackageDistanceDiffForAction() {
		return this.averagePackageDistanceDiffForAction;
	}

	public int getMaxPackageDistanceDiff() {
		return this.maxPackageDistanceDiff;
	}

	public int getMinPackageDistanceDiff() {
		return this.minPackageDistanceDiff;
	}

	public long getAverageDurationDiffForAction() {
		return this.averageDurationDiffForAction;
	}

	public long getAverageDurationDiff() {
		return this.averageDurationDiff;
	}

	public long getMaxDurationDiff() {
		return this.maxDurationDiff;
	}

	public long getMinDurationDiff() {
		return this.minDurationDiff;
	}

	public int getSameActionsCountInContext() {
		return this.sameActionsCountInContext;
	}

	public double getSameActionsRatio() {
		return this.sameActionsRatio;
	}

	public long getTimeSinceLastSameAction() {
		return this.timeSinceLastSameAction;
	}

	public int getSameActionsTransitionsCount() {
		return this.sameActionsTransitionsCount;
	}

	public double getSameActionsTransitionsRatio() {
		return this.sameActionsTransitionsRatio;
	}
	
	public EclipseAction getLastAction() {
		return this.lastAction;
	}

	public int getResourcesWorkedBefore() {
		return this.resourcesWorkedBefore;
	}

	public int getPackagesWorkedBefore() {
		return this.packagesWorkedBefore;
	}

	public int getPackagesUsedCount() {
		return this.packagesUsedCount;
	}

	public int getResourcesUsedCount() {
		return this.resourcesUsedCount;
	}
	
	
	public abstract String toStringForViewer();
	
	
}
