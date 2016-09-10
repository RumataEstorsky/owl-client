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


    private int cellSize;
    private int monthsLabelHeight;
    private int daysLabelWidth;
    private Paint borderPaint = new Paint();
    private Paint textPaint = new Paint();
    private Paint bestResultPaint = new Paint();


    private YearCalendar calendar;


    private int colors[] = {Color.LTGRAY, 0xffd6e685, 0xff8cc665, 0xff44a340, 0xff1e6823};
    private List<Paint> paints = new ArrayList<>(colors.length);

    /** Marks key: number of day since first day of calendar value (color + totalScore)*/
    private Map<Integer, Pair<Integer, Integer>> marks = new HashMap<>();

    private boolean showScores = false;
    private boolean showMounths = false;
    private boolean showWeekNumbers = false;
    private LocalDate localDate;

    public YearCalendarView(Context context) {
        super(context);
        graphInit();
    }

    public YearCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        graphInit();
    }

    public YearCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        graphInit();
    }


    private void graphInit() {
        setOnTouchListener(this);

        borderPaint.setColor(Color.WHITE);
        borderPaint.setStyle(Paint.Style.STROKE);
        textPaint.setColor(Color.BLACK);
        bestResultPaint.setColor(Color.RED);

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

        if(calendar == null) {
            return;
        }

        cellSize = getWidth() / YearCalendar.WEEKS - 1;
        monthsLabelHeight = cellSize;
        daysLabelWidth = cellSize;
        canvas.drawColor(Color.WHITE);

        drawCalendar(canvas);
    }



//    public void addMark(int year, int month, int day, int color, int score) {
//        LocalDate date = new LocalDate(year, month, day);
//        addMark(date, color, score);
//    }

//    public void addMark(LocalDate day, int score) {
//        int color = pickColor(score);
//        addMark(day, color, score);
//    }


//    public void addMark(LocalDate day, int color, int score) {
//        if(day.isBefore(calendar.getStartDate()) || day.isAfter(calendar.getEndDate())) {
//            throw new IllegalArgumentException("Date " + day.toString(DF) + " is impossible in period of this calendar!" );
//        }
//        int dc = calendar.daysSinceStart(day);
//        marks.put(dc, new Pair<>(color, score));
//    }

    public void drawDaySquare(Canvas canvas, Paint paint, LocalDate day, int score) {
        int w = calendar.getWeekNumber(day);
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
            String name = calendar.getStartDate().plusMonths(i).monthOfYear().getAsText(new Locale("ru"));
            canvas.drawText(name, cell * i, monthsLabelHeight / 2, textPaint);
        }
    }

    private void drawLabelsOfWeekdays(Canvas canvas) {
        for(int i = 0; i < YearCalendar.DAYS; ++i) {
            String name = calendar.getStartDate().plusDays(i).toString("E");
            int y = monthsLabelHeight + (i * cellSize) + (cellSize / 2);
            int rightColumnX = 3 + (cellSize * (YearCalendar.WEEKS + 1)) + 3;

            canvas.drawText(name, 3, y, textPaint);
            canvas.drawText(name, rightColumnX, y, textPaint);
        }
    }

    private void drawCalendar(Canvas canvas) {

        drawLabelsOfMonths(canvas);
        drawLabelsOfWeekdays(canvas);

        for(int i = 0; i <= calendar.getDaysCount(); i++) {
            LocalDate day = calendar.getStartDate().plusDays(i);
            int score = marks.containsKey(i) ? marks.get(i).second : 0;

//            int paintIndex = marks.containsKey(i) ? marks.get(i).first : 0;
            int paintIndex = calendar.getStepOfProductivity(score);
            Paint paint = paints.get(paintIndex);

            drawDaySquare(canvas, paint, day, score);
        }
        LocalDate first = calendar.getStartDate().plusDays(0);
        drawDaySquare(canvas, bestResultPaint, first, 111);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        String details = getDayByCoords(event.getX(), event.getY());
        Toast.makeText(getContext(), details, Toast.LENGTH_SHORT).show();

        return false;
    }

    private String getDayByCoords(float x, float y) {
        int week = (int) Math.floor(x / cellSize);
        week = week >= YearCalendar.WEEKS ? (YearCalendar.WEEKS - 1) : week;
        int day = (int) Math.floor(y / cellSize);
        day = day >= YearCalendar.DAYS ? (YearCalendar.DAYS - 1) : day;
        int dayNumber = (week) * YearCalendar.DAYS + day - 1;
        LocalDate date = calendar.getStartDate().plusDays(dayNumber);

        double score = marks.containsKey(dayNumber) ? marks.get(dayNumber).second : 0;
        return date.toString(DF) + ": " + Math.ceil(score);
    }

    public void setCalendar(YearCalendar calendar) {
        this.calendar = calendar;
    }
}
