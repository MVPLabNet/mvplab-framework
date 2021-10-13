package app.template;

import app.util.JSON;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author chi
 */
public class TemplateFunctions {
    public boolean isNullOrEmpty(String content) {
        return Strings.isNullOrEmpty(content);
    }

    public String ellipsis(String content, int limit) {
        if (content == null || content.length() < limit) {
            return content;
        } else {
            return content.substring(0, Math.max(0, limit)) + "...";
        }
    }

    public int toInt(String value) {
        if (Strings.isNullOrEmpty(value)) {
            return 0;
        }
        return Integer.parseInt(value);
    }

    public boolean toBool(String value) {
        return Boolean.valueOf(value);
    }

    public double toDouble(String value) {
        return Double.valueOf(value);
    }

    public float toFloat(String value) {
        return Float.valueOf(value);
    }

    public long toLong(String value) {
        return Long.parseLong(value);
    }

    public String trim(String value) {
        if (value == null) {
            return null;
        }
        return value.trim();
    }

    public String substring(String value, int start) {
        if (value == null) {
            return null;
        }
        return value.substring(start);
    }

    public String substring(String value, int start, int end) {
        if (value == null) {
            return null;
        }
        return value.substring(start, end);
    }

    public String format(OffsetDateTime value, String pattern) {
        return value.format(DateTimeFormatter.ofPattern(pattern));
    }

    public String format(LocalDateTime value, String pattern) {
        return value.format(DateTimeFormatter.ofPattern(pattern));
    }

    public String format(Date value, String pattern) {
        return new SimpleDateFormat(pattern).format(value);
    }

    public String format(LocalDate value, String pattern) {
        return value.format(DateTimeFormatter.ofPattern(pattern));
    }

    public String format(LocalTime value, String pattern) {
        return value.format(DateTimeFormatter.ofPattern(pattern));
    }

    public String format(Number value, String pattern) {
        return new DecimalFormat(pattern).format(value);
    }

    public String format(Number value, String pattern, String language) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.forLanguageTag(language));
        return new DecimalFormat(pattern, symbols).format(value);
    }

    public String join(List<?> list, String separator) {
        if (list != null) {
            return Joiner.on(separator).skipNulls().join(list);
        }
        return "";
    }

    public <T> String join(T[] list, String separator) {
        if (list != null) {
            return Joiner.on(separator).skipNulls().join(list);
        }
        return "";
    }

    public String join(List<?> list) {
        return join(list, ",");
    }

    public <T> String join(T... array) {
        return join(array, ",");
    }

    public String json(Object instance) {
        if (instance != null) {
            return JSON.toJSON(instance);
        }
        return "";
    }

    public String lowercase(String text) {
        if (Strings.isNullOrEmpty(text)) {
            return text;
        }
        return text.toLowerCase(Locale.US);
    }

    public String uppercase(String text) {
        if (Strings.isNullOrEmpty(text)) {
            return text;
        }
        return text.toUpperCase(Locale.US);
    }
}
