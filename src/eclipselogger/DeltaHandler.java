package eclipselogger;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

import eclipselogger.events.EclipseActionMonitor;
import eclipselogger.utils.FileUtilities;
import eclipselogger.utils.FileValidator;

public class DeltaHandler implements IResourceDeltaVisitor {

	private final List<DeltaEntry> actualDelta = new ArrayList<DeltaEntry>(); 
	private final Logger logger = Logger.getLogger(DeltaHandler.class);
	
	public void startDeltaHandling() {
		this.actualDelta.clear();
	}
	
	public void stopDeltaHandling() {
		try {
		if (this.actualDelta.size() == 0) {
			return;
		} else if (this.actualDelta.size() == 1) {
			handleOneResourceChange();
		} else {
			handleMoreResourceChanges();
		}
		} catch (final Exception e) {
			this.logger.error("stopDelta() error", e);
		}
	}
	
	private void handleMoreResourceChanges() {
		this.logger.debug("########## handleMoreResourceChanges() start");
		try {
			if (this.actualDelta.size() != 2) {
				this.logger.debug("Something is wrong, only 2 changes should be in one delta");
			} else {
				final DeltaEntry firstEntry = this.actualDelta.get(0);
				final DeltaEntry secondEntry = this.actualDelta.get(1);
				if ((firstEntry.getType() == IResourceDelta.REMOVED && secondEntry.getType() == IResourceDelta.ADDED)
						|| (firstEntry.getType() == IResourceDelta.ADDED && secondEntry.getType() == IResourceDelta.REMOVED)) {
					handleRefactoring(firstEntry, secondEntry);
				} else {
					this.logger.debug(">>> Something is wrong, 2 changes were made, but not refactoring!!!");
					this.logger.debug("First: " + firstEntry.getType() + ", second: " + secondEntry.getType());
				}
			}
		} catch (final Exception e) {
			this.logger.error("Error in handleMoreResourceChanges()", e);
		}
		this.logger.debug("########## handleMoreResourceChanges() end");
	}
	
	private void handleRefactoring(final DeltaEntry firstEntry, final DeltaEntry secondEntry) throws Exception {
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
		this.logger.debug("########### handleOneResourceChange start");
		final DeltaEntry entry = this.actualDelta.get(0);
		try {
			if (entry != null) {
				final IResource resource = entry.getResource();
				switch (entry.getType()) {
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
		} catch (final Exception e) {
			this.logger.error("handleOneResourceChange()", e);
		}
		this.logger.debug("########### handleOneResourceChange end");

	}
	
	private void handleResourceChanged(final IResource resource) {
		if (resource instanceof IFile) {
			final IFile file = (IFile) resource;
			this.logger.debug(">>>>>>>>>>>>>>>>>>> Applying changes for file: " + file.getProjectRelativePath().toOSString());
			final String fileContent = FileUtilities.fileContentToString(file);
			EclipseActionMonitor.applyContentChangesForActualFile(fileContent);
		}
	}

	private void handleResourceAdded(final IResource res) throws Exception {
		if (res.getType() == IResource.FILE) {
			final IFile file = (IFile) res;
			this.logger.debug("File was added: " + file.getProjectRelativePath());
			EclipseActionMonitor.addFile(file);
		} else if (res.getType() == IResource.FOLDER) {
			final IFolder folder = (IFolder) res;
			this.logger.debug("Folder / package was added: " + folder.getProjectRelativePath());
			EclipseActionMonitor.addFolder(folder);
		} else if (res.getType() == IResource.PROJECT) {
			final IProject project = (IProject) res;
			EclipseActionMonitor.addProject(project);
			this.logger.debug("Project was added: " + project.getName());
		}
	}
	
	private void handleResourceRemoved(final IResource res) throws Exception {
		this.logger.debug("########### handleResourceRemoved start");
		if (res.getType() == IResource.FILE) {
			final IFile file = (IFile) res;
			this.logger.debug("File deleted: " + res.getProjectRelativePath().toOSString());
			EclipseActionMonitor.deleteFile(file);
		} else if (res.getType() == IResource.FOLDER) {
			final IFolder folder = (IFolder) res;
			this.logger.debug("Folder deleted: " + res.getProjectRelativePath().toOSString());
			EclipseActionMonitor.deleteFolder(folder);
		}
		this.logger.debug("########### handleResourceRemoved end");
	}

	@Override
	public boolean visit(final IResourceDelta delta) throws CoreException {
		final IResource res = delta.getResource();
		boolean result = true;

		try {
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
		} catch (final Exception e) {
			this.logger.error("Error occured during delta visit()", e);
		}

		return result;
	}
	 
	private boolean handleChangeVisited(final IResource res) throws Exception {
		boolean result = true;
		if (res.getType() == IResource.FILE) {
			if (FileValidator.shouldBeFileLogged(res)) {
				this.logger.debug("File changed: " + res.getFullPath());
				this.actualDelta.add(new DeltaEntry(IResourceDelta.CHANGED, res));
				result = false;
			};
		}
		return result;
	}

	private boolean handleRemoveVisited(final IResource res) throws Exception {
		boolean result = true;
		if (res.getType() == IResource.FILE) {
			if (FileValidator.shouldBeFileLogged(res)) {
				result = false;
				this.logger.debug("File removed: " + res.getFullPath());
			}
		} else if (res.getType() == IResource.FOLDER) {
			if (!FileValidator.isInBinDirecotory(res)) {
				result = false;
				this.logger.debug("Folder / package removed: " + res.getFullPath());
			}
		} else if (res.getType() == IResource.PROJECT) {
			result = false;
			this.logger.debug("Project removed: " + res.getFullPath());
		}
		if (!result) {
			this.actualDelta.add(new DeltaEntry(IResourceDelta.REMOVED, res));
		}
		
		return result;
	}

	private boolean handleAddVisited(final IResource res) throws Exception {
		boolean result = true;
		if (res.getType() == IResource.FILE) {
			if (FileValidator.shouldBeFileLogged(res)) {
				result = false;
				this.logger.debug("File added: " + res.getFullPath());
			}
		} else if (res.getType() == IResource.FOLDER) {
			if (!FileValidator.isInBinDirecotory(res)) {
				result = false;
				this.logger.debug("Folder / package added: " + res.getFullPath());
			}
		} else if (res.getType() == IResource.PROJECT) {
			result = false;
			this.logger.debug("Project added: " + res.getFullPath());
		}
		
		if (!result) {
			this.actualDelta.add(new DeltaEntry(IResourceDelta.ADDED, res));
		}
		
		return result;
	}

}
