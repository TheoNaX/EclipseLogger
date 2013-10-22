package eclipselogger.events.actions;

import org.eclipse.core.resources.IProject;

public class DeleteProjectAction extends EclipseAction {
	private IProject project;
	
	public DeleteProjectAction(IProject project) {
		this.project = project;
	}
	
	public IProject getProject() {
		return this.project;
	}
}
