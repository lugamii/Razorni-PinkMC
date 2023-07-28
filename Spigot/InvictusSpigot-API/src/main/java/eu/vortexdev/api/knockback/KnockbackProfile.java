package eu.vortexdev.api.knockback;

import java.util.List;

public interface KnockbackProfile {

    String getName();

    List<KnockbackValue> getSettings();

    KnockbackValue getSetting(String key);

    <T> T getSettingValue(String key);
}