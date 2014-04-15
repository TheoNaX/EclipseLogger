package eclipselogger.views;


import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.part.ViewPart;

import eclipselogger.db.ActionLoader;
import eclipselogger.events.EclipseActionMonitor;
import eclipselogger.events.actions.EclipseAction;
import eclipselogger.gui.ActionViewComparator;
import eclipselogger.gui.ActionsContentProvider;
import eclipselogger.gui.ActionsLabelProvider;
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
	private static TableViewer viewer;
	
	private Action contextChangeAction;
	private Action contextNoChangeAction;
	
	ActionLoader loader = new ActionLoader();
	
    public LoggingView() {
    }
    
    private static Logger logger = Logger.getLogger(LoggingView.class);
    
    private final OpenFileListener listener = new OpenFileListener();
    private final IResourceChangeListener resourceListener = new ResourceChangeListener();
    
    // create thread, which is sending Eclipse actions periodically to server
    private ActionFileSender fileSender; 
    
	@Override
	public void createPartControl(final Composite parent) {

		try {
			viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
			viewer.setContentProvider(new ActionsContentProvider());
			viewer.setLabelProvider(new ActionsLabelProvider());
			viewer.setInput(getViewSite());
			viewer.setComparator(new ActionViewComparator());

			makeActions();
			hookContextMenu();
		} catch (final Throwable t) {
			logger.error("Failed to create Table viewer", t);
		}
		
		
		// init Log4J logger
		try {
			PropertyConfigurator.configure(ConfigReader.getProperties());
		} catch (final Exception e) {
			System.err.println("Failed to configure log4j logger");
			logger.error("Failed to configure log4j logger", e);
		}
		
		try {
			Workbench.getInstance().getActiveWorkbenchWindow().getPartService().addPartListener(this.listener);
		} catch (final Exception e) {
			logger.error("Failed to register part listener", e);
		}
		
		try {
			ResourcesPlugin.getWorkspace().addResourceChangeListener(this.resourceListener,
				IResourceChangeEvent.POST_CHANGE);
		} catch (final Exception e) {
			logger.error("Failed to register resource listener", e);
		}
		
		// create file sender thread
		try {
			this.fileSender = new ActionFileSender();
		} catch (final Exception e) {
			logger.error("Failed to create sender", e);
		}

		// init loggers for actions - see config.properties
		try {
			EclipseActionMonitor.init();
		} catch (final Exception e) {
			logger.error("Failed to init EclipseActionMonitor", e);
		}

		// init context change dialog
//		final Shell shell = parent.getShell();
//		final ContextChangeDialog dialog = new ContextChangeDialog(shell);
//		EclipseActionMonitor.initContextDialog(dialog);
	}
	
	private void hookContextMenu() {
		final MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(final IMenuManager manager) {
				LoggingView.this.fillContextMenu(manager);
			}
		});
		final Menu menu = menuMgr.createContextMenu(LoggingView.viewer.getControl());
		LoggingView.viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, LoggingView.viewer);
	}
	
	private void fillContextMenu(final IMenuManager manager) {
		manager.add(this.contextChangeAction);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		manager.add(this.contextNoChangeAction);
		
	}
	
	private void makeActions() {
		this.contextChangeAction = new Action() {
			@Override
			public void run() {
				final ISelection selection = LoggingView.viewer.getSelection();
				final Object obj = ((IStructuredSelection)selection).getFirstElement();
				updateContextChangeForAction(obj, true);
			}
		};
		this.contextChangeAction.setText("Context change");
		this.contextChangeAction.setToolTipText("Mark this action as context change");
		
		
		this.contextNoChangeAction = new Action() {
			@Override
			public void run() {
				final ISelection selection = LoggingView.viewer.getSelection();
				final Object obj = ((IStructuredSelection)selection).getFirstElement();
				updateContextChangeForAction(obj, false);
			}
		};
		this.contextNoChangeAction.setText("NO context change");
		this.contextNoChangeAction.setToolTipText("Mark this action as no context change");
		
	}
	
	private void updateContextChangeForAction(final Object object, final boolean context) {
		if (!(object instanceof EclipseAction)) {
			logger.error("Selected object is not Eclipse action!!!");
			return;
		}
		final EclipseAction action = (EclipseAction) object;
		try {
			logger.debug("Going to update context change for action " + action.getActionId());
			this.loader.updateUnsentActionContextChange(action.getActionId(), context);
			logger.debug("Context change successfully updated for action " + action.getActionId());
			
			viewer.refresh();
		} catch (final Exception e) {
			logger.error("Failed to update context change for action" + action.getActionId(), e);
		}
		
	}
	
	public static void refreshViewer() {
		logger.debug("Before refreshing of unsent actions view");
		if (viewer != null) {
			viewer.refresh();
		}
		logger.debug("After refreshing of unsent actions view");
	}
	

	@Override
	public void setFocus() {
		LoggingView.viewer.getControl().setFocus();
	}

	@Override
	public void dispose() {
		this.fileSender.stopSenderThread();
		try {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(this.resourceListener);
			Workbench.getInstance().getActiveWorkbenchWindow().getPartService().removePartListener(this.listener);
		} catch (final Throwable e) {
			logger.error("Error in dispose", e);
		}
	}
}