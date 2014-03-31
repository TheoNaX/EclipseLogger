package eclipselogger.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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
import eclipselogger.utils.ActionsCache;
import eclipselogger.utils.ActualFileContentCache;
import eclipselogger.utils.ConfigReader;
import eclipselogger.utils.FileChanges;
import eclipselogger.utils.FileComparator;
import eclipselogger.utils.FileUtilities;
import eclipselogger.utils.PackageDistanceCalculator;

public class EclipseActionMonitor {
	
	private static IFile actualFile;
	private static IFile previousFile;
	private static long fileWorkStart;
	private static EclipseAction lastAction;
	private static long lastActionTime;
	
	private static Logger logger = Logger.getLogger(EclipseActionMonitor.class);
	
	// loggers
	private static EclipseActiontLogIF dbLogger = new DatabaseActionLogger();
	
	// popup window for confirming context change
	private static ContextChangeDialog contextDialog;
	
	// list of loggers to log Eclipse actions
	private static final List<EclipseActiontLogIF> loggers = new ArrayList<EclipseActiontLogIF>();
	
	// cache used for storing content of actual file. This content is used to compute file changes
	private static ActualFileContentCache fileContentCache = new ActualFileContentCache(); 
	
	// hash map of working files. Contains all opened files and key to map is project path to file
	private static HashMap<String, WorkingFile> workingFiles = new LinkedHashMap<String, WorkingFile>();
	
	// cache used for storing recent actions
	private static ActionsCache recentActions = new ActionsCache();
	
	// task context of user
	private static TaskContext taskContext = new TaskContext();
	
	// initialization of Eclipse action loggers
	// database logger is used always to store actions in SQLite db an then can be sent to server
	// other loggers are configurable
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
	
	public static void resetTaskContext() {
		logger.info("Resetting programmer's context...");
		taskContext = new TaskContext();
	}
	
	public static void updateTaskContextWithFinishedAction(final EclipseAction action) {
		logger.info("Last action was not context change, updating task context");
		try {
			taskContext.updateContextWithAction(action);
		} catch (final Exception e) {
			logger.error("updateTaskContextWithFinishedAction()", e);
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
			try {
				final WorkingFile workFile = workingFiles.get(actualFile.getProjectRelativePath().toOSString());
				if (workFile != null) {
					workFile.increaseWorkingTime(workTime);

					final String fileContent = FileUtilities.fileContentToString(actualFile);
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
	
	public static void closeFile(final IFile file) {
		if (file == null) {
			logger.error("closeFile() called with null file");
			return;
		}

		final WorkingFile closed = workingFiles.remove(file.getProjectRelativePath().toOSString());
		IFile previous = null;

		try {
			if (file.equals(getActualFile())) {
				previous = getPreviousFile();
			} else {
				previous = getActualFile();
			}

			// if last action was delete file action and file equals opened file,
			// close action should not be logged
			// as file is closed automatically after it was deleted
			if (lastAction.getActionType() == ActionType.DELETE_FILE
					&& ((DeleteFileAction) lastAction).getDeletedFile().equals(
							file.getProjectRelativePath().toOSString())) {
				return;
			}

			final long timeSinceLastAction = (lastActionTime == 0) ? 0 : (System.currentTimeMillis() - lastActionTime);
			final String lastActions = recentActions.getLastActionsForEclipseAction();
			final int recentCount = recentActions.getRecentActionsWithSameType(ActionType.CLOSE_FILE).size();
			final int packageDistance = getPackageDistance(file);

			final CloseFileAction closeAction = new CloseFileAction(timeSinceLastAction, lastAction, lastActions,
					recentCount, file, previous, closed, packageDistance);
			closeAction.applyContext(taskContext);
			closeAction.setResource(file);
			showContextChangeDialog(closeAction);
			afterAction(closeAction);
		} catch (final Exception e) {
			logger.error("closeFile()", e);
		}
	}
	
	public static void openNewFile(final IFile file) {
		if (file == null) {
			logger.error("openNewFile() called with null file");
			return;
		}

		setActualFile(file);

		// if last action was add file action and file equals opened file,
		// action should not be logged
		// as file is opened automatically after it was added
		if (lastAction != null && lastAction.getActionType() == ActionType.ADD_FILE
				&& ((AddFileAction) lastAction).getAddedFile().equals(file.getProjectRelativePath().toOSString())) {
			return;
		}

		try {
			final long timeSinceLastAction = (lastActionTime == 0) ? 0 : (System.currentTimeMillis() - lastActionTime);
			final String lastActions = recentActions.getLastActionsForEclipseAction();
			final int recentCount = recentActions.getRecentActionsWithSameType(ActionType.OPEN_FILE).size();

			final int packageDistance = getPackageDistance(file);
			final OpenNewFileAction openAction = new OpenNewFileAction(timeSinceLastAction, lastAction, lastActions,
					recentCount, file, previousFile, packageDistance);
			openAction.applyContext(taskContext);
			openAction.setResource(file);
			showContextChangeDialog(openAction);
			afterAction(openAction); // TODO check if needed
		} catch (final Exception e) {
			logger.error("openNewFile()", e);
		}
	}
	
	public static void switchToFile(final IFile file) {
		if ((file == null) || (actualFile != null && actualFile.equals(file))) {
			logger.debug("Switch executed on file already switched");
			return;
		}
		setActualFile(file);

		try {
			final long timeSinceLastAction = (lastActionTime == 0) ? 0 : (System.currentTimeMillis() - lastActionTime);
			final String lastActions = recentActions.getLastActionsForEclipseAction();
			final int recentCount = recentActions.getRecentActionsWithSameType(ActionType.SWITCH_FILE).size();

			final int packageDistance = getPackageDistance(file);
			final SwitchToFileAction switchAction = new SwitchToFileAction(timeSinceLastAction, lastAction,
					lastActions, recentCount, file, previousFile, packageDistance);
			switchAction.applyContext(taskContext);
			switchAction.setResource(file);
			showContextChangeDialog(switchAction);
			afterAction(switchAction);
		} catch (final Exception e) {
			logger.error("switchToFile()", e);
		}
	}
	
	public static void refactorPackage(final IFolder oldPack, final IFolder newPack) {
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
			showContextChangeDialog(refPackAction);
			refPackAction.applyContext(taskContext);
			refPackAction.setResource(oldPack);
			afterAction(refPackAction);
		} catch (final Exception e) {
			logger.error("refactorPackage()", e);
		}
	}
	
	
	public static void refactorFile(final IFile oldFile, final IFile newFile) {
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
			refFileAction.applyContext(taskContext);
			refFileAction.setResource(oldFile);
			showContextChangeDialog(refFileAction);
			afterAction(refFileAction);
		} catch (final Exception e) {
			logger.error("refactorFile()", e);
		}
	}
	
	public static void addFolder(final IFolder folder) {
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
			action.applyContext(taskContext);
			action.setResource(folder);
			showContextChangeDialog(action);
			afterAction(action);
		} catch (final Exception e) {
			logger.error("addFolder()", e);
		}
	}
	
	public static void deleteFolder(final IFolder folder) {
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
			action.applyContext(taskContext);
			action.setResource(folder);
			showContextChangeDialog(action);
			afterAction(action);
		} catch (final Exception e) {
			logger.error("deleteFolder()", e);
		}
	}
	
