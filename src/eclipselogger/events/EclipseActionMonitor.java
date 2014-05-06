package eclipselogger.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;

import eclipselogger.context.TaskContext;
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
import eclipselogger.gui.ContextChangeDialog;
import eclipselogger.logging.ConsoleActionLogger;
import eclipselogger.logging.DatabaseActionLogger;
import eclipselogger.logging.EclipseActiontLogIF;
import eclipselogger.logging.Log4jActionLogger;
import eclipselogger.resources.EclipseFile;
import eclipselogger.resources.EclipseFolder;
import eclipselogger.resources.EclipseProject;
import eclipselogger.utils.ActionsCache;
import eclipselogger.utils.ActualFileContentCache;
import eclipselogger.utils.ConfigReader;
import eclipselogger.utils.FileChanges;
import eclipselogger.utils.FileComparator;
import eclipselogger.utils.PackageDistanceCalculator;
import eclipselogger.views.LoggingView;

/**
 * This class represents a singleton used for monitoring all actions executed in Eclipse IDE
 * All EclipseAction objects are created here, methods are called from Eclipse event listeners
 * @author Tomas
 *
 */
public class EclipseActionMonitor {
	
	private static EclipseFile actualFile;
	private static EclipseFile previousFile;
	
	private static long fileWorkStart;
	private static EclipseAction lastAction;
	private static long lastActionTime;
	
	private static Logger logger = Logger.getLogger(EclipseActionMonitor.class);
	
	/**
	 * Default database logger - inserting actions into database
	 */
	private static EclipseActiontLogIF dbLogger = new DatabaseActionLogger();
	
	/**
	 * Popup window for confirming context change
	 */
	private static ContextChangeDialog contextDialog;
	
	/**
	 * List of loggers to log Eclipse actions 
	 */
	private static final List<EclipseActiontLogIF> loggers = new ArrayList<EclipseActiontLogIF>();
	
	/**
	 * Cache used for storing content of actual file. This content is used to compute file changes
	 */
	private static ActualFileContentCache fileContentCache = new ActualFileContentCache(); 
	
	/**
	 * Hash map of working files. Contains all opened files and key to map is project path to file 
	 */
	private static HashMap<String, WorkingFile> workingFiles = new LinkedHashMap<String, WorkingFile>();
	
	/**
	 * Cache used for storing recent actions
	 */
	private static ActionsCache recentActions = new ActionsCache();
	
	/**
	 * Actual task context of user
	 */
	private static TaskContext taskContext = new TaskContext();
	
	
	/**
	 * Initialization of Eclipse action loggers
	 * Database logger is used always to store actions in SQLite db an then can be sent to server
	 * Other loggers are configurable
	 */
	public static void init() {
		loggers.add(dbLogger);
		if (ConfigReader.getLoggers().contains(EclipseActiontLogIF.LOG4J_LOGGER)) {
			final EclipseActiontLogIF log4jlogger = new Log4jActionLogger();
			loggers.add(log4jlogger);
		}
		if (ConfigReader.getLoggers().contains(EclipseActiontLogIF.CONSOLE_LOGGER)) {
			final ConsoleActionLogger logger = new ConsoleActionLogger();
			loggers.add(logger);
		}
		
	}
	
	/**
	 * Reset of task context
	 * Task context is built from beginning
	 */
	public static void resetTaskContext() {
		logger.info("Resetting programmer's context...");
		taskContext = new TaskContext();
	}
	
	/**
	 * Update actual task context with action belonging to the task context 
	 * @param action Eclipse action that belongs to the task context
	 */
	public static void updateTaskContextWithFinishedAction(final EclipseAction action) {
		logger.info("Last action was not context change, updating task context");
		try {
			taskContext.updateContextWithAction(action);
		} catch (final Exception e) {
			logger.error("updateTaskContextWithFinishedAction()", e);
		}
	}
	
	
	/**
	 * Sets actual working file
	 * Stores actual file content and updates file changes for previous file
	 * @param file
	 * @param fileContent
	 */
	public static void setActualFile(final EclipseFile file, final String fileContent) {
		if (file == null) {
			logger.error(">>> setActualFile() called with null parameter");
			return;
		}
		updateActualFile(file);
		
		// TODO how to find out if the file is really worked with, implement some time filter
		// programmer must work with file at least 5 seconds
		previousFile = actualFile;
		if (previousFile == null) {
			logger.debug(">>>> Previous file is null!");
		} else {
			logger.debug(">>>> Previous file: " + previousFile.getProjectRelativePath());
		}
		
		actualFile = file;
		logger.debug("Actual file: " + actualFile.getProjectRelativePath());
		
		// store file content of opened / switched file
		setActualFileContent(fileContent);
		
		// check if file was already used, if not store it to working files
		checkWorkingFileAndAddIfNotInWorking(file);
	}
	
