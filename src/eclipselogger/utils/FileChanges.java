package eclipselogger.utils;

public class FileChanges {
	private int changedLines;
	private int deletedLines;
	private int addedLines;
	
	public FileChanges() {}
	
	public FileChanges(int changed, int deleted, int added) {
		this.changedLines = changed;
		this.deletedLines = deleted;
		this.addedLines = added;
	}
	
	public void updateFileChanges(FileChanges changes) {
		this.changedLines += changes.getChangedLines();
		this.deletedLines += changes.getDeletedLines();
		this.addedLines += changes.getAddedLines();
	}

	public int getChangedLines() {
		return changedLines;
	}

	public void increaseChangedLines(int changedLines) {
		this.changedLines += changedLines;
	}

	public int getDeletedLines() {
		return deletedLines;
	}

	public void increaseDeletedLines(int deletedLines) {
		this.deletedLines += deletedLines;
	}

	public int getAddedLines() {
		return addedLines;
	}

	public void increaseAddedLines(int addedLines) {
		this.addedLines += addedLines;
	}
	
	@Override
	public String toString() {
		return "File line changes: changed: " + changedLines + ", added: " + addedLines
				+ ", deleted: " + deletedLines;
	}
	
	
}
