package eclipselogger.logging;

import eclipselogger.events.actions.EclipseAction;

public class DatabaseActionLogger implements EclipseActiontLogIF {

	@Override
	public void logEclipseAction(EclipseAction action) {
		switch (action.getActionType()){
		case ADD_FILE:
			logAddFileActionToDb(action);
			break;
		case ADD_PACKAGE:
			logAddPackageActionToDb(action);
			break;
		case ADD_PROJECT:
			logAddProjectActionToDb(action);
			break;
		case CLOSE_FILE:
			logCloseFileActionToDb(action);
			break;
		case DELETE_FILE:
			logDeleteFileActionToDb(action);
			break;
		case DELETE_PACKAGE:
			logDeletePackageActionToDb(action);
			break;
		case OPEN_FILE:
			logOpenFileActionToDb(action);
			break;
		case REFACTOR_FILE:
			logRefactorFileActionToDb(action);
			break;
		case REFACTOR_PACKAGE:
			logRefactorPackageActionToDb(action);
			break;
		case SWITCH_FILE:
			logSwitchToFileActionToDb(action);
			break;
		} 
	}

	private void logAddFileActionToDb(EclipseAction action) {
		// TODO Auto-generated method stub
		
	}

	private void logSwitchToFileActionToDb(EclipseAction action) {
		// TODO Auto-generated method stub
		
	}

	private void logRefactorPackageActionToDb(EclipseAction action) {
		// TODO Auto-generated method stub
		
	}

	private void logRefactorFileActionToDb(EclipseAction action) {
		// TODO Auto-generated method stub
		
	}

	private void logOpenFileActionToDb(EclipseAction action) {
		// TODO Auto-generated method stub
		
	}

	private void logDeletePackageActionToDb(EclipseAction action) {
		// TODO Auto-generated method stub
		
	}

	private void logDeleteFileActionToDb(EclipseAction action) {
		// TODO Auto-generated method stub
		
	}

	private void logCloseFileActionToDb(EclipseAction action) {
		// TODO Auto-generated method stub
		
	}

	private void logAddProjectActionToDb(EclipseAction action) {
		// TODO Auto-generated method stub
		
	}

	private void logAddPackageActionToDb(EclipseAction action) {
		// TODO Auto-generated method stub
		
	}

}
