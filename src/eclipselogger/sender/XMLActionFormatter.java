package eclipselogger.sender;

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

public class XMLActionFormatter implements ActionFormatterIF {

	@Override
	public String formatEclipseAction(final EclipseAction action) {
		final StringBuilder xml = new StringBuilder();
		final String commonParams = formatCommonEclipseParams(action);
		final String actionParams = formatSpecificEclipseActionParams(action);
		
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xml.append("\n");
		xml.append("<eclipseAction>");
		xml.append("\n");
		xml.append(commonParams);
		xml.append("\n");
		xml.append(actionParams);
		
		xml.append("</eclipseAction>");
		
		return xml.toString();
	}
	
	private String formatSpecificEclipseActionParams(final EclipseAction action) {
		String actionParams = null;
		switch (action.getActionType()){
		case ADD_FILE:
			actionParams = formatAddFileActionParams(action);
			break;
		case ADD_PACKAGE:
			actionParams = formatAddPakcageActionParams(action);
			break;
		case ADD_PROJECT:
			actionParams = formatAddProjectActionParams(action);
			break;
		case CLOSE_FILE:
			actionParams = formatCloseFileActionParams(action);
			break;
		case DELETE_FILE:
			actionParams = formatDeleteFileActionParams(action);
			break;
		case DELETE_PACKAGE:
			actionParams = formatDeletePakcageActionParams(action);
			break;
		case OPEN_FILE:
			actionParams = formatOpenFileActionParams(action);
			break;
		case REFACTOR_FILE:
			actionParams = formatRefactorFileActionParams(action);
			break;
		case REFACTOR_PACKAGE:
			actionParams = formatRefactorPackageActionParams(action);
			break;
		case SWITCH_FILE:
			actionParams = formatSwitchFileActionParams(action);
			break;
		}
		
		return actionParams;
	}
	
	public String formatCommonEclipseParams(final EclipseAction action) {
		final StringBuilder sb = new StringBuilder();
		
		sb.append("<actionType>");
		sb.append(action.getActionType().toString());
		sb.append("</actionType>");
		sb.append("\n");
		
		sb.append("<previousAction>");
		sb.append(action.getPreviousAction());
		sb.append("</previousAction>");
		sb.append("\n");
		
		sb.append("<timeSinceLastAction>");
		sb.append(action.getPreviousAction());
		sb.append("</timeSinceLastAction>");
		sb.append("\n");
		
		sb.append("<contextChange>");
		sb.append(action.getContextChange());
		sb.append("</contextChange>");
		sb.append("\n");
		
		return sb.toString();
	}
	
	public String formatAddFileActionParams(final EclipseAction a) {
		final AddFileAction action = (AddFileAction) a;
		final StringBuilder sb = new StringBuilder();
		
		sb.append("<addedFile>");
		sb.append(action.getAddedFile());
		sb.append("</addedFile>");
		sb.append("\n");
		
		sb.append("<previousFile>");
		sb.append(action.getPreviousFile());
		sb.append("</previousFile>");
		sb.append("\n");
		
		
		sb.append("<samePackage>");
		sb.append(action.isSamePackage());
		sb.append("</samePackage>");
		sb.append("\n");
		
		sb.append("<sameProject>");
		sb.append(action.isSameProject());
		sb.append("</sameProject>");
		sb.append("\n");
		
		sb.append("<sameFileType>");
		sb.append(action.isSameFileType());
		sb.append("</sameFileType>");
		sb.append("\n");
		
		return sb.toString();
	}
	
	public String formatAddPakcageActionParams(final EclipseAction a) {
		final AddPackageAction action = (AddPackageAction) a;
		final StringBuilder sb = new StringBuilder();
		
		sb.append("<addedPackage>");
		sb.append(action.getAddedPackage());
		sb.append("</addedPackage>");
		sb.append("\n");
		
		sb.append("<previousFile>");
		sb.append(action.getPreviousFile());
		sb.append("</previousFile>");
		sb.append("\n");
		
		
		sb.append("<samePackage>");
		sb.append(action.isSamePackage());
		sb.append("</samePackage>");
		sb.append("\n");
		
		sb.append("<sameProject>");
		sb.append(action.isSameProject());
		sb.append("</sameProject>");
		sb.append("\n");
		
		
		return sb.toString();
	}
	
