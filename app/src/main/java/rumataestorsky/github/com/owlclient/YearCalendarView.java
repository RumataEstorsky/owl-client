package rumataestorsky.github.com.owlclient;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Created by rumata on 12.06.16.
 */
public class YearCalendarView extends View implements View.OnTouchListener {
    private static final String TAG = "YearCalendarView";
    private static final String DF = "yyyy-MM-dd";
    private static final int WEEKS = 53;
    private static final int DAYS  = 7;


    private int cellSize;
    private int monthsLabelHeight;
    private int daysLabelWidth;
    private Paint borderPaint = new Paint();
    private Paint textPaint = new Paint();


    private LocalDate endDate;
    private LocalDate startDate;
    private int daysCount;
    private NavigableMap<Double, Integer> colorWeights = new TreeMap<>();


    private int colors[] = {Color.LTGRAY, 0xffd6e685, 0xff8cc665, 0xff44a340, 0xff1e6823};
    private List<Paint> paints = new ArrayList<>(colors.length);

    /** Marks key: number of day since first day of calendar value (color + score)*/
    private Map<Integer, Pair<Integer, Integer>> marks = new HashMap<>();

    private boolean showScores = false;
    private boolean showMounths = false;
    private boolean showWeekNumbers = false;
    private LocalDate localDate;

    public YearCalendarView(Context context) {
        super(context);
        initOnCreation();
    }

    public YearCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initOnCreation();
    }

    public YearCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initOnCreation();
    }

    private void initOnCreation() {
        graphInit();
        initDates();
    }

    private void initColorWeights(double average) {
        colorWeights.clear();
        colorWeights.put(0d, 0);
        colorWeights.put(average * 0.66, 1);
        colorWeights.put(average * 1.32, 2);
        colorWeights.put(average * 2, 3);
    }

    private void initDates() {
        //DateTimeZone dtz = DateTimeZone.forTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        DateTimeZone dtz = DateTimeZone.forOffsetHours(3);//FIXME!!!
        endDate = DateTime.now(dtz).toLocalDate();
        startDate = endDate.minusYears(1).withDayOfWeek(DateTimeConstants.MONDAY);
        daysCount = Days.daysBetween(startDate, endDate).getDays();

    }

    public void refresh(double average) {

        initColorWeights(average);
        marks.clear();

    }

    private void graphInit() {
        setOnTouchListener(this);

        borderPaint.setColor(Color.WHITE);
        borderPaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);


        for(int color: colors) {
            Paint p = new Paint();
            p.setColor(color);
            p.setStyle(Paint.Style.FILL);
            paints.add(p);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        cellSize = getWidth() / WEEKS - 1;
        monthsLabelHeight = cellSize;
        daysLabelWidth = cellSize;
        canvas.drawColor(Color.WHITE);

        drawCalendar(canvas);
    }



    public void addMark(int year, int month, int day, int color, int score) {
        LocalDate date = new LocalDate(year, month, day);
        addMark(date, color, score);
    }

    public void addMark(LocalDate day, int score) {
        int color = pickColor(score);
        addMark(day, color, score);
    }

    private int pickColor(double score) {
        Map.Entry<Double, Integer> entry = colorWeights.ceilingEntry(score);
        return (entry == null) ? 4 : entry.getValue().intValue();
    }

    public void addMark(LocalDate day, int color, int score) {
        if(day.isBefore(startDate) || day.isAfter(endDate)) {
            throw new IllegalArgumentException("Date " + day.toString(DF) + " is impossible in period of this calendar!" );
        }
        int dc = daysSinceStart(day);
        marks.put(dc, new Pair<>(color, score));
    }

    public void drawDaySquare(Canvas canvas, Paint paint, LocalDate day, int score) {
        int w = getWeekNumber(day);
        int d = day.getDayOfWeek() - 1;

        Rect r = new Rect(
                daysLabelWidth + w * cellSize,
                d * cellSize + monthsLabelHeight,
                daysLabelWidth  + (w + 1) * cellSize,
                (d + 1) * cellSize + monthsLabelHeight
        );
        canvas.drawRect(r, paint);
        String text = score == 0 ? "" : String.valueOf(score);
        textPaint.setTextSize((float) (cellSize / 2));
        canvas.drawText(text, r.left, r.centerY(), textPaint);
        canvas.drawRect(r, borderPaint);
    }

    private void drawLabelsOfMonths(Canvas canvas) {
        final int MONTHS = 12;
        int cell = getWidth() / MONTHS;
        for(int i = 0; i < MONTHS; ++i) {
            String name = startDate.plusMonths(i).monthOfYear().getAsText(new Locale("ru"));
            canvas.drawText(name, cell * i, monthsLabelHeight / 2, textPaint);
        }
    }

    private void drawLabelsOfWeekdays(Canvas canvas) {
        for(int i = 0; i < DAYS; ++i) {
            String name = startDate.plusDays(i).toString("E");
            int y = monthsLabelHeight + (i * cellSize) + (cellSize / 2);
            int rightColumnX = 3 + (cellSize * (WEEKS + 1)) + 3;

            canvas.drawText(name, 3, y, textPaint);
            canvas.drawText(name, rightColumnX, y, textPaint);
        }
    }

    private void drawCalendar(Canvas canvas) {

        drawLabelsOfMonths(canvas);
        drawLabelsOfWeekdays(canvas);

        for(int i = 0; i <= daysCount; i++) {
            LocalDate day = startDate.plusDays(i);

            int paintIndex = marks.containsKey(i) ? marks.get(i).first : 0;
            Paint paint = paints.get(paintIndex);

            int score = marks.containsKey(i) ? marks.get(i).second : 0;

            drawDaySquare(canvas, paint, day, score);
        }
    }

    public int daysSinceStart(LocalDate day) {
        return Days.daysBetween(startDate, day).getDays();
    }

    public int getWeekNumber(LocalDate day) {
        return daysSinceStart(day) / DAYS;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        String details = getDayByCoords(event.getX(), event.getY());
        Toast.makeText(getContext(), details, Toast.LENGTH_SHORT).show();

        return false;
    }

    private String getDayByCoords(float x, float y) {
        int week = (int) Math.floor(x / cellSize);
        week = week >= WEEKS ? (WEEKS - 1) : week;
        int day = (int) Math.floor(y / cellSize);
        day = day >= DAYS ? (DAYS - 1) : day;
        int dayNumber = (week) * DAYS + day - 1;
        LocalDate date = startDate.plusDays(dayNumber);

        //Log.i(TAG, "week=" + week + ";day=" + day + ";dayNumber=" + dayNumber + ";date=" + date.toString(DF));

        double score = marks.containsKey(dayNumber) ? marks.get(dayNumber).second : 0;
        return date.toString(DF) + ": " + Math.ceil(score);
    }
}
