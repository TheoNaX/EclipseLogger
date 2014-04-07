package eclipselogger.resources;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

import eclipselogger.events.EclipseResource;

public class EclipseFile extends EclipseResource {
	
	private final String relativePath;
	private final String fileName;
	private final String projectName;
	private final String fileExtension;
	
	public EclipseFile(final IFile file) {
		this.relativePath = file.getProjectRelativePath().toOSString();
		this.fileName = file.getName();
		this.projectName = file.getProject().getName();
		this.fileExtension = file.getFileExtension();
		this.resourceType = IResource.FILE;
	}
	
	@Override
	public String getProjectRelativePath() {
		return this.relativePath;
	}
	
	public String getFileName() {
		return this.fileName;
	}
	
	public String getProjectName() {
		return this.projectName;
	}
	
	public String getFileExtension() {
		return this.fileExtension;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == null || !(obj instanceof EclipseFile)) {
			return false;
		}
		final EclipseFile file = (EclipseFile) obj;
		boolean result = false;
		if (file != null && this.relativePath != null) {
			if (this.relativePath.equalsIgnoreCase(file.getProjectRelativePath())) {
				result = true;
			}
		}
		
		return result;
	}

}
