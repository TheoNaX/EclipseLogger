package eclipselogger.logging;

import java.sql.PreparedStatement;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

import eclipselogger.db.SQLiteHandler;
import eclipselogger.events.actions.AddFileAction;
import eclipselogger.events.actions.AddPackageAction;
import eclipselogger.events.actions.AddProjectAction;
import eclipselogger.events.actions.CloseFileAction;
import eclipselogger.events.actions.DeleteFileAction;
import eclipselogger.events.actions.DeletePackageAction;
import eclipselogger.events.actions.EclipseAction;
import eclipselogger.events.actions.OpenNewFileAction;
import eclipselogger.events.actions.RefactorFileAction;
import eclipselogger.events.actions.RefactorPackageAction;
import eclipselogger.events.actions.SwitchToFileAction;
import eclipselogger.utils.DBCleanupTool;

public class DatabaseActionLogger implements EclipseActiontLogIF {
	
	private final SQLiteHandler dbHandler = new SQLiteHandler();
	private final Logger logger = Logger.getLogger(DatabaseActionLogger.class);

	@Override
	public void logEclipseAction(final EclipseAction action, final boolean context) {
		switch (action.getActionType()){
		case ADD_FILE:
			logAddFileActionToDb(action, context);
			break;
		case ADD_PACKAGE:
			logAddPackageActionToDb(action, context);
			break;
		case ADD_PROJECT:
			logAddProjectActionToDb(action, context);
			break;
		case CLOSE_FILE:
			logCloseFileActionToDb(action, context);
			break;
		case DELETE_FILE:
			logDeleteFileActionToDb(action, context);
			break;
		case DELETE_PACKAGE:
			logDeletePackageActionToDb(action, context);
			break;
		case OPEN_FILE:
			logOpenFileActionToDb(action, context);
			break;
		case REFACTOR_FILE:
			logRefactorFileActionToDb(action, context);
			break;
		case REFACTOR_PACKAGE:
			logRefactorPackageActionToDb(action, context);
			break;
		case SWITCH_FILE:
			logSwitchToFileActionToDb(action, context);
			break;
		} 
	}

	private void logAddFileActionToDb(final EclipseAction action, final boolean context) {
		final AddFileAction addFile = (AddFileAction) action;
		PreparedStatement ps = null;
		try {
			final int seqNo = this.dbHandler.getNextSequenceNumberForAction(); 
			// insert common actions parameters
			insertEclipseActionIntoDb(action, context);
			
			// insert concrete action specific parameters
			final String sql = "INSERT INTO " + AddFileAction.TABLE_NAME + " (action_id, same_package, same_project, same_type, added_file, previous_file) VALUES (?, ?, ?, ?, ?, ?)";
			ps = this.dbHandler.prepareStatement(sql);
			ps.setInt(1, seqNo);
			ps.setBoolean(2, addFile.isSamePackage());
			ps.setBoolean(3, addFile.isSameProject());
			ps.setBoolean(4, addFile.isSameFileType());
			ps.setString(5, addFile.getAddedFile());
			ps.setString(6, addFile.getPreviousFile());
			
			ps.executeUpdate();
			
		} catch (final Exception e) {
			this.logger.error("FAILED to log ADD FILE ACTION to database!", e);
		} finally {
			DBCleanupTool.closeStatement(ps);
			this.dbHandler.disposeConnection();
		}
		
	}

	private void logSwitchToFileActionToDb(final EclipseAction action, final boolean context) {
		final SwitchToFileAction switchFile = (SwitchToFileAction) action;
		PreparedStatement ps = null;
		try {
			final int seqNo = this.dbHandler.getNextSequenceNumberForAction(); 
			// insert common actions parameters
			insertEclipseActionIntoDb(action, context);
			
			// insert concrete action specific parameters
			final String sql = "INSERT INTO " + SwitchToFileAction.TABLE_NAME + " (action_id, same_package, same_project, same_type, switched_file, previous_file) VALUES (?, ?, ?, ?, ?, ?)";
			ps = this.dbHandler.prepareStatement(sql);
			ps.setInt(1, seqNo);
			ps.setBoolean(2, switchFile.openedInSamePackage());
			ps.setBoolean(3, switchFile.openedInSameProject());
			ps.setBoolean(4, switchFile.isTheSameTypeAsPreviuos());
			ps.setString(5, switchFile.getSwitchedToFile());
			ps.setString(6, switchFile.getPreviousOpenedFile());
			
			ps.executeUpdate();
			
		} catch (final Exception e) {
			this.logger.error("FAILED to log SWITCH TO FILE ACTION to database!", e);
		} finally {
			DBCleanupTool.closeStatement(ps);
			this.dbHandler.disposeConnection();
		}
	}

