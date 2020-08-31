import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class TextManager {
    static ArrayList<String> readMessageContent(int lengthOfWindow, String filename) {
        try {
            File file = new File(filename);
            Scanner scanner = new Scanner(file);
            ArrayList<String> output = new ArrayList<>(Arrays.asList(scanner.nextLine(), ""));

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if(line.equals("")) {
                    output.add("");
                    output.add("");
                } else {
                    String[] words = line.split(" ");
                    StringBuilder last = new StringBuilder(output.get(output.size() - 1));

                    for(String word: words) {
                        if(DrawUtils.stringWidth(last+" "+word) < lengthOfWindow*0.95) {
                            output.set(output.size()-1, last+word+" ");
                            last.append(word).append(" ");
                        } else {
                            output.add(word+" ");
                            last = new StringBuilder(word+" ");
                        }
                    }
                }
            }
            for(int i = 0; i < output.size(); i++) {
                output.set(i, output.get(i).trim());
            }
            return output;

        } catch(Exception e) {
            return new ArrayList<>(Collections.singletonList("File "+filename+" not found"));
        }
    }
}
