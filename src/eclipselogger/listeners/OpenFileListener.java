package eclipselogger.listeners;

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

	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
		IWorkbenchPart part = partRef.getPart(false);
		if (part instanceof ITextEditor) {
			ITextEditor editor = (ITextEditor)part;
			IEditorInput input = editor.getEditorInput();
			if (input instanceof IFileEditorInput) {
				IFile file = ((IFileEditorInput)input).getFile();
				if (lastOpened == null || !lastOpened.equals(file.getProjectRelativePath().toOSString())) {
					System.out.println("Switched to file: " + file.getProjectRelativePath());
					EclipseActionMonitor.switchToFile(file);
				}
			}
			lastOpened = null;
		}
		//System.out.println("Part activated!");
		
	}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) {
		//System.out.println("part brought to top");
	}

	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
		IWorkbenchPart part = partRef.getPart(false);
		if (part instanceof ITextEditor) {
			ITextEditor editor = (ITextEditor)part;
			IEditorInput input = editor.getEditorInput();
			if (input instanceof IFileEditorInput) {
				IFile file = ((IFileEditorInput)input).getFile();
				//System.out.println("Closed file: " + file.getProjectRelativePath());
				EclipseActionMonitor.closeFile(file);
			}
		}
		//System.out.println("Part closed");
	}

	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {
		// System.out.println("Part deactivated");
	}

	@Override
	public void partOpened(IWorkbenchPartReference partRef) {
		IWorkbenchPart part = partRef.getPart(false);
		if (part instanceof ITextEditor) {
			ITextEditor editor = (ITextEditor)part;
			IEditorInput input = editor.getEditorInput();
			if (input instanceof IFileEditorInput) {
				IFile file = ((IFileEditorInput)input).getFile();
				lastOpened = file.getProjectRelativePath().toOSString();
				System.out.println("Opened new file: " + lastOpened);
				EclipseActionMonitor.openNewFile(file);
			}
		}
	}

	@Override
	public void partHidden(IWorkbenchPartReference partRef) {
		// System.out.println("Part hidden");
	}

	@Override
	public void partVisible(IWorkbenchPartReference partRef) {
		// System.out.println("Part visible");
	}

	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {
		// System.out.println("Part input changed");
	}

}
