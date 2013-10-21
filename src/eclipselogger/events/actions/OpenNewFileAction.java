package eclipselogger.events.actions;

import org.eclipse.core.resources.IFile;

import eclipselogger.utils.FileValidator;
import eclipselogger.utils.PackageUtils;

public class OpenNewFileAction extends EclipseAction {
	private IFile openedFile;
	private IFile previousFile;
	private boolean samePackage;
	private boolean sameProject;
	private boolean sameFileType;
	
	public OpenNewFileAction(IFile openedFile, IFile previousFile) {
		this.openedFile = openedFile;
		this.previousFile = previousFile;
		samePackage = PackageUtils.checkIfSamePackage(openedFile, previousFile);
		sameProject = PackageUtils.checkIfSameProject(openedFile, previousFile);
		sameFileType = FileValidator.haveFilesTheSameExtension(openedFile, previousFile);
	}
	
	public boolean openedInSamePackage() {
		return samePackage;
	}
	
	public int getPackageDistance() {
		// TODO implement package distances
		return 0;
	}
	
	public IFile getOpenedFile() {
		return this.openedFile;
	}
	
	public IFile getPreviousOpenedFile() {
		return this.previousFile;
	}
	
	public boolean openedInSameProject() {
		return this.sameProject;
	}
	
	public boolean isTheSameTypeAsPreviuos() {
		return this.sameFileType;
	}
	
	
	
}
