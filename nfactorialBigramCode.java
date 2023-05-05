import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class nfactorialBigramCode {
    public static Map<Character,Map<Character,Integer>> bigramNextCharMap = new HashMap<>();
    public static int sum = 0;
    public static void BigramAndCharMap(String fileName) throws IOException {
        List<String> names = Files.readAllLines(Path.of(fileName), StandardCharsets.UTF_8);
        for (String name : names) {
            String nameBigram = "^" + name + "$";
            int nameLength = nameBigram.length();
            for (int i = 0; i < nameLength - 1; i++) {
                String bigram = nameBigram.substring(i, i + 2);
                char firstChar = bigram.charAt(0);
                char secondChar = bigram.charAt(1);
                Map<Character, Integer> secondCharCNT = bigramNextCharMap.computeIfAbsent(firstChar, k -> new HashMap<>());
                secondCharCNT.put(secondChar, secondCharCNT.getOrDefault(secondChar, 0) +1);
            }
        }
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
    public static void probabilitiesOfNextChar(Map<Character,Map<Character,Integer>> bigramNextCharMap, char currentChar){
        sum = bigramNextCharMap.get(currentChar).values().stream().mapToInt(Integer::intValue).sum();
        bigramNextCharMap.get(currentChar).forEach((key, value) -> System.out.print(key + ": " + String.format("%.5f", (double)value/sum) + ", "));
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
        Scanner scan = new Scanner(System.in);
        System.out.println("Write path to database: ");
        String filePath = scan.next();
        BigramAndCharMap(filePath);
        List<String> names = Files.readAllLines(Path.of(filePath), StandardCharsets.UTF_8);
        int choose = 0;
        String name = "";
        while(choose != 5){
            System.out.println("Choose a function:\n1.Generate Name.\n2.Generate New Name." +
                    "\n3.See probabilities.\n4.End.\nChoose: ");
            choose = scan.nextInt();
            if(choose==1){
                name = generateNameThroughBigramChar();
                while(name.length() < 3) {
                    name = generateNameThroughBigramChar();
                }
                name = name.substring(0,1).toUpperCase() + name.substring(1,name.length() -1);
                System.out.println(name);
            }
            else if(choose==2){
                name = generateNameThroughBigramChar();
                while(name.length() < 3 && !names.contains(name)) {
                    name = generateNameThroughBigramChar();
                }
                name = name.substring(0,1).toUpperCase() + name.substring(1,name.length() -1);
                System.out.println(name);
            }
            else if(choose==3) {
                System.out.println("Choose for what you wanna see probability:" +
                        "\n1.For a specific character probability." +
                        "\n2.For all characters probability" +
                        "\n3.For first character probability" +
                        "\n4.Back." +
                        "\nChoose: ");
                choose = scan.nextInt();
                if(choose==1){
                    System.out.println("Name which character: ");
                    String n = scan.next();
                    n = n.toLowerCase(Locale.ROOT);
                    probabilitiesOfNextChar(bigramNextCharMap,n.charAt(0));
                }
                else if(choose==2) {
                    String alphabet = "abcdefghijklmnopqrstuvwxyz";
                    for (int i = 0; i < alphabet.length(); i++){
                        System.out.println("Next character probability for this character " + alphabet.charAt(i));
                        probabilitiesOfNextChar(bigramNextCharMap, alphabet.charAt(i));
                        System.out.println();
                        System.out.println();
                    }
                }
                else if(choose==3) {
                    System.out.println("First character probability: ");
                    probabilitiesOfNextChar(bigramNextCharMap,'^');
                }
                else if(choose==4){
                    choose =8;
                }
            }
            else if(choose==4){
                break;
            }
        }

    }
}
