package dev.razorni.hcfactions.utils.tablist.packet;

import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.extras.nametags.Nametag;
import dev.razorni.hcfactions.extras.nametags.extra.NameVisibility;
import dev.razorni.hcfactions.utils.tablist.TablistManager;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Getter
public abstract class TablistPacket extends Module<TablistManager> {
    protected Player player;

    public TablistPacket(TablistManager tablistManager, Player player) {
        super(tablistManager);
        this.player = player;
    }

    public abstract void update();

    public String getName(int x, int y) {
        char[] array = String.valueOf(y).toCharArray();
        return "§" + x + (y >= 10 ? "§" + array[0] + "§" + array[1] : "§" + array[0]);
    }

    public int calcSlot(int x, int y) {
        return y + ((x == 0) ? 0 : ((x == 1) ? 20 : ((x == 2) ? 40 : 60)));
    }

    public void handleTeams(Player player, String text, int pos) {
        Nametag tag = this.getInstance().getNametagManager().getNametags().get(this.player.getUniqueId());
        if (tag == null) {
            return;
        }
        String name = "00000000000000" + ((pos >= 10) ? Integer.valueOf(pos) : "0" + pos);
        if (text.length() > 16) {
            String text1 = text.substring(0, 16);
            String text2 = text.substring(16);
            if (text1.endsWith("§")) {
                text1 = text1.substring(0, text1.toCharArray().length - 1);
                text2 = StringUtils.left(ChatColor.getLastColors(text1) + "§" + text2, 16);
            } else {
                text2 = StringUtils.left(ChatColor.getLastColors(text1) + text2, 16);
            }
            tag.getPacket().create(name, "", text1, text2, false, NameVisibility.ALWAYS);
        } else {
            tag.getPacket().create(name, "", text, "", false, NameVisibility.ALWAYS);
        }
        tag.getPacket().addToTeam(player, name);
    }
}
