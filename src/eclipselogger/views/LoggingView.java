package eclipselogger.views;


import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.part.*;
import org.eclipse.swt.SWT;

import eclipselogger.listeners.OpenFileListener;
import eclipselogger.listeners.ResourceChangeListener;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class LoggingView extends ViewPart {

	Label label;
    public LoggingView() {
    }
    
    OpenFileListener listener = new OpenFileListener();
    IResourceChangeListener resourceListener = new ResourceChangeListener();
    
    
    public void createPartControl(Composite parent) {
       label = new Label(parent, SWT.WRAP);
       label.setText("Hello World");
       Workbench.getInstance().getActiveWorkbenchWindow().getPartService().addPartListener(listener);
       ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceListener,
    	       IResourceChangeEvent.PRE_CLOSE
    	       | IResourceChangeEvent.PRE_DELETE
    	       | IResourceChangeEvent.PRE_BUILD
    	       | IResourceChangeEvent.POST_BUILD
    	       | IResourceChangeEvent.POST_CHANGE);
    }
    public void setFocus() {
       // set focus to my widget.  For a label, this doesn't
       // make much sense, but for more complex sets of widgets
       // you would decide which one gets the focus.
    }
}