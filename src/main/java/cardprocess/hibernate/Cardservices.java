package cardprocess.hibernate;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class Cardservices {

	public static SessionFactory sessionfactory;
	public static Session session;

	public Cardservices() {
		final Configuration configuration = new Configuration().configure();
		final StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
				.applySettings(configuration.getProperties());
		sessionfactory = configuration.buildSessionFactory(builder.build());
		session = sessionfactory.openSession();
	}

	public CardProcess updatecardinfo(CardProcess cardinfo) {
		// session = sessionfactory.openSession();
		session.beginTransaction();

		CardProcess cpupdate = getCardInfoFromSerial(cardinfo.getSerial());
		cpupdate.setCardavailable(cardinfo.getCardavailable());
		cpupdate.setCardvalue(cardinfo.getCardvalue());
		cpupdate.setCardexpiredate(cardinfo.getCardexpiredate());
		cpupdate.setCardcheckresult(cardinfo.getCardcheckresult());

		session.saveOrUpdate(cpupdate);
		session.getTransaction().commit();
		return cpupdate;

	}

	public CardProcess parseCardinfo(MultivaluedMap<String, String> formParams) {
		// {cardvalue=[50000 đ], carddate=[31/12/2023], cardserial=[10001783210327],
		// cardstate=[Thẻ chưa sử dụng]}
		CardProcess cpinfo = new CardProcess();

		String cardstate = formParams.getFirst("cardstate");

		if (cardstate.equalsIgnoreCase("Thẻ chưa sử dụng")) {
			cpinfo.setCardavailable(1);
			String datestr = formParams.getFirst("carddate");
			try {
				java.util.Date date1;
				date1 = new SimpleDateFormat("dd/MM/yyyy").parse(datestr);
				cpinfo.setCardexpiredate(date1);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			cpinfo.setCardavailable(0);

		}

		cpinfo.setCardcheckresult(cardstate);

		if (cardstate.isEmpty()) {
			cpinfo.setCardcheckresult("Lỗi hệ thống captcha");
		}

		try {
			String cardvalueStr = formParams.getFirst("cardvalue").split(" ")[0];
			BigInteger cardvalue = BigInteger.valueOf(Long.valueOf(cardvalueStr));
			cpinfo.setCardvalue(cardvalue);

		} catch (Exception e) {
			// cpinfo.setCardvalue(BigInteger.valueOf(0l));
		}

		cpinfo.setSerial(formParams.getFirst("cardserial"));

		return updatecardinfo(cpinfo);

	}

	public CardProcess parseNaptheResult(MultivaluedMap<String, String> formParams) {
		// {'napthestatus' => "#{napthestatus}",'cardpin' => "#{cardpin}"})
		// napthestatus=[Mã bảo mật không chính xác]}
		// napthestatus =[Nạp sai quá số lần qui định trong ngày.]
		// {cardpin=[519034165271285], cardpinpref=[pin519034165271285],
		// napthestatus=[Thuê bao không đủ điều kiện nạp thẻ hộ.]}
		//{cardpin=[], cardpinpref=[pin610563392794450], napthestatus=[success]}

		CardProcess cpinfo = new CardProcess();

		String pin = formParams.getFirst("cardpin");

		if (pin.isEmpty()) {
			String prefpin = formParams.getFirst("cardpinpref");
			pin = prefpin.replace("pin", "");
		}
		System.out.println("pin = " + pin);

		String processresult = formParams.getFirst("napthestatus");
		cpinfo.setCardprocessresult(processresult);
		System.out.println("process result : " + processresult);
		if ((processresult.isEmpty() || "success".equalsIgnoreCase(processresult)) && !pin.isEmpty()) {
			System.out.println("charge success - logging to db");
			cpinfo.setCardprocesssuccess(1);
			cpinfo.setChargedtime(LocalDateTime.now().toString());
		} else {
			cpinfo.setCardprocesssuccess(0);
		}
		
		System.out.println("processresult = " + cpinfo.getCardprocessresult());
		
		cpinfo.setPin(pin);
		return updateNapTheInfo(cpinfo);

	}

	private CardProcess updateNapTheInfo(CardProcess cpinfo) {
		session.beginTransaction();

		CardProcess cpupdate = getCardInfoFromPin(cpinfo.getPin());
		cpupdate.setCardprocesssuccess(cpinfo.getCardprocesssuccess());
		cpupdate.setCardprocessresult(cpinfo.getCardprocessresult());
		cpupdate.setChargedtime(cpinfo.getChargedtime());

		session.saveOrUpdate(cpupdate);
		session.getTransaction().commit();
		session.flush();
		return cpupdate;
	}

	public CardProcess getCardInfoFromPin(String pin) {
		// TODO Auto-generated method stub
		Criteria cr = session.createCriteria(CardProcess.class);
		cr.add(Restrictions.eq("pin", pin));
		cr.addOrder(Order.desc("receivetime"));

		List<CardProcess> result = cr.list();

		CardProcess cpupdate = result.get(0);
		return cpupdate;
	}

	public CardProcess getCardInfoFromSerial(String serial) {
		// TODO Auto-generated method stub
		Criteria cr = session.createCriteria(CardProcess.class);
		cr.add(Restrictions.eq("serial", serial));
		cr.addOrder(Order.desc("receivetime"));

		List<CardProcess> result = cr.list();

		CardProcess cpupdate = result.get(0);
		return cpupdate;
	}

	public void naptheCountMvtaccount(String chargedby) {
		// TODO Auto-generated method stub
		
		session.beginTransaction();
		MyViettelAccount mvtaccNapthe = getmvtaccNapthebyPhoneNumber(chargedby);
		mvtaccNapthe.setCharged(mvtaccNapthe.getCharged()+1);
		session.saveOrUpdate(mvtaccNapthe);
		session.getTransaction().commit();		
		
	}

	private MyViettelAccount getmvtaccNapthebyPhoneNumber(String chargedby) {
		Criteria cr = session.createCriteria(MyViettelAccount.class);
		cr.add(Restrictions.eq("username", chargedby));

		List<MyViettelAccount> result = cr.list();

		MyViettelAccount myViettelAccount = result.get(0);
		return myViettelAccount;
	}

}
