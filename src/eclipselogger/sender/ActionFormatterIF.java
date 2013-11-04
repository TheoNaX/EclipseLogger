package eclipselogger.sender;

import eclipselogger.events.actions.EclipseAction;

public interface ActionFormatterIF {
	String formatEclipseAction(EclipseAction action);
}