	private void logRefactorPackageActionToDb(final EclipseAction action, final boolean context) {
		final RefactorPackageAction refactorPack = (RefactorPackageAction) action;
		PreparedStatement ps = null;
		try {
			final int seqNo = this.dbHandler.getNextSequenceNumberForAction(); 
			// insert common actions parameters
			insertEclipseActionIntoDb(action, context);
			
			// insert concrete action specific parameters
			final String sql = "INSERT INTO " + RefactorPackageAction.TABLE_NAME + " (action_id, same_package, same_project, refactor_type, refactored_package, previous_file) VALUES (?, ?, ?, ?, ?, ?)";
			ps = this.dbHandler.prepareStatement(sql);
			ps.setInt(1, seqNo);
			ps.setBoolean(2, refactorPack.isSamePackage());
			ps.setBoolean(3, refactorPack.isSameProject());
			ps.setString(4, refactorPack.getRefactorType());
			ps.setString(5, refactorPack.getRefactoredPackage());
			ps.setString(6, refactorPack.getPreviousFile());
			
			ps.executeUpdate();
			
		} catch (final Exception e) {
			this.logger.error("FAILED to log REFACTOR PACKAGE ACTION to database!", e);
		} finally {
			DBCleanupTool.closeStatement(ps);
			this.dbHandler.disposeConnection();
		}
	}

	private void logRefactorFileActionToDb(final EclipseAction action, final boolean context) {
		final RefactorFileAction refactorFile = (RefactorFileAction) action;
		PreparedStatement ps = null;
		try {
			final int seqNo = this.dbHandler.getNextSequenceNumberForAction(); 
			// insert common actions parameters
			insertEclipseActionIntoDb(action, context);
			
			// insert concrete action specific parameters
			final String sql = "INSERT INTO " + RefactorFileAction.TABLE_NAME + " (action_id, same_package, same_project, refactor_type, refactored_file, previous_file) VALUES (?, ?, ?, ?, ?, ?)";
			ps = this.dbHandler.prepareStatement(sql);
			ps.setInt(1, seqNo);
			ps.setBoolean(2, refactorFile.isSamePackage());
			ps.setBoolean(3, refactorFile.isSameProject());
			ps.setString(4, refactorFile.getRefactorType());
			ps.setString(5, refactorFile.getRefactoredFile());
			ps.setString(6, refactorFile.getPreviousFile());
			
			ps.executeUpdate();
			
		} catch (final Exception e) {
			this.logger.error("FAILED to log REFACTOR FILE ACTION to database!", e);
		} finally {
			DBCleanupTool.closeStatement(ps);
			this.dbHandler.disposeConnection();
		}
	}

	private void logOpenFileActionToDb(final EclipseAction action, final boolean context) {
		final OpenNewFileAction openFile = (OpenNewFileAction) action;
		PreparedStatement ps = null;
		try {
			final int seqNo = this.dbHandler.getNextSequenceNumberForAction(); 
			// insert common actions parameters
			insertEclipseActionIntoDb(action, context);
			
			// insert concrete action specific parameters
			final String sql = "INSERT INTO " + OpenNewFileAction.TABLE_NAME + " (action_id, same_package, same_project, same_type, opened_file, previous_file) VALUES (?, ?, ?, ?, ?, ?)";
			ps = this.dbHandler.prepareStatement(sql);
			ps.setInt(1, seqNo);
			ps.setBoolean(2, openFile.openedInSamePackage());
			ps.setBoolean(3, openFile.openedInSameProject());
			ps.setBoolean(4, openFile.isTheSameTypeAsPreviuos());
			ps.setString(5, openFile.getOpenedFile());
			ps.setString(6, openFile.getPreviousOpenedFile());
			
			ps.executeUpdate();
			
		} catch (final Exception e) {
			this.logger.error("FAILED to log OPEN FILE ACTION to database!", e);
		} finally {
			DBCleanupTool.closeStatement(ps);
			this.dbHandler.disposeConnection();
		}
	}

