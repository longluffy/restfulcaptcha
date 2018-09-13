package luffy.captcha;

import java.io.IOException;
import java.io.InputStream;

import com.DeathByCaptcha.Captcha;
import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.HttpClient;

public class CaptchaSolver {

	
	public static String readCaptcha(InputStream cropImage) throws InterruptedException {

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

				captcha = client.decode(cropImage, 120);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Failed uploading CAPTCHA");
				return "FaileduploadingCAPTCHA";
			}
			if (null != captcha) {
				System.out.println("CAPTCHA " + captcha.id + " solved: " + captcha.text);
				return captcha.text;
			} else {
				System.out.println("Failed solving CAPTCHA");
				return "captchasolvefailed";

			}
		} catch (com.DeathByCaptcha.Exception e) {
			System.out.println(e);
			return e.toString();
		}

	}
}
