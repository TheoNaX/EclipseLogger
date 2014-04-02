package eclipselogger.events;

import org.eclipse.core.resources.IFile;

import eclipselogger.utils.FileChanges;

public class WorkingFile {
	private long workingTime;
	private final FileChanges fileChanges = new FileChanges();
	private final String path;
	
	public WorkingFile(final IFile file) {
		this.path = file.getProjectRelativePath().toOSString();
	}
	
	public void increaseWorkingTime(final long time) {
		this.workingTime += time;
	}
	
	public String getWorkingFileKey() {
		return this.path;
	}
	
	public long getWorkingTime() {
		return this.workingTime;
	}
	
	public FileChanges getFileChanges() {
		return this.fileChanges;
	}
	
}
