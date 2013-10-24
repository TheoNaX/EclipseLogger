package eclipselogger.events;

import org.eclipse.core.resources.IFile;

import eclipselogger.utils.FileChanges;

public class WorkingFile {
	private IFile file;
	private long workingTime;
	private FileChanges fileChanges = new FileChanges();
	
	public WorkingFile(IFile file) {
		this.file = file;
	}
	
	public void increaseWorkingTime(long time) {
		this.workingTime += time;
	}
	
	public String getWorkingFileKey() {
		return this.file.getProjectRelativePath().toOSString();
	}
	
	public long getWorkingTime() {
		return this.workingTime;
	}
	
	public FileChanges getFileChanges() {
		return this.fileChanges;
	}
	
}
