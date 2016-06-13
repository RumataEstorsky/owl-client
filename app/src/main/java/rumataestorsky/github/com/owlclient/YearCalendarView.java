package rumataestorsky.github.com.owlclient;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Pair;
import android.view.View;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Created by rumata on 12.06.16.
 */
public class YearCalendarView extends View {
    private static final String TAG = "YearCalendarView";
    private static final String DF = "yyyy-MM-dd";
    private static final int WEEKS = 53;
    private static final int DAYS  = 7;


    private int cellSize;
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

    public YearCalendarView(Context context) {
        super(context);

        init();
        graphInit();
    }

    private void init() {
        endDate = DateTime.now().toLocalDate();
        startDate = endDate.minusYears(1).withDayOfWeek(DateTimeConstants.MONDAY);
        daysCount = Days.daysBetween(startDate, endDate).getDays();

    }

    public void initColorWeights(double average) {
        colorWeights.clear();
        colorWeights.put(0d, 0);
        colorWeights.put(average * 0.66, 1);
        colorWeights.put(average * 1.32, 2);
        colorWeights.put(average * 2, 3);
    }

    private void graphInit() {
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

        Rect r = new Rect(w * cellSize, d * cellSize, (w + 1) * cellSize, (d + 1) * cellSize);
        canvas.drawRect(r, paint);
        String text = score == 0 ? "" : String.valueOf(score);
        canvas.drawText(text, r.left, r.centerY(), textPaint);
        canvas.drawRect(r, borderPaint);
    }


    public void drawCalendar(Canvas canvas) {
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


}