	/**
	 * Stores actual file content as String to cache
	 * @param file
	 */
	private static void setActualFileContent(final String content) {
		fileContentCache.setActualFileText(content);
		fileContentCache.applyChangedContent(content);
	}
	
	public static EclipseFile getActualFile() {
		return actualFile;
	}
	
	public static EclipseFile getPreviousFile() {
		return previousFile;
	}
	
	private static void checkWorkingFileAndAddIfNotInWorking(final EclipseFile file) {
		fileWorkStart = System.currentTimeMillis();
		if (!workingFiles.containsKey(file.getProjectRelativePath())) {
			final WorkingFile workFile = new WorkingFile(file);
			workingFiles.put(workFile.getWorkingFileKey(), workFile);
		}
	}
	
	private static void updateActualFile(final EclipseFile newFile) {
		// if actual file is not null and is not the same as the new file,
		// update working time for actual file
		if (actualFile != null && !actualFile.equals(newFile)) {
			final long stopWork = System.currentTimeMillis();
			final long workTime = stopWork - fileWorkStart;
			try {
				final WorkingFile workFile = workingFiles.get(actualFile.getProjectRelativePath());
				if (workFile != null) {
					workFile.increaseWorkingTime(workTime);

					final String fileContent = fileContentCache.getChangedContent();
					final FileChanges changes = FileComparator.getFileChanges(fileContentCache.getActualFileText(),
							fileContent);

					if (changes != null) {
						workFile.getFileChanges().updateFileChanges(changes);
						taskContext.updateFileChanges(changes);
					}

				}
			} catch (final Exception e) {
				logger.error("updateActualFile()", e);
			}
		}
	}
	
	/**
	 * Collects all required data and creates CloseFileAction
	 * Displays dialog with information about the close action
	 * @param file Closed file
	 */
	public static synchronized void closeFile(final EclipseFile file) {
		if (file == null) {
			logger.error("closeFile() called with null file");
			return;
		}

		final WorkingFile closed = workingFiles.remove(file.getProjectRelativePath());
		EclipseFile previous = null;

		try {
			if (actualFile != null && actualFile.equals(file)) {
				previous = previousFile;
			} else {
				previous = actualFile;
			}

			// if last action was delete file action and file equals opened file,
			// close action should not be logged
			// as file is closed automatically after it was deleted
			if (lastAction.getActionType() == ActionType.DELETE_FILE
					&& ((DeleteFileAction) lastAction).getDeletedFile().equals(
							file.getProjectRelativePath())) {
				return;
			}

			final long timeSinceLastAction = (lastActionTime == 0) ? 0 : (System.currentTimeMillis() - lastActionTime);
			final String lastActions = recentActions.getLastActionsForEclipseAction();
			final int recentCount = recentActions.getRecentActionsWithSameType(ActionType.CLOSE_FILE).size();
			final int packageDistance = getPackageDistance(file);

			final CloseFileAction closeAction = new CloseFileAction(timeSinceLastAction, lastAction, lastActions,
					recentCount, file, previous, closed, packageDistance);
			closeAction.setResource(file);
			closeAction.applyContext(taskContext);
			
			showContextChangeDialog(closeAction);
			afterAction(closeAction);
		} catch (final Exception e) {
			logger.error("closeFile()", e);
		}
	}
	
