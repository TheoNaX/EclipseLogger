package eclipselogger.events.actions;

import java.sql.ResultSet;
import java.sql.SQLException;

import eclipselogger.db.ActionDB;
import eclipselogger.db.DynamicQuery;
import eclipselogger.events.WorkingFile;
import eclipselogger.resources.EclipseFile;
import eclipselogger.utils.FileChanges;
import eclipselogger.utils.FileValidator;
import eclipselogger.utils.PackageUtils;

public class CloseFileAction extends EclipseAction {
	
	public static final String TABLE_NAME = "close_file";
	
	private final String closedFile;
	private String previousFile;
	private final boolean samePackage;
	private final boolean sameProject;
	private final boolean sameFileType;
	private FileChanges fileChanges = new FileChanges();
	private long workingTime = 0;
	
	public CloseFileAction(final long timeSinceLastAction, final EclipseAction previousAction, final String recentActions, 
			final int recentSameActionsCount, final EclipseFile closedFile, final EclipseFile previousFile, final WorkingFile workFile, final int packageDistance) {
		super(timeSinceLastAction, previousAction, recentActions, recentSameActionsCount, packageDistance);
		this.closedFile = closedFile.getProjectRelativePath();
		if (previousFile != null) {
			this.previousFile = previousFile.getProjectRelativePath();
		}
		this.samePackage = PackageUtils.checkIfSamePackage(closedFile, previousFile);
		this.sameProject = PackageUtils.checkIfSameProject(closedFile, previousFile);
		this.sameFileType = FileValidator.haveFilesTheSameExtension(closedFile, previousFile);
		if (workFile != null) {
			this.fileChanges = workFile.getFileChanges();
			this.workingTime = workFile.getWorkingTime();
		} 
	}
	
	public CloseFileAction(final ResultSet rs) throws SQLException {
		super(rs);
		this.previousFile = rs.getString(ActionDB.PREVIOUS_FILE);
		this.closedFile = rs.getString(ActionDB.CLOSED_FILE);
		this.samePackage = rs.getBoolean(ActionDB.SAME_PACKAGE);
		this.sameProject = rs.getBoolean(ActionDB.SAME_PROJECT);
		this.sameFileType = rs.getBoolean(ActionDB.SAME_TYPE);
		this.fileChanges = new FileChanges(rs.getInt(ActionDB.LINES_CHANGED), rs.getInt(ActionDB.LINES_DELETED), rs.getInt(ActionDB.LINES_ADDED));
		this.workingTime = rs.getLong(ActionDB.WORK_TIME);
	}
	
	public boolean closedInSamePackage() {
		return this.samePackage;
	}
	
	public String getClosedFile() {
		return this.closedFile;
	}
	
	public String getPreviousFile() {
		return this.previousFile;
	}
	
	public boolean closedInSameProject() {
		return this.sameProject;
	}
	
	public boolean isTheSameTypeAsPreviuos() {
		return this.sameFileType;
	}
	
	public long getFileWorkingTime() {
		return this.workingTime;
	}
	
	public FileChanges getFileChanges() {
		return this.fileChanges;
	}

	@Override
	public ActionType getActionType() {
		return ActionType.CLOSE_FILE;
	}

	public static DynamicQuery createQuery() {
		final DynamicQuery query = new DynamicQuery(TABLE_NAME, EclipseAction.createQuery());

		query.addColumnToSelect(ActionDB.SAME_PACKAGE);
		query.addColumnToSelect(ActionDB.SAME_PROJECT);
		query.addColumnToSelect(ActionDB.SAME_TYPE);
		query.addColumnToSelect(ActionDB.CLOSED_FILE);
		query.addColumnToSelect(ActionDB.PREVIOUS_FILE);
		query.addColumnToSelect(ActionDB.LINES_ADDED);
		query.addColumnToSelect(ActionDB.LINES_DELETED);
		query.addColumnToSelect(ActionDB.LINES_CHANGED);
		query.addColumnToSelect(ActionDB.WORK_TIME);
		
		query.setJoinColumn(ActionDB.ACTION_ID);
		query.setJoinColumnForJoinedTable(ActionDB.ECLIPSE_ACTION_ID);
		
		return query;
	}
	
	@Override
	public String toString() {
		return "Action: " + getActionType() + 
				", same package: " + this.samePackage + ", same project: " + this.sameProject + ", same type: " + this.sameFileType + "\n" +
				", closed file: " + this.closedFile + "\n, previous file: " + this.previousFile; 
	}
	
	public String toString2() {
		return "Action: " + getActionType() + 
				", same package: " + this.samePackage + ", same project: " + this.sameProject + ", same type: " + this.sameFileType + "\n" +
				", closed file: " + this.closedFile + ", previous file: " + this.previousFile + "\n" + 
				"Lines: deleted: " + this.fileChanges.getDeletedLines() + ", added: " + this.fileChanges.getAddedLines() + ", changed: " + this.fileChanges.getChangedLines() + "\n" + 
				"Working time: " + this.workingTime;
	}

	@Override
	public String toStringForViewer() {
		return "Action: " + getActionType() + 
				", same package: " + this.samePackage + ", same project: " + this.sameProject + ", same type: " + this.sameFileType + "\n" +
				", closed file: " + this.closedFile + "\n previous file: " + this.previousFile + ", CONTEXT CHANGE: " + this.contextChange;
	}
}
