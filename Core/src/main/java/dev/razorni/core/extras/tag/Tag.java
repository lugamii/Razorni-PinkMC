package dev.razorni.core.extras.tag;

import dev.razorni.core.util.CC;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class Tag implements Comparable<Tag> {

    private String name;
    private String category;
    private String prefix = "";
    private int weight;

    public Tag(String name) {
        this.name = name;
        this.category = category;
    }

    public String getPrefixInfo() {
        return this.getName() + CC.RESET + "(Weight: " + this.getWeight() + ") (Prefix: " + this.getPrefix() + ")";
    }

    public String getCategory() {
        return this.category;
    }

    @Override
    public int compareTo(Tag other) {
        return Integer.compare(this.weight, other.weight);
    }
}
