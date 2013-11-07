package eclipselogger.logging;

import org.apache.log4j.Logger;

import eclipselogger.events.actions.EclipseAction;

public class Log4jActionLogger implements EclipseActiontLogIF {

	Logger logger = Logger.getLogger(Log4jActionLogger.class);
	
	@Override
	public void logEclipseAction(final EclipseAction action, final boolean context) {
		this.logger.debug(action.toString());
		
	}
	

}
