package eclipselogger.utils;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;

public class FileUtilities {
	
	private static Logger logger = Logger.getLogger(FileUtilities.class);
	
	public static String fileContentToString(final IFile file) {
		String content = null;
		try {
			content = IOUtils.toString(file.getContents());
		} catch (final Exception e) {
			logger.error("Failed to get content from file", e);
		}
		
		return content;
	}
	
	public static String ensureDirectoryProperFormat(final String dir) {
		if (dir != null && dir.length() < 1) {
			return "";
		} else {
			final StringBuilder sb = new StringBuilder(dir);
			if (!dir.endsWith("/")) {
				sb.append("/");
			}
			return sb.toString();
		}
		
	}
	
	
}
