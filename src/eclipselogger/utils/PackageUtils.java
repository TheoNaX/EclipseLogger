package eclipselogger.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;

import eclipselogger.events.EclipseFile;

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
	
	public static boolean checkIfSamePackage(final IFile actual, final EclipseFile previous) {
		boolean result = false;
		if (actual == null || previous == null) {
			return result;
		}
		
		String actualPath = actual.getProjectRelativePath().toOSString();
		actualPath = actualPath.substring(0, actualPath.indexOf(actual.getName()));
		
		String previousPath = previous.getRelativePath();
		previousPath = previousPath.substring(0, previousPath.indexOf(previous.getName()));
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
	
	public static boolean checkIfSameProject(final IFile actual, final EclipseFile previous) {
		boolean result = false;
		if (actual == null || previous == null) {
			return result;
		}
		
		final String actualProjectName = actual.getProject().getName();
		final String previousProjectName = previous.getProjectName();
		if (actualProjectName != null && actualProjectName.equals(previousProjectName)) {
			result = true;
		}
		
		return result;
	}
	
	public static boolean checkIfSameProject(final IFolder folder, final EclipseFile file) {
		boolean result = false;
		if (folder == null || file == null) {
			return result;
		}
		final String actualProjectName = folder.getProject().getName();
		final String previousProjectName = file.getProjectName();
		if (actualProjectName != null && actualProjectName.equals(previousProjectName)) {
			result = true;
		}
		
		return result;
	}
	
	public static boolean checkIfSamePackage(final IFolder folder, final EclipseFile file) {
		boolean result = false;
		if (folder == null || file == null) {
			return result;
		}
		
		String filePath = file.getRelativePath();
		filePath = filePath.substring(0, filePath.indexOf(file.getName()));
		
		final String folderParentPath = folder.getParent().getProjectRelativePath().toOSString();
		if (folderParentPath != null && folderParentPath.equalsIgnoreCase(filePath)) {
			result = true;
		}
		
		return result;
	}
	
}
