package eclipselogger.utils;

import java.io.File;
import java.util.Vector;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

import eclipselogger.events.EclipseResource;
import eclipselogger.resources.EclipseFile;

public class PackageDistanceCalculator {
	
	
	public static int calculatePackageDistance(final EclipseResource first, final EclipseResource second) {
		final Vector<String> firstFoldersVector = getFoldersVector(first);
		System.out.println(">>>> First resource: " + first.getProjectRelativePath());
		System.out.println(">>>>> First vector: " + firstFoldersVector);
		final Vector<String> secondFoldersVector = getFoldersVector(second);
		System.out.println(">>>> Second resource: " + second.getProjectRelativePath());
		System.out.println(">>>>> Second vector: " + secondFoldersVector);
		final int firstLength = firstFoldersVector.size();
		final int secondLength = secondFoldersVector.size();
		
		if (firstLength >= secondLength) {
			return calculateFoldersVectorDistance(firstFoldersVector, secondFoldersVector);
		} else {
			return calculateFoldersVectorDistance(secondFoldersVector, firstFoldersVector);
		}
		
	}
	
	private static Vector<String> getFoldersVector(final IResource res) {
		String path = null;
		if (res instanceof IFile) {
			final IFile file = (IFile) res;
			path = res.getProjectRelativePath().toOSString();
			path = path.substring(0, path.indexOf(file.getName())-1);
		} else {
			path = res.getProjectRelativePath().toOSString();
		}
		final String[] folders = path.split(Pattern.quote(File.separator));
		final Vector<String> result = new Vector<String>();
		for (int i=0; i<folders.length; i++) {
			result.add(folders[i]);
		}
		
		return result;
	}
	
	private static Vector<String> getFoldersVector(final EclipseResource res) {
		String path = null;
		if (res.getType() == IResource.FILE) {
			final EclipseFile file = (EclipseFile) res;
			path = res.getProjectRelativePath();
			path = path.substring(0, path.indexOf(file.getFileName())-1);
		} else {
			path = res.getProjectRelativePath();
		}
		final String[] folders = path.split(Pattern.quote(File.separator));
		final Vector<String> result = new Vector<String>();
		for (int i=0; i<folders.length; i++) {
			result.add(folders[i]);
		}
		
		return result;
	}
	
	public static Vector<String> getFoldersVector(final String res) {
		final String path = res;
		
		final String[] folders = path.split(Pattern.quote(File.separator));
		final Vector<String> result = new Vector<String>();
		for (int i=0; i<folders.length; i++) {
			result.add(folders[i]);
		}
		
		return result;
	}
	
	public static int calculateFoldersVectorDistance(final Vector<String> longer, final Vector<String> shorter) {
		int distance = 0;
		final int longerSize = longer.size();
		final int shorterSize = shorter.size();
		
		int position = 0;
		for (int i=0; i<shorterSize; i++) {
			if (longer.get(i).equalsIgnoreCase(shorter.get(i))) {
				position++;
			} else {
				break;
			}
		}
		if (longerSize > (position)) {
			distance += (longerSize-position);
		}
		if (shorterSize > (position+1)) {
			distance += (shorterSize-position);
		}
		
		return distance;
	}
	
	
	public static void main(final String[] args) {
		final String path1 = "src\\eclipselogger\\events\\actions\\lol\\lala";
		final String path2 = "src\\eclipselogger\\events\\actions\\lulu\\lele";
		
		final Vector<String> v1 = getFoldersVector(path1);
		final Vector<String> v2 = getFoldersVector(path2);
		System.out.println(v1);
		System.out.println(v2);
		
		System.out.println(calculateFoldersVectorDistance(v1, v2));
	}
}
