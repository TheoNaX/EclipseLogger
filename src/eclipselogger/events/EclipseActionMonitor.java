package eclipselogger.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

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
import eclipselogger.logging.ConsoleActionLogger;
import eclipselogger.logging.DatabaseActionLogger;
import eclipselogger.logging.EclipseActiontLogIF;
import eclipselogger.logging.Log4jActionLogger;
import eclipselogger.utils.ActionsCache;
import eclipselogger.utils.ActualFileContentCache;
import eclipselogger.utils.ConfigReader;
import eclipselogger.utils.FileChanges;
import eclipselogger.utils.FileComparator;
import eclipselogger.utils.FileUtilities;

public class EclipseActionMonitor {
	private static boolean DEFAULT_CONTEXT = false;
	
	private static IFile actualFile;
	private static IFile previousFile;
	private static long fileWorkStart;
	private static EclipseAction lastAction;
	private static long lastActionTime;
	
	// loggers
	private static EclipseActiontLogIF dbLogger = new DatabaseActionLogger();
	
	
	private static final List<EclipseActiontLogIF> loggers = new ArrayList<EclipseActiontLogIF>();
	
	private static ActualFileContentCache fileContentCache = new ActualFileContentCache(); 
	
	private static HashMap<String, WorkingFile> workingFiles = new LinkedHashMap<String, WorkingFile>();
	private static ActionsCache recentActions = new ActionsCache();
	
	public static void init() {
		loggers.add(dbLogger);
		if (ConfigReader.getLoggers().contains(EclipseActiontLogIF.LOG4J_LOGGER)) {
			final EclipseActiontLogIF log4jlogger = new Log4jActionLogger();
			loggers.add(log4jlogger);
		}
		if (ConfigReader.getLoggers().contains(EclipseActiontLogIF.LOG4J_LOGGER)) {
			final ConsoleActionLogger logger = new ConsoleActionLogger();
			loggers.add(logger);
		}
		
	}
	
	public static void setActualFile(final IFile file) {
		updateActualFile(file);
		
		// TODO how to find out if the file is really worked with, implement some time filter
		// programmer must work with file at least 5 seconds
		previousFile = actualFile;
		actualFile = file;
		
		// store file content of opened / switched file
		setActualFileContent(file);
		
		// check if file was already used, if not store it to working files
		checkWorkingFileAndAddIfNotInWorking(file);
	}
	
	/**
	 * Stores actual file content as String to cache
	 * @param file
	 */
	private static void setActualFileContent(final IFile file) {
		final String content = FileUtilities.fileContentToString(file);
		fileContentCache.setActualFileText(content);
	}
	
	public static IFile getActualFile() {
		return actualFile;
	}
	
	public static IFile getPreviousFile() {
		return previousFile;
	}
	
	private static void checkWorkingFileAndAddIfNotInWorking(final IFile file) {
		// TODO really use project relative? If more projects with the same packages? 
		fileWorkStart = System.currentTimeMillis();
		if (!workingFiles.containsKey(file.getProjectRelativePath().toOSString())) {
			final WorkingFile workFile = new WorkingFile(file);
			workingFiles.put(workFile.getWorkingFileKey(), workFile);
		}
	}
	
	private static void updateActualFile(final IFile newFile) {
		// if actual file is not null and is not the same as the new file,
		// update working time for actual file
		if (actualFile != null && !actualFile.equals(newFile)) {
			final long stopWork = System.currentTimeMillis();
			final long workTime = stopWork - fileWorkStart;
			final WorkingFile workFile = workingFiles.get(actualFile.getProjectRelativePath().toOSString());
			if (workFile != null) {
				System.out.println("Update file: Working file is not null, increasing working time");
				workFile.increaseWorkingTime(workTime);
				
				System.out.println(">>>>> Resolving file changes ...");
				final String fileContent = FileUtilities.fileContentToString(actualFile);
				final FileChanges changes = FileComparator.getFileChanges(fileContentCache.getActualFileText(), fileContent);
				
				if (changes != null) {
					System.out.println(">>>> Updating file changes: " + changes);
					workFile.getFileChanges().updateFileChanges(changes);
				}
				
			}
		}
	}
	
	public static void closeFile(final IFile file) {
		final WorkingFile closed = workingFiles.remove(file.getProjectRelativePath().toOSString());
		IFile previous = null;
		if (file.equals(getActualFile())) {
			previous = getPreviousFile();
		} else {
			previous = getActualFile();
		}
		
		final long timeSinceLastAction = (lastActionTime == 0) ? 0 : (System.currentTimeMillis() - lastActionTime);
		final String lastActions = recentActions.getLastActionsForEclipseAction();
		final int recentCount = recentActions.getRecentActionsWithSameType(ActionType.CLOSE_FILE).size();
		final CloseFileAction closeAction = new CloseFileAction(timeSinceLastAction, lastAction, lastActions, recentCount, file, previous, closed);
		logEclipseAction(closeAction, DEFAULT_CONTEXT);
		afterAction(closeAction);
	}
	
	public static void openNewFile(final IFile file) {
		setActualFile(file);
		
		final long timeSinceLastAction = (lastActionTime == 0) ? 0 : (System.currentTimeMillis() - lastActionTime);
		final String lastActions = recentActions.getLastActionsForEclipseAction();
		final int recentCount = recentActions.getRecentActionsWithSameType(ActionType.OPEN_FILE).size();
		
		final OpenNewFileAction openAction = new OpenNewFileAction(timeSinceLastAction, lastAction, lastActions, recentCount, file, previousFile);
		logEclipseAction(openAction, DEFAULT_CONTEXT);
		afterAction(openAction); // TODO check if needed 
	}
	
