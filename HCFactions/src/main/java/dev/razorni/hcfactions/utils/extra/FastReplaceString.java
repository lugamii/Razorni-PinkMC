package dev.razorni.hcfactions.utils.extra;

public class FastReplaceString {

    private String string;

    public FastReplaceString(String s) {
        this.string = s;
    }

    public FastReplaceString replaceAll(String s1, String s2) {
        int size = s1.length();
        int i = this.string.indexOf(s1);
        if (size == 0) {
            return this;
        }
        if (i == -1) {
            return this;
        }
        StringBuilder stringBuilder = new StringBuilder((size > s2.length()) ? this.string.length() : (this.string.length() * 2));
        int index = 0;
        do {
            stringBuilder.append(this.string, index, i);
            stringBuilder.append(s2);
            index = i + size;
            i = this.string.indexOf(s1, index);
        } while (i > 0);
        stringBuilder.append(this.string, index, this.string.length());
        this.string = String.valueOf(stringBuilder);
        return this;
    }

    public String endResult() {
        return this.string;
    }
}
