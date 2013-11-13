package eclipselogger.events.actions;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.core.resources.IFile;

import eclipselogger.db.ActionDB;
import eclipselogger.db.DynamicQuery;
import eclipselogger.utils.FileValidator;
import eclipselogger.utils.PackageUtils;

public class AddFileAction extends EclipseAction {
	
	public static final String TABLE_NAME = "add_file";
	 
	
	private String previousFile;
	private final String addedFile;
	private final boolean samePackage;
	private final boolean sameProject;
	private final boolean sameFileType;
	private int recentAddFileActionsCount;
	
	
	public AddFileAction(final long timeSinceLastAction, final EclipseAction previousAction, 
			final String recentActions, final int recentSameActionsCount, final IFile addedFile, final IFile previousFile) {
		super(timeSinceLastAction, previousAction, recentActions, recentSameActionsCount);
		this.addedFile = addedFile.getProjectRelativePath().toOSString();
		if (previousFile != null) {
			this.previousFile = previousFile.getProjectRelativePath().toOSString();
		}
		this.samePackage = PackageUtils.checkIfSamePackage(addedFile, previousFile);
		this.sameProject = PackageUtils.checkIfSameProject(addedFile, previousFile);
		this.sameFileType = FileValidator.haveFilesTheSameExtension(addedFile, previousFile);
	}
	
	public AddFileAction(final ResultSet rs) throws SQLException {
		super(rs);
		this.previousFile = rs.getString(ActionDB.PREVIOUS_FILE);
		this.addedFile = rs.getString(ActionDB.ADDED_FILE);
		this.samePackage = rs.getBoolean(ActionDB.SAME_PACKAGE);
		this.sameProject = rs.getBoolean(ActionDB.SAME_PROJECT);
		this.sameFileType = rs.getBoolean(ActionDB.SAME_TYPE);
	}

	public String getPreviousFile() {
		return this.previousFile;
	}

	public String getAddedFile() {
		return this.addedFile;
	}

	public boolean isSamePackage() {
		return this.samePackage;
	}

	public boolean isSameProject() {
		return this.sameProject;
	}

	@Override
	public ActionType getActionType() {
		return ActionType.ADD_FILE;
	}
	
	public boolean isSameFileType() {
		return this.sameFileType;
	}
	
	public int getRecentAddFileActionsCount() {
		return this.recentAddFileActionsCount;
	}
	
	public static DynamicQuery createQuery() {
		final DynamicQuery query = new DynamicQuery(TABLE_NAME, EclipseAction.createQuery());
		query.addColumnToSelect(ActionDB.SAME_PACKAGE);
		query.addColumnToSelect(ActionDB.SAME_PROJECT);
		query.addColumnToSelect(ActionDB.SAME_TYPE);
		query.addColumnToSelect(ActionDB.ADDED_FILE);
		query.addColumnToSelect(ActionDB.PREVIOUS_FILE);
		
		query.setJoinColumn(ActionDB.ACTION_ID);
		query.setJoinColumnForJoinedTable(ActionDB.ECLIPSE_ACTION_ID);
		
		return query;
	}
	
	@Override
	public String toString() {
		return "Action: " + getActionType() + 
				", same package: " + this.samePackage + ", same project: " + this.sameProject + ", same type: " + this.sameFileType + "\n" +
				", added file: " + this.addedFile + ", previous file: " + this.previousFile;
	}
}
