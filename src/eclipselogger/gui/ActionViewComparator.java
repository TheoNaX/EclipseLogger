package eclipselogger.gui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import eclipselogger.events.actions.EclipseAction;

public class ActionViewComparator extends ViewerComparator {

	@Override
	public int compare(final Viewer viewer, final Object e1, final Object e2) {
        if (e1 instanceof EclipseAction && e2 instanceof EclipseAction) {
        	final int e1Id = ((EclipseAction)e1).getActionId();
        	final int e2Id = ((EclipseAction)e2).getActionId();
        	if (e1Id < e2Id) {
        		return 1;
        	} else if (e1Id > e2Id) {
        		return -1;
        	} else {
        		return 0;
        	}
        } else {
        	return super.compare(viewer, e1, e2);
        }
    }
	
}
