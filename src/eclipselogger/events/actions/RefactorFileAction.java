package eclipselogger.events.actions;

import java.sql.ResultSet;
import java.sql.SQLException;

import eclipselogger.db.ActionDB;
import eclipselogger.db.DynamicQuery;
import eclipselogger.resources.EclipseFile;
import eclipselogger.utils.PackageUtils;

public class RefactorFileAction extends EclipseAction {
	
	public static final String TABLE_NAME = "refactor_file";
	
	public static final String FILE_RENAME = "FILE_RENAME";
	public static final String FILE_MOVE = "FILE_MOVE";
	
	private final String oldFilePath;
	private final String newFilePath;
	private final String refactorType;
	private final boolean samePackage;
	private final boolean sameProject;
	private String previousFile;
	private final String refactoredFile;
		
	
	public RefactorFileAction(final long timeSinceLastAction, final EclipseAction previousAction, final String recentActions, 
			final int recentSameActionsCount, final EclipseFile oldFile, final EclipseFile newFile, final EclipseFile previousFile, final int packageDistance) {
		super(timeSinceLastAction, previousAction, recentActions, recentSameActionsCount, packageDistance);
		this.oldFilePath = oldFile.getProjectRelativePath();
		this.newFilePath = newFile.getProjectRelativePath();
		this.refactorType = resolveRefactorType(oldFile, newFile);
		if (previousFile != null) {
			this.previousFile = previousFile.getProjectRelativePath();
		}
		this.samePackage = PackageUtils.checkIfSamePackage(oldFile, previousFile);
		this.sameProject = PackageUtils.checkIfSameProject(oldFile, previousFile);
		this.refactoredFile = newFile.getProjectRelativePath();
	}
	
	public RefactorFileAction(final ResultSet rs) throws SQLException {
		super(rs);
		this.previousFile = rs.getString(ActionDB.PREVIOUS_FILE);
		this.samePackage = rs.getBoolean(ActionDB.SAME_PACKAGE);
		this.sameProject = rs.getBoolean(ActionDB.SAME_PROJECT);
		this.refactorType = rs.getString(ActionDB.REFACTOR_TYPE);
		this.refactoredFile = rs.getString(ActionDB.REFACTORED_FILE);
		this.oldFilePath = rs.getString(ActionDB.REFACTOR_OLD_FILE);
		this.newFilePath = rs.getString(ActionDB.REFACTOR_NEW_FILE);
	}

	public String getOldFilePath() {
		return this.oldFilePath;
	}

	public String getNewFilePath() {
		return this.newFilePath;
	}

	public String getRefactorType() {
		return this.refactorType;
	}
	
	private static String resolveRefactorType(final EclipseFile oldFile, final EclipseFile newFile) {
		if (oldFile == null || newFile == null) {
			return null;
		}
		if (oldFile.getFileName().equals(newFile.getFileName())) {
			return FILE_MOVE;
		} else {
			return FILE_RENAME;
		}
	}
	
	public boolean isSamePackage() {
		return this.samePackage;
	}

	public boolean isSameProject() {
		return this.sameProject;
	}

	public String getPreviousFile() {
		return this.previousFile;
	}
	
	public String getRefactoredFile() {
		return this.refactoredFile;
	}

	@Override
	public ActionType getActionType() {
		return ActionType.REFACTOR_FILE;
	}
	
	@Override
	public String toString() {
		return "Action: " + getActionType() + 
				", same package: " + this.samePackage + ", same project: " + this.sameProject + ", refactor type: " + this.refactorType + "\n" +
				", refactored file: " + this.refactoredFile + "\n, previous file: " + this.previousFile;
	}

	public static DynamicQuery createQuery() {
		final DynamicQuery query = new DynamicQuery(TABLE_NAME, EclipseAction.createQuery());
		
		query.addColumnToSelect(ActionDB.SAME_PACKAGE);
		query.addColumnToSelect(ActionDB.SAME_PROJECT);
		query.addColumnToSelect(ActionDB.REFACTOR_TYPE);
		query.addColumnToSelect(ActionDB.REFACTORED_FILE);
		query.addColumnToSelect(ActionDB.PREVIOUS_FILE);
		query.addColumnToSelect(ActionDB.REFACTOR_NEW_FILE);
		query.addColumnToSelect(ActionDB.REFACTOR_OLD_FILE);
		
		query.setJoinColumn(ActionDB.ACTION_ID);
		query.setJoinColumnForJoinedTable(ActionDB.ECLIPSE_ACTION_ID);
		
		return query;
	}

	@Override
	public String toStringForViewer() {
		return "Action: " + getActionType() + 
				", same package: " + this.samePackage + ", same project: " + this.sameProject + "\n" +
				", refactored file: " + this.refactoredFile + "\n, previous file: " + this.previousFile + ", CONTEXT CHANGE: " + this.contextChange;
	}	
	
}
