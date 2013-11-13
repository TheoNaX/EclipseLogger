package eclipselogger.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import eclipselogger.events.actions.ActionType;
import eclipselogger.events.actions.EclipseAction;

public class ActionsCache {
	private final List<EclipseAction> actions = new LinkedList<EclipseAction>();
	
	private final int MAX_SIZE = 50;
	
	public void addEclipseActionToChache(final EclipseAction action) {
		if (this.actions.size() == this.MAX_SIZE) {
			this.actions.remove(0);
		}
		this.actions.add(action);
	}
	
	public List<EclipseAction> getAllActionsInChache() {
		return this.actions;
	}
	
	public List<EclipseAction> getRecentActionsWithSameType(final ActionType type) {
		final int seconds = ConfigReader.getRecentSameTypeActionsCountInterval();
		final List<EclipseAction> list = new ArrayList<EclipseAction>();
		final long currentTime = System.currentTimeMillis();
		
		for (final EclipseAction action : this.actions) {
			if (action.getActionType() == type) {
				final Date actionTimeStamp = action.getTimestamp();
				if (actionTimeStamp.getTime() >= currentTime - seconds * 1000) {
					list.add(action);
				}
			}
		}
		
		return list;
	}
	
	public String getLastActionsForEclipseAction() {
		final StringBuilder sb = new StringBuilder();
		final int maxLastCount = ConfigReader.getCountOfLastActions();
		final int actionsCount = this.actions.size();
		int count = 0;
		for (int i=actionsCount - 1; i>=0; i--) {
			final EclipseAction action = this.actions.get(i);
			sb.append(action.getActionType().getValue());
			sb.append(",");
			count++;
			if (count == maxLastCount) {
				break;
			}
		}
		
		final String actionsAsString = sb.toString();
		if (actionsAsString.length() > 2) {
			return actionsAsString.substring(0, actionsAsString.length()-1);
		} else {
			return "";
		}
	}
}
