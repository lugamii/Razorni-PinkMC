package eu.vortexdev.invictusspigot.util.commenter;

import eu.vortexdev.invictusspigot.util.JavaUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class YamlCommenter {

    private final HashMap<String, String> comments = new HashMap<>();
    private String header = "";

    /**
     * Add comment to a config option.<br>
     * Supports multiline comments!
     *
     * @param path    Config path to add comment to
     * @param comment Comment to add
     */
    public void addComment(String path, String comment) {
        comments.put(path, comment);
    }

    /**
     * Set the header for this config file
     *
     * @param header Header to add
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * Saves comments to config file
     *
     * @param file            File to save to
     * @param loadWorldConfig Whether to load world configuration comments
     * @throws IOException io
     */
    public void saveComments(File file, boolean loadWorldConfig) throws IOException {
        ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(file.toPath());
        lines.removeIf(s -> s.trim().startsWith("#") || s.trim().length() <= 4);
        lines.add(0, "# " + header.replace("\n", "\n# ") + "\n");
        for (Map.Entry<String, String> comment : comments.entrySet()) {
            if (!loadWorldConfig && comment.getKey().startsWith("world-settings."))
                continue; // World settings are initialized after NachoConfig, while NachoConfig loads their comments.
            //       This causes an issue on new configs, with comments not working for world settings, and causing an exception due to being unable to find the configuration option.
            int line = JavaUtil.findKey(lines, comment.getKey());

            if (line == -1) {
                throw new IllegalStateException(String.format(
                        "You are trying to add a comment to key \"%s\" which does not exist!",
                        comment.getKey()
                ));
            }

            String prefix = JavaUtil.repeat(" ", getIndentation(lines.get(line))) + "# ";
            boolean noNewline = getIndentation(lines.get(line)) > getIndentation(lines.get(line - 1));
            if (line >= 0)
                lines.add(line, (noNewline ? "" : "\n") + prefix + comment.getValue().replace("\n", "\n" + prefix));
            else System.out.printf("Failed to find key %s in %s!", comment.getKey(), file);
        }
        FileWriter fw = new FileWriter(file);
        fw.write(String.join("\n", lines));
        fw.close();
    }

    private int getIndentation(String s) {
        if (!s.startsWith(" ")) return 0;
        int i = 0;
        while ((s = s.replaceFirst(" ", "")).startsWith(" ")) i++;
        return i + 1;
    }
}
