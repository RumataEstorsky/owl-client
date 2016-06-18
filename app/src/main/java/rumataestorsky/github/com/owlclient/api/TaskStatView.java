package rumataestorsky.github.com.owlclient.api;

public class TaskStatView {
    public final long id;
    public final String name;
    public final double totalScore;
    public final int totalEl;
    public final int times;
    public final double  avgTimes;
    public final Integer daysAgo;

    public TaskStatView(long id, String name, double totalScore, int totalEl, int times, double avgTimes, Integer daysAgo) {
        this.id = id;
        this.name = name;
        this.totalScore = totalScore;
        this.totalEl = totalEl;
        this.times = times;
        this.avgTimes = avgTimes;
        this.daysAgo = daysAgo;
    }

    @Override
    public String toString() {
        return  "(" + daysAgo + ")" + name;
    }
}