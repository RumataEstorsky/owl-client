package rumataestorsky.github.com.owlclient.api;

import org.joda.time.LocalDate;

/**
 * Created by rumata on 02.09.16.
 */
public class DaysProductivityView {
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
