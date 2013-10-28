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
	public void logEclipseAction(EclipseAction action) {
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
	

	private void logDeleteFileAction(EclipseAction action) {
		DeleteFileAction deleteAction = (DeleteFileAction) action;
		System.out.println("============================================");
		System.out.println("File deleted: " + deleteAction.getDeletedFile().getProjectRelativePath());
		System.out.println("In same package: " + deleteAction.isSamePackage() + ", in same project: " + deleteAction.isSameProject() + ", same file type: " + deleteAction.isSameFileType());
		
		if (deleteAction.getWorkingFile() != null) {
			System.out.println("Working time: "
					+ deleteAction.getWorkingFile().getWorkingTime() + " ms");
			System.out.println(deleteAction.getWorkingFile().getFileChanges());
		} else {
			System.out.println("Deleted file was not worked with!!!");
		}
		logCommonParams(action);
		System.out.println("============================================");
	}

	private void logCloseFileAction(EclipseAction action) {
		CloseFileAction closeAction = (CloseFileAction) action;
		System.out.println("============================================");
		System.out.println("File closed: " + closeAction.getClosedFile().getProjectRelativePath());
		System.out.println("In same package: " + closeAction.closedInSamePackage() + ", in same project: " + closeAction.closedInSameProject() + ", same file type: " + closeAction.isTheSameTypeAsPreviuos());
		
		if (closeAction.getWorkingFile() != null) {
			System.out.println("Working time: " + closeAction.getWorkingFile().getWorkingTime() + " ms");
			System.out.println(closeAction.getWorkingFile().getFileChanges());
		}
		logCommonParams(action);
		System.out.println("============================================");
	}
	
	private void logOpenedFileAction(EclipseAction action) {
		OpenNewFileAction openAction = (OpenNewFileAction) action;
		System.out.println("============================================");
		System.out.println("File opened: " + openAction.getOpenedFile().getProjectRelativePath());
		System.out.println("In same package: " + openAction.openedInSamePackage() + ", in same project: " + openAction.openedInSameProject() + ", same file type: " + openAction.isTheSameTypeAsPreviuos());
		logCommonParams(action);
		System.out.println("============================================");
	}
	
	private void logSwitchToFileAction(EclipseAction action) {
		SwitchToFileAction switchAction = (SwitchToFileAction) action;
		System.out.println("============================================");
		System.out.println("File switched: " + switchAction.getSwitchedToFile().getProjectRelativePath());
		System.out.println("In same package: " + switchAction.openedInSamePackage() + ", in same project: " + switchAction.openedInSameProject() + ", same file type: " + switchAction.isTheSameTypeAsPreviuos());
		logCommonParams(action);
		System.out.println("============================================");
	}
	
	private void logAddedFileAction(EclipseAction action) {
		AddFileAction openAction = (AddFileAction) action;
		System.out.println("============================================");
		System.out.println("File added: " + openAction.getAddedFile().getProjectRelativePath());
		System.out.println("In same package: " + openAction.isSamePackage() + ", in same project: " + openAction.isSameProject());
		logCommonParams(action);
		System.out.println("============================================");
	}
	
	private void logFileRefactorAction(EclipseAction action) {
		RefactorFileAction fileAction = (RefactorFileAction) action;
		System.out.println("============================================");
		System.out.println("File refactored, old:" + fileAction.getOldFilePath() + ", new: " + fileAction.getNewFilePath());
		logCommonParams(action);
		System.out.println("============================================");
	}
	
	private void logPackageRefactorAction(EclipseAction action) {
		RefactorPackageAction packageAction = (RefactorPackageAction) action;
		System.out.println("============================================");
		System.out.println("Package refactored, old:" + packageAction.getOldFilePath() + ", new: " + packageAction.getNewFilePath());
		logCommonParams(action);
		System.out.println("============================================");
	}
	
	private void logPackageAdded(EclipseAction action) {
		AddPackageAction addAction = (AddPackageAction) action;
		System.out.println("============================================");
		System.out.println("Package added: " + addAction.getAddedPackage().getProjectRelativePath());
		System.out.println("Is in same package: " + addAction.isSamePackage());
		System.out.println("Is in same project: " + addAction.isSameProject());
		logCommonParams(action);
		System.out.println("============================================");
	}
	
	private void logPackageDeleted(EclipseAction action) {
		DeletePackageAction deleteAction = (DeletePackageAction) action;
		System.out.println("============================================");
		System.out.println("Package deleted: " + deleteAction.getAddedPackage().getProjectRelativePath());
		System.out.println("Is in same package: " + deleteAction.isSamePackage());
		System.out.println("Is in same project: " + deleteAction.isSameProject());
		logCommonParams(action);
		System.out.println("============================================");
	}
	
	private void logCommonParams(EclipseAction action) {
		System.out.println("Last action: " + action.getPreviousAction() + ", time since last action: " + action.getTimeSinceLastAction() + " ms");
	}
	

}