	public String formatAddProjectActionParams(final EclipseAction a) {
		final AddProjectAction action = (AddProjectAction) a;
		final StringBuilder sb = new StringBuilder();
		
		sb.append("<projectName>");
		sb.append(action.getProjectName());
		sb.append("</projectName>");
		sb.append("\n");
		
		return sb.toString();
	}
	
	public String formatCloseFileActionParams(final EclipseAction a) {
		final CloseFileAction action = (CloseFileAction) a;
		final StringBuilder sb = new StringBuilder();
		
		sb.append("<closedFile>");
		sb.append(action.getClosedFile());
		sb.append("</closedFile>");
		sb.append("\n");
		
		sb.append("<previousFile>");
		sb.append(action.getPreviousFile());
		sb.append("</previousFile>");
		sb.append("\n");
		
		
		sb.append("<samePackage>");
		sb.append(action.closedInSamePackage());
		sb.append("</samePackage>");
		sb.append("\n");
		
		sb.append("<sameProject>");
		sb.append(action.closedInSameProject());
		sb.append("</sameProject>");
		sb.append("\n");
		
		sb.append("<sameFileType>");
		sb.append(action.isTheSameTypeAsPreviuos());
		sb.append("</sameFileType>");
		sb.append("\n");
		
		sb.append("<addedLines>");
		sb.append(action.getFileChanges().getAddedLines());
		sb.append("</addedLines>");
		sb.append("\n");
		
		sb.append("<deletedLines>");
		sb.append(action.getFileChanges().getDeletedLines());
		sb.append("</deletedLines>");
		sb.append("\n");
		
		sb.append("<changedLines>");
		sb.append(action.getFileChanges().getChangedLines());
		sb.append("</changedLines>");
		sb.append("\n");
		
		sb.append("<workingTime>");
		sb.append(action.getFileWorkingTime());
		sb.append("</workingTime>");
		sb.append("\n");
		
		return sb.toString();
	}
	
	public String formatDeleteFileActionParams(final EclipseAction a) {
		final DeleteFileAction action = (DeleteFileAction) a;
		final StringBuilder sb = new StringBuilder();
		
		sb.append("<deletedFile>");
		sb.append(action.getDeletedFile());
		sb.append("</deletedFile>");
		sb.append("\n");
		
		sb.append("<previousFile>");
		sb.append(action.getPreviousFile());
		sb.append("</previousFile>");
		sb.append("\n");
		
		
		sb.append("<samePackage>");
		sb.append(action.isSamePackage());
		sb.append("</samePackage>");
		sb.append("\n");
		
		sb.append("<sameProject>");
		sb.append(action.isSameProject());
		sb.append("</sameProject>");
		sb.append("\n");
		
		sb.append("<sameFileType>");
		sb.append(action.isSameFileType());
		sb.append("</sameFileType>");
		sb.append("\n");
		
		sb.append("<addedLines>");
		sb.append(action.getFileChanges().getAddedLines());
		sb.append("</addedLines>");
		sb.append("\n");
		
		sb.append("<deletedLines>");
		sb.append(action.getFileChanges().getDeletedLines());
		sb.append("</deletedLines>");
		sb.append("\n");
		
		sb.append("<changedLines>");
		sb.append(action.getFileChanges().getChangedLines());
		sb.append("</changedLines>");
		sb.append("\n");
		
		sb.append("<workingTime>");
		sb.append(action.getFileWorkingTime());
		sb.append("</workingTime>");
		sb.append("\n");
		
		return sb.toString();
	}
	
	public String formatDeletePakcageActionParams(final EclipseAction a) {
		final DeletePackageAction action = (DeletePackageAction) a;
		final StringBuilder sb = new StringBuilder();
		
		sb.append("<addedPackage>");
		sb.append(action.getDeletedPackage());
		sb.append("</addedPackage>");
		sb.append("\n");
		
		sb.append("<previousFile>");
		sb.append(action.getPreviousFile());
		sb.append("</previousFile>");
		sb.append("\n");
		
		
		sb.append("<samePackage>");
		sb.append(action.isSamePackage());
		sb.append("</samePackage>");
		sb.append("\n");
		
		sb.append("<sameProject>");
		sb.append(action.isSameProject());
		sb.append("</sameProject>");
		sb.append("\n");
		
		
		return sb.toString();
	}
	
