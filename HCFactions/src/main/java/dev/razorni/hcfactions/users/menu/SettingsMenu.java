package dev.razorni.hcfactions.users.menu;

import dev.razorni.hcfactions.extras.framework.menu.Menu;
import dev.razorni.hcfactions.extras.framework.menu.MenuManager;
import dev.razorni.hcfactions.extras.framework.menu.button.Button;
import dev.razorni.hcfactions.users.User;
import dev.razorni.hcfactions.users.settings.UserSetting;
import dev.razorni.hcfactions.utils.ItemBuilder;
import dev.razorni.hcfactions.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class SettingsMenu extends Menu {
    private final Set<String> keys;
    private final List<String> userSettings;

    public SettingsMenu(MenuManager manager, Player player) {
        super(manager, player, manager.getLanguageConfig().getString("SETTINGS_COMMAND.TITLE"), manager.getLanguageConfig().getInt("SETTINGS_COMMAND.SIZE"), false);
        this.keys = this.getLanguageConfig().getConfigurationSection("SETTINGS_COMMAND.ITEMS").getKeys(false);
        this.userSettings = new ArrayList<>(Arrays.asList(UserSetting.values())).stream().map(Enum::name).collect(Collectors.toList());
    }

    private String convert(User user, String input) {
        switch (input) {
            case "SCOREBOARD": {
                return this.convertBoolean(user.isScoreboard());
            }
            case "SCOREBOARD_CLAIM": {
                return this.convertBoolean(user.isScoreboardClaim());
            }
            case "PUBLIC_CHAT": {
                return this.convertBoolean(user.isPublicChat());
            }
            case "COBBLE": {
                return this.convertBoolean(user.isCobblePickup());
            }
            case "FOUND_DIAMOND": {
                return this.convertBoolean(user.isFoundDiamondAlerts());
            }
        }
        return "";
    }

    private String convertBoolean(boolean value) {
        return value ? "ENABLED" : "DISABLED";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> map = new HashMap<>();
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        for (String s : this.userSettings) {
            if (!this.keys.contains(s)) {
                continue;
            }
            map.put(this.getLanguageConfig().getInt("SETTINGS_COMMAND.ITEMS." + s + ".SLOT"), new Button() {
                @Override
                public ItemStack getItemStack() {
                    ItemBuilder builder = new ItemBuilder(ItemUtils.getMat(SettingsMenu.this.getLanguageConfig().getString("SETTINGS_COMMAND.ITEMS." + s + ".MATERIAL")));
                    builder.setName(SettingsMenu.this.getLanguageConfig().getString("SETTINGS_COMMAND.ITEMS." + s + ".NAME"));
                    builder.setLore(SettingsMenu.this.getLanguageConfig().getStringList("SETTINGS_COMMAND.ITEMS." + s + ".LORE_" + SettingsMenu.this.convert(user, s)));
                    return builder.toItemStack();
                }

                @Override
                public void onClick(InventoryClickEvent event) {
                    event.setCancelled(true);
                    switch (s) {
                        case "SCOREBOARD": {
                            user.setScoreboard(!user.isScoreboard());
                            break;
                        }
                        case "SCOREBOARD_CLAIM": {
                            user.setScoreboardClaim(!user.isScoreboardClaim());
                            break;
                        }
                        case "PUBLIC_CHAT": {
                            user.setPublicChat(!user.isPublicChat());
                            break;
                        }
                        case "COBBLE": {
                            user.setCobblePickup(!user.isCobblePickup());
                            break;
                        }
                        case "FOUND_DIAMOND": {
                            user.setFoundDiamondAlerts(!user.isFoundDiamondAlerts());
                        }
                    }
                    SettingsMenu.this.update();
                }
            });
        }
        return map;
    }
}
