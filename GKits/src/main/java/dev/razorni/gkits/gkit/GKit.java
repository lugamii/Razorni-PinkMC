package dev.razorni.gkits.gkit;

import dev.razorni.gkits.GKits;
import cc.invictusgames.ilib.utils.InventoryUtils;
import cc.invictusgames.ilib.utils.TimeUtils;
import lombok.Data;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Data
public class GKit {

    private final UUID uuid;
    private String name;

    private String displayName;
    private int slot;
    private long coolDown;

    private ItemStack[] armor = new ItemStack[4];
    private ItemStack[] contents = new ItemStack[36];
    private ItemStack icon;

    private List<String> description = new ArrayList<>();

    boolean applyArmor = false;

    public GKit(String name) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.displayName = ChatColor.AQUA + name;
        this.icon = new ItemStack(Material.DIAMOND);
        this.coolDown = -1;
        this.description = Arrays.asList("Default description", "please change!");
    }

    public GKit(Document document) {
        uuid = UUID.fromString(document.getString("uuid"));
        name = document.getString("name");
        displayName = document.getString("displayName");

        if (document.containsKey("description"))
            description = document.getList("description", String.class);

        if (!document.getString("icon").isEmpty())
            this.icon = InventoryUtils.itemFromBase64(document.getString("icon"));

        if (document.containsKey("slot"))
            slot = document.getInteger("slot");

        if (document.containsKey("coolDown"))
            this.coolDown = document.getLong("coolDown");

        contents = InventoryUtils.inventoryFromBase64(document.getString("contents"));
        armor = InventoryUtils.inventoryFromBase64(document.getString("armor"));

        if (document.containsKey("applyArmor"))
            this.applyArmor = document.getBoolean("applyArmor");
    }

    public String getFormatted() {
        if (coolDown == 0)
            return "None";

        return TimeUtils.formatTimeShort(coolDown);
    }

    public Document toBson() {
        return new Document()
                .append("uuid", uuid.toString())
                .append("name", name)
                .append("displayName", displayName)
                .append("slot", slot)
                .append("coolDown", coolDown)
                .append("description", description)
                .append("icon", InventoryUtils.itemToBase64(icon))
                .append("contents", InventoryUtils.inventoryToBase64(contents))
                .append("armor", InventoryUtils.inventoryToBase64(armor))
                .append("applyArmor", applyArmor);
    }

    public void apply(Player player) {
        List<ItemStack> allItems = new ArrayList<>(Arrays.asList(contents));

        if (!player.hasPermission("aresenic.gkit." + this.getName())) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use "
                    + this.getName() + " gkit.");
            return;
        }

        if (!applyArmor) {
            allItems.addAll(Arrays.asList(armor));
        } else {
            for (int i = 0; i < armor.length; i++) {
                ItemStack itemStack = player.getInventory().getArmorContents()[i];
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    player.getWorld().dropItemNaturally(player.getLocation(), armor[i]);
                    continue;
                }

                ((CraftPlayer) player).getHandle().setEquipment(i, CraftItemStack.asNMSCopy(armor[i]));
            }
        }

        for (ItemStack itemStack : allItems) {
            if (player.getInventory().firstEmpty() == -1) {
                if (itemStack == null
                        || itemStack.getType().equals(Material.AIR))
                    continue;

                player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                continue;
            }

            if (player.getInventory().getItem(player.getInventory().firstEmpty()) != null
                    && player.getInventory().getItem(player.getInventory().firstEmpty()).getAmount() >= 0)
                player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
            else player.getInventory().setItem(player.getInventory().firstEmpty(), itemStack);

        }

        Bukkit.getScheduler().runTaskLater(GKits.get(), player::updateInventory, 10L);
    }

    public void clearDescription() {
        this.description = new ArrayList<>();
    }

    public String getDisplayName() {
        return ChatColor.translateAlternateColorCodes('&', displayName);
    }
}
