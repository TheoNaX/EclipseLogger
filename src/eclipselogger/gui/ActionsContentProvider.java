package eclipselogger.gui;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import eclipselogger.db.ActionLoader;
import eclipselogger.events.actions.EclipseAction;

public class ActionsContentProvider implements IStructuredContentProvider {

	private List<EclipseAction> unsentActions;
	private final ActionLoader loader = new ActionLoader();
	
	private static Logger logger = Logger.getLogger(ActionsContentProvider.class);
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object[] getElements(final Object inputElement) {
		this.unsentActions = null;
		try {
			this.unsentActions = this.loader.loadNotSentEclipseActions();
		} catch (final Exception e) {
			logger.error("Failed to load unsent actions", e);
		}
		if (this.unsentActions != null) {
			return this.unsentActions.toArray();
		} else {
			return new String[] {"No actions loaded"};
		}
		
	}

}
