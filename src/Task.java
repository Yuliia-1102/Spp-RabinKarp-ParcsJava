import java.io.Serializable;
import java.util.List;

public class Task implements Serializable {
    public final List<String> patterns;
    public final String text;

    public Task(List<String> patterns, String text) {
        this.patterns = patterns;
        this.text = text;
    }
}
