package rumataestorsky.github.com.owlclient;

import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import rumataestorsky.github.com.owlclient.api.DaysProductivityView;

import static org.junit.Assert.assertEquals;

/**
 * Created by rumata on 02.09.16.
 */
public class YearCalendarTest {
    @Test
    public void creation() throws Exception {
        YearCalendar calendar = new YearCalendar(getTestDays());
        assertEquals(calendar.getDaysCount(), 2);
    }

    private List<DaysProductivityView> getTestDays() {
        DaysProductivityView day1 = new DaysProductivityView("2016-06-03", 22, 44, 1);
        DaysProductivityView day2 = new DaysProductivityView("2016-06-07", 2004.5, 425, 6);
        return Arrays.asList(day1, day2);
    }


}
