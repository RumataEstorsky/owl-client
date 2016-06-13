package rumataestorsky.github.com.owlclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.joda.time.LocalDate;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import rumataestorsky.github.com.owlclient.api.OwlApi;

public class CalendarActivity extends AppCompatActivity {
    public static final String TAG = "CALENDAR_ACTIVITY";
    private YearCalendarView calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        calendar = new YearCalendarView(this);
        setContentView(calendar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            fillCalendar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fillCalendar() throws IOException {
        Call<List<OwlApi.DaysProductivityView>> call = OwlApi.getApi().getDaysStatistics();
        List<OwlApi.DaysProductivityView> days = call.execute().body();

        double avg = getAverage(days);
        calendar.initColorWeights(avg);

        for(OwlApi.DaysProductivityView day : days) {
            calendar.addMark(LocalDate.parse(day.day), (int) Math.round(day.totalScore));//FIXME (double)
        }
    }



    private double getAverage(List<OwlApi.DaysProductivityView> days) {
        double sum = 0d;
        for(OwlApi.DaysProductivityView day : days) {
            sum += day.totalScore;
        }
        return days.isEmpty() ? 0d : sum / days.size();
    }
}
