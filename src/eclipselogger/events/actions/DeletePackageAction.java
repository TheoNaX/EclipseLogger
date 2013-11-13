package eclipselogger.events.actions;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;

import eclipselogger.db.ActionDB;
import eclipselogger.db.DynamicQuery;
import eclipselogger.utils.PackageUtils;

public class DeletePackageAction extends EclipseAction {
	
	public static final String TABLE_NAME = "delete_package";
	
	private String previousFile;
	private final String deletedPackage;
	private final boolean samePackage;
	private final boolean sameProject;	
	
	public DeletePackageAction(final long timeSinceLastAction, final EclipseAction previousAction, final String recentActions, final int recentSameActionsCount, final IFolder deletedPackage, final IFile previousFile) {
		super(timeSinceLastAction, previousAction, recentActions, recentSameActionsCount);
		this.deletedPackage = deletedPackage.getProjectRelativePath().toOSString();
		if (previousFile != null) {
			this.previousFile = previousFile.getProjectRelativePath().toOSString();
		}
		this.samePackage = PackageUtils.checkIfSamePackage(deletedPackage, previousFile);
		this.sameProject = PackageUtils.checkIfSameProject(deletedPackage, previousFile);
	}
	
	public DeletePackageAction(final ResultSet rs) throws SQLException {
		super(rs);
		this.previousFile = rs.getString(ActionDB.PREVIOUS_FILE);
		this.deletedPackage = rs.getString(ActionDB.DELETED_PACKAGE);
		this.samePackage = rs.getBoolean(ActionDB.SAME_PACKAGE);
		this.sameProject = rs.getBoolean(ActionDB.SAME_PROJECT);
	}

	public String getPreviousFile() {
		return this.previousFile;
	}

	public String getDeletedPackage() {
		return this.deletedPackage;
	}

	public boolean isSamePackage() {
		return this.samePackage;
	}

	public boolean isSameProject() {
		return this.sameProject;
	}

	@Override
	public ActionType getActionType() {
		return ActionType.DELETE_PACKAGE;
	}

	public static DynamicQuery createQuery() {
		final DynamicQuery query = new DynamicQuery(TABLE_NAME, EclipseAction.createQuery());

		query.addColumnToSelect(ActionDB.SAME_PACKAGE);
		query.addColumnToSelect(ActionDB.SAME_PROJECT);
		query.addColumnToSelect(ActionDB.DELETED_PACKAGE);
		query.addColumnToSelect(ActionDB.PREVIOUS_FILE);
		
		query.setJoinColumn(ActionDB.ACTION_ID);
		query.setJoinColumnForJoinedTable(ActionDB.ECLIPSE_ACTION_ID);
		
		return query;
	}
	
	@Override
	public String toString() {
		return "Action: " + getActionType() + 
				", same package: " + this.samePackage + ", same project: " + this.sameProject + "\n" +
				", deleted package: " + this.deletedPackage + ", previous file: " + this.previousFile;
	}
}
