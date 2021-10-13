package app.util.date;

import app.ApplicationException;
import com.google.common.base.Strings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneId;

/**
 * @author chi
 */
public class DateTimes {
    public static OffsetDateTime parseJSONFormat(String input) {
        if (Strings.isNullOrEmpty(input)) {
            return null;
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
        if (input.endsWith("Z")) {
            input = input.substring(0, input.length() - 1) + "GMT-00:00";
        } else {
            int inset = 6;
            String s0 = input.substring(0, input.length() - inset);
            String s1 = input.substring(input.length() - inset);
            input = s0 + "GMT" + s1;
        }
        try {
            return OffsetDateTime.ofInstant(df.parse(input).toInstant(), ZoneId.of("UTC"));
        } catch (ParseException e) {
            throw new ApplicationException(e);
        }
    }
}
