package eclipselogger.listeners;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;

import eclipselogger.DeltaHandler;

public class ResourceChangeListener implements IResourceChangeListener {
	
	DeltaHandler handler = new DeltaHandler();

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		IResource res = event.getResource();
		try {
			switch (event.getType()) {
			case IResourceChangeEvent.POST_CHANGE:
				System.out.println(">>>> Resource changed >>>>>>");
				handler.startDeltaHandling();
				event.getDelta().accept(handler);
				handler.stopDeltaHandling();
				break;
			case IResourceChangeEvent.POST_BUILD:
				System.out.println("Build complete.");
				// event.getDelta().accept(handler);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
