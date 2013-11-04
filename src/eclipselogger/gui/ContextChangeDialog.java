package eclipselogger.gui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import eclipselogger.events.EclipseActionMonitor;
import eclipselogger.events.actions.EclipseAction;

public class ContextChangeDialog extends Dialog {

	private EclipseAction actualAction;
	
	protected ContextChangeDialog(final Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	  protected Control createDialogArea(final Composite parent) {
	    final Composite container = (Composite) super.createDialogArea(parent);
	    
	    final Button yesButton = new Button(container, SWT.PUSH);
	    yesButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
	        false));
	    yesButton.setText("YES");
	    yesButton.addSelectionListener(new SelectionAdapter() {
	      @Override
	      public void widgetSelected(final SelectionEvent e) {
	    	  logEclipseActionAndClose(false);
	      }
	    });
	    
	    final Button noButton = new Button(container, SWT.PUSH);
	    noButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
	        false));
	    noButton.setText("NO");
	    noButton.addSelectionListener(new SelectionAdapter() {
	      @Override
	      public void widgetSelected(final SelectionEvent e) {
	    	  logEclipseActionAndClose(false);
	      }
	    });

	    return container;
	  }
	
	// overriding this methods allows you to set the
	  // title of the custom dialog
	  @Override
	  protected void configureShell(final Shell newShell) {
	    super.configureShell(newShell);
	    newShell.setText("Context change");
	  }

	  @Override
	  protected Point getInitialSize() {
	    return new Point(450, 300);
	  }
	  
	  private void logEclipseActionAndClose(final boolean contextChange) {
		  EclipseActionMonitor.logEclipseAction(this.actualAction, contextChange);
		  this.close();
	  }
	  
	  @Override
	  public void okPressed() {
		  EclipseActionMonitor.logEclipseAction(this.actualAction, false);
		  super.okPressed();
	  }
	  
	  @Override
	  public void cancelPressed() {
		  EclipseActionMonitor.logEclipseAction(this.actualAction, false);
		  super.cancelPressed();
	  }
	  
	  public void showContextChangeDialog(final EclipseAction action) {
		  // TODO
	  }

}
