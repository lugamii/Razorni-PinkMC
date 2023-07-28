package dev.razorni.hcfactions.extras.trade;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.razorni.hcfactions.utils.menuapi.CC;
import dev.razorni.hcfactions.utils.menuapi.ItemBuilder;
import dev.razorni.hcfactions.utils.menuapi.menu.Button;
import dev.razorni.hcfactions.utils.menuapi.menu.Menu;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.users.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class TradeMenu extends Menu {

    private final Map<UUID, List<ItemStack>> itemsLeft = Maps.newHashMap();
    private final Map<UUID, List<ItemStack>> itemsRight = Maps.newHashMap();
    private final Player target;
    private final Player trader;

    private int countdown = 4;
    private BukkitRunnable runnable;

    private List<UUID> confirmed = Lists.newArrayList();

    public TradeMenu(Player target, Player trader) {
        this.target = target;
        this.trader = trader;

        openMenu(target);

        itemsLeft.put(trader.getUniqueId(), Lists.newArrayList());
        itemsRight.put(target.getUniqueId(), Lists.newArrayList());

        setAutoUpdate(true);
    }

    @Override
    public int size(Player player) {
        return 9 * 6;
    }

    @Override
    public String getTitle(Player player) {
        return CC.translate("&dTrade with " + target.getName());
    }

    @Override
    public void onOpen(Player player) {
        setClosedByMenu(false);
    }

    @Override
    public void onClose(Player player) {

        if (isClosedByMenu()) {
            return;
        }

        if (getItemsByPlayer(trader) != null) {
            getItemsByPlayer(trader).forEach(item -> {
                if (item != null) {
                    trader.getInventory().addItem(item);
                }
            });
        }

        if (getItemsByPlayer(target) != null) {
            getItemsByPlayer(target).forEach(item -> {
                if (item != null) {
                    target.getInventory().addItem(item);
                }
            });
        }

        if (player == target) {
            setClosedByMenu(true);
            trader.closeInventory();
        } else {
            setClosedByMenu(true);
            target.closeInventory();
        }

        target.sendMessage(CC.translate("&cTrade has been canceled."));
        trader.sendMessage(CC.translate("&cTrade has been canceled."));

        confirmed.clear();
        itemsRight.clear();
        itemsLeft.clear();
        if (runnable != null) {
            runnable.cancel();
        }
    }

    public List<ItemStack> getItemsByPlayer(Player player) {
        if (itemsRight.containsKey(player.getUniqueId())) {
            return itemsRight.get(player.getUniqueId());
        }

        return itemsLeft.get(player.getUniqueId());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        for (int i = 0; i < 9; i++) {
            if (runnable != null) {
                buttons.put(i,
                        Button.fromItem(new ItemBuilder(Material.STAINED_GLASS_PANE)
                                .name(" ")
                                .durability((short) 5)
                                .amount(countdown)
                                .build()));
            } else {
                buttons.put(i, Button.fromItem(new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .name(" ")
                        .durability((short) 7)
                        .build()));
            }
        }

        for (int i = 0; i < 6; i++) {
            if (runnable == null) {
                buttons.put(getSlot(4, i),
                        Button.fromItem(
                                new ItemBuilder(Material.STAINED_GLASS_PANE)
                                        .name(" ").durability((short) 7)
                                        .amount(1)
                                        .build()));
            } else {
                buttons.put(getSlot(4, i),
                        Button.fromItem(
                                new ItemBuilder(Material.STAINED_GLASS_PANE)
                                        .name(" ").durability((short) 5)
                                        .amount(countdown)
                                        .build()));
            }
        }

        User traderProfiler = HCF.getPlugin().getUserManager().getByUUID(trader.getUniqueId());

        buttons.put(3, Button.fromItem(
                new ItemBuilder(Material.SKULL_ITEM).durability((short) 3).skull(trader.getName()).name("&d" + trader.getName() + "'s Balance")
                        .addToLore(CC.translate("&fBalance: &d$" + traderProfiler.getBalance())).build()));

        buttons.put(4, new Button() {
            public String getName(Player player) {
                return "&cConfirmation";
            }

            public List<String> getDescription(Player player) {
                return Lists.newArrayList(
                        "",
                        "&fBoth players have to confirm the trade.",
                        "",
                        "&d" + trader.getName() + ": " + (confirmed.contains(trader.getUniqueId())
                                ? "&aConfirmed" : "&cPending..."),
                        "&d" + target.getName() + ": " + (confirmed.contains(target.getUniqueId())
                                ? "&aConfirmed" : "&cPending..."),
                        "",
                        "&dClick to confirm."
                );
            }

            public Material getMaterial(Player player) {
                return Material.EMPTY_MAP;
            }

            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(getMaterial(player)).lore(getDescription(player)).name(getName(player)).build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                if (confirmed.contains(player.getUniqueId())) {
                    player.sendMessage(CC.translate("&cYou have already confirmed the trade!"));
                    return;
                }

                confirmed.add(player.getUniqueId());

                setClosedByMenu(true);
                player.playSound(target.getLocation(), Sound.CLICK, 1, 1);
                if (confirmed.size() == 2) {
                    runnable = new BukkitRunnable() {
                        @Override
                        public void run() {

                            if (countdown == 1) {
                                User targetProfile = HCF.getPlugin().getUserManager().getByUUID(target.getUniqueId());
                                User traderProfile =  HCF.getPlugin().getUserManager().getByUUID(trader.getUniqueId());

                                itemsLeft.get(trader.getUniqueId()).forEach(item -> {
                                        target.getInventory().addItem(item);
                                });

                                itemsRight.get(target.getUniqueId()).forEach(item -> {
                                        trader.getInventory().addItem(item);
                                });


                                trader.sendMessage(CC.translate("&aYou have succesfully traded."));
                                target.sendMessage(CC.translate("&aYou have succesfully traded."));

                                setClosedByMenu(true);

                                target.playSound(target.getLocation(), Sound.LEVEL_UP, 1, 1);
                                trader.playSound(trader.getLocation(), Sound.LEVEL_UP, 1, 1);

                                target.closeInventory();
                                trader.closeInventory();
                                cancel();
                                return;
                            }

                            target.playSound(target.getLocation(), Sound.NOTE_PLING, 1, 1);
                            trader.playSound(trader.getLocation(), Sound.NOTE_PLING, 1, 1);

                            countdown -= 1;
                        }
                    };

                    runnable.runTaskTimer(HCF.getPlugin(), 0, 20);
                }
            }
        });

        User targetProfile = HCF.getPlugin().getUserManager().getByUUID(target.getUniqueId());

        buttons.put(5, Button.fromItem(
                new ItemBuilder(Material.SKULL_ITEM).durability((short) 3).skull(target.getName()).name("&d" + target.getName() + "'s Balance")
                        .addToLore(CC.translate("&fBalance: &d$" + targetProfile.getBalance())).build()));

        buttons.put(0, new Button() {

            public String getName(Player player) {
                return "&dCancel Trade";
            }

            public List<String> getDescription(Player player) {
                return Lists.newArrayList("");
            }

            public Material getMaterial(Player player) {
                return Material.REDSTONE;
            }

            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(getMaterial(player)).name(getName(player)).build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                target.closeInventory();
                trader.closeInventory();
                trader.sendMessage(CC.RED + "Trade has been cancelled.");
                target.sendMessage(CC.RED + "Trade has been cancelled.");
            }
        });

        int maxLeftX = 3;

        int x = 0;
        int y = 1;
        for (List<ItemStack> items : itemsLeft.values()) {
            for (ItemStack item : items) {
                if (item != null) {
                    buttons.put(getSlot(x++, y), new ItemButton(item));

                    if (x > maxLeftX) {
                        x = 0;
                        y++;
                    }
                }
            }
        }

        int maxRightX = 8;
        x = 5;
        y = 1;

        for (List<ItemStack> items : itemsRight.values()) {
            for (ItemStack item : items) {
                if (item != null) {
                    buttons.put(getSlot(x++, y), new ItemButton(item));

                    if (x > maxRightX) {
                        x = 5;
                        y++;
                    }
                }
            }
        }

        return buttons;
    }

    @RequiredArgsConstructor
    public class ItemButton extends Button {

        private final ItemStack item;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(item).type(item.getType()).durability(item.getDurability()).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {

            List<ItemStack> items = getItemsByPlayer(player);

            if (items.contains(item)) {
                items.remove(item);

                player.getInventory().addItem(item);
            }

            confirmed.clear();

            setClosedByMenu(true);
        }
    }
}