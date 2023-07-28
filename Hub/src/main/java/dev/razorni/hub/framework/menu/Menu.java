package dev.razorni.hub.framework.menu;

import dev.razorni.hub.framework.Module;
import dev.razorni.hub.framework.menu.button.Button;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;

@Setter
@Getter
public abstract class Menu extends Module<MenuManager> {
    private Inventory inventory;
    private BukkitTask updater;
    private Map<Integer, Button> buttons;
    private ItemStack filler;
    private Player player;
    private boolean fillEnabled;

    public Menu(MenuManager manager, Player player, String name, int size, boolean updater) {
        super(manager);
        this.player = player;
        this.inventory = Bukkit.createInventory(null, size, name);
        this.updater = (updater ? Bukkit.getScheduler().runTaskTimer(this.getInstance(), this::update, 10L, 10L) : null);
        this.fillEnabled = false;
    }

    public void open() {
        this.buttons = this.getButtons(this.player);
        for (Map.Entry<Integer, Button> buttons : this.buttons.entrySet()) {
            this.inventory.setItem(buttons.getKey() - 1, buttons.getValue().getItemStack());
        }
        if (this.fillEnabled) {
            for (int i = 0; i < this.inventory.getSize(); ++i) {
                ItemStack stack = this.inventory.getItem(i);
                if (stack == null || stack.getType() == Material.AIR) {
                    this.inventory.setItem(i, this.filler);
                }
            }
        }
        this.player.openInventory(this.inventory);
        this.getManager().getMenus().put(this.player.getUniqueId(), this);
    }

    public Player getPlayer() {
        return this.player;
    }

    public void destroy() {
        this.buttons.clear();
        this.inventory.clear();
        this.getManager().getMenus().remove(this.player.getUniqueId());
        if (this.updater != null) {
            this.updater.cancel();
        }
    }

    public abstract Map<Integer, Button> getButtons(Player p0);

    public Map<Integer, Button> getButtons() {
        return this.buttons;
    }

    public void update() {
        if (!this.player.isOnline()) {
            this.destroy();
            return;
        }
        Map<Integer, Button> buttons = this.getButtons(this.player);
        this.buttons = buttons;
        for (Map.Entry<Integer, Button> entry : buttons.entrySet()) {
            this.inventory.setItem(entry.getKey() - 1, entry.getValue().getItemStack());
        }
        if (this.fillEnabled) {
            for (int i = 0; i < this.inventory.getSize(); ++i) {
                ItemStack stack = this.inventory.getItem(i);
                if (stack == null || stack.getType() == Material.AIR) {
                    this.inventory.setItem(i, this.filler);
                }
            }
        }
    }

}