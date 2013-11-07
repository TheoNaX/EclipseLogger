package eclipselogger.listeners;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.texteditor.ITextEditor;

import eclipselogger.events.EclipseActionMonitor;

public class OpenFileListener implements IPartListener2 {
	
	private String lastOpened;
	private final Logger logger = Logger.getLogger(OpenFileListener.class);

	@Override
	public void partActivated(final IWorkbenchPartReference partRef) {
		final IWorkbenchPart part = partRef.getPart(false);
		if (part instanceof ITextEditor) {
			final ITextEditor editor = (ITextEditor)part;
			final IEditorInput input = editor.getEditorInput();
			if (input instanceof IFileEditorInput) {
				final IFile file = ((IFileEditorInput)input).getFile();
				if (this.lastOpened == null || !this.lastOpened.equals(file.getProjectRelativePath().toOSString())) {
					this.logger.info("Switched to file: " + file.getProjectRelativePath().toOSString());
					EclipseActionMonitor.switchToFile(file);
				}
			}
			this.lastOpened = null;
		}
		
	}

	@Override
	public void partBroughtToTop(final IWorkbenchPartReference partRef) {
		//System.out.println("part brought to top");
	}

	@Override
	public void partClosed(final IWorkbenchPartReference partRef) {
		final IWorkbenchPart part = partRef.getPart(false);
		if (part instanceof ITextEditor) {
			final ITextEditor editor = (ITextEditor)part;
			final IEditorInput input = editor.getEditorInput();
			if (input instanceof IFileEditorInput) {
				final IFile file = ((IFileEditorInput)input).getFile();
				this.logger.info("File closed: " + file.getProjectRelativePath().toOSString());
				EclipseActionMonitor.closeFile(file);
			}
		}
	}

	@Override
	public void partDeactivated(final IWorkbenchPartReference partRef) {
		// System.out.println("Part deactivated");
	}

	@Override
	public void partOpened(final IWorkbenchPartReference partRef) {
		final IWorkbenchPart part = partRef.getPart(false);
		if (part instanceof ITextEditor) {
			final ITextEditor editor = (ITextEditor)part;
			final IEditorInput input = editor.getEditorInput();
			if (input instanceof IFileEditorInput) {
				final IFile file = ((IFileEditorInput)input).getFile();
				this.lastOpened = file.getProjectRelativePath().toOSString();
				this.logger.info("File opened: " + file.getProjectRelativePath().toOSString());
				EclipseActionMonitor.openNewFile(file);
			}
		}
	}

	@Override
	public void partHidden(final IWorkbenchPartReference partRef) {
		// System.out.println("Part hidden");
	}

	@Override
	public void partVisible(final IWorkbenchPartReference partRef) {
		// System.out.println("Part visible");
	}

	@Override
	public void partInputChanged(final IWorkbenchPartReference partRef) {
		// System.out.println("Part input changed");
	}

}