	/**
	 * Collects all required data and creates a new OpenNewFileAction object.
	 * Displays dialog with information about the open action 
	 * @param file Opened file
	 * @param fileContent Actual content of opened file
	 */
	public static void openNewFile(final EclipseFile file, final String fileContent) {
		if (file == null) {
			logger.error("openNewFile() called with null file");
			return;
		}

		setActualFile(file, fileContent);

		// if last action was add file action and file equals opened file,
		// action should not be logged
		// as file is opened automatically after it was added
		if (lastAction != null && lastAction.getActionType() == ActionType.ADD_FILE
				&& ((AddFileAction) lastAction).getAddedFile().equals(file.getProjectRelativePath())) {
			return;
		}

		try {
			final long timeSinceLastAction = (lastActionTime == 0) ? 0 : (System.currentTimeMillis() - lastActionTime);
			final String lastActions = recentActions.getLastActionsForEclipseAction();
			final int recentCount = recentActions.getRecentActionsWithSameType(ActionType.OPEN_FILE).size();

			final int packageDistance = getPackageDistance(file);
			final OpenNewFileAction openAction = new OpenNewFileAction(timeSinceLastAction, lastAction, lastActions,
					recentCount, file, previousFile, packageDistance);
			openAction.setResource(file);
			openAction.applyContext(taskContext);
			
			showContextChangeDialog(openAction);
			afterAction(openAction); // TODO check if needed
		} catch (final Exception e) {
			logger.error("openNewFile()", e);
		}
	}
	
	/**
	 * Collects all required data and creates a new SwitchToFileAction object.
	 * Displays dialog with information about the switch action 
	 * @param file Switched file
	 * @param fileContent Actual content of switched file
	 */
	public synchronized static void switchToFile(final EclipseFile file, final String fileContent) {
		if ((file == null) || (actualFile != null && actualFile.equals(file))) {
			logger.debug("Switch executed on file already switched");
			return;
		}
		setActualFile(file, fileContent);

		try {
			final long timeSinceLastAction = (lastActionTime == 0) ? 0 : (System.currentTimeMillis() - lastActionTime);
			final String lastActions = recentActions.getLastActionsForEclipseAction();
			final int recentCount = recentActions.getRecentActionsWithSameType(ActionType.SWITCH_FILE).size();

			final int packageDistance = getPackageDistance(file);
			final SwitchToFileAction switchAction = new SwitchToFileAction(timeSinceLastAction, lastAction,
					lastActions, recentCount, file, previousFile, packageDistance);
			switchAction.setResource(file);
			switchAction.applyContext(taskContext);
			
			showContextChangeDialog(switchAction);
			afterAction(switchAction);
		} catch (final Exception e) {
			logger.error("switchToFile()", e);
		}
	}
	
	/**
	 * Collects all required data and creates a new RefactorPackageAction object
	 * Called when package or folder is moved or renamed
	 * @param oldPack Old package / folder 
	 * @param newPack Refactored package / folder
	 */
	public static void refactorPackage(final EclipseFolder oldPack, final EclipseFolder newPack) {
		if (oldPack == null || newPack == null) {
			logger.error("refactorPackage() called with null");
			return;
		}
		
		final long timeSinceLastAction = (lastActionTime == 0) ? 0 : (System.currentTimeMillis() - lastActionTime);
		final String lastActions = recentActions.getLastActionsForEclipseAction();
		final int recentCount = recentActions.getRecentActionsWithSameType(ActionType.REFACTOR_PACKAGE).size();
		
		try {
			final int packageDistance = getPackageDistance(oldPack);
			final RefactorPackageAction refPackAction = new RefactorPackageAction(timeSinceLastAction, lastAction, lastActions, recentCount, oldPack, newPack, previousFile, packageDistance);
			
			refPackAction.setResource(oldPack);
			refPackAction.applyContext(taskContext);
			
			showContextChangeDialog(refPackAction);
			afterAction(refPackAction);
		} catch (final Exception e) {
			logger.error("refactorPackage()", e);
		}
	}
	
