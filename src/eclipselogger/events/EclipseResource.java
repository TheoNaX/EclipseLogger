package eclipselogger.events;

public abstract class EclipseResource {
	
	
	protected int resourceType;
	
	public int getType() {
		return this.resourceType;
	}
	
	public abstract String getProjectRelativePath();
	

}
