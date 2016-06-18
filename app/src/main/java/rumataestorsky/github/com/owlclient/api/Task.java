package rumataestorsky.github.com.owlclient.api;

public class Task {
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
