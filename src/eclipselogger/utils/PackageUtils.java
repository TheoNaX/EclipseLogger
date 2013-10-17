package eclipselogger.utils;

import org.eclipse.core.resources.IFile;

public class PackageUtils {
	
	public static boolean checkIfSameDirectory(IFile actual, IFile previous) {
		boolean result = false;
		
		String actualPath = actual.getProjectRelativePath().toOSString();
		actualPath = actualPath.substring(0, actualPath.indexOf(actual.getName()));
		
		String previousPath = previous.getProjectRelativePath().toOSString();
		previousPath = previousPath.substring(0, previousPath.indexOf(previous.getName()));
		if (actualPath != null && actualPath.equals(previousPath)) {
			result = true;
		}
		
		return result;
	}
	
	public static boolean checkIfSameProject(IFile actual, IFile previous) {
		boolean result = false;
		
		String actualProjectName = actual.getProject().getName();
		String previousProjectName = actual.getProject().getName();
		if (actualProjectName != null && actualProjectName.equals(previousProjectName)) {
			result = true;
		}
		
		return result;
	}
	
}
