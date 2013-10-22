package eclipselogger.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

import eclipselogger.events.actions.AddFileAction;
import eclipselogger.events.actions.AddPackageAction;
import eclipselogger.events.actions.AddProjectAction;
import eclipselogger.events.actions.CloseFileAction;
import eclipselogger.events.actions.DeleteFileAction;
import eclipselogger.events.actions.DeletePackageAction;
import eclipselogger.events.actions.DeleteProjectAction;
import eclipselogger.events.actions.EclipseAction;
import eclipselogger.events.actions.OpenNewFileAction;
import eclipselogger.events.actions.RefactorFileAction;
import eclipselogger.events.actions.RefactorPackageAction;
import eclipselogger.events.actions.SaveFileAction;
import eclipselogger.events.actions.SwitchToFileAction;
import eclipselogger.logging.ConsoleActionLogger;
import eclipselogger.logging.EclipseActiontLogIF;

public class EclipseActionMonitor {
	private static IFile actualFile;
	private static IFile previousFile;
	private static IProject actualProject;
	private static long fileWorkStart; 
	
	// logger
	private static EclipseActiontLogIF logger = new ConsoleActionLogger();
	
	private static final int LAST_ACTIONS_PRESERVED_COUNT = 10;
	
	private static HashMap<String, WorkingFile> workingFiles = new LinkedHashMap<String, WorkingFile>();
	private static List<EclipseAction> lastActions = new ArrayList<EclipseAction>();
	
	public static void setActualFile(IFile file) {
		updateActualFile(file);
		previousFile = actualFile;
		actualFile = file;
		checkWorkingFileAndAddIfNotInWorking(file);
	}
	
	public static IFile getActualFile() {
		return actualFile;
	}
	
	public static IFile getPreviousFile() {
		return previousFile;
	}
	
	private static void checkWorkingFileAndAddIfNotInWorking(IFile file) {
		// TODO really use project relative? If more projects with the same packages? 
		fileWorkStart = System.currentTimeMillis();
		if (!workingFiles.containsKey(file.getProjectRelativePath())) {
			WorkingFile workFile = new WorkingFile(file);
			workingFiles.put(workFile.getWorkingFileKey(), workFile);
		}
	}
	
	private static void updateActualFile(IFile newFile) {
		// if actual file is not null and is not the same as the new file,
		// update working time for actual file
		if (actualFile != null && !actualFile.equals(newFile)) {
			long stopWork = System.currentTimeMillis();
			long workTime = stopWork - fileWorkStart;
			WorkingFile workFile = workingFiles.get(actualFile.getProjectRelativePath().toOSString());
			if (workFile != null) {
				System.out.println("Working file is not null, increasing working time");
				workFile.increaseWorkingTime(workTime);
			}
		}
	}
	
	public static void closeFile(IFile file) {
		WorkingFile closed = workingFiles.remove(file.getProjectRelativePath().toOSString());
		IFile previous = null;
		if (file.equals(getActualFile())) {
			previous = getPreviousFile();
		} else {
			previous = getActualFile();
		}
		CloseFileAction closeAction = new CloseFileAction(file, previous, closed);
		logger.logEclipseAction(closeAction);
	}
	
	public static void openNewFile(IFile file) {
		setActualFile(file);
		OpenNewFileAction openAction = new OpenNewFileAction(file, previousFile);
		logger.logEclipseAction(openAction);
	}
	
	public static void switchToFile(IFile file) {
		setActualFile(file);
		SwitchToFileAction switchAction = new SwitchToFileAction(file, previousFile);
		logger.logEclipseAction(switchAction);
	}
	
	public static void refactorPackage(IFolder oldPack, IFolder newPack) {
		RefactorPackageAction refPackAction = new RefactorPackageAction(oldPack.getProjectRelativePath().toOSString(), newPack.getProjectRelativePath().toOSString());
		logger.logEclipseAction(refPackAction);
	}
	
	public static void refactorFile(IFile oldFile, IFile newFile) {
		RefactorFileAction refFileAction = new RefactorFileAction(oldFile.getProjectRelativePath().toOSString(), newFile.getProjectRelativePath().toOSString());
		logger.logEclipseAction(refFileAction);
	}
	
	public static void addFolder(IFolder folder) {
		AddPackageAction action = new AddPackageAction(folder, actualFile);
		logger.logEclipseAction(action);
	}
	
	public static void deleteFolder(IFolder folder) {
		DeletePackageAction action = new DeletePackageAction(folder, actualFile);
		logger.logEclipseAction(action);
	}
	
	public static void addFile(IFile file) {
		AddFileAction action = new AddFileAction(file, actualFile);
		logger.logEclipseAction(action);
	}
	
	public static void deleteFile(IFile file) {
		IFile previous;
		if (file.equals(actualFile)) {
			previous = previousFile;
		} else {
			previous = actualFile;
		}
		DeleteFileAction deleteFile = new DeleteFileAction(file, previous);
		logger.logEclipseAction(deleteFile);
	}
	
	public static void fileChanged(IFile file) {
		if (file.equals(actualFile)) {
			WorkingFile saved = workingFiles.get(file.getProjectRelativePath().toOSString());
			SaveFileAction saveAction = new SaveFileAction(file, saved);
			logger.logEclipseAction(saveAction);
		}
	}

	public static void addProject(IProject project) {
		AddProjectAction action = new AddProjectAction(project);
		logger.logEclipseAction(action);
	}

	public static void deleteProject(IProject project) {
		DeleteProjectAction action = new DeleteProjectAction(project);
		logger.logEclipseAction(action);
	}
	
	
	
	
}
