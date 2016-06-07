package rumataestorsky.github.com.owlclient;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.io.IOException;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MAIN_ACTIVITY";
    public static final String API_URL = "http://owl.lastochka-os.ru";
    private OwlService owl;
    private Retrofit retrofit;

    public static class Task {
        public final int id;
        public final String name;
        public final boolean isFrozen;
        public final double cost;
        public final double score;


        public Task(int id, String name, boolean isFrozen, double cost, double score) {
            this.id = id;
            this.name = name;
            this.isFrozen = isFrozen;
            this.cost = cost;
            this.score = score;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public interface OwlService {
        @GET("/task")
        Call<List<Task>> taskList();

        @PUT("/task/{taskId}/exec/{count}")
        Call<Task> addExec(@Path("taskId") int taskId, @Path("count") Integer count);
    }

    public MainActivity() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        owl = retrofit.create(OwlService.class);
    }

    private void fillSpinner(Spinner spinner) throws IOException {
        Call<List<Task>> call = owl.taskList();
        List<Task> tasks = call.execute().body();

        ArrayAdapter<Task> adapter = new ArrayAdapter<Task>(this, android.R.layout.simple_spinner_item, tasks.toArray(new Task[0]));
        spinner.setAdapter(adapter);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        final Spinner taskSpinner = (Spinner) findViewById(R.id.taskSpinner);
        try {
            fillSpinner(taskSpinner);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onClickSendExecButton(View v) throws IOException {

        final EditText editText = (EditText) findViewById(R.id.countText);
        final Spinner spinner = (Spinner) findViewById(R.id.taskSpinner);

        final String sCount = editText.getText().toString().trim();
        if (!sCount.isEmpty()) {
            Task task = (Task) spinner.getSelectedItem();
            int count = Integer.valueOf(sCount);

            Response<Task> added = owl.addExec(task.id, count).execute();
            if (added.isSuccessful()) {
                final String msg = "Successfully added execution #" + added.body().id + ", score = " + added.body().score;
                Toast.makeText(v.getContext(), msg, Toast.LENGTH_LONG).show();
                editText.setText("");
            }

        }

    }
}

