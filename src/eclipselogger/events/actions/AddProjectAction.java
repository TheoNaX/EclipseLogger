package eclipselogger.events.actions;

import org.eclipse.core.resources.IProject;

public class AddProjectAction extends EclipseAction {
	
	public static final String TABLE_NAME = "add_project";
	
	private final IProject project;
	private final String projectName;
	
	public AddProjectAction(final long timeSinceLastAction, final EclipseAction previousAction, final IProject project) {
		super(timeSinceLastAction, previousAction);
		this.project = project;
		this.projectName = project.getName();
	}
	
	public IProject getProject() {
		return this.project;
	}
	
	public String getProjectName() {
		return this.projectName;
	}

	@Override
	public ActionType getActionType() {
		return ActionType.ADD_PROJECT;
	}

}
