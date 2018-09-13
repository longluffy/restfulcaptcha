package luffy.captcha;
import com.DeathByCaptcha.AccessDeniedException;
import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.HttpClient;
import com.DeathByCaptcha.SocketClient;
import com.DeathByCaptcha.Captcha;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

class ExampleSimple {
	public static void main(String[] args) throws Exception {
		// Put your DBC username & password here:
		String username = "longluffy";
		String password = "Chopper2791";
		Client client = (Client) (new HttpClient(username, password));
		// Client client = (Client)(new SocketClient(args[0], args[1]));
		client.isVerbose = true;

		try {
			try {
				System.out.println("Your balance is " + client.getBalance() + " US cents");
			} catch (IOException e) {
				System.out.println("Failed fetching balance: " + e.toString());
				return;
			}

			Captcha captcha = null;
			
			 
			try {

				FileInputStream imagefile = new java.io.FileInputStream("D:\\captcha\\captcha1.png");
				// Upload a CAPTCHA and poll for its status with 120 seconds timeout.
				// Put you CAPTCHA image file name, file object, input stream, or
				// vector of bytes, and optional solving timeout (in seconds) here.
				captcha = client.decode(imagefile, 120);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Failed uploading CAPTCHA");
				return;
			}
			if (null != captcha) {
				System.out.println("CAPTCHA " + captcha.id + " solved: " + captcha.text);

				// Report incorrectly solved CAPTCHA if necessary.
				// Make sure you've checked if the CAPTCHA was in fact incorrectly
				// solved, or else you might get banned as abuser.
				/*
				 * try { if (client.report(captcha)) {
				 * System.out.println("Reported as incorrectly solved"); } else {
				 * System.out.println("Failed reporting incorrectly solved CAPTCHA"); } } catch
				 * (IOException e) {
				 * System.out.println("Failed reporting incorrectly solved CAPTCHA: " +
				 * e.toString()); }
				 */
			} else {
				System.out.println("Failed solving CAPTCHA");
			}
		} catch (com.DeathByCaptcha.Exception e) {
			System.out.println(e);
		}
	}

	public static String readCaptcha(InputStream inpStream) throws InterruptedException {

		String username = "longluffy";
		String password = "Chopper2791";
		Client client = (Client) (new HttpClient(username, password));
		client.isVerbose = true;

		try {
			try {
				System.out.println("Your balance is " + client.getBalance() + " US cents");
			} catch (IOException e) {
				System.out.println("Failed fetching balance: " + e.toString());
				return "DBC not enough balance";
			}

			Captcha captcha = null;
			try {

				captcha = client.decode(inpStream, 120);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Failed uploading CAPTCHA");
				return "Failed uploading CAPTCHA";
			}
			if (null != captcha) {
				System.out.println("CAPTCHA " + captcha.id + " solved: " + captcha.text);

				return captcha.text;
			} else {
				System.out.println("Failed solving CAPTCHA");
			}
		} catch (com.DeathByCaptcha.Exception e) {
			System.out.println(e);
		}

		return "";
	}
	
	public static String readCaptcha(File inpStream) throws InterruptedException {

		String username = "longluffy";
		String password = "Chopper2791";
		Client client = (Client) (new HttpClient(username, password));
		client.isVerbose = true;

		try {
			try {
				System.out.println("Your balance is " + client.getBalance() + " US cents");
			} catch (IOException e) {
				System.out.println("Failed fetching balance: " + e.toString());
				return "DBC not enough balance";
			}

			Captcha captcha = null;
			try {

				captcha = client.decode(inpStream, 120);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Failed uploading CAPTCHA");
				return "Failed uploading CAPTCHA";
			}
			if (null != captcha) {
				System.out.println("CAPTCHA " + captcha.id + " solved: " + captcha.text);

				return captcha.text;
			} else {
				System.out.println("Failed solving CAPTCHA");
			}
		} catch (com.DeathByCaptcha.Exception e) {
			System.out.println(e);
		}

		return "";
	}
	
	
	public static String readCaptcha(String inpStream) throws InterruptedException {

		String username = "longluffy";
		String password = "Chopper2791";
		Client client = (Client) (new HttpClient(username, password));
		client.isVerbose = true;

		try {
			try {
				System.out.println("Your balance is " + client.getBalance() + " US cents");
			} catch (IOException e) {
				System.out.println("Failed fetching balance: " + e.toString());
				return "DBC not enough balance";
			}

			Captcha captcha = null;
			try {

				captcha = client.decode(inpStream, 120);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Failed uploading CAPTCHA");
				return "Failed uploading CAPTCHA";
			}
			if (null != captcha) {
				System.out.println("CAPTCHA " + captcha.id + " solved: " + captcha.text);

				return captcha.text;
			} else {
				System.out.println("Failed solving CAPTCHA");
			}
		} catch (com.DeathByCaptcha.Exception e) {
			System.out.println(e);
		}

		return "";
	}
}
