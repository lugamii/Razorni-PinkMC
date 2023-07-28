package dev.razorni.hcfactions.signs.economy;

import dev.razorni.hcfactions.extras.spawners.Spawner;
import dev.razorni.hcfactions.signs.CustomSign;
import dev.razorni.hcfactions.signs.CustomSignManager;
import dev.razorni.hcfactions.utils.ItemBuilder;
import dev.razorni.hcfactions.utils.ItemUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class EconomySign extends CustomSign {
    private final Map<UUID, BukkitTask> taskCache;
    private final Map<String, ItemStack> cache;

    public EconomySign(CustomSignManager manager, List<String> lines) {
        super(manager, lines);
        this.cache = new HashMap<>();
        this.taskCache = new HashMap<>();
    }

    public ItemStack getItemStack(String stack) {
        if (this.cache.containsKey(stack)) {
            return this.cache.get(stack);
        }
        if (stack.contains("Spawner ")) {
            String[] lines = stack.split(" ");
            Spawner spawner = this.getInstance().getSpawnerManager().getByName(lines[1]);
            if (spawner == null) {
                return null;
            }
            return spawner.getItemStack();
        } else if (stack.contains(" Spawner")) {
            String[] lines = stack.split(" ");
            Spawner spawner = this.getInstance().getSpawnerManager().getByName(lines[0]);
            if (spawner == null) {
                return null;
            }
            return spawner.getItemStack();
        } else {
            if (stack.contains(":")) {
                String[] lines = stack.split(":");
                ItemStack itemStack = new ItemBuilder(ItemUtils.getMat(lines[0])).data(this.getManager(), Short.parseShort(lines[1])).toItemStack();
                this.cache.put(stack, itemStack);
                return itemStack;
            }
            Material material = ItemUtils.getMat(stack);
            ItemStack toReturn = new ItemStack(material);
            this.cache.put(stack, toReturn);
            return toReturn;
        }
    }

    @Override
    public void onClick(Player player, Sign sign) {
    }

    public void sendSignChange(Player player, Sign sign, String[] lines) {
        if (this.taskCache.containsKey(player.getUniqueId())) {
            return;
        }
        player.sendSignChange(sign.getLocation(), lines);
        this.taskCache.put(player.getUniqueId(), new BukkitRunnable() {
            public void run() {
                player.sendSignChange(sign.getLocation(), sign.getLines());
                EconomySign.this.taskCache.remove(player.getUniqueId());
            }
        }.runTaskLater(this.getInstance(), 40L));
    }
}
