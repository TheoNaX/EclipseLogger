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
import eclipselogger.resources.EclipseFile;
import eclipselogger.resources.ResourceBuilder;
import eclipselogger.utils.FileUtilities;

public class OpenFileListener implements IPartListener2 {
	
	private String lastOpened;
	private String lastSwitched;
	private final Logger logger = Logger.getLogger(OpenFileListener.class);

	// TODO how to force not to show switch if delete action or close action performed on opened file
	// some kind of lock
	// lock when this operation is performed on opened file
	// unlock when operation finished
	@Override
	public void partActivated(final IWorkbenchPartReference partRef) {
		final IWorkbenchPart part = partRef.getPart(false);
		if (part instanceof ITextEditor) {
			final ITextEditor editor = (ITextEditor)part;
			final IEditorInput input = editor.getEditorInput();
			if (input instanceof IFileEditorInput) {
				final IFile file = ((IFileEditorInput)input).getFile();
				if ((this.lastOpened == null || !this.lastOpened.equals(file.getProjectRelativePath().toOSString()))
						|| (this.lastSwitched == null || !this.lastSwitched.equals(file.getProjectRelativePath().toOSString()))) {
					this.logger.info("Switched to file: " + file.getProjectRelativePath().toOSString());
					this.lastSwitched = file.getProjectRelativePath().toOSString();
					
					final EclipseFile eFile = ResourceBuilder.buildEclipseFile(file);
					final String content = FileUtilities.fileContentToString(file);
					EclipseActionMonitor.switchToFile(eFile, content);
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
				this.logger.debug("File closed: " + file.getProjectRelativePath().toOSString());
				final EclipseFile eFile = ResourceBuilder.buildEclipseFile(file);
				EclipseActionMonitor.closeFile(eFile);
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
				this.logger.debug("File opened: " + file.getProjectRelativePath().toOSString());
				final EclipseFile eFile = ResourceBuilder.buildEclipseFile(file);
				final String content = FileUtilities.fileContentToString(file);
				EclipseActionMonitor.openNewFile(eFile, content);
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
