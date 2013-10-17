package eclipselogger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

import eclipselogger.utils.FileValidator;

public class DeltaHandler implements IResourceDeltaVisitor {

	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource res = delta.getResource();
		
		switch (delta.getKind()) {
		case IResourceDelta.ADDED:
			handleResourceAdded(res);
			break;
		case IResourceDelta.REMOVED:
			handleResourceRemoved(res);
			break;
		case IResourceDelta.CHANGED:
			handleResourceChanged(res);
			break;
		}
		
		return true;
	}

	private void handleResourceChanged(IResource res) {
		if (res.getType() == IResource.FILE) {
			if (FileValidator.shouldBeFileLogged((IFile)res)) {
				System.out.println("File changed: " + res.getFullPath());
			};
		}
	}

	private void handleResourceRemoved(IResource res) {
		if (res.getType() == IResource.FILE) {
			if (FileValidator.shouldBeFileLogged((IFile)res)) {
				System.out.println("File removed: " + res.getFullPath());
			}
		} else if (res.getType() == IResource.FOLDER) {
			if (!FileValidator.isInBinDirecotory((IFolder)res)) {
				System.out.println("Folder / package removed: " + res.getFullPath());
			}
		} else if (res.getType() == IResource.PROJECT) {
			System.out.println("Project removed: " + res.getFullPath());
		}
	}

	private void handleResourceAdded(IResource res) {
		if (res.getType() == IResource.FILE) {
			if (FileValidator.shouldBeFileLogged((IFile)res)) {
				System.out.println("File added: " + res.getFullPath());
			}
		} else if (res.getType() == IResource.FOLDER) {
			if (!FileValidator.isInBinDirecotory((IFolder)res)) {
				System.out.println("Folder / package added: " + res.getFullPath());
			}
		} else if (res.getType() == IResource.PROJECT) {
			System.out.println("Project added: " + res.getFullPath());
		}
	}

}
