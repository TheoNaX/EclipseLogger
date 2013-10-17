package eclipselogger.listeners;

import org.eclipse.jgit.lib.Repository;

public class GitListener {

	public void init() {
		Repository.getGlobalListenerList().addConfigChangedListener(null);
	}
}
