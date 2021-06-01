import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

/**
 * Class that reads the data from text files.
 */
public class TextManager {
    /**
     * Reads text which is inside the "Help" message window.
     * Checks the width of the message window and cuts the
     * text to fit the window and not sticking out.
     * Every line is the element in the output list.
     * Empty strings will be interpreted as vertical gaps.
     * @param widthOfWindow - width of the message window (in pixels)
     * @param filename - filename that has an important text
     * @return - list of lines to be drawn
     */
    static ArrayList<String> readMessageContent(int widthOfWindow, String filename) {
        try {
            File file = new File(filename);
            Scanner scanner = new Scanner(file);
            ArrayList<String> output = new ArrayList<>(Arrays.asList(scanner.nextLine(), ""));

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if(line.equals("")) {
                    output.add("");
                    output.add("");
                } else if(line.charAt(0) == '$') {
                    output.add(line);
                    output.add("");
                } else {
                    String[] words = line.split(" ");
                    StringBuilder last = new StringBuilder(output.get(output.size() - 1));

                    for(String word: words) {
                        if(DrawUtils.stringWidth(last+" "+word) < widthOfWindow*0.95) {
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

    static ArrayList<TextLine> transformStringsToLines(ArrayList<String> texts, int fontSize, int baseX, int baseY) {
        final int HEIGHT = fontSize + 5;
        ArrayList<TextLine> output = new ArrayList<>();

        for(int i = 0; i < texts.size(); i++) {
            output.add(new TextLine(texts.get(i), baseX, baseY + HEIGHT*i, fontSize));
        }

        return output;
    }
}
