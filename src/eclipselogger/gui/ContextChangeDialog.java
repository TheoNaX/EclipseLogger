package eclipselogger.gui;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import eclipselogger.events.EclipseActionMonitor;
import eclipselogger.events.actions.EclipseAction;

public class ContextChangeDialog extends PopupDialog {

	private EclipseAction actualAction;
	private boolean logged = false;
	
	private Label label;
	
	public ContextChangeDialog(final Shell parentShell) {
		super(parentShell, 
				SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE, 
				false, // do not take focus when opened
	        	false, // do not persist the bounds
	        	false, // do not show a resize menu
	        	false, // do not show a menu item for persisting bounds
	        	false,
	        	"Context change", //  no title
	        	"Is this action a context change milestone?"); // no info text
	}
	
	@Override
	  protected Control createDialogArea(final Composite parent) {
	    final Composite container = (Composite) super.createDialogArea(parent);
	    this.label = new Label(container, SWT.CENTER);
	    this.label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
	    final Button yesButton = new Button(container, SWT.PUSH);
	    yesButton.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false));
	    yesButton.setText("YES");
	    yesButton.addSelectionListener(new SelectionAdapter() {
	      @Override
	      public void widgetSelected(final SelectionEvent e) {
	    	  ContextChangeDialog.this.logged = true;
	    	  logEclipseActionAndClose(true);
	      }
	    });
	    
	    final Button noButton = new Button(container, SWT.PUSH);
	    noButton.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, false));
	    noButton.setText("NO");
	    noButton.addSelectionListener(new SelectionAdapter() {
	      @Override
	      public void widgetSelected(final SelectionEvent e) {
	    	  ContextChangeDialog.this.logged = true;
	    	  logEclipseActionAndClose(false);
	      }
	    });

	    this.label.setText(this.actualAction.toString());
	    setBlockOnOpen(false);
	    
	    return container;
	  }
	
	// overriding this methods allows you to set the
	  // title of the custom dialog
//	  @Override
//	  protected void configureShell(final Shell newShell) {
//	    super.configureShell(newShell);
//	    newShell.setText("Context change");
//	  }

//	  @Override
//	  protected Point getInitialSize() {
//	    return new Point(100, 75);
//	  }
//	  
	  
	  private void logEclipseActionAndClose(final boolean contextChange) {
		  EclipseActionMonitor.logEclipseAction(this.actualAction, contextChange);
		  this.close();
	  }
	  
	  public void setActualAction(final EclipseAction action) {
		  if (this.actualAction != null && !this.logged) {
			  EclipseActionMonitor.logEclipseAction(this.actualAction, false);
		  }
		  this.actualAction = action;
		  this.logged = false;
		  
	  }
	  
	public Point getCenterPoint() {
		//final Shell parentShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		final Shell parentShell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
		final Rectangle shellBounds = parentShell.getBounds();
		return new Point(shellBounds.x + shellBounds.width, (shellBounds.y + shellBounds.height));
	}

	@Override
	public Point getInitialLocation(final Point initialSize) {
		final Point shellCenter = getCenterPoint();
		return new Point(shellCenter.x - initialSize.x / 2, shellCenter.y - initialSize.y / 2);
	}
	
	public void refreshActionDetails() {
		this.label.setText(this.actualAction.toString());
	}
	
	@Override
	public boolean close() {
		if (this.actualAction != null && !this.logged) {
			EclipseActionMonitor.logEclipseAction(this.actualAction, false);
		}
		
		return super.close();
	}

}
