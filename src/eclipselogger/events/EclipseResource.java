package eclipselogger.events;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

public class EclipseResource {
	
	
	private final String relativePath;
	private final int resourceType;
	private String fileName = null;
	
	public EclipseResource(final IResource resource) {
		this.relativePath = resource.getProjectRelativePath().toOSString();
		this.resourceType = resource.getType();
		if (resource instanceof IFile) {
			this.fileName = ((IFile)resource).getName();
		}
	}
	
	public int getType() {
		return this.resourceType;
	}
	
	public String getProjectRelativePath() {
		return this.relativePath;
	}
	
	public String getFileName() {
		return this.fileName;
	}
	

}
