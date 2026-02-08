import parcs.*;
import java.io.*;
import java.nio.file.*;
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

        String text = Files.readString(Path.of(filename));

        System.out.print("Enter pattern to search: ");
        Scanner scanner = new Scanner(System.in);
        String pattern = scanner.nextLine();

        if (pattern.trim().isEmpty()) {
            System.out.println("Empty pattern -> nothing to search.");
            return;
        }
        if (pattern.length() > text.length()) {
            System.out.println("No matches (pattern longer than text).");
            return;
        }

        workers = Math.max(1, Math.min(workers, text.length())); // щоб не було workers > length і без роботи не простоювалися
        workers = Math.min(workers, maxAvailableDaemons);

        task curtask = new task();
        curtask.addJarFile("RabinKarp.jar");
        AMInfo info = new AMInfo(curtask, null);

        long startTime = System.nanoTime();
        int n = text.length();
        int chunkSize = n / workers;
        int overlap = pattern.length() - 1;

        List<channel> chans = new ArrayList<>(workers);

        for (int i = 0; i < workers; i++) {
            int start = i * chunkSize;
            int end = (i == workers - 1) ? n : (i + 1) * chunkSize; // якщо не націло n / workers, то залишкові символи обробляються останнім воркером

            // на межах чанків міг би обрізатися потрібний паттерн, тому +overlap (к-сть символів у паттерна - 1)
            int extendedEnd = Math.min(n, end + overlap);
            String chunk = text.substring(start, extendedEnd);

            int validStartOfChunk = start;
            int validStartEndOfChunk = end - 1;

            Chunk rkTask = new Chunk(chunk, pattern, validStartOfChunk, validStartEndOfChunk);

            point p = info.createPoint();
            channel c = p.createChannel();
            p.execute("RabinKarp");
            c.write(rkTask); // надсилаємо дані воркеру

            chans.add(c);
        }

        List<Integer> all = new ArrayList<>();
        for (channel c : chans) {
            Result r = (Result) c.readObject(); // отримуємо результати від воркера
            all.addAll(r.indexes);
        }
        Collections.sort(all);

        long endTime = System.nanoTime();
        double timeParallel = (endTime - startTime) / 1e9;
        System.out.println("Time with " + workers + ": " + timeParallel + " sec.");

        if (all.isEmpty()) {
            System.out.println("No matches.");
        } else {
            System.out.println("Match start indexes:");
            for (int i = 0; i < all.size(); i++) {
                if (i > 0) System.out.print(", ");
                System.out.print(all.get(i));
            }
            System.out.println();
        }

        curtask.end();
    }
}