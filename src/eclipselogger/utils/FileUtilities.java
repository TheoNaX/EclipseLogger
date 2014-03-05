package eclipselogger.utils;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;

public class FileUtilities {
	
	public static String fileContentToString(final IFile file) {
		String content = null;
		try {
			content = IOUtils.toString(file.getContents());
		} catch (final Exception ignore) {}
		
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
