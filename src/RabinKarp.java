import parcs.*;
import java.util.*;

public class RabinKarp implements AM {

    @Override
    public void run(AMInfo info) {
        Chunk task = (Chunk) info.parent.readObject(); // воркер читає дані

        Result res = new Result();
        List<Integer> local = rabinKarpAll(task.textChunk, task.pattern);

        for (int posInChunk : local) {
            int globalPos = task.validStartOfChunk + posInChunk;
            if (globalPos >= task.validStartOfChunk && globalPos <= task.validStartEndOfChunk) {
                res.add(globalPos);
            }
        }
        info.parent.write(res); // воркер відправляє дані
    }

    private static List<Integer> rabinKarpAll(String text, String pattern) {
        List<Integer> occ = new ArrayList<>();
        if (text == null || pattern == null) return occ;

        int n = text.length();
        int m = pattern.length();
        if (m == 0 || m > n) return occ;

        final long q = 1_000_000_007L;
        final long d = 256L;

        long h = 1;
        for (int i = 0; i < m - 1; i++) h = (h * d) % q;

        long pHash = 0;
        long tHash = 0;

        for (int i = 0; i < m; i++) {
            pHash = (d * pHash + pattern.charAt(i)) % q;
            tHash = (d * tHash + text.charAt(i)) % q;
        }

        for (int i = 0; i <= n - m; i++) {
            if (pHash == tHash) {
                int j = 0;
                for (; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) break;
                }
                if (j == m) occ.add(i);
            }

            if (i < n - m) {
                long left = (text.charAt(i) * h) % q;
                tHash = (d * ((tHash - left + q) % q) + text.charAt(i + m)) % q;
            }
        }

        return occ;
    }
}