package eclipselogger.listeners;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;

import eclipselogger.DeltaHandler;

public class ResourceChangeListener implements IResourceChangeListener {
	
	private final DeltaHandler handler = new DeltaHandler();
	private final Logger logger = Logger.getLogger(ResourceChangeListener.class);

	@Override
	public void resourceChanged(final IResourceChangeEvent event) {
		final IResource res = event.getResource();
		try {
			switch (event.getType()) {
			case IResourceChangeEvent.POST_CHANGE:
				if (res != null) {
					this.logger.debug("Resource changed: " + res.getProjectRelativePath().toOSString());
				}
				this.handler.startDeltaHandling();
				event.getDelta().accept(this.handler);
				this.handler.stopDeltaHandling();
				break;
			case IResourceChangeEvent.POST_BUILD:
				this.logger.debug("Build complete");
				// event.getDelta().accept(handler);
				break;
			}
		} catch (final Exception e) {
			this.logger.error("Exception occured during delta handling", e);
		}

	}

}
