package eclipselogger.sender;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DummyLocalUploader implements ActionUploaderIF {

	private int counter = 0;
	private static final String DIR = "xmlfiles\\";
	
	@Override
	public void uploadEclipseActionToServer(final String xmlFile) throws UploadActionException {
		this.counter++;
		BufferedWriter writer = null;
		final String timestamp = getFormattedTimestamp();
		final String fileName = DIR + "ACTION_" + timestamp + "_" + this.counter + ".xml";
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
	
	private String getFormattedTimestamp() {
		final SimpleDateFormat format = new SimpleDateFormat("yyyymmddHHmmss");
		return format.format(new Date());
	}
	
}
