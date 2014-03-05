package eclipselogger.sender;

public class UploadActionException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private Exception nestedException;
	
	public UploadActionException() {
		super();
	}
	
	public UploadActionException(final Exception e) {
		super(e);
		this.nestedException = e;
	}
	
	public UploadActionException(final String message) {
		super(message);
	}
	
	public Exception getNestedException() {
		return this.nestedException;
	}
	
	

}