	public static void addFile(final IFile file) {
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
			action.applyContext(taskContext);
			action.setResource(file);
			showContextChangeDialog(action);
			afterAction(action);
		} catch (final Exception e) {
			logger.error("addFile()", e);
		}
	}
	
	public static void deleteFile(final IFile file) {
		if (file == null) {
			logger.error("deleteFile() called with null parameter");
			return;
		}
		IFile previous;
		if (file.equals(actualFile)) {
			previous = previousFile;
		} else {
			previous = actualFile;
		}
		final long timeSinceLastAction = (lastActionTime == 0) ? 0 : (System.currentTimeMillis() - lastActionTime);
		final String lastActions = recentActions.getLastActionsForEclipseAction();
		final int recentCount = recentActions.getRecentActionsWithSameType(ActionType.DELETE_FILE).size();

		try {
			final WorkingFile deleted = workingFiles.remove(file.getProjectRelativePath().toOSString());
			final int packageDistance = getPackageDistance(file);
			final DeleteFileAction deleteFileAction = new DeleteFileAction(timeSinceLastAction, lastAction,
					lastActions, recentCount, file, previous, deleted, packageDistance);
			deleteFileAction.applyContext(taskContext);
			deleteFileAction.setResource(file);
			showContextChangeDialog(deleteFileAction);
			afterAction(deleteFileAction);
		} catch (final Exception e) {
			logger.error("deleteFile()", e);
		}
	}
	

	public static void addProject(final IProject project) {
		if (project == null) {
			logger.error("addProject() called with null parameter !");
			return;
		}
		
		final long timeSinceLastAction = (lastActionTime == 0) ? 0 : (System.currentTimeMillis() - lastActionTime);
		final String lastActions = recentActions.getLastActionsForEclipseAction();
		final int recentCount = recentActions.getRecentActionsWithSameType(ActionType.ADD_PROJECT).size();
		
		final AddProjectAction action = new AddProjectAction(timeSinceLastAction, lastAction, lastActions, recentCount, project, 0);
		action.applyContext(taskContext);
		action.setResource(project);
		showContextChangeDialog(action);
		afterAction(action);
	}
	
	private static void afterAction(final EclipseAction action) {
		lastAction = action;
		lastActionTime = System.currentTimeMillis();
		recentActions.addEclipseActionToChache(action);
	}
	
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
	}
	
	private static int getPackageDistance(final IResource res) {
		if (lastAction == null || lastAction.getActionType() == ActionType.ADD_PROJECT) {
			return 0;
		} else {
			return PackageDistanceCalculator.calculatePackageDistance(res, lastAction.getResource());
		}
	}
	
//	public static void initContextDialog(final ContextChangeDialog dialog) {
//		contextDialog = dialog;
//	}
	
	private static void showContextChangeDialog(final EclipseAction action) {
		//final Shell parentShell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
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
