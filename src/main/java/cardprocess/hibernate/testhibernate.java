package cardprocess.hibernate;

import java.sql.Time;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class testhibernate {

	static CardProcess cardProcess;


	public static void main(String[] args) {
		
		Cardservices cs = new Cardservices();
		
		cardProcess = cs.getCardInfoFromSerial("10001783210327");
		System.out.println(cardProcess);

		cardProcess.setCardcheckresult("khong ");
		cs.updatecardinfo(cardProcess);
		
		
		System.out.println(cardProcess);
		
		
		
		
		
	

	}
}
