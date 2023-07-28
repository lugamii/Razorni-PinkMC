package eu.vortexdev.invictusspigot.util;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JavaUtil {

    public static String repeat(String string, int count) {
        return new String(new char[count]).replace("\0", string);
    }

    public static String compile(Object[] objs) {
        return compile(objs, " ");
    }

    public static String compile(Object[] objs, String seperator) {
        StringBuilder builder = new StringBuilder();
        for (Object s : objs) {
            if (builder.length() != 0) builder.append(seperator);
            builder.append(s.toString());
        }
        return builder.toString();
    }

    public static String consolidateStrings(String[] args, int start) {
        String ret = args[start];
        if (args.length > start + 1)
            for (int i = start + 1; i < args.length; i++)
                ret = ret + " " + args[i];
        return ret;
    }

    public static String compile(int[] ints, String seperator) {
        StringBuilder builder = new StringBuilder();
        for (Object s : ints) {
            if (builder.length() != 0) builder.append(seperator);
            builder.append(s.toString());
        }
        return builder.toString();
    }

    public static int findKey(List<String> lines, String key) {
        String[] parts = key.split("\\.");
        int line;
        List<String> path = new ArrayList<>();
        for (String part : parts) {
            path.add(part);
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).trim().startsWith(part)) {
                    line = i;
                    if (String.join(".", path).equals(key)) {
                        return line;
                    }
                }
            }
        }
        return -1;
    }

    public static String compile(Object[] objs, String seperator, int start) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < objs.length; i++) {
            Object s = objs[i];
            if (builder.length() != 0) builder.append(seperator);
            builder.append(s.toString());
        }
        return builder.toString();
    }

    public static void createIfNotExists(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveFile(FileConfiguration config, File file) {
        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Integer tryParseInteger(String string) {
        try {
            return Integer.parseInt(string);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public static Long tryParseLong(String string) {
        try {
            return Long.parseLong(string);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public static Float tryParseFloat(String string) {
        try {
            return Float.parseFloat(string);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public static Double tryParseDouble(String string) {
        try {
            return Double.parseDouble(string);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public static <E> List<E> createList(Object object, Class<E> type, boolean ignoreNulls) {
        List<E> result = new ArrayList<>();
        if (object instanceof List) {
            for (Object value : (List<Object>) object) {
                if (!ignoreNulls && value == null) {
                    result.add(null);
                    continue;
                }
                if (value != null) {
                    Class<?> clazz = value.getClass();
                    if (clazz != null) {
                        if (!type.isAssignableFrom(clazz))
                            throw new AssertionError("Cannot cast to list! Key " + value + " is not a " + type.getSimpleName());
                        result.add(type.cast(value));
                    }
                }
            }
        }
        return result;
    }

    public static <E> List<E> createList(Object object, Class<E> type) {
        return createList(object, type, true);
    }

}
