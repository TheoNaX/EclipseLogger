package eclipselogger.events;

import org.eclipse.core.resources.IFile;

public class EclipseFile {
	
	private final String relativePath;
	private final String fileName;
	private final String projectName;
	private final String fileExtension;
	
	public EclipseFile(final IFile file) {
		this.relativePath = file.getProjectRelativePath().toOSString();
		this.fileName = file.getName();
		this.projectName = file.getProject().getName();
		this.fileExtension = file.getFileExtension();
	}
	
	public String getRelativePath() {
		return this.relativePath;
	}
	
	public String getName() {
		return this.fileName;
	}
	
	public String getProjectName() {
		return this.projectName;
	}
	
	public String getFileExtension() {
		return this.fileExtension;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == null || !(obj instanceof IFile)) {
			return false;
		}
		final IFile file = (IFile) obj;
		boolean result = false;
		if (file != null && this.relativePath != null) {
			if (this.relativePath.equalsIgnoreCase(file.getProjectRelativePath().toOSString())) {
				result = true;
			}
		}
		
		return result;
	}

}