	private void logDeletePackageActionToDb(final EclipseAction action, final boolean context) {
		final DeletePackageAction deletePack = (DeletePackageAction) action;
		PreparedStatement ps = null;
		try {
			final int seqNo = this.dbHandler.getNextSequenceNumberForAction(); 
			// insert common actions parameters
			insertEclipseActionIntoDb(action, context);
			
			// insert concrete action specific parameters
			final String sql = "INSERT INTO " + DeletePackageAction.TABLE_NAME + " (action_id, same_package, same_project, deleted_package, previous_file) VALUES (?, ?, ?, ?, ?)";
			ps = this.dbHandler.prepareStatement(sql);
			ps.setInt(1, seqNo);
			ps.setBoolean(2, deletePack.isSamePackage());
			ps.setBoolean(3, deletePack.isSameProject());
			ps.setString(4, deletePack.getDeletedPackage());
			ps.setString(5, deletePack.getPreviousFile());
			
			ps.executeUpdate();
			
		} catch (final Exception e) {
			this.logger.error("FAILED to log DELETE PACKAGE ACTION to database!", e);
		} finally {
			DBCleanupTool.closeStatement(ps);
			this.dbHandler.disposeConnection();
		}
	}

	private void logDeleteFileActionToDb(final EclipseAction action, final boolean context) {
		final DeleteFileAction deleteFile = (DeleteFileAction) action;
		PreparedStatement ps = null;
		try {
			final int seqNo = this.dbHandler.getNextSequenceNumberForAction(); 
			// insert common actions parameters
			insertEclipseActionIntoDb(action, context);
			
			// insert concrete action specific parameters
			final String sql = "INSERT INTO " + DeleteFileAction.TABLE_NAME + " (action_id, same_package, same_project, same_type, deleted_file, previous_file, deleted_lines, added_lines, changed_lines, work_time)"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			ps = this.dbHandler.prepareStatement(sql);
			ps.setInt(1, seqNo);
			ps.setBoolean(2, deleteFile.isSamePackage());
			ps.setBoolean(3, deleteFile.isSameProject());
			ps.setBoolean(4, deleteFile.isSameFileType());
			ps.setString(5, deleteFile.getDeletedFile());
			ps.setString(6, deleteFile.getPreviousFile());
			ps.setInt(7, deleteFile.getFileChanges().getDeletedLines());
			ps.setInt(8, deleteFile.getFileChanges().getAddedLines());
			ps.setInt(9, deleteFile.getFileChanges().getChangedLines());
			ps.setLong(10, deleteFile.getFileWorkingTime());
			
			ps.executeUpdate();
			
		} catch (final Exception e) {
			this.logger.error("FAILED to log DELETE FILE ACTION to database!", e);
		} finally {
			DBCleanupTool.closeStatement(ps);
			this.dbHandler.disposeConnection();
		}
	}

	private void logCloseFileActionToDb(final EclipseAction action, final boolean context) {
		final CloseFileAction closeFile = (CloseFileAction) action;
		PreparedStatement ps = null;
		try {
			final int seqNo = this.dbHandler.getNextSequenceNumberForAction(); 
			// insert common actions parameters
			insertEclipseActionIntoDb(action, context);
			
			// insert concrete action specific parameters
			final String sql = "INSERT INTO " + CloseFileAction.TABLE_NAME + " (action_id, same_package, same_project, same_type, closed_file, previous_file, deleted_lines, added_lines, changed_lines, work_time)"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			ps = this.dbHandler.prepareStatement(sql);
			ps.setInt(1, seqNo);
			ps.setBoolean(2, closeFile.closedInSamePackage());
			ps.setBoolean(3, closeFile.closedInSameProject());
			ps.setBoolean(4, closeFile.isTheSameTypeAsPreviuos());
			ps.setString(5, closeFile.getClosedFile());
			ps.setString(6, closeFile.getPreviousFile());
			ps.setInt(7, closeFile.getFileChanges().getDeletedLines());
			ps.setInt(8, closeFile.getFileChanges().getAddedLines());
			ps.setInt(9, closeFile.getFileChanges().getChangedLines());
			ps.setLong(10, closeFile.getFileWorkingTime());
			
			ps.executeUpdate();
			
		} catch (final Exception e) {
			this.logger.error("FAILED to log CLOSE FILE ACTION to database!", e);
		} finally {
			DBCleanupTool.closeStatement(ps);
			this.dbHandler.disposeConnection();
		}
	}

	private void logAddProjectActionToDb(final EclipseAction action, final boolean context) {
		final AddProjectAction addProject = (AddProjectAction) action;
		PreparedStatement ps = null;
		try {
			final int seqNo = this.dbHandler.getNextSequenceNumberForAction(); 
			// insert common actions parameters
			insertEclipseActionIntoDb(action, context);
			
			// insert concrete action specific parameters
			final String sql = "INSERT INTO " + AddProjectAction.TABLE_NAME + " (action_id, name) VALUES (?, ?)";
			ps = this.dbHandler.prepareStatement(sql);
			ps.setInt(1, seqNo);
			ps.setString(2, addProject.getProjectName());
			
			ps.executeUpdate();
			
		} catch (final Exception e) {
			this.logger.error("FAILED to log ADD PROJECT ACTION to database!", e);
		} finally {
			DBCleanupTool.closeStatement(ps);
			this.dbHandler.disposeConnection();
		}
	}

