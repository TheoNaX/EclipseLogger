package eclipselogger;

import org.eclipse.core.resources.IResource;

public class DeltaEntry {
	private Integer type;
	private IResource resource;
	
	public DeltaEntry(Integer type, IResource resource) {
		this.type = type;
		this.resource = resource;
	}

	public Integer getType() {
		return type;
	}

	public IResource getResource() {
		return resource;
	}
	
	
}
