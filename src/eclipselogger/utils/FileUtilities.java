package eclipselogger.utils;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;

public class FileUtilities {
	
	public static String fileContentToString(IFile file) {
		String content = null;
		try {
			content = IOUtils.toString(file.getContents());
		} catch (Exception ignore) {}
		
		return content;
	}
	
	
}
