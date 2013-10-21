package eclipselogger.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;

public class PackageUtils {
	
	public static boolean checkIfSamePackage(IFile actual, IFile previous) {
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
	
	public static boolean checkIfSameProject(IFile actual, IFile previous) {
		boolean result = false;
		if (actual == null || previous == null) {
			return result;
		}
		
		String actualProjectName = actual.getProject().getName();
		String previousProjectName = actual.getProject().getName();
		if (actualProjectName != null && actualProjectName.equals(previousProjectName)) {
			result = true;
		}
		
		return result;
	}
	
	public static boolean checkIfSameProject(IFolder folder, IFile file) {
		boolean result = false;
		if (folder == null || file == null) {
			return result;
		}
		String actualProjectName = folder.getProject().getName();
		String previousProjectName = file.getProject().getName();
		if (actualProjectName != null && actualProjectName.equals(previousProjectName)) {
			result = true;
		}
		
		return result;
	}
	
	public static boolean checkIfSamePackage(IFolder folder, IFile file) {
		boolean result = false;
		if (folder == null || file == null) {
			return result;
		}
		
		String filePath = file.getProjectRelativePath().toOSString();
		filePath = filePath.substring(0, filePath.indexOf(file.getName()));
		
		String folderParentPath = folder.getParent().getProjectRelativePath().toOSString();
		if (folderParentPath != null && folderParentPath.equalsIgnoreCase(filePath)) {
			result = true;
		}
		
		return result;
	}
	
}
