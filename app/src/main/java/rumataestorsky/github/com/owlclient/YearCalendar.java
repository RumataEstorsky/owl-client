package rumataestorsky.github.com.owlclient;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import rumataestorsky.github.com.owlclient.api.DaysProductivityView;

/**
 * Created by rumata on 02.09.16.
 */
public class YearCalendar {
    public static final int WEEKS = 53;
    public static final int DAYS  = 7;


    private LocalDate endDate;
    private LocalDate startDate;
    private int daysCount;
    /** key = number of day - value calendar entry */
    private Map<Integer, DaysProductivityView> days = new HashMap<>();
    private double sumScore = 0d;
    private double min = 0d;
    private int sumExecCount = 0;
    private NavigableMap<Double, Integer> weights = new TreeMap<>();

    public YearCalendar(List<DaysProductivityView> days) {
        //DateTimeZone dtz = DateTimeZone.forTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        DateTimeZone dtz = DateTimeZone.forOffsetHours(3);//FIXME!!!
        endDate = DateTime.now(dtz).toLocalDate();
        startDate = endDate.minusYears(1).withDayOfWeek(DateTimeConstants.MONDAY);
        daysCount = Days.daysBetween(startDate, endDate).getDays();
        initDaysMap(days);
        calculateStatistics();
        initColorWeights(getAverageScore()); //TODO by scope and by count
//        yearCalendarView.refresh(taskId == TOTAL_CALENDAR_ID
//                ? calendar.getAverageScore()
//                : calendar.getAverageExecCount());
    }

    private void initDaysMap(List<DaysProductivityView> daysList) {
        days.clear();
        for(DaysProductivityView day: daysList) {
            Integer daysSince = daysSinceStart(LocalDate.parse(day.day));
            days.put(daysSince, day);
        }
    }

    private void initColorWeights(double average) {
        weights.clear();
        weights.put(0d, 0);
        weights.put(average * 0.66, 1);
        weights.put(average * 1.32, 2);
        weights.put(average * 2, 3);
    }

    private void calculateStatistics() {
        //minScore = days.isEmpty() ? 0d : days.get(0).totalScore;
        for(DaysProductivityView day : days.values()) {
            sumScore += day.totalScore;
            sumExecCount += day.execCount;
            //if()
        }
    }

    public int getStepOfProductivity(double score) {
        Map.Entry<Double, Integer> entry = weights.ceilingEntry(score);
        return (entry == null) ? 4 : entry.getValue().intValue();
    }

    public int daysSinceStart(LocalDate day) {
        return Days.daysBetween(startDate, day).getDays();
    }

    public int getWeekNumber(LocalDate day) {
        return daysSinceStart(day) / DAYS;
    }

    private double getAverage(boolean scoreOrExecs) {
        double sum = 0d;
        return days.isEmpty() ? 0d : sum / days.size();
    }


    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public int getDaysCount() {
        return daysCount;
    }

    public double getAverageScore() {
        return sumScore / days.size();
    }

    public double getAverageExecCount() {
        return sumExecCount / days.size();
    }

}
