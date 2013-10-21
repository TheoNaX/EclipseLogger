package eclipselogger;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

import eclipselogger.events.EclipseActionMonitor;
import eclipselogger.utils.FileValidator;

public class DeltaHandler implements IResourceDeltaVisitor {

	private List<DeltaEntry> actualDelta = new ArrayList<DeltaEntry>(); 
	
	public void startDeltaHandling() {
		actualDelta.clear();
	}
	
	public void stopDeltaHandling() {
		if (actualDelta.size() == 0) {
			return;
		} else if (actualDelta.size() == 1) {
			handleOneResourceChange();
		} else {
			handleMoreResourceChanges();
		}
	}
	
	private void handleMoreResourceChanges() {
		if (actualDelta.size() != 2) {
			System.out.println("Something is wrong, only 2 changes should be in one delta");
		} else {
			DeltaEntry firstEntry = actualDelta.get(0);
			DeltaEntry secondEntry = actualDelta.get(1);
			if ((firstEntry.getType() == IResourceDelta.REMOVED && secondEntry.getType() == IResourceDelta.ADDED)
					|| (firstEntry.getType() == IResourceDelta.ADDED && secondEntry.getType() == IResourceDelta.REMOVED)) {
				handleRefactoring(firstEntry, secondEntry);
			} else {
				System.out.println(">>> Something is wrong, 2 changes were made, but not refactoring!!!");
				System.out.println("First: " + firstEntry.getType() + ", second: " + secondEntry.getType());
			}
		}
	}
	
	private void handleRefactoring(DeltaEntry firstEntry, DeltaEntry secondEntry) {
		if (firstEntry.getResource().getType() == IResource.FILE && secondEntry.getResource().getType() == IResource.FILE) {
			if (firstEntry.getType() == IResourceDelta.REMOVED) {
				EclipseActionMonitor.refactorFile((IFile)firstEntry.getResource(), (IFile)secondEntry.getResource());
			} else {
				EclipseActionMonitor.refactorFile((IFile)secondEntry.getResource(), (IFile)firstEntry.getResource());
			}
		} else if (firstEntry.getResource().getType() == IResource.FOLDER && secondEntry.getResource().getType() == IResource.FOLDER) {
			if (firstEntry.getType() == IResourceDelta.REMOVED) {
				EclipseActionMonitor.refactorPackage((IFolder)firstEntry.getResource(), (IFolder)secondEntry.getResource());
			} else {
				EclipseActionMonitor.refactorPackage((IFolder)secondEntry.getResource(), (IFolder)firstEntry.getResource());
			}
		}
		
	}
	
	private void handleOneResourceChange() {
		DeltaEntry entry = actualDelta.get(0);
		if (entry != null) {
			IResource resource = entry.getResource();
			switch(entry.getType()) {
			case IResourceDelta.ADDED:
				handleResourceAdded(resource);
				break;
			case IResourceDelta.REMOVED:
				handleResourceRemoved(resource);
				break;
			case IResourceDelta.CHANGED:
				handleResourceChanged(resource);
				break;
			}
		}
		
	}
	
	private void handleResourceAdded(IResource res) {
		if (res.getType() == IResource.FILE) {
			IFile file = (IFile) res;
			//System.out.println("File was added: " + file.getProjectRelativePath());
			EclipseActionMonitor.addFile(file);
		} else if (res.getType() == IResource.FOLDER) {
			IFolder folder = (IFolder) res;
			//System.out.println("Folder / package was added: " + folder.getProjectRelativePath());
			EclipseActionMonitor.addFolder(folder);
		} else if (res.getType() == IResource.PROJECT) {
			IProject project = (IProject) res;
			EclipseActionMonitor.addProject(project);
			//System.out.println("Project was added: " + project.getName());
		}
	}
	
	private void handleResourceRemoved(IResource res) {
		if (res.getType() == IResource.FILE) {
			IFile file = (IFile) res;
			EclipseActionMonitor.deleteFile(file);
			//System.out.println("File was removed: " + file.getProjectRelativePath());
		} else if (res.getType() == IResource.FOLDER) {
			IFolder folder = (IFolder) res;
			EclipseActionMonitor.deleteFolder(folder);
			//System.out.println("Folder / package was removed: " + folder.getProjectRelativePath());
		} else if (res.getType() == IResource.PROJECT) {
			IProject project = (IProject) res;
			EclipseActionMonitor.deleteProject(project);
			System.out.println("Project was removed: " + project.getName());
		}
	}
	
	private void handleResourceChanged(IResource res) {
		if (res.getType() == IResource.FILE) {
			IFile file = (IFile) res;
			EclipseActionMonitor.fileChanged(file);
			//System.out.println("File was changed: " + file.getProjectRelativePath());
		} 
	}
	
	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource res = delta.getResource();
		boolean result = true;
		
		switch (delta.getKind()) {
		case IResourceDelta.ADDED:
			result = handleAddVisited(res);
			break;
		case IResourceDelta.REMOVED:
			result = handleRemoveVisited(res);
			break;
		case IResourceDelta.CHANGED:
			result = handleChangeVisited(res);
			break;
		}
		
		return result;
	}
	 
	private boolean handleChangeVisited(IResource res) {
		boolean result = true;
		if (res.getType() == IResource.FILE) {
			if (FileValidator.shouldBeFileLogged((IFile)res)) {
				//System.out.println("File changed: " + res.getFullPath());
				actualDelta.add(new DeltaEntry(IResourceDelta.CHANGED, res));
				result = false;
			};
		}
		return result;
	}

	private boolean handleRemoveVisited(IResource res) {
		boolean result = true;
		if (res.getType() == IResource.FILE) {
			if (FileValidator.shouldBeFileLogged((IFile)res)) {
				result = false;
				//System.out.println("File removed: " + res.getFullPath());
			}
		} else if (res.getType() == IResource.FOLDER) {
			if (!FileValidator.isInBinDirecotory((IFolder)res)) {
				result = false;
				//System.out.println("Folder / package removed: " + res.getFullPath());
			}
		} else if (res.getType() == IResource.PROJECT) {
			result = false;
			//System.out.println("Project removed: " + res.getFullPath());
		}
		if (!result) {
			actualDelta.add(new DeltaEntry(IResourceDelta.REMOVED, res));
		}
		
		return result;
	}

	private boolean handleAddVisited(IResource res) {
		boolean result = true;
		if (res.getType() == IResource.FILE) {
			if (FileValidator.shouldBeFileLogged((IFile)res)) {
				result = false;
				//System.out.println("File added: " + res.getFullPath());
			}
		} else if (res.getType() == IResource.FOLDER) {
			if (!FileValidator.isInBinDirecotory((IFolder)res)) {
				result = false;
				//System.out.println("Folder / package added: " + res.getFullPath());
			}
		} else if (res.getType() == IResource.PROJECT) {
			result = false;
			//System.out.println("Project added: " + res.getFullPath());
		}
		
		if (!result) {
			actualDelta.add(new DeltaEntry(IResourceDelta.ADDED, res));
		}
		
		return result;
	}

}
