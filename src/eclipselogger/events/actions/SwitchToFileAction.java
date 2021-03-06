package eclipselogger.events.actions;

import java.sql.ResultSet;
import java.sql.SQLException;

import eclipselogger.db.ActionDB;
import eclipselogger.db.DynamicQuery;
import eclipselogger.resources.EclipseFile;
import eclipselogger.utils.FileValidator;
import eclipselogger.utils.PackageUtils;

public class SwitchToFileAction extends EclipseAction {
	
	public static final String TABLE_NAME = "switch_file";
	
	private final String switchedToFile;
	private String previousFile;
	private final boolean samePackage;
	private final boolean sameProject;
	private final boolean sameFileType;
	
	
	public SwitchToFileAction(final long timeSinceLastAction, final EclipseAction previousAction, final String recentActions, 
			final int recentSameActionsCount, final EclipseFile openedFile, final EclipseFile previousFile, final int packageDistance) {
		super(timeSinceLastAction, previousAction, recentActions, recentSameActionsCount, packageDistance);
		this.switchedToFile = openedFile.getProjectRelativePath();
		if (previousFile != null) {
			this.previousFile = previousFile.getProjectRelativePath();
		}
		this.samePackage = PackageUtils.checkIfSamePackage(openedFile, previousFile);
		this.sameProject = PackageUtils.checkIfSameProject(openedFile, previousFile);
		this.sameFileType = FileValidator.haveFilesTheSameExtension(openedFile, previousFile);
	}
	
	public SwitchToFileAction(final ResultSet rs) throws SQLException {
		super(rs);
		this.previousFile = rs.getString(ActionDB.PREVIOUS_FILE);
		this.switchedToFile = rs.getString(ActionDB.SWITCHED_FILE);
		this.samePackage = rs.getBoolean(ActionDB.SAME_PACKAGE);
		this.sameProject = rs.getBoolean(ActionDB.SAME_PROJECT);
		this.sameFileType = rs.getBoolean(ActionDB.SAME_TYPE);
	}
	
	public boolean openedInSamePackage() {
		return this.samePackage;
	}
	
	public String getSwitchedToFile() {
		return this.switchedToFile;
	}
	
	public String getPreviousOpenedFile() {
		return this.previousFile;
	}
	
	public boolean openedInSameProject() {
		return this.sameProject;
	}
	
	public boolean isTheSameTypeAsPreviuos() {
		return this.sameFileType;
	}
	
	@Override
	public ActionType getActionType() {
		return ActionType.SWITCH_FILE;
	}
	
	@Override
	public String toString() {
		return "Action: " + getActionType() + 
				", same package: " + this.samePackage + ", same project: " + this.sameProject + ", same type: " + this.sameFileType + "\n" +
				", switched file: " + this.switchedToFile + "\n, previous file: " + this.previousFile;
	}

	public static DynamicQuery createQuery() {
		final DynamicQuery query = new DynamicQuery(TABLE_NAME, EclipseAction.createQuery());

		query.addColumnToSelect(ActionDB.SAME_PACKAGE);
		query.addColumnToSelect(ActionDB.SAME_PROJECT);
		query.addColumnToSelect(ActionDB.SAME_TYPE);
		query.addColumnToSelect(ActionDB.SWITCHED_FILE);
		query.addColumnToSelect(ActionDB.PREVIOUS_FILE);
		
		query.setJoinColumn(ActionDB.ACTION_ID);
		query.setJoinColumnForJoinedTable(ActionDB.ECLIPSE_ACTION_ID);
		
		return query;
	}

	@Override
	public String toStringForViewer() {
		return "Action: " + getActionType() + 
				", same package: " + this.samePackage + ", same project: " + this.sameProject + ", same type: " + this.sameFileType + "\n" +
				", switched file: " + this.switchedToFile + ", previous file: " + this.previousFile  + ", CONTEXT CHANGE: " + this.contextChange;
	}
	
}
