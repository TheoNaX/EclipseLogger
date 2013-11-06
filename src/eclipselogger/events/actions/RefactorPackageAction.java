package eclipselogger.events.actions;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;

import eclipselogger.db.ActionDB;
import eclipselogger.db.DynamicQuery;
import eclipselogger.utils.PackageUtils;

public class RefactorPackageAction extends EclipseAction {
	
	public static final String TABLE_NAME = "refactor_package";
	
	public static final String PACKAGE_RENAME = "PACKAGE_RENAME";
	public static final String PACKAGE_MOVE = "PACKAGE_MOVE";
	
	private final String refactorType;
	private final boolean samePackage;
	private final boolean sameProject;
	private String previousFile;
	private String refactoredPackage;
	
	public boolean isSamePackage() {
		return this.samePackage;
	}

	public boolean isSameProject() {
		return this.sameProject;
	}

	public RefactorPackageAction(final long timeSinceLastAction, final EclipseAction previousAction, final IFolder oldPackage, final IFolder newPackage, final IFile previousFile) {
		super(timeSinceLastAction, previousAction);
		this.refactorType = resolveRefactorType(oldPackage, newPackage);
		if (previousFile != null) {
			this.previousFile = previousFile.getProjectRelativePath().toOSString();
		}
		this.samePackage = PackageUtils.checkIfSamePackage(oldPackage, previousFile);
		this.sameProject = PackageUtils.checkIfSameProject(oldPackage, previousFile);
	}
	
	public RefactorPackageAction(final ResultSet rs) throws SQLException {
		super(rs);
		this.previousFile = rs.getString(ActionDB.PREVIOUS_FILE);
		this.samePackage = rs.getBoolean(ActionDB.SAME_PACKAGE);
		this.sameProject = rs.getBoolean(ActionDB.SAME_PROJECT);
		this.refactorType = rs.getString(ActionDB.REFACTOR_TYPE);
		this.refactoredPackage = rs.getString(ActionDB.REFACTORED_PACKAGE);
	}

	public String getRefactorType() {
		return this.refactorType;
	}
	
	private static String resolveRefactorType(final IFolder oldFolder, final IFolder newFolder) {
		if (oldFolder == null || newFolder == null) {
			return null;
		}
		if (oldFolder.getName().equals(newFolder.getName())) {
			return PACKAGE_MOVE;
		} else {
			return PACKAGE_RENAME;
		}
	}

	@Override
	public ActionType getActionType() {
		return ActionType.REFACTOR_PACKAGE;
	}
	
	public String getPreviousFile() {
		return this.previousFile;
	}
	
	public String getRefactoredPackage() {
		return this.refactoredPackage;
	}
	
	@Override
	public String toString() {
		return "Action: " + getActionType() + 
				", same package: " + this.samePackage + ", same project: " + this.sameProject + ", refactor type: " + this.refactorType + "\n" +
				", refactored package: " + this.refactoredPackage + ", previous file: " + this.previousFile;
	}

	public static DynamicQuery createQuery() {
		final DynamicQuery query = new DynamicQuery(TABLE_NAME, EclipseAction.createQuery());
		
		query.addColumnToSelect(ActionDB.SAME_PACKAGE);
		query.addColumnToSelect(ActionDB.SAME_PROJECT);
		query.addColumnToSelect(ActionDB.REFACTOR_TYPE);
		query.addColumnToSelect(ActionDB.REFACTORED_PACKAGE);
		query.addColumnToSelect(ActionDB.PREVIOUS_FILE);
		
		query.setJoinColumn(ActionDB.ACTION_ID);
		query.setJoinColumnForJoinedTable(ActionDB.ECLIPSE_ACTION_ID);
		
		return query;
	}
}
