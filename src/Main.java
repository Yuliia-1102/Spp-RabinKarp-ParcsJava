import parcs.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Please specify: <text_file> <workers>.");
            return;
        }

        String filename = args[0];
        int workers = Integer.parseInt(args[1]);
        int maxAvailableDaemons = 10;

        List<String> lines = Files.readAllLines(Path.of(filename));
        List<String> strings = new ArrayList<>();
        for (String l : lines) strings.add(l.strip());

        if (strings.isEmpty()) {
            System.out.println("Empty input file.");
            return;
        }

        String text = strings.get(strings.size() - 1);
        List<String> patterns = strings.subList(0, strings.size() - 1);

        if (patterns.isEmpty()) {
            System.out.println("No patterns to search.");
            return;
        }

        workers = Math.max(1, Math.min(workers, patterns.size()));
        workers = Math.min(workers, maxAvailableDaemons);

        task curtask = new task();
        curtask.addJarFile("RabinKarp.jar");
        AMInfo info = new AMInfo(curtask, null);

        long startTime = System.nanoTime();

        int n = patterns.size();
        int step = n / workers;

        List<channel> chans = new ArrayList<>(workers);

        for (int i = 0; i < workers; i++) {
            int startPattern = i * step;
            int endPattern = (i == workers - 1) ? n : (i + 1) * step;

            List<String> slice = new ArrayList<>(patterns.subList(startPattern, endPattern));
            Task taskObj = new Task(slice, text);

            point p = info.createPoint();
            channel c = p.createChannel();
            p.execute("RabinKarp");
            c.write(taskObj);

            chans.add(c);
        }

        List<PatternResult> allResults = new ArrayList<>();
        for (channel c : chans) {
            Result r = (Result) c.readObject();
            allResults.addAll(r.results);
        }

        long endTime = System.nanoTime();
        double timeParallel = (endTime - startTime) / 1e9;
        System.out.println("Time with " + workers + " workers: " + timeParallel + " sec.\n");

        if (allResults.isEmpty()) {
            System.out.println("No matches.");
        } else {
            for (PatternResult pr : allResults) {
                System.out.println("Pattern: " + pr.pattern);
                System.out.print("Occurrence indexes: ");
                if (pr.indexes.isEmpty()) {
                    System.out.print("none");
                } else {
                    for (int i = 0; i < pr.indexes.size(); i++) {
                        if (i > 0) System.out.print(", ");
                        System.out.print(pr.indexes.get(i));
                    }
                }
                System.out.println("\n");
            }
        }

        curtask.end();
    }
}