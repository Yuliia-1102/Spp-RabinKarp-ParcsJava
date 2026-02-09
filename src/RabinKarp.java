import parcs.*;

import java.util.ArrayList;
import java.util.List;

public class RabinKarp implements AM {

    public void run(AMInfo info) {
        Task task = (Task) info.parent.readObject();

        List<PatternResult> res = new ArrayList<>();
        for (String p : task.patterns) {
            List<Integer> inxs = getMatchesRabinKarp(p, task.text);
            res.add(new PatternResult(p, inxs));
        }

        info.parent.write(new Result(res));
    }

    public static List<Integer> getMatchesRabinKarp(String pattern, String text) {
        List<Integer> occurrences = new ArrayList<>();
        if (pattern == null || text == null) return occurrences;

        int patLen = pattern.length();
        int txtLen = text.length();
        if (patLen == 0 || patLen > txtLen) return occurrences;

        int q = 101;
        int d = 26;

        long h = 1;
        for (int i = 0; i < patLen - 1; i++) {
            h = (h * d) % q;
        }

        long patHash = 0;
        long txtHash = 0;

        for (int i = 0; i < patLen; i++) {
            patHash = (d * patHash + pattern.charAt(i)) % q;
            txtHash = (d * txtHash + text.charAt(i)) % q;
        }

        for (int i = 0; i <= txtLen - patLen; i++) {
            if (patHash == txtHash) {
                boolean ok = true;
                for (int j = 0; j < patLen; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        ok = false;
                        break;
                    }
                }
                if (ok) occurrences.add(i);
            }

            if (i < txtLen - patLen) {
                txtHash = (d * (txtHash - text.charAt(i) * h) + text.charAt(i + patLen)) % q;
                if (txtHash < 0) txtHash += q;
            }
        }

        return occurrences;
    }
}
