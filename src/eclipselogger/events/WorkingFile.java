package eclipselogger.events;

import eclipselogger.resources.EclipseFile;
import eclipselogger.utils.FileChanges;

public class WorkingFile {
	private long workingTime;
	private final FileChanges fileChanges = new FileChanges();
	private final String path;
	
	public WorkingFile(final EclipseFile file) {
		this.path = file.getProjectRelativePath();
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