	public static void switchToFile(final IFile file) {
		setActualFile(file);
		
		final long timeSinceLastAction = (lastActionTime == 0) ? 0 : (System.currentTimeMillis() - lastActionTime);
		final String lastActions = recentActions.getLastActionsForEclipseAction();
		final int recentCount = recentActions.getRecentActionsWithSameType(ActionType.SWITCH_FILE).size();
		
		final SwitchToFileAction switchAction = new SwitchToFileAction(timeSinceLastAction, lastAction, lastActions, recentCount, file, previousFile);
		logEclipseAction(switchAction, DEFAULT_CONTEXT);
		afterAction(switchAction);
	}
	
	public static void refactorPackage(final IFolder oldPack, final IFolder newPack) {
		final long timeSinceLastAction = (lastActionTime == 0) ? 0 : (System.currentTimeMillis() - lastActionTime);
		final String lastActions = recentActions.getLastActionsForEclipseAction();
		final int recentCount = recentActions.getRecentActionsWithSameType(ActionType.SWITCH_FILE).size();
		
		final RefactorPackageAction refPackAction = new RefactorPackageAction(timeSinceLastAction, lastAction, lastActions, recentCount, oldPack, newPack, previousFile);
		logEclipseAction(refPackAction, DEFAULT_CONTEXT);
		afterAction(refPackAction);
	}
	
	public static void refactorFile(final IFile oldFile, final IFile newFile) {
		final long timeSinceLastAction = (lastActionTime == 0) ? 0 : (System.currentTimeMillis() - lastActionTime);
		final String lastActions = recentActions.getLastActionsForEclipseAction();
		final int recentCount = recentActions.getRecentActionsWithSameType(ActionType.REFACTOR_FILE).size();
		
		final RefactorFileAction refFileAction = new RefactorFileAction(timeSinceLastAction, lastAction, lastActions, recentCount, oldFile, newFile, previousFile);
		logEclipseAction(refFileAction, DEFAULT_CONTEXT);
		afterAction(refFileAction);
	}
	
	public static void addFolder(final IFolder folder) {
		final long timeSinceLastAction = (lastActionTime == 0) ? 0 : (System.currentTimeMillis() - lastActionTime);
		final String lastActions = recentActions.getLastActionsForEclipseAction();
		final int recentCount = recentActions.getRecentActionsWithSameType(ActionType.ADD_PACKAGE).size();
		
		final AddPackageAction action = new AddPackageAction(timeSinceLastAction, lastAction, lastActions, recentCount, folder, actualFile);
		logEclipseAction(action, DEFAULT_CONTEXT);
		afterAction(action);
	}
	
	public static void deleteFolder(final IFolder folder) {
		final long timeSinceLastAction = (lastActionTime == 0) ? 0 : (System.currentTimeMillis() - lastActionTime);
		final String lastActions = recentActions.getLastActionsForEclipseAction();
		final int recentCount = recentActions.getRecentActionsWithSameType(ActionType.DELETE_FILE).size();
		
		final DeletePackageAction action = new DeletePackageAction(timeSinceLastAction, lastAction, lastActions, recentCount, folder, actualFile);
		logEclipseAction(action, DEFAULT_CONTEXT);
		afterAction(action);
	}
	
	public static void addFile(final IFile file) {
		final long timeSinceLastAction = (lastActionTime == 0) ? 0 : (System.currentTimeMillis() - lastActionTime);
		final String lastActions = recentActions.getLastActionsForEclipseAction();
		final int recentCount = recentActions.getRecentActionsWithSameType(ActionType.ADD_FILE).size();
		
		final AddFileAction action = new AddFileAction(timeSinceLastAction, lastAction, lastActions, recentCount, file, actualFile);
		logEclipseAction(action, DEFAULT_CONTEXT);
		afterAction(action);
	}
	
	public static void deleteFile(final IFile file) {
		IFile previous;
		if (file.equals(actualFile)) {
			previous = previousFile;
		} else {
			previous = actualFile;
		}
		final long timeSinceLastAction = (lastActionTime == 0) ? 0 : (System.currentTimeMillis() - lastActionTime);
		final String lastActions = recentActions.getLastActionsForEclipseAction();
		final int recentCount = recentActions.getRecentActionsWithSameType(ActionType.DELETE_FILE).size();
		
		final WorkingFile deleted = workingFiles.remove(file.getProjectRelativePath().toOSString());
		final DeleteFileAction deleteFileAction = new DeleteFileAction(timeSinceLastAction, lastAction, lastActions, recentCount, file, previous, deleted);
		logEclipseAction(deleteFileAction, DEFAULT_CONTEXT);
		afterAction(deleteFileAction);
	}
	

	public static void addProject(final IProject project) {
		final long timeSinceLastAction = (lastActionTime == 0) ? 0 : (System.currentTimeMillis() - lastActionTime);
		final String lastActions = recentActions.getLastActionsForEclipseAction();
		final int recentCount = recentActions.getRecentActionsWithSameType(ActionType.ADD_PROJECT).size();
		
		final AddProjectAction action = new AddProjectAction(timeSinceLastAction, lastAction, lastActions, recentCount, project);
		logEclipseAction(action, DEFAULT_CONTEXT);
		afterAction(action);
	}
	
	private static void afterAction(final EclipseAction action) {
		lastAction = action;
		lastActionTime = System.currentTimeMillis();
		recentActions.addEclipseActionToChache(action);
	}
	
	public static void logEclipseAction(final EclipseAction action, final boolean contextChange) {
		for (final EclipseActiontLogIF logger : loggers) {
			logger.logEclipseAction(action, contextChange);
		}
	}
	
	
	
}