	private void logAddPackageActionToDb(final EclipseAction action, final boolean context) {
		final AddPackageAction addPack = (AddPackageAction) action;
		PreparedStatement ps = null;
		try {
			final int seqNo = this.dbHandler.getNextSequenceNumberForAction(); 
			// insert common actions parameters
			insertEclipseActionIntoDb(action, context);
			
			// insert concrete action specific parameters
			final String sql = "INSERT INTO " + AddPackageAction.TABLE_NAME + " (action_id, same_package, same_project, added_package, previous_file) VALUES (?, ?, ?, ?, ?)";
			ps = this.dbHandler.prepareStatement(sql);
			ps.setInt(1, seqNo);
			ps.setBoolean(2, addPack.isSamePackage());
			ps.setBoolean(3, addPack.isSameProject());
			ps.setString(4, addPack.getAddedPackage());
			ps.setString(5, addPack.getPreviousFile());
			
			ps.executeUpdate();
			
		} catch (final Exception e) {
			this.logger.error("FAILED to log ADD PACKAGE ACTION to database!", e);
		} finally {
			DBCleanupTool.closeStatement(ps);
			this.dbHandler.disposeConnection();
		}
	}
	
	private void insertEclipseActionIntoDb(final EclipseAction action, final boolean context) throws Exception {
		final String sql = "INSERT INTO eclipse_action(last_action, time_since_last, action, context_change, last_actions, recent_same_actions_count, send_status, "
				+ "time_stamp, package_distance, recent_lines_added, recent_lines_changed, recent_lines_deleted, average_lines_added, average_lines_changed, average_lines_deleted, "
				+ "average_package_diff, average_package_action_diff, max_package_diff, min_package_diff, average_duration_diff, average_duration_action_diff, max_duration_diff, "
				+ "min_duration_diff, context_same_actions, same_actions_ratio, time_since_last_same, same_transitions_count, same_transitions_ratio) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
		PreparedStatement ps = null;
		try {
			ps = this.dbHandler.prepareStatement(sql);
			ps.setInt(1, action.getPreviousAction());
			ps.setLong(2, action.getTimeSinceLastAction());
			ps.setInt(3, action.getActionType().getValue());
			ps.setBoolean(4, context);
			ps.setString(5, action.getRecentActions());
			ps.setInt(6, action.getRecentSameActionsCount());
			ps.setInt(7, EclipseAction.SEND_STATUS_UNSENT);

			final Timestamp timestamp = new Timestamp(action.getTimestamp().getTime());
			ps.setTimestamp(8, timestamp);
			ps.setInt(9, action.getPackageDistanceFromLastAction());
			
			ps.setInt(10, action.getMostRecentFileChanges().getAddedLines());
			ps.setInt(11, action.getMostRecentFileChanges().getChangedLines());
			ps.setInt(12, action.getMostRecentFileChanges().getDeletedLines());
			ps.setInt(13, action.getAverageFileChanges().getAddedLines());
			ps.setInt(14, action.getAverageFileChanges().getChangedLines());
			ps.setInt(15, action.getAverageFileChanges().getDeletedLines());
			
			ps.setDouble(16, action.getAveragePackageDistanceDiff());
			ps.setDouble(17, action.getAveragePackageDistanceDiffForAction());
			ps.setInt(18, action.getMaxPackageDistanceDiff());
			ps.setInt(19, action.getMinPackageDistanceDiff());
			
			ps.setLong(20, action.getAverageDurationDiff());
			ps.setLong(21, action.getAverageDurationDiffForAction());
			ps.setLong(22, action.getMaxDurationDiff());
			ps.setLong(23, action.getMinDurationDiff());
			
			ps.setInt(24, action.getSameActionsCountInContext());
			ps.setDouble(25, action.getSameActionsRatio());
			ps.setLong(26, action.getTimeSinceLastSameAction());
			ps.setInt(27, action.getSameActionsTransitionsCount());
			ps.setDouble(28, action.getSameActionsTransitionsRatio());
			
			ps.executeUpdate();
		}
		finally {
			DBCleanupTool.closeStatement(ps);
			this.dbHandler.disposeConnection();
		}
	}

}