	/**
	 * Collects all required data and creates a new RefactorFileAction object
	 * Called when file is moved or renamed
	 * @param oldFile File before refactoring
	 * @param newFile Refactored file
	 */
	public static void refactorFile(final EclipseFile oldFile, final EclipseFile newFile) {
		if (oldFile == null || newFile == null) {
			logger.error("refactorFile() called with null!");
			return;
		}

		final long timeSinceLastAction = (lastActionTime == 0) ? 0 : (System.currentTimeMillis() - lastActionTime);
		final String lastActions = recentActions.getLastActionsForEclipseAction();
		final int recentCount = recentActions.getRecentActionsWithSameType(ActionType.REFACTOR_FILE).size();

		try {
			final int packageDistance = getPackageDistance(oldFile);
			final RefactorFileAction refFileAction = new RefactorFileAction(timeSinceLastAction, lastAction,
					lastActions, recentCount, oldFile, newFile, previousFile, packageDistance);
			refFileAction.setResource(oldFile);
			refFileAction.applyContext(taskContext);
			
			showContextChangeDialog(refFileAction);
			afterAction(refFileAction);
		} catch (final Exception e) {
			logger.error("refactorFile()", e);
		}
	}
	
	/**
	 * Called if new folder / package is added in project
	 * @param folder Added folder / package
	 */
	public static void addFolder(final EclipseFolder folder) {
		if (folder == null) {
			logger.error("addFolder() called with null !");
			return;
		}
		final long timeSinceLastAction = (lastActionTime == 0) ? 0 : (System.currentTimeMillis() - lastActionTime);
		final String lastActions = recentActions.getLastActionsForEclipseAction();
		final int recentCount = recentActions.getRecentActionsWithSameType(ActionType.ADD_PACKAGE).size();

		try {
			final int packageDistance = getPackageDistance(folder);
			final AddPackageAction action = new AddPackageAction(timeSinceLastAction, lastAction, lastActions,
					recentCount, folder, actualFile, packageDistance);
			action.setResource(folder);
			action.applyContext(taskContext);
			
			showContextChangeDialog(action);
			afterAction(action);
		} catch (final Exception e) {
			logger.error("addFolder()", e);
		}
	}
	
	/**
	 * Called when folder / package is deleted from project
	 * @param folder Deleted package / folder
	 */
	public static synchronized void deleteFolder(final EclipseFolder folder) {
		if (folder == null) {
			logger.error("deleteFolder() called with null parameter !");
			return;
		}

		final long timeSinceLastAction = (lastActionTime == 0) ? 0 : (System.currentTimeMillis() - lastActionTime);
		final String lastActions = recentActions.getLastActionsForEclipseAction();
		final int recentCount = recentActions.getRecentActionsWithSameType(ActionType.DELETE_PACKAGE).size();

		try {
			final int packageDistance = getPackageDistance(folder);
			final DeletePackageAction action = new DeletePackageAction(timeSinceLastAction, lastAction, lastActions,
					recentCount, folder, actualFile, packageDistance);
			action.setResource(folder);
			action.applyContext(taskContext);
			
			showContextChangeDialog(action);
			afterAction(action);
		} catch (final Throwable e) {
			logger.error("deleteFolder()", e);
		}
	}
	
	/**
	 * Called when a new file is added to the project
	 * @param file Added file
	 */
	public static void addFile(final EclipseFile file) {
		if (file == null) {
			logger.error("addFile() called with null parameter !");
			return;
		}
		
		final long timeSinceLastAction = (lastActionTime == 0) ? 0 : (System.currentTimeMillis() - lastActionTime);
		final String lastActions = recentActions.getLastActionsForEclipseAction();
		final int recentCount = recentActions.getRecentActionsWithSameType(ActionType.ADD_FILE).size();
		
		try {
			final int packageDistance = getPackageDistance(file);
			final AddFileAction action = new AddFileAction(timeSinceLastAction, lastAction, lastActions, recentCount,
					file, actualFile, packageDistance);
			action.setResource(file);
			action.applyContext(taskContext);
			showContextChangeDialog(action);
			afterAction(action);
		} catch (final Exception e) {
			logger.error("addFile()", e);
		}
	}
	
