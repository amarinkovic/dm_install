package pro.documentum.util.convert.datastore;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import com.documentum.fc.common.DfPreferences;
import com.documentum.fc.common.DfTime;
import com.documentum.fc.common.DfValue;
import com.documentum.fc.common.IDfTime;
import com.documentum.fc.common.IDfValue;
import com.documentum.fc.impl.util.DateFormatUtil;

import pro.documentum.util.convert.IConverter;
import pro.documentum.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class IDfTimeDataStoreConverter<F> extends
        AbstractDataStoreConverter<F, IDfTime> {

    private static final Locale LOCALE;

    private static final TimeZone TIME_ZONE;
    private static final Map<Class<?>, IConverter<?, IDfTime>> CONVERTERS;
    private static List<SimpleDateFormat> simpleDateFormats;

    static {
        DfPreferences preferences = DfPreferences.getInstance();
        LOCALE = new Locale(preferences.getLocale());
        TIME_ZONE = TimeZone.getTimeZone(preferences.getTimeZone());
    }

    static {
        CONVERTERS = new HashMap<>();
        CONVERTERS.put(String.class, new StringToIDfTime());
        CONVERTERS.put(IDfValue.class, new IDfValueToIDfTime());
        CONVERTERS.put(DfValue.class, CONVERTERS.get(IDfValue.class));
        CONVERTERS.put(IDfTime.class, new IDfTimeToIDfTime());
        CONVERTERS.put(DfTime.class, CONVERTERS.get(IDfTime.class));
        CONVERTERS.put(Date.class, new DateToIDfTime());
        CONVERTERS.put(Long.class, new LongToIDfTime());
        CONVERTERS.put(long.class, CONVERTERS.get(Long.class));
        CONVERTERS.put(Calendar.class, new CalendarToIDfTime());
        CONVERTERS.put(GregorianCalendar.class, CONVERTERS.get(Calendar.class));
    }

    public IDfTimeDataStoreConverter() {
        super();
    }

    private static SimpleDateFormat newSimpleDateFormat(final String pattern,
            final Locale locale) {
        return newSimpleDateFormat(pattern, locale, null);
    }

    private static SimpleDateFormat newSimpleDateFormat(final String pattern) {
        return newSimpleDateFormat(pattern, null, null);
    }

    private static SimpleDateFormat newSimpleDateFormat(final String pattern,
            final Locale locale, final TimeZone timeZone) {
        Locale fmtLocale = locale;
        if (fmtLocale == null) {
            fmtLocale = LOCALE;
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern, fmtLocale);
        TimeZone fmtTimeZone = timeZone;
        if (fmtTimeZone == null) {
            fmtTimeZone = TIME_ZONE;
        }
        format.setTimeZone(fmtTimeZone);
        format.setLenient(false);
        return format;
    }

    private static void truncateHours(final Calendar calendar) {
        truncate(calendar, Calendar.HOUR_OF_DAY, Calendar.MINUTE,
                Calendar.SECOND, Calendar.MILLISECOND);
    }

    private static void truncate(final Calendar calendar, final int... fields) {
        for (int field : fields) {
            calendar.set(field, 0);
        }
    }

    private static Date guessDateFormat(final String value)
        throws ParseException {
        for (SimpleDateFormat format : getCandidateFormats()) {
            if (!isMatches(value, format)) {
                continue;
            }
            Date date = tryFormat(value, format);
            if (date == null) {
                continue;
            }
            return date;
        }
        throw new ParseException(value, 0);
    }

    private static boolean isMatches(final String value,
            final SimpleDateFormat format) {
        String pattern = format.toPattern();
        if (!isMatches(value, pattern, new String[] {" ", "/", ":", "-", "." })) {
            return false;
        }
        return !(value.matches(ISODateFormat.ISO8601_REGEXP) && !ISODateFormat.ISO8601_FORMAT
                .equals(pattern));
    }

    private static boolean isMatches(final String value, final String pattern,
            final String[] separators) {
        for (String sep : separators) {
            if (StringUtils.countMatches(value, sep) == StringUtils
                    .countMatches(pattern, sep)) {
                return true;
            }
        }
        return false;
    }

    private static synchronized Date tryFormat(final String value,
            final SimpleDateFormat format) {
        try {
            return format.parse(value);
        } catch (ParseException ex) {
            Logger.debug(ex);
        }
        return null;
    }

    private static synchronized List<SimpleDateFormat> getCandidateFormats() {
        if (simpleDateFormats != null) {
            return simpleDateFormats;
        }
        simpleDateFormats = new ArrayList<>();

        final String pattern = DfPreferences.getInstance().getDateFormat();
        final String patternWithoutTime = DateFormatUtil.removeTime(pattern);

        // ISO
        simpleDateFormats.add(ISODateFormat.getInstance(LOCALE));
        simpleDateFormats.add(newSimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        simpleDateFormats.add(newSimpleDateFormat("yyyy-MM-dd"));

        simpleDateFormats.add(newSimpleDateFormat("dd-MMM-yyyy"));

        // Locale specific
        simpleDateFormats.add(newSimpleDateFormat(pattern));
        simpleDateFormats.add(newSimpleDateFormat(patternWithoutTime));
        String patternWithTime = patternWithoutTime.replace(" ", "")
                + " HH:mm:ss";
        if (!patternWithTime.equals(pattern)) {
            simpleDateFormats.add(newSimpleDateFormat(patternWithTime));
        }

        // todo, application specific format
        simpleDateFormats.add(newSimpleDateFormat("dd.MM.yyyy HH:mm:ss"));
        simpleDateFormats.add(newSimpleDateFormat("dd.MM.yyyy"));
        simpleDateFormats.add(newSimpleDateFormat("yyyy/MM/dd HH:mm:ss"));
        simpleDateFormats.add(newSimpleDateFormat("yyyy/MM/dd"));
        simpleDateFormats.add(newSimpleDateFormat("MM/dd/yyyy HH:mm:ss"));
        simpleDateFormats.add(newSimpleDateFormat("MM/dd/yyyy"));
        simpleDateFormats.add(newSimpleDateFormat("MMM dd,yyyy"));
        simpleDateFormats.add(newSimpleDateFormat("EEE MMM dd HH:mm:ss yyyy"));
        simpleDateFormats.add(newSimpleDateFormat("EEE MMM dd HH:mm:ss yyyy",
                Locale.US));
        simpleDateFormats.add(newSimpleDateFormat("MMM dd HH:mm:ss yyyy"));
        simpleDateFormats.add(newSimpleDateFormat("MMM dd HH:mm:ss yyyy",
                Locale.US));
        return simpleDateFormats;
    }

    @Override
    public int getDataStoreType() {
        return IDfValue.DF_TIME;
    }

    @Override
    protected Map<Class<?>, IConverter<?, IDfTime>> getConverters() {
        return CONVERTERS;
    }

    static class IDfValueToIDfTime implements IConverter<IDfValue, IDfTime> {

        IDfValueToIDfTime() {
            super();
        }

        @Override
        public IDfTime convert(final IDfValue value) {
            return value.asTime();
        }

    }

    static class IDfTimeToIDfTime implements IConverter<IDfTime, IDfTime> {

        IDfTimeToIDfTime() {
            super();
        }

        @Override
        public IDfTime convert(final IDfTime value) {
            return value;
        }

    }

    static class DateToIDfTime implements IConverter<Date, IDfTime> {

        DateToIDfTime() {
            super();
        }

        @Override
        public IDfTime convert(final Date value) {
            return new DfTime(value);
        }

    }

    static class CalendarToIDfTime implements IConverter<Calendar, IDfTime> {

        CalendarToIDfTime() {
            super();
        }

        @Override
        public IDfTime convert(final Calendar value) throws ParseException {
            return new DfTime(value.getTime());
        }

    }

    static class LongToIDfTime implements IConverter<Long, IDfTime> {

        LongToIDfTime() {
            super();
        }

        @Override
        public IDfTime convert(final Long value) throws ParseException {
            Date date = new Date(value);
            return new DfTime(date);
        }

    }

    static class StringToIDfTime implements IConverter<String, IDfTime> {

        StringToIDfTime() {
            super();
        }

        @Override
        public IDfTime convert(final String value) throws ParseException {
            return new DfTime(convertToDate(value));
        }

        private Date convertToDate(final String value) throws ParseException {
            Calendar result = Calendar.getInstance(LOCALE);
            result.setTimeZone(TIME_ZONE);
            switch (value.toLowerCase()) {
            case "now":
                return new Date();
            case "today":
                truncateHours(result);
                return result.getTime();
            case "tomorrow":
                result.add(Calendar.DAY_OF_YEAR, 1);
                truncateHours(result);
                return result.getTime();
            case "yesterday":
                result.add(Calendar.DAY_OF_YEAR, -1);
                truncateHours(result);
                return result.getTime();
            default:
                result.setTime(guessDateFormat(value));
                return result.getTime();
            }
        }

    }

    static class ISODateFormat extends SimpleDateFormat {

        public static final String ISO8601_REGEXP = "^([0-9]){4}(-([0-9]){2}){2}T([0-9]{2}:){2}([0-9]){2}";

        public static final String ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

        private static final long serialVersionUID = 1L;

        ISODateFormat() {
            super(ISO8601_FORMAT);
        }

        ISODateFormat(final Locale locale) {
            super(ISO8601_FORMAT, locale);
        }

        public static SimpleDateFormat getInstance(final Locale locale) {
            return new ISODateFormat(locale);
        }

        @Override
        public Date parse(final String source, final ParsePosition pos) {
            String src = source;
            if (src.endsWith("Z")) {
                src = src.substring(0, src.length() - 1) + "+00:00";
            }
            src = src.replaceFirst(":(?=[0-9]{2}$)", "");
            return super.parse(src, pos);
        }
    }

}
