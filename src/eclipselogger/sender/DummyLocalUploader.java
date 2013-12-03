package eclipselogger.sender;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import eclipselogger.utils.ConfigReader;

public class DummyLocalUploader implements ActionUploaderIF {

	private final String targetDirectory;
	
	public DummyLocalUploader() {
		this.targetDirectory = ConfigReader.getLocalFileTargetDirectory();
	}
	
	@Override
	public void uploadEclipseActionToServer(final int actionId, final Date timestamp, final String xmlFile) throws UploadActionException {
		BufferedWriter writer = null;
		final String formattedTime = getFormattedTimestamp(timestamp);
		final String fileName = createFileName(formattedTime, actionId);
		try {
			writer = new BufferedWriter(new FileWriter(fileName));
			writer.write(xmlFile);
		} catch (final Exception e) {
			e.printStackTrace();
			throw new UploadActionException();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (final Exception ignore) {}
			}
		}
		
	}
	
	private String getFormattedTimestamp(final Date date) {
		final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return format.format(date);
	}
	
	private String createFileName(final String timestamp, final int action_id) {
		return String.format("%sACTION_%s_%05d.xml", this.targetDirectory, timestamp, action_id);
	}
	
}
