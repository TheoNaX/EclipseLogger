package eclipselogger.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

import eclipselogger.resources.EclipseFile;

public class FileValidator {
	
	public static boolean shouldBeFileLogged(final IResource resource) {
		boolean result = true;
		if (isClassFile(resource)) {
			result = false;
		}
		if (isInBinDirecotory(resource)) {
			result = false;
		}
		
		return result;
	}
	
	private static boolean isClassFile(final IResource resource) {
		boolean result = false;
		if (resource instanceof IFile) {
			final IFile file = (IFile)resource;
			final String extension = file.getFileExtension();
			if (extension != null && extension.equalsIgnoreCase("class")) {
				result = true;
			}
		}
		return result;
			
	}
	
	public static boolean isInBinDirecotory(final IResource resource) {
		final IPath relativePath = resource.getProjectRelativePath();
		if (relativePath.toOSString().startsWith("bin")) {
			return true;
		}
		
		return false;
	}
	
	public static boolean haveFilesTheSameExtension(final EclipseFile firstFile, final EclipseFile secondFile) {
		boolean result = false;
		if (firstFile == null || secondFile == null) {
			return result;
		}
		final String firstExt = firstFile.getFileExtension();
		final String secondExt = secondFile.getFileExtension();
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
