package eclipselogger.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import eclipselogger.events.actions.ActionType;
import eclipselogger.events.actions.EclipseAction;
import eclipselogger.utils.FileChanges;

public class TaskContext {
	
	private static Logger logger = Logger.getLogger(TaskContext.class);
	
	private final HashMap<ActionType, Integer> actionsCounter = new HashMap<ActionType, Integer>();
	private int totalActionsCount;
	
	private final List<EclipseAction> contextActions = new ArrayList<EclipseAction>();
	
	//private final HashMap<ActionType, FileChanges> fileChangesPerAction = new HashMap<ActionType, FileChanges>();
	
	private final HashMap<ActionType, Long> averageDurationPerAction = new HashMap<ActionType, Long>();
	private final HashMap<ActionType, Long> totalDurationPerAction = new HashMap<ActionType, Long>();
	private long lastActionDuration;
	private long maxActionDuration;
	private long minActionDuration;
	private long averageDuration;
	private long totalDuration;
	
	private double averagePackageDistance;
	private int totalPackageDistances;
	private int maxPackageDistance;
	private int minPackageDistance;
	
	private final HashMap<ActionType, Double> averagePackageDistancePerAction = new HashMap<ActionType, Double>();
	private final HashMap<ActionType, Integer> totalPackageDistancePerAction = new HashMap<ActionType, Integer>();
	
	private final FileChanges overallFileChanges = new FileChanges();
	private FileChanges lastFileChanges;
	private FileChanges averageFileChanges;
	
	
	public void updateContextWithAction(final EclipseAction action) {
		logger.debug("Starting to update task context with action:");
		logger.debug(action.toString());
		this.totalActionsCount++;
		this.contextActions.add(action);
		updateCounter(action);
		updateDurations(action);
		updatePackageDistances(action);
		logger.debug("Update of context ended successfully");
		
	}
	
	private void updateCounter(final EclipseAction action) {
		Integer actual = this.actionsCounter.get(action.getActionType());
		if (actual != null) {
			logger.debug("Action already existed in context, increasing number. Before increase: " + actual);
			actual++;
			logger.debug("Count increased. After increase: " + actual);
		} else {
			logger.debug("First time action in task context, inserting into map ...");
			this.actionsCounter.put(action.getActionType(), new Integer(1));
		}
	}
	
	private void updateDurations(final EclipseAction action) {
		this.lastActionDuration = action.getTimeSinceLastAction();
		if (this.lastActionDuration > this.maxActionDuration) {
			this.maxActionDuration = this.lastActionDuration;
		}
		if (this.lastActionDuration < this.minActionDuration) {
			this.minActionDuration = this.lastActionDuration;
		}
		this.totalDuration += action.getTimeSinceLastAction();
		this.averageDuration = this.totalDuration / this.totalActionsCount;
		
		Long totalActionsDuration = this.totalDurationPerAction.get(action.getActionType());
		if (totalActionsDuration != null) {
			logger.debug("Action already existed, total actions duration before: " + totalActionsDuration);
			totalActionsDuration += action.getTimeSinceLastAction();
			final Integer counter = this.actionsCounter.get(action.getActionType());
			final long average = totalActionsDuration / counter;
			this.averageDurationPerAction.put(action.getActionType(), average);
			logger.debug("Total duration for action updated, after update: " + this.totalDurationPerAction.get(action.getActionType()));
		} else {
			logger.debug("First action of this type in task context");
			totalActionsDuration = action.getTimeSinceLastAction();
			this.totalDurationPerAction.put(action.getActionType(), totalActionsDuration);
			this.averageDurationPerAction.put(action.getActionType(), totalActionsDuration);
		}
		
	}
	
