import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Result implements Serializable {
    public final List<Integer> indexes = new ArrayList<>();
    public void add(int idx) {
        indexes.add(idx);
    }
}
