package br.com.sankhya.jaco.actions.geracompromissos.utils;

import java.sql.Timestamp;
import java.util.GregorianCalendar;

public class JacoUtils {
	public static Timestamp dataAddMonth(Timestamp data, int amount) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(data.getTime());
		cal.add(2, amount);
		return new Timestamp(cal.getTimeInMillis());
	}
}