	private void updatePackageDistances(final EclipseAction action) {
		final int packageDistance = action.getPackageDistance();
		if (packageDistance > this.maxPackageDistance) {
			this.maxPackageDistance = packageDistance;
		}
		if (packageDistance < this.minPackageDistance) {
			this.minPackageDistance = packageDistance;
		}
		this.totalPackageDistances += action.getPackageDistance();
		this.averagePackageDistance = this.totalPackageDistances / this.totalActionsCount;
		
		
		Integer totalActionDistance = this.totalPackageDistancePerAction.get(action.getActionType());
		if (totalActionDistance != null) {
			totalActionDistance += action.getPackageDistance();
			final Integer counter = this.actionsCounter.get(action.getActionType());
			final double average = totalActionDistance / counter;
			this.averagePackageDistancePerAction.put(action.getActionType(), average);
		} else {
			totalActionDistance = action.getPackageDistance();
			this.totalPackageDistancePerAction.put(action.getActionType(), totalActionDistance);
			this.averagePackageDistancePerAction.put(action.getActionType(), (double)totalActionDistance);
		}
		
		
	}
	
	public void updateFileChanges(final FileChanges changes) {
		this.overallFileChanges.updateFileChanges(changes);
		this.lastFileChanges = changes;
		final int deleted = (this.overallFileChanges.getDeletedLines()) / (this.totalActionsCount+1);
		final int added = (this.overallFileChanges.getAddedLines()) / (this.totalActionsCount+1);
		final int changed = (this.overallFileChanges.getChangedLines()) / (this.totalActionsCount+1);
		this.averageFileChanges = new FileChanges(changed, deleted, added);
	}

	public int getTotalActionsCount() {
		return this.totalActionsCount;
	}

	public HashMap<ActionType, Long> getTotalDurationPerAction() {
		return this.totalDurationPerAction;
	}

	public long getMaxActionDuration() {
		return this.maxActionDuration;
	}

	public long getMinActionDuration() {
		return this.minActionDuration;
	}

	public long getAverageDuration() {
		return this.averageDuration;
	}

	public long getTotalDuration() {
		return this.totalDuration;
	}

	public double getAveragePackageDistance() {
		return this.averagePackageDistance;
	}

	public int getTotalPackageDistances() {
		return this.totalPackageDistances;
	}

	public int getMaxPackageDistance() {
		return this.maxPackageDistance;
	}

	public int getMinPackageDistance() {
		return this.minPackageDistance;
	}

	public HashMap<ActionType, Integer> getTotalPackageDistancePerAction() {
		return this.totalPackageDistancePerAction;
	}

	public FileChanges getOverallFileChanges() {
		return this.overallFileChanges;
	}

	public FileChanges getLastFileChanges() {
		return this.lastFileChanges;
	}
	
	public FileChanges getAverageFileChanges() {
		return this.averageFileChanges;
	}
	
	public List<EclipseAction> getContextActions() {
		return this.contextActions;
	}
	
	public long getAverageDurationForAction(final ActionType type) {
		final Long duration = this.averageDurationPerAction.get(type);
		if (duration != null) {
			return duration;
		} else {
			return 0L;
		}
	}
	
	public double getAveragePackageDistanceForAction(final ActionType type) {
		final Double distance = this.averagePackageDistancePerAction.get(type);
		if (distance != null) {
			return distance;
		} else {
			return 0.0;
		}
	}
	
	public EclipseAction getLastSameAction(final ActionType type) {
		EclipseAction action = null;
		final int count = this.contextActions.size();
		for (int i=count-1; i>=0; i--) {
			final EclipseAction a = this.contextActions.get(i);
			if (a.getActionType() == type) {
				action = a;
				break;
			}
		}
		
		return action;
	}
	
	public int getSameActionsCount(final ActionType type) {
		int count = 0;
		for (int i=0; i<this.contextActions.size(); i++) {
			if (this.contextActions.get(i).getActionType() == type) {
				count++;
			}
		}
		
		return count;
	}
	
	public int getSameActionsTransitions(final ActionType first, final ActionType second) {
		int count = 0;
		final int actionsCount = this.contextActions.size();
		for (int i=0; i<actionsCount-1; i++) {
			if (this.contextActions.get(i).getActionType() == first && this.contextActions.get(i+1).getActionType() == second) {
				count++;
			}
		}
		
		return count;
	}
}
