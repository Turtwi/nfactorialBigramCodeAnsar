import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class nfactorialBigramCode {
    public static Map<String, Integer> bigramMap = new HashMap<>();
    public static Map<Character,Map<Character,Integer>> bigramNextCharMap = new HashMap<>();
    public static int sum = 0;
    public static void BigramAndCharMap(String fileName) throws IOException {
        List<String> names = Files.readAllLines(Path.of(fileName), StandardCharsets.UTF_8);
        for (String name : names) {
            String nameBigram = "^" + name + "$";
            int nameLength = nameBigram.length();
            for (int i = 0; i < nameLength - 1; i++) {
                String bigram = nameBigram.substring(i, i + 2);
                bigramMap.put(bigram, bigramMap.getOrDefault(bigram, 0) + 1);
                char firstChar = bigram.charAt(0);
                char secondChar = bigram.charAt(1);
                Map<Character, Integer> secondCharCNT = bigramNextCharMap.computeIfAbsent(firstChar, k -> new HashMap<>());
                secondCharCNT.put(secondChar, secondCharCNT.getOrDefault(secondChar, 0) +1);
            }
        }
    }

    public static String generateNameThroughBigram(Map<String, Integer> bigramCounts){
        StringBuilder name = new StringBuilder();
        String add = "11";
        Random random = new Random();
        while(add.charAt(0) != '^'){
            Object[] keys =  bigramCounts.keySet().toArray();
            add = (String) keys[random.nextInt(keys.length)];
        }
        name.append(add.charAt(1));
        while(add.charAt(1) != '$'){
            Object[] keys =  bigramCounts.keySet().toArray();
            add = (String) keys[random.nextInt(keys.length)];
            if(add.charAt(0) == '^'){
                name.append(add.charAt(1));
            }
            else {
                if(add.charAt(1) != '$'){
                    name.append(add);
                }
            }
        }
        name.append(add.charAt(0));
        return name.toString();
    }

    public static String generateNameThroughBigramChar(){
        StringBuilder name = new StringBuilder();
        char currentChar = '^';
        char nextChar;
        while (true){
            nextChar = choseNextChar(currentChar,bigramNextCharMap);
            if(nextChar == '$'){
                break;
            }
            else {
                name.append(nextChar);
                currentChar = nextChar;
            }
        }

        return name.toString();
    }
    public static void probabilitiesOfBigram(Map<String, Integer> bigramMap){
        sum = bigramMap.values().stream().mapToInt(Integer::intValue).sum();
        bigramMap.forEach((key, value) -> System.out.println(key + ": " + String.format("%.5f", (double)value/sum)));
    }

    public static void probabilitiesOfNextChar(Map<Character,Map<Character,Integer>> bigramNextCharMap, char currentChar){
        sum = bigramNextCharMap.get(currentChar).values().stream().mapToInt(Integer::intValue).sum();
        bigramNextCharMap.get(currentChar).forEach((key, value) -> System.out.println(key + ": " + String.format("%.5f", (double)value/sum)));
    }

    public static char choseNextChar(char currentChar, Map<Character,Map<Character,Integer>> bigramNextCharMap){
        sum = bigramNextCharMap.get(currentChar).values().stream().mapToInt(Integer::intValue).sum();
        double random = new Random().nextInt(sum);
        for(Map.Entry<Character,Integer> entry: bigramNextCharMap.get(currentChar).entrySet()) {
            random -= entry.getValue();
            if (random < 0) {
                return entry.getKey();
            }
        }
        return '$';
    }

    public static void main(String[] args) throws IOException {
        BigramAndCharMap("C:\\Users\\Admin\\IdeaProjects\\Alibek\\src\\names.txt");
        List<String> names = Files.readAllLines(Path.of("C:\\Users\\Admin\\IdeaProjects\\Alibek\\src\\names.txt"), StandardCharsets.UTF_8);
        String name = generateNameThroughBigramChar();
        while (name.length() < 2){
            name = generateNameThroughBigramChar();
        }
        System.out.println(name);
        probabilitiesOfBigram(bigramMap);
    }
}
