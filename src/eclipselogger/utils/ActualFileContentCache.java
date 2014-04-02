package eclipselogger.utils;

public class ActualFileContentCache {
	private String actualFileContent;
	private String changedContent;
	
	public String getActualFileText() {
		return this.actualFileContent;
	}
	
	public void applyChangedContent(final String content) {
		this.changedContent = content;
	}
	
	public String getChangedContent() {
		return this.changedContent;
	}
	
	public void setActualFileText(final String content) {
		this.actualFileContent = content;
	}

}
