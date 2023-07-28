package dev.razorni.core.profile.grant.menu;

import dev.razorni.core.database.redis.packets.GrantAddPacket;
import dev.razorni.core.extras.rank.Rank;
import dev.razorni.core.extras.rank.comparator.RankComparator;
import dev.razorni.core.util.BukkitUtils;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.ItemBuilder;
import dev.razorni.core.util.duration.Duration;
import dev.razorni.core.util.menu.Button;
import dev.razorni.core.util.menu.menus.ConfirmMenu;
import dev.razorni.core.util.menu.pagination.PaginatedMenu;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.profile.grant.Grant;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class RankSelectionMenu extends PaginatedMenu
{
    private Profile profile;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return CC.GOLD + "Grant: " + profile.getUsername();
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        Rank.getRanks().values().stream().sorted(new RankComparator()).collect(Collectors.toList()).forEach(rank -> buttons.put(buttons.size(), new RankDisplayButton(rank, this.profile)));
        return buttons;
    }

    public RankSelectionMenu(Profile profile) {
        this.profile = profile;
    }

    @Override
    public int size(Player player) {
        return size(getButtons(player));
    }

    private static class RankDisplayButton extends Button
    {
        private Rank rank;
        private Profile targetData;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add(CC.SB_BAR);
            lore.add(ChatColor.WHITE + "Click to grant " + this.targetData.getColoredUsername() + ChatColor.WHITE + " the " + this.rank.getColor() + this.rank.getDisplayName() + ChatColor.WHITE + " rank.");
            lore.add(CC.SB_BAR);
            ChatColor chatColor = ChatColor.getByChar(this.rank.getColor().getChar());
            int color = BukkitUtils.toDyeColor(chatColor);
            return new ItemBuilder(Material.WOOL).name(ChatColor.GOLD + this.rank.getDisplayName()).durability(color).lore(lore).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();

            Rank senderRank = Profile.getByUuid(player.getUniqueId()).getActiveRank();
            if (senderRank.getWeight() < rank.getWeight()) {
                player.sendMessage(CC.translate("&cNo permission."));
                return;
            }

            if (!player.hasPermission("gravity.command.grant." + rank.getDisplayName())) {
                player.sendMessage(CC.translate("&cNo permission to grant that rank to that player."));
                return;
            }

            Button button = new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(Material.PAPER).name(CC.YELLOW + "Are you sure you want to grant " + RankDisplayButton.this.targetData.getUsername() + " " + RankDisplayButton.this.rank.getDisplayName() + "?").lore(Arrays.asList(CC.SB_BAR, CC.YELLOW + "Rank: " + RankDisplayButton.this.rank.getDisplayName(), CC.SB_BAR, CC.YELLOW + "Are you sure you want", CC.YELLOW + "grant " + RankDisplayButton.this.rank.getDisplayName(), CC.YELLOW + "to " + RankDisplayButton.this.targetData.getUsername() + "?", CC.SB_BAR)).build();
                }
            };

            Button[] middleButtons = { button, button, button };
            final Grant[] grant = new Grant[1];
            new ConfirmMenu("Confirm grant?", data -> {
                if (data) {
                    player.sendMessage(CC.GREEN + "You have updated " + this.targetData.getUsername() + CC.GREEN + " rank to: " + this.rank.getDisplayName());
                    grant[0] = new Grant(UUID.randomUUID(), this.rank, player.getUniqueId(), System.currentTimeMillis(), "Granted", Duration.fromString("perm").getValue());

//                    JsonBuilder builder = new JsonBuilder();
//                    builder.addProperty("playerUUID", this.targetData.getUuid().toString());
//                    builder.addProperty("grantUUID", grant[0].getUuid().toString());
//                    builder.addProperty("grantRank", grant[0].getRank().getDisplayName());
//                    builder.addProperty("grantAddedByUUID", grant[0].getAddedBy().toString());
//                    builder.addProperty("grantAddedAt", grant[0].getAddedAt());
//                    builder.addProperty("grantAddedReason", grant[0].getAddedReason());
//                    builder.addProperty("duration", grant[0].getDuration());

//                    new GrantAddPacket(builder).send();
                    new GrantAddPacket(this.targetData.getUuid(), grant[0]).send();
                }
                else {
                    player.sendMessage(CC.RED + "Cancelled the grant procedure for " + this.targetData.getPlayer().getName() + '.');
                }
            }, true, middleButtons).openMenu(player);
        }

        public RankDisplayButton(Rank rank, Profile targetData) {
            this.rank = rank;
            this.targetData = targetData;
        }
    }
}

