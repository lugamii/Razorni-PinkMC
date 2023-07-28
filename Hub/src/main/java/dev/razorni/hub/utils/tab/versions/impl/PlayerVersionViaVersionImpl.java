package dev.razorni.hub.utils.tab.versions.impl;

import dev.razorni.hub.utils.tab.versions.module.IPlayerVersion;
import dev.razorni.hub.utils.tab.versions.module.PlayerVersion;
import org.bukkit.entity.Player;
import us.myles.ViaVersion.api.Via;

/**
 * Created By LeandroSSJ
 * Created on 22/09/2021
 */
public class PlayerVersionViaVersionImpl implements IPlayerVersion
{
    @Override
    public PlayerVersion getPlayerVersion(Player player) {
        return PlayerVersion.getVersionFromRaw(Via.getAPI().getPlayerVersion(player));
    }
}
