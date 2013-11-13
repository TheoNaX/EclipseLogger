package eclipselogger.events.actions;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;

import eclipselogger.db.ActionDB;
import eclipselogger.db.DynamicQuery;
import eclipselogger.utils.PackageUtils;

public class AddPackageAction extends EclipseAction {
	
	public static final String TABLE_NAME = "add_package";
	
	private String previousFile;
	private final String addedPackage;
	private final boolean samePackage;
	private final boolean sameProject;
	
	public AddPackageAction(final long timeSinceLastAction, final EclipseAction previousAction, final String recentActions, final int recentSameActionsCount, final IFolder addedPackage, final IFile previousFile) {
		super(timeSinceLastAction, previousAction, recentActions, recentSameActionsCount);
		this.addedPackage = addedPackage.getProjectRelativePath().toOSString();
		if (previousFile != null) {
			this.previousFile = previousFile.getProjectRelativePath().toOSString();
		}
		this.samePackage = PackageUtils.checkIfSamePackage(addedPackage, previousFile);
		this.sameProject = PackageUtils.checkIfSameProject(addedPackage, previousFile);
	}
	
	public AddPackageAction(final ResultSet rs) throws SQLException {
		super(rs);
		this.previousFile = rs.getString(ActionDB.PREVIOUS_FILE);
		this.addedPackage = rs.getString(ActionDB.ADDED_PACKAGE);
		this.samePackage = rs.getBoolean(ActionDB.SAME_PACKAGE);
		this.sameProject = rs.getBoolean(ActionDB.SAME_PROJECT);
	}

	public String getPreviousFile() {
		return this.previousFile;
	}

	public String getAddedPackage() {
		return this.addedPackage;
	}

	public boolean isSamePackage() {
		return this.samePackage;
	}

	public boolean isSameProject() {
		return this.sameProject;
	}

	@Override
	public ActionType getActionType() {
		return ActionType.ADD_PACKAGE;
	}
	
	
	public static DynamicQuery createQuery() {
		final DynamicQuery query = new DynamicQuery(TABLE_NAME, EclipseAction.createQuery());
		
		query.addColumnToSelect(ActionDB.SAME_PACKAGE);
		query.addColumnToSelect(ActionDB.SAME_PROJECT);
		query.addColumnToSelect(ActionDB.ADDED_PACKAGE);
		query.addColumnToSelect(ActionDB.PREVIOUS_FILE);
		
		query.setJoinColumn(ActionDB.ACTION_ID);
		query.setJoinColumnForJoinedTable(ActionDB.ECLIPSE_ACTION_ID);
		
		return query;
	}
	
	@Override
	public String toString() {
		return "Action: " + getActionType() + 
				", same package: " + this.samePackage + ", same project: " + this.sameProject + "\n" +
				", added package: " + this.addedPackage + ", previous file: " + this.previousFile;
	}
	
}
