package eclipselogger.gui;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import eclipselogger.events.actions.EclipseAction;

public class ActionsLabelProvider extends LabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(final Object element, final int columnIndex) {
		return getImage(element);
	}

	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		if (element instanceof EclipseAction) {
			final EclipseAction action = (EclipseAction) element;
			return action.toStringForViewer();
		} else {
			return "No Eclipse action";
		}
	}
	
	@Override
	public Image getImage(final Object obj) {
		return PlatformUI.getWorkbench().
				getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
	}
	
	

}
