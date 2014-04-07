package eclipselogger.resources;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;

import eclipselogger.events.EclipseResource;

public class EclipseFolder extends EclipseResource {
	
	private final String relativePath;
	private final String folderName;
	private final String projectName;
	private final String parentFolderPath;
	
	public EclipseFolder(final IFolder folder) {
		this.relativePath = folder.getProjectRelativePath().toOSString();
		this.folderName = folder.getName();
		this.projectName = folder.getProject().getName();
		this.parentFolderPath = folder.getParent().getProjectRelativePath().toOSString();
		this.resourceType = IResource.FOLDER;
	}
	
	@Override
	public String getProjectRelativePath() {
		return this.relativePath;
	}
	
	public String getParentPath() {
		return this.parentFolderPath;
	}
	
	public String getProjectName() {
		return this.projectName;
	}
	
	public String getName() {
		return this.folderName;
	}
}
