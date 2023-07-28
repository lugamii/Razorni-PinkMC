package dev.razorni.hcfactions.utils.menuapi.menu;

import com.google.common.base.Preconditions;
import dev.razorni.hcfactions.HCF;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Menu {
    private final ConcurrentHashMap<Integer, Button> buttons=new ConcurrentHashMap<>();
    private boolean autoUpdate=false;
    private boolean updateAfterClick=true;
    private boolean closedByMenu = false;
    private boolean placeholder=false;
    private boolean noncancellingInventory=false;
    private String staticTitle=null;
    public static Map<String, Menu> currentlyOpenedMenus = new HashMap<>();
    public static Map<String, BukkitRunnable> checkTasks = new HashMap<>();

    private Inventory createInventory(Player player) {
        Map<Integer, Button> invButtons=this.getButtons(player);
        Inventory inv=Bukkit.createInventory(player, size(player), this.getTitle(player));
        for ( Map.Entry<Integer, Button> buttonEntry : invButtons.entrySet() ) {
            this.buttons.put(buttonEntry.getKey(), buttonEntry.getValue());
            inv.setItem(buttonEntry.getKey(), buttonEntry.getValue().getButtonItem(player));
        }
        if (this.isPlaceholder()) {
            Button placeholder=Button.placeholder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15));
            for ( int index=0; index < size(player); ++index ) {
                if (invButtons.get(index) != null) continue;
                this.buttons.put(index, placeholder);
                inv.setItem(index, placeholder.getButtonItem(player));
            }
        }
        return inv;
    }

    public Menu() {
    }

    public Menu(String staticTitle) {
        this.staticTitle=(String) Preconditions.checkNotNull((Object) staticTitle);
    }

    public void openMenu(Player player) {

        Inventory inv=this.createInventory(player);
        try {
            player.openInventory(inv);
            this.update(player);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void update(final Player player) {
        Menu.cancelCheck(player);
        currentlyOpenedMenus.put(player.getName(), this);
        this.onOpen(player);
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    Menu.cancelCheck(player);
                    currentlyOpenedMenus.remove(player.getName());
                }
                try {
                    if (Menu.this.isAutoUpdate()) {
                        player.getOpenInventory().getTopInventory().setContents(Menu.this.createInventory(player).getContents());
                    }
                } catch (IllegalArgumentException ignored) {
                    openMenu(player);
                }
            }
        };
        runnable.runTaskTimer(HCF.getPlugin(), 10L, 10L);
        checkTasks.put(player.getName(), runnable);
    }

    public static void cancelCheck(Player player) {
        if (checkTasks.containsKey(player.getName())) {
            checkTasks.remove(player.getName()).cancel();
        }
    }

    public int getSlot(int x, int y) {
        return 9 * y + x;
    }

    public String getTitle(Player player) {
        return this.staticTitle;
    }

    public abstract Map<Integer, Button> getButtons(Player player);

    public abstract int size(Player player);

    public void onOpen(Player player) {
    }

    public void onClose(Player player) {
    }

    public ConcurrentHashMap<Integer, Button> getButtons() {
        return this.buttons;
    }

    public boolean isAutoUpdate() {
        return this.autoUpdate;
    }

    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate=autoUpdate;
    }

    public boolean isClosedByMenu() {
        return this.closedByMenu;
    }

    public void setClosedByMenu(boolean closedByMenu) {
        this.closedByMenu=closedByMenu;
    }

    public boolean isUpdateAfterClick() {
        return this.updateAfterClick;
    }

    public void setUpdateAfterClick(boolean updateAfterClick) {
        this.updateAfterClick=updateAfterClick;
    }

    public boolean isPlaceholder() {
        return this.placeholder;
    }

    public void setPlaceholder(boolean placeholder) {
        this.placeholder=placeholder;
    }

    public boolean isNoncancellingInventory() {
        return this.noncancellingInventory;
    }

    public void setNoncancellingInventory(boolean noncancellingInventory) {
        this.noncancellingInventory=noncancellingInventory;
    }

    public int size(Map<Integer, Button> buttons) {
        int highest = 0;
        Iterator var3 = buttons.keySet().iterator();

        while(var3.hasNext()) {
            int buttonValue = (Integer)var3.next();
            if (buttonValue > highest) {
                highest = buttonValue;
            }
        }

        return (int)(Math.ceil((double)(highest + 1) / 9.0D) * 9.0D);
    }
}

