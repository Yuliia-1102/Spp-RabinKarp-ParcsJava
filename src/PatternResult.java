import java.io.Serializable;
import java.util.List;

public class PatternResult implements Serializable {
    public final String pattern;
    public final List<Integer> indexes;

    public PatternResult(String pattern, List<Integer> indexes) {
        this.pattern = pattern;
        this.indexes = indexes;
    }
}
