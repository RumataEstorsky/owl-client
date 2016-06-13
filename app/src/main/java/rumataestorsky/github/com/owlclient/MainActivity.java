package rumataestorsky.github.com.owlclient;

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
import rumataestorsky.github.com.owlclient.api.OwlApi;
import rumataestorsky.github.com.owlclient.api.OwlApi.Task;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MAIN_ACTIVITY";


    public MainActivity() {
    }

    private void fillSpinner() throws IOException {
        final Spinner spinner = (Spinner) findViewById(R.id.taskSpinner);
        Call<List<Task>> call = OwlApi.getApi().taskList();
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
        try {
            fillSpinner();
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

            Response<Task> added = OwlApi.getApi().addExec(task.id, count).execute();
            if (added.isSuccessful()) {
                final String msg = "Successfully added execution #" + added.body().id + ", score = " + added.body().score;
                Toast.makeText(v.getContext(), msg, Toast.LENGTH_LONG).show();
                editText.setText("");
            }

        }

    }
}

