package dev.razorni.hcfactions.kits;

import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.utils.Serializer;
import dev.razorni.hcfactions.utils.Utils;
import dev.razorni.hcfactions.utils.extra.Cooldown;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
public class Kit extends Module<KitManager> {
    private int seconds;
    private Cooldown cooldown;
    private ItemStack[] contents;
    private ItemStack[] armorContents;
    private String name;

    public Kit(KitManager manager, String name) {
        super(manager);
        this.name = name;
        this.seconds = 3;
        this.cooldown = new Cooldown(manager);
        this.contents = new ItemStack[36];
        this.armorContents = new ItemStack[4];
    }

    public Kit(KitManager manager, Map<String, Object> map) {
        super(manager);
        this.name = (String) map.get("name");
        this.seconds = Integer.parseInt((String) map.get("seconds"));
        this.cooldown = new Cooldown(manager);
        this.contents = Utils.createList(map.get("contents"), String.class).stream().map(s -> Serializer.deserializeItem(this.getManager(), s)).toArray(ItemStack[]::new);
        this.armorContents = Utils.createList(map.get("armorContents"), String.class).stream().map(s -> Serializer.deserializeItem(this.getManager(), s)).toArray(ItemStack[]::new);
    }

    public void equip(Player player) {
        for (int i = 0; i < this.contents.length; ++i) {
            ItemStack stack = this.contents[i];
            if (stack != null) {
                if (stack.getType() != Material.AIR) {
                    player.getInventory().setItem(i, stack);
                }
            }
        }
        for (int i = 0; i < this.armorContents.length; ++i) {
            ItemStack stack = this.armorContents[i];
            if (stack != null) {
                if (stack.getType() != Material.AIR) {
                    player.getInventory().setItem(36 + i, stack);
                }
            }
        }
        this.cooldown.applyCooldown(player, this.seconds);
        player.updateInventory();
    }

    public String getName() {
        return this.name;
    }

    public void update(ItemStack[] contents, ItemStack[] armorContents) {
        this.contents = new ItemStack[contents.length];
        this.armorContents = new ItemStack[armorContents.length];
        for (int i = 0; i < contents.length; ++i) {
            ItemStack stack = contents[i];
            this.contents[i] = ((stack != null) ? stack.clone() : null);
        }
        for (int i = 0; i < armorContents.length; ++i) {
            ItemStack stack = armorContents[i];
            this.armorContents[i] = ((stack != null) ? stack.clone() : null);
        }
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", this.name);
        map.put("seconds", this.seconds + "");
        map.put("contents", Arrays.stream(this.contents).map(s -> Serializer.serializeItem(this.getManager(), s)).collect(Collectors.toList()));
        map.put("armorContents", Arrays.stream(this.armorContents).map(s -> Serializer.serializeItem(this.getManager(), s)).collect(Collectors.toList()));
        return map;
    }

    public ItemStack[] getArmorContents() {
        return this.armorContents;
    }

    public void save() {
        this.getManager().getKits().put(this.name, this);
        this.getKitsData().getValues().put(this.name, this.serialize());
        this.getKitsData().save();
    }

    public void delete() {
        this.getManager().getKits().remove(this.name);
        this.getKitsData().getValues().remove(this.name);
        this.getKitsData().save();
    }
}