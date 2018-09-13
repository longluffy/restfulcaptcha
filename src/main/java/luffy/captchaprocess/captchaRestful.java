package luffy.captchaprocess;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;

import javax.imageio.ImageIO;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MultivaluedMap;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import cardprocess.hibernate.CardProcess;
import cardprocess.hibernate.Cardservices;
import luffy.captcha.CaptchaSolver;
import sun.misc.BASE64Decoder;

@Path("/")
public class captchaRestful {

	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	private static Cardservices cs = new Cardservices();
	private final static String RESPONSE_QUEUE_NAME = "robux_result";
	private static ConnectionFactory factory;
	String userName = "robuxreceiver";
	String password = "robuxreceiver";
	String virtualHost = "/";
	int portNumber = 5672;
	String hostName = "27.72.30.109";

	@Path("requestcaptcha")
	@GET
	@Produces("application/plaintext")
	public String getCaptcha() {
		try {
			Thread.sleep(2000l);
			System.out.println("got it");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "testGetok";
	}

	@Path("getcardinfo")
	@POST
	@Produces("application/plaintext")
	public String getCardInfo(final MultivaluedMap<String, String> formParams) {
		// {cardvalue=[500000 ?], carddate=[31/12/2024], cardserial=[10001012541160],

		// {cardvalue=[], carddate=[], cardserial=[10001782909544], cardstate=[Số lần
		// tra cứu quá giới hạn]}

		System.out.println(formParams);
		cs.parseCardinfo(formParams);

		return "CheckCardsuccess";
	}

	/**
	 * @param factory
	 * @param result_msg
	 * @throws IOException
	 */
	private static void responseToRobux(ConnectionFactory factory, CardProcess result_msg) throws IOException {
		Connection connection_result;
		try {
			connection_result = factory.newConnection();
			Gson gson = new Gson();
			String jsonInString = gson.toJson(result_msg);

			Channel channel_res = connection_result.createChannel();
			channel_res.queueDeclare(RESPONSE_QUEUE_NAME, false, false, false, null);
			channel_res.basicPublish("", RESPONSE_QUEUE_NAME, null, jsonInString.getBytes());
			System.out.println("result sent to " + RESPONSE_QUEUE_NAME + ": " + jsonInString);
			channel_res.close();
			connection_result.close();

		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("failed to response to robux");
		}
	}

	@Path("naptheresult")
	@POST
	@Produces("application/plaintext")
	public String naptheresult(final MultivaluedMap<String, String> formParams) throws TimeoutException {
		// {'napthestatus' => "#{napthestatus}",'cardpin' => "#{cardpin}"})
		// {cardpin=[], cardpinpref=[pin610563392794450], napthestatus=[success]}
		if (null == factory) {
			factory = new ConnectionFactory();
			factory.setUsername(userName);
			factory.setPassword(password);
			factory.setVirtualHost(virtualHost);
			factory.setHost(hostName);
			factory.setPort(portNumber);
		}
		String result_msg = "";
		try {
			System.out.println(formParams);
			CardProcess naptheResult = cs.parseNaptheResult(formParams);

			System.out.println("id CardProcess" + naptheResult.getId());
			System.out.println("process result" + naptheResult.getCardprocessresult());
			System.out.println("process success " + naptheResult.getCardprocesssuccess());

			if (naptheResult.getCardcheckresult().equalsIgnoreCase("Mã bảo mật không chính xác")) {
				// Check Card value and availability
				CheckcardServiceRequest checkcardServices = new CheckcardServiceRequest();
				checkcardServices.checkCard(naptheResult);
				return "";
			}

			if (naptheResult.getCardprocesssuccess() == 1) {
				cs.naptheCountMvtaccount(naptheResult.getChargedby());

				naptheResult.setCardprocessresult("Success");
				responseToRobux(factory, naptheResult);

			} else {
				responseToRobux(factory, naptheResult);
			}

		} catch (IOException e) {
			// response failed
			System.out.println("failed to response to robux");
			e.printStackTrace();
		}

		return "naptheSuccess";
	}

	@SuppressWarnings("restriction")
	@Path("requestcaptchacheckcard")
	@POST
	@Produces("application/plaintext")
	public String postCaptchacheckcard(String body) throws IOException, TimeoutException {
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] imageByte = decoder.decodeBuffer(body);
		ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
		BufferedImage image = ImageIO.read(bis);
		bis.close();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_");
		LocalDateTime now = LocalDateTime.now();

		File outputfileorigin = new File("d:\\checkcardcaptchaimg\\" + dtf.format(now) + "origin.png");
		ImageIO.write(image, "png", outputfileorigin);

		// Samsung HD screen 540x960
		// BufferedImage cropImage = image.getSubimage(310, 310, 150, 40);

		// Samsung full HD s5 active 1080 x 1920
		BufferedImage cropImage = image.getSubimage(620, 610, 300, 80);

		// xiaomi HD+ screen 1080x2160
//		BufferedImage cropImage = image.getSubimage(660, 564, 270, 75);

		// write the image to a file
		File outputfile = new File("d:\\checkcardcaptchaimg\\" + dtf.format(now) + ".png");
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(cropImage, "png", outputfile);
		ImageIO.write(cropImage, "png", os);

		InputStream cropImageStream = new ByteArrayInputStream(os.toByteArray());

		String result = "";
		try {
			result = CaptchaSolver.readCaptcha(cropImageStream);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("restriction")
	@Path("requestcaptchanapthe")
	@POST
	@Produces("application/plaintext")
	public String postCaptchaNapthe(String body) throws IOException, TimeoutException {
		// System.out.println("processing request from" + body);
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] imageByte = decoder.decodeBuffer(body);
		ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
		BufferedImage image = ImageIO.read(bis);
		bis.close();

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_");
		LocalDateTime now = LocalDateTime.now();

		File outputfileorigin = new File("d:\\napthecaptchaimg\\" + dtf.format(now) + "origin.png");

		ImageIO.write(image, "png", outputfileorigin);

		// Samsung HD screen 540x960
		// BufferedImage cropImage = image.getSubimage(310, 310, 150, 40);

		// Samsung full HD s5 active 1080 x 1920
		BufferedImage cropImage = image.getSubimage(640, 1480, 360, 95);

		// xiaomi HD+ screen 1080x2160
//		BufferedImage cropImage = image.getSubimage(660, 564, 270, 75);

		// write the image to a file
		File outputfile = new File("d:\\napthecaptchaimg\\" + dtf.format(now) + ".png");
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(cropImage, "png", outputfile);
		ImageIO.write(cropImage, "png", os);

		InputStream cropImageStream = new ByteArrayInputStream(os.toByteArray());

		String result = "";
		try {
			result = CaptchaSolver.readCaptcha(cropImageStream);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("restriction")
	@Path("solvecaptcha")
	@POST
	@Produces("application/plaintext")
	public String postCaptchaImage(String body) throws IOException, TimeoutException {
		// System.out.println("processing request from" + body);
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] imageByte = decoder.decodeBuffer(body);
		ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
		InputStream cropImageStream = bis;

		String result = "";
		try {
			result = CaptchaSolver.readCaptcha(cropImageStream);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}

}