	// TODO why previous file is null
	/**
	 * Called when a file is deleted from project 
	 * @param file Deleted file
	 */
	public static synchronized void deleteFile(final EclipseFile file) {
		if (file == null) {
			logger.error("deleteFile() called with null parameter");
			return;
		}
		EclipseFile previous;
		if (actualFile != null && actualFile.equals(file)) {
			previous = previousFile;
		} else {
			previous = actualFile;
		}
		final long timeSinceLastAction = (lastActionTime == 0) ? 0 : (System.currentTimeMillis() - lastActionTime);
		final String lastActions = recentActions.getLastActionsForEclipseAction();
		final int recentCount = recentActions.getRecentActionsWithSameType(ActionType.DELETE_FILE).size();

		try {
			final WorkingFile deleted = workingFiles.remove(file.getProjectRelativePath());
			final int packageDistance = getPackageDistance(file);
			final DeleteFileAction deleteFileAction = new DeleteFileAction(timeSinceLastAction, lastAction,
					lastActions, recentCount, file, previous, deleted, packageDistance);
			
			deleteFileAction.setResource(file);
			deleteFileAction.applyContext(taskContext);
			
			showContextChangeDialog(deleteFileAction);
			afterAction(deleteFileAction);
		} catch (final Throwable e) {
			logger.error("deleteFile()", e);
		}
	}
	

	/**
	 * Called when new project is created in workspace
	 * @param project New project
	 */
	public static void addProject(final EclipseProject project) {
		if (project == null) {
			logger.error("addProject() called with null parameter !");
			return;
		}
		
		final long timeSinceLastAction = (lastActionTime == 0) ? 0 : (System.currentTimeMillis() - lastActionTime);
		final String lastActions = recentActions.getLastActionsForEclipseAction();
		final int recentCount = recentActions.getRecentActionsWithSameType(ActionType.ADD_PROJECT).size();
		
		final AddProjectAction action = new AddProjectAction(timeSinceLastAction, lastAction, lastActions, recentCount, project, 0);
		action.setResource(project);
		action.applyContext(taskContext);
		
		showContextChangeDialog(action);
		afterAction(action);
	}
	
	private static void afterAction(final EclipseAction action) {
		lastAction = action;
		lastActionTime = System.currentTimeMillis();
		recentActions.addEclipseActionToChache(action);
	}
	
	/**
	 * Log eclipse action via all registered loggers
	 * Eclipse action is always inserted into database
	 * If action represents context change, task context is reseted, otherwise task context is updated
	 * @param action
	 * @param contextChange
	 */
	public static void logEclipseAction(final EclipseAction action, final boolean contextChange) {
		logger.debug("Logging Eclipse action ...");
		try {
			for (final EclipseActiontLogIF logger : loggers) {
				logger.logEclipseAction(action, contextChange);
			}
			if (contextChange) {
				resetTaskContext();
			} else {
				taskContext.updateContextWithAction(action);
			}
		} catch (final Exception e) {
			logger.error("logEclipseAction()", e);
		}
		
		// Refresh table with unsent entries
		try {
			LoggingView.refreshViewer();
		} catch (final Exception e) {
			logger.error("Failed to refresh unsent actions viewer", e);
		}
	}
	
	private static int getPackageDistance(final EclipseResource res) {
		if (lastAction == null || lastAction.getActionType() == ActionType.ADD_PROJECT) {
			return 0;
		} else {
			return PackageDistanceCalculator.calculatePackageDistance(res, lastAction.getResource());
		}
	}
	
	public static void applyContentChangesForActualFile(final String content) {
		fileContentCache.applyChangedContent(content);
	}
	
//	public static void initContextDialog(final ContextChangeDialog dialog) {
//		contextDialog = dialog;
//	}
	
	private static void showContextChangeDialog(final EclipseAction action) {
		//final Shell parentShell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
		if (!ConfigReader.getShowContextPopup()) {
			logEclipseAction(action, false);
			return;
		}
		
		Display.getDefault().asyncExec(new Runnable() {
		    @Override
			public void run() {
		    	if (contextDialog != null) {
					contextDialog.close();
				}
		    	
		    	contextDialog = new ContextChangeDialog(null);
				contextDialog.setActualAction(action);
				contextDialog.open();
				contextDialog.refreshActionDetails();
		    }
		});
		
	}
	
}