	public String formatOpenFileActionParams(final EclipseAction a) {
		final OpenNewFileAction action = (OpenNewFileAction) a;
		final StringBuilder sb = new StringBuilder();
		
		sb.append("<openedFile>");
		sb.append(action.getOpenedFile());
		sb.append("</openedFile>");
		sb.append("\n");
		
		sb.append("<previousFile>");
		sb.append(action.getPreviousOpenedFile());
		sb.append("</previousFile>");
		sb.append("\n");
		
		
		sb.append("<samePackage>");
		sb.append(action.openedInSamePackage());
		sb.append("</samePackage>");
		sb.append("\n");
		
		sb.append("<sameProject>");
		sb.append(action.openedInSameProject());
		sb.append("</sameProject>");
		sb.append("\n");
		
		sb.append("<sameFileType>");
		sb.append(action.isTheSameTypeAsPreviuos());
		sb.append("</sameFileType>");
		sb.append("\n");
		
		return sb.toString();
	}
	
	public String formatRefactorFileActionParams(final EclipseAction a) {
		final RefactorFileAction action = (RefactorFileAction) a;
		final StringBuilder sb = new StringBuilder();
		
		sb.append("<refactoredFile>");
		sb.append(action.getRefactoredFile());
		sb.append("</refactoredFile>");
		sb.append("\n");
		
		sb.append("<previousFile>");
		sb.append(action.getPreviousFile());
		sb.append("</previousFile>");
		sb.append("\n");
		
		
		sb.append("<samePackage>");
		sb.append(action.isSamePackage());
		sb.append("</samePackage>");
		sb.append("\n");
		
		sb.append("<sameProject>");
		sb.append(action.isSameProject());
		sb.append("</sameProject>");
		sb.append("\n");
		
		sb.append("<refactorType>");
		sb.append(action.getRefactorType());
		sb.append("</refactorType>");
		sb.append("\n");
		
		return sb.toString();
	}
	
	public String formatRefactorPackageActionParams(final EclipseAction a) {
		final RefactorPackageAction action = (RefactorPackageAction) a;
		final StringBuilder sb = new StringBuilder();
		
		sb.append("<refactoredFile>");
		sb.append(action.getRefactoredPackage());
		sb.append("</refactoredFile>");
		sb.append("\n");
		
		sb.append("<previousFile>");
		sb.append(action.getPreviousFile());
		sb.append("</previousFile>");
		sb.append("\n");
		
		
		sb.append("<samePackage>");
		sb.append(action.isSamePackage());
		sb.append("</samePackage>");
		sb.append("\n");
		
		sb.append("<sameProject>");
		sb.append(action.isSameProject());
		sb.append("</sameProject>");
		sb.append("\n");
		
		sb.append("<refactorType>");
		sb.append(action.getRefactorType());
		sb.append("</refactorType>");
		sb.append("\n");
		
		return sb.toString();
	}
	
	public String formatSwitchFileActionParams(final EclipseAction a) {
		final SwitchToFileAction action = (SwitchToFileAction) a;
		final StringBuilder sb = new StringBuilder();
		
		sb.append("<openedFile>");
		sb.append(action.getSwitchedToFile());
		sb.append("</openedFile>");
		sb.append("\n");
		
		sb.append("<previousFile>");
		sb.append(action.getPreviousOpenedFile());
		sb.append("</previousFile>");
		sb.append("\n");
		
		
		sb.append("<samePackage>");
		sb.append(action.openedInSamePackage());
		sb.append("</samePackage>");
		sb.append("\n");
		
		sb.append("<sameProject>");
		sb.append(action.openedInSameProject());
		sb.append("</sameProject>");
		sb.append("\n");
		
		sb.append("<sameFileType>");
		sb.append(action.isTheSameTypeAsPreviuos());
		sb.append("</sameFileType>");
		sb.append("\n");
		
		return sb.toString();
	}

}
