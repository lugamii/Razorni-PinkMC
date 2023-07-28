package eu.vortexdev.invictusspigot.knockback;

public class CraftKnockbackValue implements eu.vortexdev.api.knockback.KnockbackValue {
    private final String key;
    private Object value;

    public CraftKnockbackValue(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
