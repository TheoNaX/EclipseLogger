package eclipselogger.views;


import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.part.ViewPart;

import eclipselogger.events.EclipseActionMonitor;
import eclipselogger.listeners.OpenFileListener;
import eclipselogger.listeners.ResourceChangeListener;
import eclipselogger.sender.ActionFileSender;
import eclipselogger.utils.ConfigReader;


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
    
    private final OpenFileListener listener = new OpenFileListener();
    private final IResourceChangeListener resourceListener = new ResourceChangeListener();
    
    // create thread, which is sending Eclipse actions periodically to server
    private ActionFileSender fileSender; 
    
	@Override
	public void createPartControl(final Composite parent) {
		
		// init Log4J logger
		PropertyConfigurator.configure(ConfigReader.getProperties());
		
		Workbench.getInstance().getActiveWorkbenchWindow().getPartService().addPartListener(this.listener);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this.resourceListener,
				IResourceChangeEvent.POST_CHANGE);
		
		// create file sender thread
		try {
			this.fileSender = new ActionFileSender();
		} catch (final Exception e) {
			e.printStackTrace();
		}

		// init loggers for actions - see config.properties
		EclipseActionMonitor.init();

		// init context change dialog
//		final Shell shell = parent.getShell();
//		final ContextChangeDialog dialog = new ContextChangeDialog(shell);
//		EclipseActionMonitor.initContextDialog(dialog);
	}

	@Override
	public void setFocus() {
		// set focus to my widget. For a label, this doesn't
		// make much sense, but for more complex sets of widgets
		// you would decide which one gets the focus.
	}

	@Override
	public void dispose() {
		this.fileSender.stopSenderThread();
	}
}