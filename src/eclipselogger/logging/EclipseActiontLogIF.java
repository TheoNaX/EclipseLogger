package eclipselogger.logging;

import eclipselogger.events.actions.EclipseAction;

public interface EclipseActiontLogIF {
	
	public static final String LOG4J_LOGGER = "log4j";
	public static final String CONSOLE_LOGGER = "console";
	
	void logEclipseAction(EclipseAction action, boolean context);
}
