package eclipselogger.utils;

import java.util.Random;

public class EclipseUtils {
	
	private static Random generator = new Random();
	
	private static String generateIdNumber() {
		final int id = generator.nextInt(4000) + 100;
		return String.format("%04d", id);
	}
	
	public static String generateUserId() {
		final String userName = System.getProperty("user.name", "eclipse");
		final String number = generateIdNumber();
		return userName + number;
	}
}
