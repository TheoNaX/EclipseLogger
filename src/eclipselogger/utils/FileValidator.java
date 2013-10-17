package eclipselogger.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

public class FileValidator {
	
	public static boolean shouldBeFileLogged(IResource resource) {
		boolean result = true;
		if (isClassFile(resource)) {
			result = false;
		}
		if (isInBinDirecotory(resource)) {
			result = false;
		}
		
		return result;
	}
	
	private static boolean isClassFile(IResource resource) {
		boolean result = false;
		if (resource instanceof IFile) {
			IFile file = (IFile)resource;
			String extension = file.getFileExtension();
			if (extension != null && extension.equalsIgnoreCase("class")) {
				result = true;
			}
		}
		return result;
			
	}
	
	public static boolean isInBinDirecotory(IResource resource) {
		IPath relativePath = resource.getProjectRelativePath();
		if (relativePath.toOSString().startsWith("bin")) {
			return true;
		}
		
		return false;
	}
	
	public static boolean haveFilesTheSameExtension(IFile firstFile, IFile secondFile) {
		boolean result = false;
		String firstExt = firstFile.getFileExtension();
		String secondExt = secondFile.getFileExtension();
		if (firstExt == null && secondExt == null) {
			result = true;
		} else if (firstExt == null || secondExt == null) {
			result = false;
		} else if (firstExt.equalsIgnoreCase(secondExt)) {
			result = true;
		}
		
		return result;
	}
}
