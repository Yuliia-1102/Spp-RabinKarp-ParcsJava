import java.io.Serializable;
import java.util.List;

public class Result implements Serializable {
    public final List<PatternResult> results;

    public Result(List<PatternResult> results) {
        this.results = results;
    }
}
