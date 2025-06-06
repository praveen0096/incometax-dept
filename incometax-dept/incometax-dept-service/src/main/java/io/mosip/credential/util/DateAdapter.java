/**
 * 
 */
package io.mosip.credential.util;

/**
 * @author Ramadurai Pandian
 * Date Adaptor class to print date to specific format
 */

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateAdapter extends XmlAdapter<String, LocalDateTime> {

	/**
	 * @param v formatted date
	 * @return Date
	 */
	@Override
	public LocalDateTime unmarshal(String v) throws Exception {
		ZonedDateTime parse = ZonedDateTime.parse(v, DateTimeFormatter.ISO_DATE_TIME)
				.withZoneSameInstant(ZoneId.of("UTC"));
		LocalDateTime locale = parse.toLocalDateTime();
		return locale;
	}

	/**
	 * @param v date
	 * @return String formatted date
	 */
	@Override
	public String marshal(LocalDateTime v) throws Exception {
		return v.toInstant(ZoneOffset.UTC).toString();
	}

}