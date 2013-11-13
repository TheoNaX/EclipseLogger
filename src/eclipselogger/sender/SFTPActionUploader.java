package eclipselogger.sender;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import eclipselogger.utils.ConfigReader;

public class SFTPActionUploader implements ActionUploaderIF {

	private final String ipAddress;
	private final int port;
	private final String userName;
	private final String password;
	private final String targetDirectory;
	
	private final JSch jsch = new JSch();
	private Session session;
	private ChannelSftp sftp;
	
	public SFTPActionUploader() throws Exception {
		this.ipAddress = ConfigReader.getSftpServerIPAddress();
		if (this.ipAddress == null) {
			throw new Exception("Missing IP address of SFTP server");
		}
		this.port = ConfigReader.getSftpServerPort();
		this.targetDirectory = ConfigReader.getSftpFileTargetDirectory();
		this.userName = ConfigReader.getSftpUserName();
		this.password = ConfigReader.getSftpPassword();
		if (this.userName == null || this.password == null) {
			throw new Exception("Missing authentication credentials for SFTP server");
		}
	}
	
	private void checkConnection() throws JSchException {
		if ((this.session == null || !this.session.isConnected()) || (this.sftp == null || !this.sftp.isConnected())) {
			reconnect();
		}
	}
	
	private void reconnect() throws JSchException {
		try {
			if (this.sftp != null) {
				this.sftp.disconnect();
			}
		} catch (final Exception ignore) {}
		
		try {
			if (this.session != null) {
				this.session.disconnect();
			}
		} catch (final Exception ignore) {}
		
		this.session = this.jsch.getSession(this.userName, this.ipAddress, this.port);
		this.session.setPassword(this.password);
		
		this.sftp = (ChannelSftp) this.session.openChannel("sftp");
	}
	
	@Override
	public void uploadEclipseActionToServer(final String xmlFile) throws UploadActionException {
		// TODO filename should contain timestamp + some id
		final String fileName = this.targetDirectory + "";
		InputStream is = null;
		try {
			// check connection
			checkConnection();

			// create input stream from XML string
			is = new ByteArrayInputStream(xmlFile.getBytes());
			// upload file
			this.sftp.put(is, fileName);
		} catch (final Exception e) {
			throw new UploadActionException();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (final Exception ignore) {}
			}
		}
	}

}
