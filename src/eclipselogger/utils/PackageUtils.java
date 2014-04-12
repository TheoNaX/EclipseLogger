package eclipselogger.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

import eclipselogger.events.EclipseResource;
import eclipselogger.resources.EclipseFile;
import eclipselogger.resources.EclipseFolder;

public class PackageUtils {
	
	public static boolean checkIfSamePackage(final IFile actual, final IFile previous) {
		boolean result = false;
		if (actual == null || previous == null) {
			return result;
		}
		
		String actualPath = actual.getProjectRelativePath().toOSString();
		actualPath = actualPath.substring(0, actualPath.indexOf(actual.getName()));
		
		String previousPath = previous.getProjectRelativePath().toOSString();
		previousPath = previousPath.substring(0, previousPath.indexOf(previous.getName()));
		if (actualPath != null && actualPath.equals(previousPath)) {
			result = true;
		}
		
		return result;
	}
	
	public static boolean checkIfSamePackage(final EclipseFile actual, final EclipseFile previous) {
		boolean result = false;
		if (actual == null || previous == null) {
			return result;
		}
		
		String actualPath = actual.getProjectRelativePath();
		actualPath = actualPath.substring(0, actualPath.indexOf(actual.getFileName()));
		
		String previousPath = previous.getProjectRelativePath();
		previousPath = previousPath.substring(0, previousPath.indexOf(previous.getFileName()));
		if (actualPath != null && actualPath.equals(previousPath)) {
			result = true;
		}
		
		return result;
	}
	
	public static boolean checkIfSameProject(final IFile actual, final IFile previous) {
		boolean result = false;
		if (actual == null || previous == null) {
			return result;
		}
		
		final String actualProjectName = actual.getProject().getName();
		final String previousProjectName = previous.getProject().getName();
		if (actualProjectName != null && actualProjectName.equals(previousProjectName)) {
			result = true;
		}
		
		return result;
	}
	
	public static boolean checkIfSameProject(final EclipseFile actual, final EclipseFile previous) {
		boolean result = false;
		if (actual == null || previous == null) {
			return result;
		}
		
		final String actualProjectName = actual.getProjectName();
		final String previousProjectName = previous.getProjectName();
		if (actualProjectName != null && actualProjectName.equals(previousProjectName)) {
			result = true;
		}
		
		return result;
	}
	
	public static boolean checkIfSameProject(final EclipseFolder folder, final EclipseFile file) {
		boolean result = false;
		if (folder == null || file == null) {
			return result;
		}
		final String actualProjectName = folder.getProjectName();
		final String previousProjectName = file.getProjectName();
		if (actualProjectName != null && actualProjectName.equals(previousProjectName)) {
			result = true;
		}
		
		return result;
	}
	
	public static boolean checkIfSamePackage(final EclipseFolder folder, final EclipseFile file) {
		boolean result = false;
		if (folder == null || file == null) {
			return result;
		}
		
		String filePath = file.getProjectRelativePath();
		filePath = filePath.substring(0, filePath.indexOf(file.getFileName()));
		
		final String folderParentPath = folder.getParentPath();
		if (folderParentPath != null && folderParentPath.equalsIgnoreCase(filePath)) {
			result = true;
		}
		
		return result;
	}
	
	public static String getPackageFromResource(final EclipseResource resource) {
		String ePackage = null;
		if (resource.getType() == IResource.FOLDER) {
			final EclipseFolder folder = (EclipseFolder) resource;
			final String folderParentPath = folder.getParentPath();
			ePackage = folderParentPath;
		} else if (resource.getType() == IResource.FILE) {
			final EclipseFile file = (EclipseFile) resource;
			ePackage = getPackageFromFile(file);
		}
		
		if (ePackage == null) {
			ePackage = "root";
		}
		return ePackage;
		
	}

	private static String getPackageFromFile(final EclipseFile file) {
		String filePath = file.getProjectRelativePath();
		filePath = filePath.substring(0, filePath.indexOf(file.getFileName()));
		
		return filePath;
	}
	
}
