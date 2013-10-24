package eclipselogger.events.actions;

import org.eclipse.core.resources.IFile;

import eclipselogger.events.WorkingFile;
import eclipselogger.utils.FileValidator;
import eclipselogger.utils.PackageUtils;


public class DeleteFileAction extends EclipseAction {
	private IFile deletedFile;
	private IFile previousFile;
	private boolean samePackage;
	private boolean sameProject;
	private boolean sameFileType;
	private WorkingFile workFile;
	
	// TODO implement count of deleted files within some period ???
	
	public DeleteFileAction(IFile deletedFile, IFile previoiusFile, WorkingFile workFile) {
		this.deletedFile = deletedFile;
		this.previousFile = previoiusFile;
		this.workFile = workFile;
		samePackage = PackageUtils.checkIfSamePackage(deletedFile, previoiusFile);
		sameProject = PackageUtils.checkIfSameProject(deletedFile, previoiusFile);
		sameFileType = FileValidator.haveFilesTheSameExtension(deletedFile, previoiusFile);
	}

	public IFile getDeletedFile() {
		return deletedFile;
	}

	public IFile getPreviousFile() {
		return previousFile;
	}

	public boolean isSamePackage() {
		return samePackage;
	}

	public boolean isSameProject() {
		return sameProject;
	}

	public boolean isSameFileType() {
		return sameFileType;
	}
	
	public WorkingFile getWorkingFile() {
		return this.workFile;
	}
	
	
}
