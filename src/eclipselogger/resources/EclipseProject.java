package eclipselogger.resources;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import eclipselogger.events.EclipseResource;

public class EclipseProject extends EclipseResource {
	
	private final String relativePath;
	private final String projectName;
	
	public EclipseProject(final IProject project) {
		this.resourceType = IResource.PROJECT;
		this.relativePath = project.getProjectRelativePath().toOSString();
		this.projectName = project.getName();
	}

	@Override
	public String getProjectRelativePath() {
		return this.relativePath;
	}
	
	public String getName() {
		return this.projectName;
	}

}
