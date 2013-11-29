package eclipselogger.utils;

import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

public class PackageDistanceCalculator {
	public static int calculatePackageDistance(final IResource first, final IResource second) {
		final Vector<String> firstFoldersVector = getFoldersVector(first);
		final Vector<String> secondFoldersVector = getFoldersVector(second);
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
		final String[] folders = path.split("/");
		final Vector<String> result = new Vector<String>();
		for (int i=0; i<folders.length; i++) {
			result.add(folders[i]);
		}
		
		return result;
	}
	
	public static Vector<String> getFoldersVector(final String res) {
		final String path = res;
		
		final String[] folders = path.split("/");
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
		final String path1 = "src/eclipselogger/events/actions/lol/lala";
		final String path2 = "src/eclipselogger/events/actions/lulu/lele";
		
		final Vector<String> v1 = getFoldersVector(path1);
		final Vector<String> v2 = getFoldersVector(path2);
		System.out.println(v1);
		System.out.println(v2);
		
		System.out.println(calculateFoldersVectorDistance(v1, v2));
	}
}
