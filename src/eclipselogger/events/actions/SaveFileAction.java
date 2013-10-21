package eclipselogger.events.actions;

import org.eclipse.core.resources.IFile;

import eclipselogger.events.WorkingFile;

public class SaveFileAction extends EclipseAction {
	private IFile file;
	private WorkingFile workFile;
	
	public SaveFileAction(IFile file, WorkingFile workFile) {
		this.file = file;
		this.workFile = workFile;
	}

	public IFile getFile() {
		return file;
	}

	public WorkingFile getWorkFile() {
		return workFile;
	}
	
	
}
