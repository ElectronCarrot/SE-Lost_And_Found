import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

    // S-01 spike: is a simple keyword search over 8000 found-item records fast enough?
    public class SearchLatencySpike {

        static final String[] COLORS = {"black", "blue", "red", "green", "white", "gray", "pink", "brown"};
        static final String[] ITEMS = {"backpack", "laptop", "water bottle", "hoodie", "umbrella", "calculator",
                "AirPods", "keys", "wallet", "notebook", "charger", "glasses", "badge", "hat", "jacket"};
        static final String[] PLACES = {"ECSS lobby", "Student Union", "McDermott Library", "Recreation Center",
                "Parking Structure 1", "Science Building", "Dining Hall", "Residence Hall West"};
        static final String[] QUERIES = {"backpack", "blue laptop", "AirPods", "ECSS", "wallet", "black hoodie",
                "keys", "library", "water bottle", "pink", "calculator", "charger"};

        // Count records that contain every word of the query

        static int search(String[] records, String query) {
            String[] words = query.toLowerCase(Locale.ROOT).trim().split("\\s+");
            int count = 0;
            for (String record : records) {
                boolean all = true;
                for (String w : words) {
                    if (w.isEmpty() || !record.contains(w)) {
                        all = false;
                        break;
                    }
                }
                if (all) count++;
            }
            return count;
        }

        public static void main(String[] args) {
            Random rng = new Random(42);
            String[] records = new String[8000];
            for (int i = 0; i < records.length; i++) {
                String title = COLORS[rng.nextInt(COLORS.length)] + " " + ITEMS[rng.nextInt(ITEMS.length)];
                String place = PLACES[rng.nextInt(PLACES.length)];
                records[i] = (title + " | Found a " + title + " near " + place + ". Tag #" + i + " | " + place).toLowerCase(Locale.ROOT);
            }

            long totalMatches = 0;
            for (int i = 0; i < 50; i++) {             // warm-up - not timed
                totalMatches += search(records, QUERIES[i % QUERIES.length]);
            }

            int n = 200;
            double[] ms = new double[n];
            for (int i = 0; i < n; i++) {
                String q = QUERIES[i % QUERIES.length];
                long start = System.nanoTime();
                int matches = search(records, q);
                ms[i] = (System.nanoTime() - start) / 1_000_000.0;
                totalMatches += matches;
                System.err.printf("%d,%s,%.6f,%d%n", i, q, ms[i], matches);
            }

            double[] sorted = ms.clone();
            Arrays.sort(sorted);
            double median = (sorted[n / 2 - 1] + sorted[n / 2]) / 2;
            double p95 = sorted[(int) Math.ceil(0.95 * n) - 1];
            double max = sorted[n - 1];
            double mean = Arrays.stream(ms).sum() / n;
            long under = Arrays.stream(ms).filter(t -> t <= 2000).count();

            System.out.println("S-01 keyword search spike (Java)");
            System.out.println("records: 8000, seed: 42, warm-up: 50 (untimed), timed searches: " + n);
            System.out.printf("median: %.3f ms%n", median);
            System.out.printf("p95 (nearest-rank): %.3f ms%n", p95);
            System.out.printf("max: %.3f ms%n", max);
            System.out.printf("mean: %.3f ms%n", mean);
            System.out.println("searches <= 2000 ms: " + under + " / " + n);
            System.out.println("total matches (warm-up + timed): " + totalMatches);
            System.out.println("p95 <= 2000 ms? " + (p95 <= 2000 ? "YES" : "NO"));
        }
    }

