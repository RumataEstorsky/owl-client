package rumataestorsky.github.com.owlclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.joda.time.LocalDate;

import java.io.IOException;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;
import rumataestorsky.github.com.owlclient.api.*;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final String TAG = "MAIN_ACTIVITY";

    private Spinner spinner;
    private EditText editText;
    private YearCalendarView yearCalendarView;

    public MainActivity() {
    }

    private void fillSpinner() throws IOException {
//        Call<List<Task>> call = OwlApi.getApi().taskList();
        Call<List<TaskStatView>> call = OwlApi.getApi().activeTaskStatView();
        List<TaskStatView> tasks = call.execute().body();

        ArrayAdapter<TaskStatView> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tasks.toArray(new TaskStatView[0]));
        spinner.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = (Spinner) findViewById(R.id.taskSpinner);
        spinner.setOnItemSelectedListener(this);
        editText = (EditText) findViewById(R.id.countText);
        yearCalendarView = (YearCalendarView) findViewById(R.id.yearCalendarView);

    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            fillSpinner();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onClickSendExecButton(View v) throws IOException {
        final String sCount = editText.getText().toString().trim();
        if (!sCount.isEmpty()) {
            TaskStatView task = (TaskStatView) spinner.getSelectedItem();
            int count = Integer.valueOf(sCount);

            Response<Task> added = OwlApi.getApi().addExec(task.id, count).execute();
            if (added.isSuccessful()) {
                final String msg = "Successfully added execution #" + added.body().id + ", score = " + added.body().score;
                Toast.makeText(v.getContext(), msg, Toast.LENGTH_LONG).show();
                editText.setText("");
            }

        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TaskStatView task = (TaskStatView) spinner.getSelectedItem();

        try {
            fillCalendar(task.id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void fillCalendar(Long taskId) throws IOException {
        Call<List<OwlApi.DaysProductivityView>> call;

        call = (taskId == null) ? OwlApi.getApi().getAnnualStatistics()
                                : OwlApi.getApi().getAnnualStatisticsByTask(taskId);

        List<OwlApi.DaysProductivityView> days = call.execute().body();

        double avg = getAverage(days);
        yearCalendarView.refresh(avg);

        for(OwlApi.DaysProductivityView day : days) {
            yearCalendarView.addMark(LocalDate.parse(day.day), (int) Math.round(day.totalScore));//FIXME (double)
        }

        yearCalendarView.invalidate();

    }



    private double getAverage(List<OwlApi.DaysProductivityView> days) {
        double sum = 0d;
        for(OwlApi.DaysProductivityView day : days) {
            sum += day.totalScore;
        }
        return days.isEmpty() ? 0d : sum / days.size();
    }

}

