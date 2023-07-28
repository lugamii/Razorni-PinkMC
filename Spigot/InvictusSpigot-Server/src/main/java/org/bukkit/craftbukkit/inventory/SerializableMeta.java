package org.bukkit.craftbukkit.inventory;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.NoSuchElementException;

@SerializableAs("ItemMeta")
public class SerializableMeta implements ConfigurationSerializable {
    static final String TYPE_FIELD = "meta-type";

    static final ImmutableMap<Class<? extends CraftMetaItem>, String> classMap;
    static final ImmutableMap<String, Constructor<? extends CraftMetaItem>> constructorMap;

    static {
        classMap = ImmutableMap.<Class<? extends CraftMetaItem>, String>builder()
                .put(CraftMetaBanner.class, "BANNER")
                .put(CraftMetaBlockState.class, "TILE_ENTITY")
                .put(CraftMetaBook.class, "BOOK")
                .put(CraftMetaBookSigned.class, "BOOK_SIGNED")
                .put(CraftMetaSkull.class, "SKULL")
                .put(CraftMetaLeatherArmor.class, "LEATHER_ARMOR")
                .put(CraftMetaMap.class, "MAP")
                .put(CraftMetaPotion.class, "POTION")
                .put(CraftMetaEnchantedBook.class, "ENCHANTED")
                .put(CraftMetaFirework.class, "FIREWORK")
                .put(CraftMetaCharge.class, "FIREWORK_EFFECT")
                .put(CraftMetaItem.class, "UNSPECIFIC")
                .build();

        final ImmutableMap.Builder<String, Constructor<? extends CraftMetaItem>> classConstructorBuilder = ImmutableMap.builder();
        for (Map.Entry<Class<? extends CraftMetaItem>, String> mapping : classMap.entrySet()) {
            try {
                classConstructorBuilder.put(mapping.getValue(), mapping.getKey().getDeclaredConstructor(Map.class));
            } catch (NoSuchMethodException e) {
                throw new AssertionError(e);
            }
        }
        constructorMap = classConstructorBuilder.build();
    }

    private SerializableMeta() {
    }

    public static ItemMeta deserialize(Map<String, Object> map) throws Throwable {
        Validate.notNull(map, "Cannot deserialize null map");

        String type = getString(map, TYPE_FIELD, false);
        Constructor<? extends CraftMetaItem> constructor = constructorMap.get(type);

        if (constructor == null) {
            throw new IllegalArgumentException(type + " is not a valid " + TYPE_FIELD);
        }

        try {
            return constructor.newInstance(map);
        } catch (final InstantiationException | IllegalAccessException e) {
            throw new AssertionError(e);
        } catch (final InvocationTargetException e) {
            throw e.getCause();
        }
    }

    static String getString(Map<?, ?> map, Object field, boolean nullable) {
        return getObject(String.class, map, field, nullable);
    }

    static boolean getBoolean(Map<?, ?> map, Object field) {
        Boolean value = getObject(Boolean.class, map, field, true);
        return value != null && value;
    }

    static <T> T getObject(Class<T> clazz, Map<?, ?> map, Object field, boolean nullable) {
        final Object object = map.get(field);

        if (clazz.isInstance(object)) {
            return clazz.cast(object);
        }
        if (object == null) {
            if (!nullable) {
                throw new NoSuchElementException(map + " does not contain " + field);
            }
            return null;
        }
        throw new IllegalArgumentException(field + "(" + object + ") is not a valid " + clazz);
    }

    public Map<String, Object> serialize() {
        throw new AssertionError();
    }
}