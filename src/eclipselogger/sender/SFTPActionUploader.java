package eclipselogger.sender;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import eclipselogger.utils.ConfigReader;
import eclipselogger.utils.EclipseUtils;
import eclipselogger.utils.FileUtilities;

public class SFTPActionUploader implements ActionUploaderIF {

	private final String ipAddress;
	private final int port;
	private final String userName;
	private final String password;
	private final String targetDirectory;
	
	private final JSch jsch = new JSch();
	private Session session;
	private ChannelSftp sftp;
	
	private final String userId;
	
	private static Logger logger = Logger.getLogger(SFTPActionUploader.class);
	
	/**
	 * Constructor of SFTPActionUloader
	 * @throws Exception
	 */
	public SFTPActionUploader() throws Exception {
		this.ipAddress = ConfigReader.getSftpServerIPAddress();
		if (this.ipAddress == null) {
			throw new Exception("Missing IP address of SFTP server");
		}
		this.port = ConfigReader.getSftpServerPort();
		this.targetDirectory = FileUtilities.ensureDirectoryProperFormat(ConfigReader.getSftpFileTargetDirectory());
		this.userName = ConfigReader.getSftpUserName();
		this.password = ConfigReader.getSftpPassword();
		if (this.userName == null || this.password == null) {
			throw new Exception("Missing authentication credentials for SFTP server");
		}
		
		this.userId = getUserId();
	}
	
	private void checkConnection() throws JSchException {
		if ((this.session == null || !this.session.isConnected()) || (this.sftp == null || !this.sftp.isConnected())) {
			logger.info("Session is not connected or SFTP channel down, reconnecting ...");
			reconnect();
		}
	}
	
	private void reconnect() throws JSchException {
		try {
			if (this.sftp != null) {
				logger.debug("SFTP channel not null, disconnecting");
				this.sftp.disconnect();
			}
		} catch (final Exception ignore) {logger.error("Disconnect of SFTP failed");}
		
		try {
			if (this.session != null) {
				logger.debug("Session not null, disconnecting");
				this.session.disconnect();
			}
		} catch (final Exception ignore) {logger.error("Disconnect of session failed");}
		
		this.session = this.jsch.getSession(this.userName, this.ipAddress, this.port);
		this.session.setConfig("StrictHostKeyChecking", "no");
		this.session.setPassword(this.password);
		logger.debug("Connectiong session");
		this.session.connect();
		logger.debug("Session connected: " + this.session.isConnected());
		
		this.sftp = (ChannelSftp) this.session.openChannel("sftp");
		if (this.sftp == null) {
			logger.debug("SFTP channel is null");
		} else {
			logger.debug("Connectiong SFTP channel");
			this.sftp.connect();
		}
	}
	
	@Override
	public void uploadEclipseActionToServer(final int actionId, final Date timestamp, final String xmlFile) throws UploadActionException {
		// TODO maybe some user identification should be used as for more users file name can be the same
		final String fileName = createFileName(getFormattedTimestamp(timestamp), actionId);
		InputStream is = null;
		try {
			// check connection
			logger.debug("Going to check connection");
			checkConnection();

			// create input stream from XML string
			is = new ByteArrayInputStream(xmlFile.getBytes());
			// upload file
			logger.debug("Uploading file: " + fileName);
			this.sftp.put(is, fileName);
		} catch (final Exception e) {
			throw new UploadActionException(e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (final Exception ignore) {}
			}
		}
	}
	
	private String createFileName(final String timestamp, final int action_id) {
		return String.format("%sACTION_%s_%05d_%s.xml", this.targetDirectory, timestamp, action_id, this.userId);
	}
	
	private static String getUserId() {
		String userId = ConfigReader.getUserId();
		if (userId == null) {
			userId = EclipseUtils.generateUserId();
			ConfigReader.setUserId(userId);
			ConfigReader.saveProperties();
		}
		
		return userId;
		
	}
	
	private String getFormattedTimestamp(final Date date) {
		final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return format.format(date);
	}

}
