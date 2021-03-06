package eclipselogger.events.actions;

import java.sql.ResultSet;
import java.sql.SQLException;

import eclipselogger.db.ActionDB;
import eclipselogger.db.DynamicQuery;
import eclipselogger.resources.EclipseProject;

public class AddProjectAction extends EclipseAction {
	
	public static final String TABLE_NAME = "add_project";
	
	private final String projectName;
	
	public AddProjectAction(final long timeSinceLastAction, final EclipseAction previousAction, 
			final String recentActions, final int recentSameActionsCount, final EclipseProject project, final int packageDistance) {
		super(timeSinceLastAction, previousAction, recentActions, recentSameActionsCount, packageDistance);
		this.projectName = project.getName();
	}
	
	public AddProjectAction(final ResultSet rs) throws SQLException {
		super(rs);
		this.projectName = rs.getString(ActionDB.PROJECT_NAME);
	}
	
	public String getProjectName() {
		return this.projectName;
	}

	@Override
	public ActionType getActionType() {
		return ActionType.ADD_PROJECT;
	}

	public static DynamicQuery createQuery() {
		final DynamicQuery query = new DynamicQuery(TABLE_NAME, EclipseAction.createQuery());
		
		query.addColumnToSelect(ActionDB.PROJECT_NAME);
		query.setJoinColumn(ActionDB.ACTION_ID);
		query.setJoinColumnForJoinedTable(ActionDB.ECLIPSE_ACTION_ID);
		
		return query;
	}

	
	@Override
	public String toString() {
		return "Action: " + getActionType() + 
				", Project name: " + this.projectName;
	}
	
	@Override
	public String toStringForViewer() {
		return "Action: " + getActionType() + 
				", Project name: " + this.projectName + ", CONTEXT CHANGE: " + this.contextChange;
	}

}
