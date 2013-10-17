package eclipselogger.listeners;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;

import eclipselogger.DeltaHandler;

public class ResourceChangeListener implements IResourceChangeListener {
	
	DeltaHandler handler = new DeltaHandler();

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		IResource res = event.getResource();
        try {
		switch (event.getType()) {
           case IResourceChangeEvent.PRE_CLOSE:
              System.out.print("Project ");
              System.out.print(res.getFullPath());
              System.out.println(" is about to close.");
              break;
           case IResourceChangeEvent.PRE_DELETE:
              System.out.print("Project ");
              System.out.print(res.getFullPath());
              System.out.println(" is about to be deleted.");
              break;
           case IResourceChangeEvent.POST_CHANGE:
              System.out.println("Resources have changed.");
              event.getDelta().accept(handler);
              break;
           case IResourceChangeEvent.PRE_BUILD:
              System.out.println("Build about to run.");
              event.getDelta().accept(handler);
              break;
           case IResourceChangeEvent.POST_BUILD:
              System.out.println("Build complete.");
              event.getDelta().accept(handler);
              break;
        }
        } catch (Exception e) {
        	e.printStackTrace();
        }
		
	}
	
	class DeltaPrinter implements IResourceDeltaVisitor {
		public boolean visit(IResourceDelta delta) {
			IResource res = delta.getResource();
			if (res.getType() == IResource.FILE) {
				System.out.println("Resource is FILE");
			} else if (res.getType() == IResource.FOLDER) {
				System.out.println("Resource is folder");
			} else if (res.getType() == IResource.PROJECT) {
				System.out.println("Resource is project");
			}
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:

				System.out.print("Resource ");
				System.out.print(res.getFullPath());
				System.out.println(" was added.");
				break;
			case IResourceDelta.REMOVED:
				System.out.print("Resource ");
				System.out.print(res.getFullPath());
				System.out.println(" was removed.");
				break;
			case IResourceDelta.CHANGED:
				if (res.getType() == IResource.FILE) {
					System.out.print("Resource ");
					System.out.print(res.getFullPath());
					System.out.println(" has changed.");
				}
				break;
			}
			return true; // visit the children
		}
	}

}
