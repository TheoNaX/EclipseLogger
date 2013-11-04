package eclipselogger.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import eclipselogger.db.DynamicQuery.WhereParam;
import eclipselogger.events.actions.ActionType;
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

public class ActionLoader {

	SQLiteHandler dbHandler = new SQLiteHandler();
	
	public List<EclipseAction> loadEclipseActionsInLastFiveMinutes() throws Exception {
		final List<EclipseAction> actions = new ArrayList<EclipseAction>();
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -5);
		final Date limitTime = cal.getTime();
		final WhereParam param = new DynamicQuery.WhereParam(("b." + ActionDB.TIMESTAMP), DynamicQuery.WhereParam.CONDITION_LARGER);
		final ActionType[] actionTypes = ActionType.values();
		for (int i=0; i<actionTypes.length; i++) {
			loadEclipseActions(actionTypes[i], actions, param, limitTime);
		}
		
		return actions;
	}
	
	public List<EclipseAction> loadNotSentEclipseActions() throws Exception {
		final List<EclipseAction> actions = new ArrayList<EclipseAction>();
		
		final WhereParam param = new DynamicQuery.WhereParam(("b." + ActionDB.SEND_STATUS));
		final ActionType[] actionTypes = ActionType.values();
		for (int i=0; i<actionTypes.length; i++) {
			loadEclipseActions(actionTypes[i], actions, param, EclipseAction.SEND_STATUS_UNSENT);
		}
		
		return actions;
	}
	
	private void loadEclipseActions(final ActionType actionType, final List<EclipseAction> actions, final WhereParam param, final Object value) throws Exception {
		final DynamicQuery query = createQuery(actionType);
		query.addWhereParam(param);
		
		final PreparedStatement ps = this.dbHandler.prepareStatement(query.buildQuery());
		ps.setObject(1, value);
		
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				final EclipseAction action = createEclipseAction(actionType, rs);
				actions.add(action);
			}
		} finally {
			DBCleanupTool.closeResultSet(rs);
			DBCleanupTool.closeStatement(ps);
		}
	}
	
	private EclipseAction createEclipseAction(final ActionType actionType, final ResultSet rs) throws SQLException {
		EclipseAction action = null;
		switch (actionType) {
		case ADD_FILE:
			action = new AddFileAction(rs);
			break;
		case ADD_PACKAGE:
			action = new AddPackageAction(rs);
			break;
		case ADD_PROJECT:
			action = new AddProjectAction(rs);
			break;
		case CLOSE_FILE:
			action = new CloseFileAction(rs);
			break;
		case DELETE_FILE:
			action = new DeleteFileAction(rs);
			break;
		case DELETE_PACKAGE:
			action = new DeletePackageAction(rs);
			break;
		case OPEN_FILE:
			action = new OpenNewFileAction(rs);
			break;
		case REFACTOR_FILE:
			action = new RefactorFileAction(rs);
			break;
		case REFACTOR_PACKAGE:
			action = new RefactorPackageAction(rs);
			break;
		case SWITCH_FILE:
			action = new SwitchToFileAction(rs);
			break;
		}
		
		return action;
	}

	private DynamicQuery createQuery(final ActionType action) {
		DynamicQuery query = null;
		switch (action) {
		case ADD_FILE:
			query = AddFileAction.createQuery();
			break;
		case ADD_PACKAGE:
			query = AddPackageAction.createQuery();
			break;
		case ADD_PROJECT:
			query = AddProjectAction.createQuery();
			break;
		case CLOSE_FILE:
			query = CloseFileAction.createQuery();
			break;
		case DELETE_FILE:
			query = DeleteFileAction.createQuery();
			break;
		case DELETE_PACKAGE:
			query = DeletePackageAction.createQuery();
			break;
		case OPEN_FILE:
			query = OpenNewFileAction.createQuery();
			break;
		case REFACTOR_FILE:
			query = RefactorFileAction.createQuery();
			break;
		case REFACTOR_PACKAGE:
			query = RefactorPackageAction.createQuery();
			break;
		case SWITCH_FILE:
			query = SwitchToFileAction.createQuery();
			break;
		}
		
		return query;
	}
	
	public void updateActionSendStatus(final int actionID, final int sendStatus) throws Exception {
		final String sql = "UPDATE " + EclipseAction.TABLE_NAME + " SET " + ActionDB.SEND_STATUS + " = ? WHERE " + ActionDB.ACTION_ID + " = ?";
		PreparedStatement ps = null;
		try {
			ps = this.dbHandler.prepareStatement(sql);
			ps.setInt(1, sendStatus);
			ps.setInt(2, actionID);
		} finally {
			DBCleanupTool.closeStatement(ps);
		}
	}
	
}
