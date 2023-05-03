import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class HashTable {

    private static final String CYRILLIC = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
    private static final String LATIN = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final int size;
    private final List<Pair<String, String>>[] table;


    public HashTable(int size) {
        this.size = size;
        this.table = new ArrayList[size];
        for (int i = 0; i < size; i++) {
            this.table[i] = new ArrayList<>();
        }
    }

    private int getBucketIndex(String key) {
        return getHashCode(key) % size;
    }

    public void insert(String key, String value) {
        int bucketIndex = getBucketIndex(key);
        List<Pair<String, String>> bucket = table[bucketIndex];
        for (Pair<String, String> p : bucket) {
            if (p.key.equals(key)) {
                bucket.set(bucket.indexOf(p), new Pair<>(key, value));
                return;
            }
        }
        bucket.add(new Pair<>(key, value));
    }

    public void delete(String key) {
        int bucketIndex = getBucketIndex(key);
        table[bucketIndex] = table[bucketIndex].stream().filter(p -> !p.key.equals(key)).collect(Collectors.toList());
    }

    public String get(String key) {
        int bucketIndex = getBucketIndex(key);
        List<Pair<String, String>> bucket = table[bucketIndex];
        for (Pair<String, String> p : bucket) {
            if (p.key.equals(key))
                return p.value;
        }
        return null;
    }

    public boolean hasCollision(String key) {
        return table[getBucketIndex(key)].size() > 1;
    }

    public int getHashCode(String key) {
        char first = key.toUpperCase(Locale.ROOT).charAt(0);
        if (!isLatin(first) && !isCyrillic(first))
            throw new RuntimeException("Unsupported key character!");
        int base = isLatin(first) ? LATIN.length() : CYRILLIC.length();

        char second = key.toUpperCase(Locale.ROOT).charAt(1);
        if (!isLatin(second) && !isCyrillic(second))
            throw new RuntimeException("Unsupported value character!");

        return getCharIndex(first) * base + getCharIndex(second);
    }

    @Override
    public String toString() {
        StringBuilder strTable = new StringBuilder();
        Arrays.stream(table).forEach(l -> l.forEach(p -> strTable
                        .append(p.key)
                        .append("(")
                        .append("V: ")
                        .append(getHashCode(p.key))
                        .append(", H: ")
                        .append(getBucketIndex(p.key))
                        .append(")")
                        .append("\n")
                )
        );
        for (int line = 0; line < size; line++) {
            strTable.append(line)
                    .append(": ")
                    .append(table[line])
                    .append("\n");
        }
        return strTable.toString();
    }

    private static boolean isLatin(char ch) {
        return LATIN.indexOf(ch) != -1;
    }

    private static boolean isCyrillic(char ch) {
        return CYRILLIC.indexOf(ch) != -1;
    }

    private static int getCharIndex(char ch) {
        return !isCyrillic(ch) ? (!isLatin(ch) ? -1 : LATIN.indexOf(ch)) : CYRILLIC.indexOf(ch);
    }

    record Pair<K, V>(K key, V value) {
        @Override
        public String toString() {
            return key + " -> " + value;
        }
    }
}
