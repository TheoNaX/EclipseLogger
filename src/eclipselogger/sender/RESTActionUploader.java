package eclipselogger.sender;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class RESTActionUploader implements ActionUploaderIF {

	public static final String WEBSERVICE_URL = "";
	CloseableHttpClient httpClient = HttpClients.createDefault();
	
	@Override
	public void uploadEclipseActionToServer(final String xmlFile) throws UploadActionException{
		StringEntity input = null;
		final HttpPost httpPost = new HttpPost(WEBSERVICE_URL);
		try {
			input = new StringEntity(xmlFile);
			input.setContentType("text/xml");
			httpPost.setEntity(input);
			final HttpResponse response = this.httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new Exception("HTTP POST response did not have code 200");
			}
			
		} catch (final Exception e) {
			throw new UploadActionException();
		}
	}

}
