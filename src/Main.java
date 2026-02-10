import parcs.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Please specify: <input_file> <workers>.");
            return;
        }

        String inputFile = args[0];
        int workers = Integer.parseInt(args[1]);
        int maxAvailableDaemons = 10;
        String outputFile = "output.txt";

        List<String> lines = Files.readAllLines(Path.of(inputFile));
        List<String> strings = new ArrayList<>();
        for (String l : lines) strings.add(l.strip());

        if (strings.isEmpty()) {
            System.out.println("Empty input file.");
            Files.writeString(Path.of(outputFile), "");
            return;
        }

        String text = strings.get(strings.size() - 1);
        List<String> patterns = strings.subList(0, strings.size() - 1);

        if (patterns.isEmpty()) {
            System.out.println("No patterns to search.");
            Files.writeString(Path.of(outputFile), "");
            return;
        }

        workers = Math.max(1, Math.min(workers, patterns.size())); // щоб не було workers > patterns і без роботи не простоювалися
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
        System.out.println("Time of " + workers + " workers: " + timeParallel + " sec.");

        StringBuilder sb = new StringBuilder();
        for (PatternResult pr : allResults) {
            sb.append("Pattern: ").append(pr.pattern).append("\n");
            sb.append("Occurrence indexes: ");

            if (pr.indexes == null || pr.indexes.isEmpty()) {
                sb.append("none");
            } else {
                for (int i = 0; i < pr.indexes.size(); i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(pr.indexes.get(i));
                }
            }
            sb.append("\n\n");
        }
        Files.writeString(Path.of(outputFile), sb.toString());

        curtask.end();
    }
}