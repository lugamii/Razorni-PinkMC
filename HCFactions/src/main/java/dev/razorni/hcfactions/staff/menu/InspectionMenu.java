package dev.razorni.hcfactions.staff.menu;

import dev.razorni.hcfactions.extras.framework.menu.Menu;
import dev.razorni.hcfactions.extras.framework.menu.MenuManager;
import dev.razorni.hcfactions.extras.framework.menu.button.Button;
import dev.razorni.hcfactions.users.User;
import dev.razorni.hcfactions.utils.Formatter;
import dev.razorni.hcfactions.utils.ItemBuilder;
import dev.razorni.hcfactions.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;

public class InspectionMenu extends Menu {
    private final Player target;
    private final ItemStack pane;

    public InspectionMenu(MenuManager manager, Player player, Player target) {
        super(manager, player, manager.getConfig().getString("STAFF_MODE.INSPECTION_MENU.TITLE"), manager.getConfig().getInt("STAFF_MODE.INSPECTION_MENU.SIZE"), true);
        this.target = target;
        this.pane = new ItemBuilder(ItemUtils.getMat(this.getConfig().getString("STAFF_MODE.INSPECTION_MENU.FILLER.MATERIAL"))).setName(this.getConfig().getString("STAFF_MODE.INSPECTION_MENU.FILLER.NAME")).data(manager, (short) this.getConfig().getInt("STAFF_MODE.INSPECTION_MENU.FILLER.DATA")).toItemStack();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        PlayerInventory inventory = this.target.getInventory();
        for (int i = 0; i < inventory.getContents().length; ++i) {
            int iT = i;
            buttons.put(i + 1, new Button() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    event.setCancelled(true);
                }

                @Override
                public ItemStack getItemStack() {
                    return inventory.getItem(iT);
                }
            });
        }
        buttons.put(this.getConfig().getInt("STAFF_MODE.INSPECTION_MENU.CLOSE_INSPECTION.SLOT"), new Button() {
            @Override
            public void onClick(InventoryClickEvent event) {
                event.setCancelled(true);
                player.closeInventory();
            }

            @Override
            public ItemStack getItemStack() {
                ItemBuilder builder = new ItemBuilder(ItemUtils.getMat(InspectionMenu.this.getConfig().getString("STAFF_MODE.INSPECTION_MENU.CLOSE_INSPECTION.MATERIAL"))).setName(InspectionMenu.this.getConfig().getString("STAFF_MODE.INSPECTION_MENU.CLOSE_INSPECTION.NAME")).setLore(InspectionMenu.this.getConfig().getStringList("STAFF_MODE.INSPECTION_MENU.CLOSE_INSPECTION.LORE")).data(InspectionMenu.this.getManager(), (short) InspectionMenu.this.getConfig().getInt("STAFF_MODE.INSPECTION_MENU.CLOSE_INSPECTION.DATA"));
                return builder.toItemStack();
            }
        });
        buttons.put(this.getConfig().getInt("STAFF_MODE.INSPECTION_MENU.POTIONS_VIEWER.SLOT"), new Button() {
            @Override
            public void onClick(InventoryClickEvent event) {
                event.setCancelled(true);
            }

            @Override
            public ItemStack getItemStack() {
                ItemBuilder builder = new ItemBuilder(ItemUtils.getMat(InspectionMenu.this.getConfig().getString("STAFF_MODE.INSPECTION_MENU.POTIONS_VIEWER.MATERIAL"))).setName(InspectionMenu.this.getConfig().getString("STAFF_MODE.INSPECTION_MENU.POTIONS_VIEWER.NAME")).data(InspectionMenu.this.getManager(), (short) InspectionMenu.this.getConfig().getInt("STAFF_MODE.INSPECTION_MENU.POTIONS_VIEWER.DATA"));
                for (PotionEffect effect : InspectionMenu.this.target.getActivePotionEffects()) {
                    int duration = effect.getDuration();
                    int amplifier = effect.getAmplifier() + 1;
                    long finalDuration = duration / 20L * 1000L;
                    builder.addLoreLine(InspectionMenu.this.getConfig().getString("STAFF_MODE.INSPECTION_MENU.POTIONS_VIEWER.FORMAT").replaceAll("%effect%", effect.getType().getName()).replaceAll("%time%", (duration > 1000000) ? "Permanent" : Formatter.formatMMSS(finalDuration)).replaceAll("%amplifier%", String.valueOf(amplifier)));
                }
                return builder.toItemStack();
            }
        });
        buttons.put(this.getConfig().getInt("STAFF_MODE.INSPECTION_MENU.PLAYER_INFO.SLOT"), new Button() {
            @Override
            public ItemStack getItemStack() {
                User user = InspectionMenu.this.getInstance().getUserManager().getByUUID(InspectionMenu.this.target.getUniqueId());
                ItemBuilder builder = new ItemBuilder(ItemUtils.getMat(InspectionMenu.this.getConfig().getString("STAFF_MODE.INSPECTION_MENU.PLAYER_INFO.MATERIAL"))).setName(InspectionMenu.this.getConfig().getString("STAFF_MODE.INSPECTION_MENU.PLAYER_INFO.NAME").replaceAll("%player%", InspectionMenu.this.target.getName())).data(InspectionMenu.this.getManager(), (short) InspectionMenu.this.getConfig().getInt("STAFF_MODE.INSPECTION_MENU.PLAYER_INFO.DATA"));
                for (String s : InspectionMenu.this.getConfig().getStringList("STAFF_MODE.INSPECTION_MENU.PLAYER_INFO.LORE")) {
                    builder.addLoreLine(s.replaceAll("%kills%", String.valueOf(user.getKills())).replaceAll("%deaths%", String.valueOf(user.getDeaths())).replaceAll("%lives%", String.valueOf(user.getLives())));
                }
                return builder.toItemStack();
            }

            @Override
            public void onClick(InventoryClickEvent event) {
                event.setCancelled(true);
            }
        });
        for (int i = 0; i < 4; ++i) {
            String armor = (i == 0) ? "HELMET" : ((i == 1) ? "CHESTPLATE" : ((i == 2) ? "LEGGINGS" : "BOOTS"));
            buttons.put(this.getConfig().getInt("STAFF_MODE.INSPECTION_MENU." + armor + "_SLOT"), new Button() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    event.setCancelled(true);
                }

                @Override
                public ItemStack getItemStack() {
                    return armor.equals("HELMET") ? inventory.getHelmet() : (armor.equals("CHESTPLATE") ? inventory.getChestplate() : (armor.equals("LEGGINGS") ? inventory.getLeggings() : inventory.getBoots()));
                }
            });
        }
        for (int i = 37; i <= 45; ++i) {
            buttons.put(i, new Button() {
                @Override
                public ItemStack getItemStack() {
                    return InspectionMenu.this.pane;
                }

                @Override
                public void onClick(InventoryClickEvent event) {
                    event.setCancelled(true);
                }
            });
        }
        for (int i = 52; i <= 53; ++i) {
            buttons.put(i, new Button() {
                @Override
                public ItemStack getItemStack() {
                    return InspectionMenu.this.pane;
                }

                @Override
                public void onClick(InventoryClickEvent event) {
                    event.setCancelled(true);
                }
            });
        }
        return buttons;
    }
}
