package eclipselogger.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import eclipselogger.events.actions.CloseFileAction;
import eclipselogger.events.actions.EclipseAction;
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
	
	public static WorkingFile closeFile(IFile file) {
		WorkingFile closed = workingFiles.remove(file.getProjectRelativePath().toOSString());
		IFile previous = null;
		if (file.equals(getActualFile())) {
			previous = getPreviousFile();
		} else {
			previous = getActualFile();
		}
		CloseFileAction closeAction = new CloseFileAction(file, previous, closed);
		logger.logEclipseAction(closeAction);
		
		return closed;
	}
	
	public static void openNewFile(IFile file) {
		setActualFile(file);
	}
	
	public static void switchToFile(IFile file) {
		setActualFile(file);
	}
	
	
	
	
}
