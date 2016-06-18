package rumataestorsky.github.com.owlclient.api;

import android.os.StrictMode;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by rumata on 09.06.16.
 */
public class OwlApi {
    public static final String API_URL = "http://owl.lastochka-os.ru";
    private static OwlInterface owl;






    public static class DaysProductivityView {
        public final String day;
        public final double totalScore;
        public final int execCount;
        public final int typesTasksCount;

        public DaysProductivityView(String day, double totalScore, int execCount, int typesTasksCount) {
            this.day = day;
            this.totalScore = totalScore;
            this.execCount = execCount;
            this.typesTasksCount = typesTasksCount;
        }

        @Override
        public String toString() {
            return day + " = " + totalScore + ";";
        }
    }


    public interface OwlInterface {
        @GET("/task")
        Call<List<Task>> taskList();

        @GET("/task-stat")
        Call<List<TaskStatView>> activeTaskStatView();

        @PUT("/task/{taskId}/exec/{count}")
        Call<Task> addExec(@Path("taskId") long taskId, @Path("count") Integer count);

        @GET("/statistics/days")
        Call<List<DaysProductivityView>> getAnnualStatistics();

        @GET("/task/{taskId}/days")
        Call<List<DaysProductivityView>> getAnnualStatisticsByTask(@Path("taskId") long taskId);
    }


    public static OwlInterface getApi() {
        if (owl == null) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            owl = retrofit.create(OwlInterface.class);

        }
        return owl;
    }

}
