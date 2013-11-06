package eclipselogger.events.actions;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.core.resources.IFile;

import eclipselogger.db.ActionDB;
import eclipselogger.db.DynamicQuery;
import eclipselogger.utils.FileValidator;
import eclipselogger.utils.PackageUtils;

public class OpenNewFileAction extends EclipseAction {
	
	public static final String TABLE_NAME = "open_file";
	
	private final String openedFile;
	private String previousFile;
	private final boolean samePackage;
	private final boolean sameProject;
	private final boolean sameFileType;
	
	public OpenNewFileAction(final long timeSinceLastAction, final EclipseAction previousAction, final IFile openedFile, final IFile previousFile) {
		super(timeSinceLastAction, previousAction);
		this.openedFile = openedFile.getProjectRelativePath().toOSString();
		if (previousFile != null) {
			this.previousFile = previousFile.getProjectRelativePath().toOSString();
		}
		this.samePackage = PackageUtils.checkIfSamePackage(openedFile, previousFile);
		this.sameProject = PackageUtils.checkIfSameProject(openedFile, previousFile);
		this.sameFileType = FileValidator.haveFilesTheSameExtension(openedFile, previousFile);
	}
	
	public OpenNewFileAction(final ResultSet rs) throws SQLException {
		super(rs);
		this.previousFile = rs.getString(ActionDB.PREVIOUS_FILE);
		this.openedFile = rs.getString(ActionDB.OPENED_FILE);
		this.samePackage = rs.getBoolean(ActionDB.SAME_PACKAGE);
		this.sameProject = rs.getBoolean(ActionDB.SAME_PROJECT);
		this.sameFileType = rs.getBoolean(ActionDB.SAME_TYPE);
	}
	
	public boolean openedInSamePackage() {
		return this.samePackage;
	}
	
	public String getOpenedFile() {
		return this.openedFile;
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
		return ActionType.OPEN_FILE;
	}
	
	@Override
	public String toString() {
		return "Action: " + getActionType() + 
				", same package: " + this.samePackage + ", same project: " + this.sameProject + ", same type: " + this.sameFileType + "\n" +
				", opened file: " + this.openedFile + ", previous file: " + this.previousFile;
	}
	

	public static DynamicQuery createQuery() {
		final DynamicQuery query = new DynamicQuery(TABLE_NAME, EclipseAction.createQuery());
		
		query.addColumnToSelect(ActionDB.SAME_PACKAGE);
		query.addColumnToSelect(ActionDB.SAME_PROJECT);
		query.addColumnToSelect(ActionDB.SAME_TYPE);
		query.addColumnToSelect(ActionDB.OPENED_FILE);
		query.addColumnToSelect(ActionDB.PREVIOUS_FILE);
		
		query.setJoinColumn(ActionDB.ACTION_ID);
		query.setJoinColumnForJoinedTable(ActionDB.ECLIPSE_ACTION_ID);
		
		return query;
	}	
	
}
