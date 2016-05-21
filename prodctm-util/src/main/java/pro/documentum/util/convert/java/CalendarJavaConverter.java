package pro.documentum.util.convert.java;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import com.documentum.fc.common.IDfTime;
import com.documentum.fc.common.IDfValue;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class CalendarJavaConverter extends AbstractJavaConverter<Calendar> {

    public CalendarJavaConverter() {
        super();
    }

    @Override
    public List<Class<Calendar>> getJavaType() {
        return Collections.singletonList(Calendar.class);
    }

    @Override
    protected Calendar doConvert(final IDfValue value) throws ParseException {
        IDfTime time = value.asTime();
        if (time == null || time.isNullDate()) {
            return defaultValue();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time.getDate());
        return calendar;
    }

}
