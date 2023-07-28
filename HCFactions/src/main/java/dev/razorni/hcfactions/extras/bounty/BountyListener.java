package dev.razorni.hcfactions.extras.bounty;

import org.bukkit.event.Listener;

public class BountyListener implements Listener {

/*    public static UUID currentBountyPlayer;
    boolean pickingNewBounty;
    public long lastPositionBroadcastMessage;
    public long lastSuitablePositionTime;
    public int secondsUnsuitable;

    public BountyListener() {
        this.pickingNewBounty = false;
        this.lastPositionBroadcastMessage = -1L;
        this.lastSuitablePositionTime = -1L;
        this.secondsUnsuitable = 0;
        FrozenCommandHandler.registerPackage(HCF.getPlugin(), "dev.razorni.hcfactions.extras.bounty.command");
        Bukkit.getScheduler().runTaskTimerAsynchronously(HCF.getPlugin(), this::checkBounty, 40L, 40L);
    }

    private void checkBounty() {
        Player targetBountyPlayer = (currentBountyPlayer == null) ? null :
                Bukkit.getPlayer(currentBountyPlayer);
        if ((targetBountyPlayer == null || !targetBountyPlayer.isOnline()) && !this.pickingNewBounty) {
            this.newBounty();
            return;
        }
        if (!this.check(targetBountyPlayer)) {
            if (1000L < System.currentTimeMillis() - this.lastSuitablePositionTime && 30 <= this.secondsUnsuitable++) {
                currentBountyPlayer = null;
                this.secondsUnsuitable = 0;
                this.newBounty();
            }
        } else {
            this.lastSuitablePositionTime = System.currentTimeMillis();
            this.secondsUnsuitable = 0;
        }
        this.checkBroadcast();
    }

    private void newBounty() {
        this.pickingNewBounty = true;
        if (Bukkit.getOnlinePlayers().size() < 6) {
            return;
        }
        broadcast(HCF.getPlugin().getConfig().getString("BOUNTY.NEXT"));
        Bukkit.getScheduler().runTaskLaterAsynchronously(HCF.getPlugin(), this::pickBounty, 180 * 20);
    }

    private void pickBounty() {
        List<Player> suitablePlayers = Bukkit.getOnlinePlayers().stream().filter(this::check).collect(Collectors.toList());
        if (suitablePlayers.isEmpty()) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(HCF.getPlugin(),
                    this::pickBounty, 180 * 20);
            return;
        }
        if (!this.pickingNewBounty) {
            return;
        }
        Player bountyPlayer =
                suitablePlayers.get(
                        HCF.RANDOM.nextInt(
                                suitablePlayers.size()));
        this.pickingNewBounty = false;
        currentBountyPlayer = bountyPlayer.getUniqueId();
        for (String s : HCF.getPlugin().getConfig().getStringList("BOUNTY.PLACED")) {
            broadcast(CC.translate(s).replaceAll("%player%", Core.getInstance().getCoreAPI().getRankColor(bountyPlayer) + bountyPlayer.getName()));
        }
    }

    private void checkBroadcast() {
        Player player = (currentBountyPlayer == null) ? null :
                Bukkit.getPlayer(currentBountyPlayer);
        if (player == null) {
            return;
        }
        if (!this.check(player)) {
            return;
        }
        if (30000L <= System.currentTimeMillis() - this.lastPositionBroadcastMessage) {
            for (String s : HCF.getPlugin().getConfig().getStringList("BOUNTY.SPOTTED")) {
                Bukkit.getServer().broadcastMessage(CC.translate(s).replaceAll("%z%", String.valueOf(player.getLocation().getBlockZ())).replaceAll("%y%", String.valueOf(player.getLocation().getBlockY())).replaceAll("%x%", String.valueOf(player.getLocation().getBlockX())).replaceAll("%player%",  Core.getInstance().getCoreAPI().getRankColor(player) + player.getName()));
            }
            this.lastPositionBroadcastMessage = System.currentTimeMillis();
        }
    }

    private boolean check(Player player) {
        if (player == null) {
            return false;
        }
        if (player.getGameMode() == GameMode.CREATIVE) {
            return false;
        }
        if (150.0 <= player.getLocation().getY()) {
            return false;
        }
        if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            return false;
        }
        Team cTeam = HCF.getPlugin().getTeamManager().getClaimManager().getTeam(player.getLocation());
        return (!(cTeam instanceof SafezoneTeam) &&
                500.0 >= Math.abs(player.getLocation().getX()) &&
                500.0 >= Math.abs(player.getLocation().getZ()));
    }

    public static void broadcast(String... messages) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (String message : messages) {
                player.sendMessage(CC.translate(message));
            }
        }
        for (String message2 : messages) {
            Bukkit.getConsoleSender().sendMessage(CC.translate(message2));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onKill(PlayerDeathEvent event) {
        Player died = event.getEntity();
        Player killer = event.getEntity().getKiller();
        if (!died.getUniqueId().equals(currentBountyPlayer)) {
            return;
        }
        if (killer != null) {
            currentBountyPlayer = null;
            Bukkit.broadcastMessage(CC.translate(HCF.getPlugin().getConfig().getString("BOUNTY.BOUNTY-DEAD")
                    .replace("%death%", Core.getInstance().getCoreAPI().getRankColor(died) + died.getName())
                    .replace("%killer%", Core.getInstance().getCoreAPI().getRankColor(killer) + killer.getName())));
            for (String s : HCF.getPlugin().getConfig().getStringList("BOUNTY.REWARDS")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replaceAll("%player%", killer.getName()));
            }
        } else {
            currentBountyPlayer = null;
        }
    }

    public UUID getCurrentBountyPlayer() {
        return currentBountyPlayer;
    }

 */

}
