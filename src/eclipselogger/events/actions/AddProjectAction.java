package eclipselogger.events.actions;

import org.eclipse.core.resources.IProject;

public class AddProjectAction extends EclipseAction {
	private IProject project;
	
	public AddProjectAction(long timeSinceLastAction, EclipseAction previousAction, IProject project) {
		super(timeSinceLastAction, previousAction);
		this.project = project;
	}
	
	public IProject getProject() {
		return this.project;
	}

	@Override
	public ActionType getActionType() {
		return ActionType.ADD_PROJECT;
	}

}
