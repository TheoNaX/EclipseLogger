package eclipselogger.logging;

import eclipselogger.events.actions.AddFileAction;
import eclipselogger.events.actions.AddPackageAction;
import eclipselogger.events.actions.CloseFileAction;
import eclipselogger.events.actions.DeleteFileAction;
import eclipselogger.events.actions.DeletePackageAction;
import eclipselogger.events.actions.EclipseAction;
import eclipselogger.events.actions.OpenNewFileAction;
import eclipselogger.events.actions.RefactorFileAction;
import eclipselogger.events.actions.RefactorPackageAction;
import eclipselogger.events.actions.SwitchToFileAction;

public class ConsoleActionLogger implements EclipseActiontLogIF {

	@Override
	public void logEclipseAction(final EclipseAction action, final boolean context) {
		if (action instanceof CloseFileAction) {
			logCloseFileAction(action);
		}
		if (action instanceof OpenNewFileAction) {
			logOpenedFileAction(action);
		}
		if (action instanceof AddFileAction) {
			logAddedFileAction(action);
		}
		if (action instanceof DeleteFileAction) {
			logDeleteFileAction(action);
		}
		if (action instanceof RefactorFileAction) {
			logFileRefactorAction(action);
		}
		if (action instanceof RefactorPackageAction) {
			logPackageRefactorAction(action);
		}
		if (action instanceof AddPackageAction) {
			logPackageAdded(action);
		}
		if (action instanceof DeletePackageAction) {
			logPackageDeleted(action);
		}
		if (action instanceof SwitchToFileAction) {
			logSwitchToFileAction(action);
		}
	}
	

	private void logDeleteFileAction(final EclipseAction action) {
		final DeleteFileAction deleteAction = (DeleteFileAction) action;
		System.out.println("============================================");
		System.out.println("File deleted: " + deleteAction.getDeletedFile());
		System.out.println("In same package: " + deleteAction.isSamePackage() + ", in same project: " + deleteAction.isSameProject() + ", same file type: " + deleteAction.isSameFileType());
		
		if (deleteAction.getFileChanges() != null) {
			System.out.println("Working time: "
					+ deleteAction.getFileWorkingTime() + " ms");
			System.out.println(deleteAction.getFileChanges());
		} else {
			System.out.println("Deleted file was not worked with!!!");
		}
		logCommonParams(action);
		System.out.println("============================================");
	}

	private void logCloseFileAction(final EclipseAction action) {
		final CloseFileAction closeAction = (CloseFileAction) action;
		System.out.println("============================================");
		System.out.println("File closed: " + closeAction.getClosedFile());
		System.out.println("In same package: " + closeAction.closedInSamePackage() + ", in same project: " + closeAction.closedInSameProject() + ", same file type: " + closeAction.isTheSameTypeAsPreviuos());
		
		if (closeAction.getFileChanges() != null) {
			System.out.println("Working time: " + closeAction.getFileWorkingTime() + " ms");
			System.out.println(closeAction.getFileChanges());
		}
		logCommonParams(action);
		System.out.println("============================================");
	}
	
	private void logOpenedFileAction(final EclipseAction action) {
		final OpenNewFileAction openAction = (OpenNewFileAction) action;
		System.out.println("============================================");
		System.out.println("File opened: " + openAction.getOpenedFile());
		System.out.println("In same package: " + openAction.openedInSamePackage() + ", in same project: " + openAction.openedInSameProject() + ", same file type: " + openAction.isTheSameTypeAsPreviuos());
		logCommonParams(action);
		System.out.println("============================================");
	}
	
	private void logSwitchToFileAction(final EclipseAction action) {
		final SwitchToFileAction switchAction = (SwitchToFileAction) action;
		System.out.println("============================================");
		System.out.println("File switched: " + switchAction.getSwitchedToFile().getProjectRelativePath());
		System.out.println("In same package: " + switchAction.openedInSamePackage() + ", in same project: " + switchAction.openedInSameProject() + ", same file type: " + switchAction.isTheSameTypeAsPreviuos());
		logCommonParams(action);
		System.out.println("============================================");
	}
	
	private void logAddedFileAction(final EclipseAction action) {
		final AddFileAction openAction = (AddFileAction) action;
		System.out.println("============================================");
		System.out.println("File added: " + openAction.getAddedFile());
		System.out.println("In same package: " + openAction.isSamePackage() + ", in same project: " + openAction.isSameProject());
		logCommonParams(action);
		System.out.println("============================================");
	}
	
	private void logFileRefactorAction(final EclipseAction action) {
		final RefactorFileAction fileAction = (RefactorFileAction) action;
		System.out.println("============================================");
		System.out.println("File refactored, old:" + fileAction.getOldFilePath() + ", new: " + fileAction.getNewFilePath());
		logCommonParams(action);
		System.out.println("============================================");
	}
	
	private void logPackageRefactorAction(final EclipseAction action) {
		final RefactorPackageAction packageAction = (RefactorPackageAction) action;
		System.out.println("============================================");
		System.out.println("Package refactored, old:" + packageAction.getOldFilePath() + ", new: " + packageAction.getNewFilePath());
		logCommonParams(action);
		System.out.println("============================================");
	}
	
	private void logPackageAdded(final EclipseAction action) {
		final AddPackageAction addAction = (AddPackageAction) action;
		System.out.println("============================================");
		System.out.println("Package added: " + addAction.getAddedPackage());
		System.out.println("Is in same package: " + addAction.isSamePackage());
		System.out.println("Is in same project: " + addAction.isSameProject());
		logCommonParams(action);
		System.out.println("============================================");
	}
	
	private void logPackageDeleted(final EclipseAction action) {
		final DeletePackageAction deleteAction = (DeletePackageAction) action;
		System.out.println("============================================");
		System.out.println("Package deleted: " + deleteAction.getDeletedPackage());
		System.out.println("Is in same package: " + deleteAction.isSamePackage());
		System.out.println("Is in same project: " + deleteAction.isSameProject());
		logCommonParams(action);
		System.out.println("============================================");
	}
	
	private void logCommonParams(final EclipseAction action) {
		System.out.println("Last action: " + action.getPreviousAction() + ", time since last action: " + action.getTimeSinceLastAction() + " ms");
	}
	

}
