package dev.razorni.gkits.hook;

import org.bukkit.entity.Player;

public interface GKitPluginHook {

    GKitPluginHook DEFAULT_HOOK = new GKitPluginHook() {
        @Override
        public boolean canUseGKitCommand(Player player) {
            return true;
        }

        @Override
        public int getBalance(Player player) {
            return 0;
        }

        @Override
        public void setBalance(Player player, int balance) {

        }
    };

    boolean canUseGKitCommand(Player player);

    int getBalance(Player player);

    void setBalance(Player player, int balance);

}
