package eclipselogger.logging;

import eclipselogger.events.actions.CloseFileAction;
import eclipselogger.events.actions.EclipseAction;

public class ConsoleActionLogger implements EclipseActiontLogIF {

	@Override
	public void logEclipseAction(EclipseAction action) {
		if (action instanceof CloseFileAction) {
			logCloseFileAction(action);
		}
		
	}
	
	private void logCloseFileAction(EclipseAction action) {
		CloseFileAction closeAction = (CloseFileAction) action;
		System.out.println("============================================");
		System.out.println("File closed: " + closeAction.getClosedFile().getProjectRelativePath());
		System.out.println("Working time: " + closeAction.getWorkingFile().getWorkingTime() + " ms");
		System.out.println("In same package: " + closeAction.closedInSamePackage() + ", in same project: " + closeAction.closedInSameProject() + ", same file type: " + closeAction.isTheSameTypeAsPreviuos());
		System.out.println("============================================");
	}

}
