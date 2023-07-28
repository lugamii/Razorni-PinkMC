package dev.razorni.hcfactions.teams.type;

import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.TeamManager;
import dev.razorni.hcfactions.teams.claims.Claim;
import dev.razorni.hcfactions.teams.enums.TeamType;
import dev.razorni.hcfactions.teams.extra.TeamChest;
import dev.razorni.hcfactions.utils.ItemBuilder;
import dev.razorni.hcfactions.utils.ItemUtils;
import dev.razorni.hcfactions.utils.Serializer;
import dev.razorni.hcfactions.utils.Utils;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Getter
public class CitadelTeam extends Team {
    private final List<Location> chests;
    private final List<TeamChest> randomItems;

    public CitadelTeam(TeamManager manager, Map<String, Object> map) {
        super(manager, map, true, TeamType.CITADEL);
        this.randomItems = new ArrayList<>();
        this.chests = Utils.createList(map.get("chests"), String.class).stream().map(Serializer::deserializeLoc).collect(Collectors.toList());
        this.load();
    }

    public CitadelTeam(TeamManager manager, String name) {
        super(manager, name, UUID.randomUUID(), true, TeamType.CITADEL);
        this.randomItems = new ArrayList<>();
        this.chests = new ArrayList<>();
        this.load();
    }

    public void saveBlocks() {
        for (Claim claim : this.claims) {
            for (Block block : claim) {
                if (block.getType() == Material.AIR) {
                    continue;
                }
                if (!block.getType().name().contains("CHEST") || this.chests.contains(block.getLocation())) {
                    continue;
                }
                this.chests.add(block.getLocation());
            }
            this.save();
        }
    }

    private void addRandomItem(Chest chest) {
        if (this.randomItems.isEmpty()) {
            return;
        }
        chest.getInventory().clear();
        int min = this.getConfig().getInt("CITADEL.CHEST_CONFIG.MIN_ITEM_AMOUNT");
        int max = this.getConfig().getInt("CITADEL.CHEST_CONFIG.MAX_ITEM_AMOUNT");
        int random = ThreadLocalRandom.current().nextInt(max);
        int sel = Math.max(random, min);
        List<TeamChest> chests = new ArrayList<>();
        for (TeamChest cc : this.randomItems) {
            for (int i = 0; i < cc.getPercentage(); ++i) {
                chests.add(cc);
            }
        }
        for (int i = 0; i < sel; ++i) {
            int cMin = ThreadLocalRandom.current().nextInt(chests.size());
            int cMax = ThreadLocalRandom.current().nextInt(chest.getInventory().getSize());
            ItemStack stack = chest.getInventory().getItem(cMax);
            if (stack != null) {
                --i;
            } else {
                chest.getInventory().setItem(cMax, chests.get(cMin).getItemStack());
            }
        }
        chests.clear();
        chest.update(true);
    }

    @Override
    public String getDisplayName(Player player) {
        return this.getTeamConfig().getString("SYSTEM_TEAMS.CITADEL") + super.getDisplayName(player);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("chests", this.chests.stream().map(Serializer::serializeLoc).collect(Collectors.toList()));
        return map;
    }

    public void resetBlocks() {
        Iterator<Location> chests = this.chests.iterator();
        while (chests.hasNext()) {
            Location location = chests.next();
            if (!(location.getBlock().getState() instanceof Chest)) {
                chests.remove();
            } else {
                Chest chest = (Chest) location.getBlock().getState();
                this.addRandomItem(chest);
            }
        }
    }

    private void load() {
        for (String s : this.getConfig().getStringList("CITADEL.CHEST_CONFIG.RANDOM_ITEMS")) {
            String[] array = s.split(", ");
            if (s.startsWith("ABILITY")) {
                String ability = array[0].split(":")[1].toUpperCase();
                ItemStack stack = this.getInstance().getAbilityManager().getAbility(ability).getItem().clone();
                stack.setAmount(Integer.parseInt(array[1]));
                this.randomItems.add(new TeamChest(stack, Double.parseDouble(array[2].replaceAll("%", ""))));
                continue;
            }
            ItemBuilder builder = new ItemBuilder(ItemUtils.getMat(array[0]), Integer.parseInt(array[1])).data(this.getManager(), Short.parseShort(array[2]));
            if (!array[3].equalsIgnoreCase("NONE")) {
                builder.setName(array[3]);
            }
            if (!array[4].equalsIgnoreCase("NONE")) {
                for (String sName : array[4].split(";")) {
                    String[] enchantment = sName.split(":");
                    builder.addUnsafeEnchantment(Enchantment.getByName(enchantment[0]), Integer.parseInt(enchantment[1]));
                }
            }
            if (!array[5].equalsIgnoreCase("NONE")) {
                for (String loreLine : array[5].split(";")) {
                    builder.addLoreLine(loreLine);
                }
            }
            this.randomItems.add(new TeamChest(builder.toItemStack(), Double.parseDouble(array[6].replaceAll("%", ""))));
        }
    }
}
