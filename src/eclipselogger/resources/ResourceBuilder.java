package eclipselogger.resources;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

public class ResourceBuilder {
	
	public static EclipseFile buildEclipseFile(final IFile file) {
		final EclipseFile eclipseFile = new EclipseFile(file);
		return eclipseFile;
	}
	
	public static EclipseFolder buildEclipseFolder(final IFolder folder) {
		final EclipseFolder eclipseFolder = new EclipseFolder(folder);
		return eclipseFolder;
	}
	
	public static EclipseProject buildEclipseProject(final IProject project) {
		final EclipseProject eclipseProject = new EclipseProject(project);
		return eclipseProject;
	}
}
