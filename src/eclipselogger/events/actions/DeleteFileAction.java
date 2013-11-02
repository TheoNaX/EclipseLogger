package eclipselogger.events.actions;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.core.resources.IFile;

import eclipselogger.db.ActionDB;
import eclipselogger.db.DynamicQuery;
import eclipselogger.events.WorkingFile;
import eclipselogger.utils.FileChanges;
import eclipselogger.utils.FileValidator;
import eclipselogger.utils.PackageUtils;


public class DeleteFileAction extends EclipseAction {
	
	public static final String TABLE_NAME = "delete_file";
	
	private final String deletedFile;
	private final String previousFile;
	private final boolean samePackage;
	private final boolean sameProject;
	private final boolean sameFileType;
	private FileChanges changes = new FileChanges();
	private long workingTime = 0;
	
	// TODO implement count of deleted files within some period ???
	
	public DeleteFileAction(final long timeSinceLastAction, final EclipseAction previousAction, final IFile deletedFile, final IFile previoiusFile, final WorkingFile workFile) {
		super(timeSinceLastAction, previousAction);
		this.deletedFile = deletedFile.getProjectRelativePath().toOSString();
		this.previousFile = previoiusFile.getProjectRelativePath().toOSString();
		this.samePackage = PackageUtils.checkIfSamePackage(deletedFile, previoiusFile);
		this.sameProject = PackageUtils.checkIfSameProject(deletedFile, previoiusFile);
		this.sameFileType = FileValidator.haveFilesTheSameExtension(deletedFile, previoiusFile);
		if (workFile != null) {
			this.changes = workFile.getFileChanges();
			this.workingTime = workFile.getWorkingTime();
		}
	}
	
	public DeleteFileAction(final ResultSet rs) throws SQLException {
		super(rs);
		this.previousFile = rs.getString(ActionDB.PREVIOUS_FILE);
		this.deletedFile = rs.getString(ActionDB.DELETED_FILE);
		this.samePackage = rs.getBoolean(ActionDB.SAME_PACKAGE);
		this.sameProject = rs.getBoolean(ActionDB.SAME_PROJECT);
		this.sameFileType = rs.getBoolean(ActionDB.SAME_TYPE);
		this.changes = new FileChanges(rs.getInt(ActionDB.LINES_CHANGED), rs.getInt(ActionDB.LINES_DELETED), rs.getInt(ActionDB.LINES_ADDED));
		this.workingTime = rs.getLong(ActionDB.WORK_TIME);
	}

	public String getDeletedFile() {
		return this.deletedFile;
	}

	public String getPreviousFile() {
		return this.previousFile;
	}

	public boolean isSamePackage() {
		return this.samePackage;
	}

	public boolean isSameProject() {
		return this.sameProject;
	}

	public boolean isSameFileType() {
		return this.sameFileType;
	}
	
	public FileChanges getFileChanges() {
		return this.changes;
	}
	
	public long getFileWorkingTime() {
		return this.workingTime;
	}

	@Override
	public ActionType getActionType() {
		return ActionType.DELETE_FILE;
	}

	public static DynamicQuery createQuery() {
		final DynamicQuery query = new DynamicQuery(TABLE_NAME, EclipseAction.createQuery());
		
		query.addColumnToSelect(ActionDB.SAME_PACKAGE);
		query.addColumnToSelect(ActionDB.SAME_PROJECT);
		query.addColumnToSelect(ActionDB.SAME_TYPE);
		query.addColumnToSelect(ActionDB.DELETED_FILE);
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
				", deleted file: " + this.deletedFile + ", previous file: " + this.previousFile + "\n" + 
				"Lines: deleted: " + this.changes.getDeletedLines() + ", added: " + this.changes.getAddedLines() + ", changed: " + this.changes.getChangedLines() + "\n" + 
				"Working time: " + this.workingTime;
	}
}
