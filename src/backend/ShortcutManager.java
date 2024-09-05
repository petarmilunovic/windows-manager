package backend;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static frontend.ShortcutFrame.selectedShortcutPosition;

//todo merge loadShortcuts and loadShortcutsFromFile methods
public class ShortcutManager {

    public static Map<String, List<Integer>> savedShortcuts;

    /**
     * Loads shortcuts from the file and stores them in the global savedShortcuts map
     */
    public static void loadShortcuts() {
        savedShortcuts = loadShortcutsFromFile();
    }

    /**
     * First reads the existing shortcuts from the file, updates them with the new shortcuts made during application runtime
     * which are stored in savedShortcuts map, and then writes the combined set of shortcuts back to the file.
     * If a shortcut already exists, it will be updated with the new value.
     */
    public static void saveShortcuts() {

        Map<String, List<Integer>> shortcuts = loadShortcutsFromFile();

        // update existing shortcuts with new values made during applications runtime
        shortcuts.putAll(savedShortcuts);

        // write all shortcuts to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("shortcuts.txt"))) {
            for (Map.Entry<String, List<Integer>> entry : shortcuts.entrySet()) {

                String windowPosition = entry.getKey();
                String keyCodes = entry.getValue().stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));

                writer.write(windowPosition + "=" + keyCodes);
                writer.newLine();
            }
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    /**
     * Reads keyboard shortcuts from the file and returns them as a map.
     */
    private static Map<String, List<Integer>> loadShortcutsFromFile() {

        Map<String, List<Integer>> fileShortcuts = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("shortcuts.txt"))) {

            String line;
            while ((line = reader.readLine()) != null) {

                String[] parts = line.split("=");
                if (parts.length == 2) {

                    String windowPosition = parts[0].trim();
                    List<Integer> keyCodes = Arrays.stream(parts[1].trim().split(","))
                            .map(String::trim)
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());
                    fileShortcuts.put(windowPosition, keyCodes);
                }
            }
        } catch (IOException err) {
            err.printStackTrace();
        }

        return fileShortcuts;
    }

    /**
     * Checks if the newly set shortcut is already used for a different position
     * by comparing it with existing shortcuts stored in savedShortcuts HashMap.
     */
    public static boolean isShortcutAlreadyUsed(List<Integer> newlySetShortcut) {

        for (Map.Entry<String, List<Integer>> entry : savedShortcuts.entrySet())
            if (!entry.getKey().equals(selectedShortcutPosition) && entry.getValue().equals(newlySetShortcut))
                return true;

        return false;
    }

}
