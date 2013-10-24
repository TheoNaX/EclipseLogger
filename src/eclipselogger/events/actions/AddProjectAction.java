package eclipselogger.events.actions;

import org.eclipse.core.resources.IProject;

public class AddProjectAction extends EclipseAction {
	private IProject project;
	
	public AddProjectAction(IProject project) {
		this.project = project;
	}
	
	public IProject getProject() {
		return this.project;
	}

}
