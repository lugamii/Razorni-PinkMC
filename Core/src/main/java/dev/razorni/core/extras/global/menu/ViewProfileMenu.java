package dev.razorni.core.extras.global.menu;

import dev.razorni.core.util.CC;
import dev.razorni.core.util.ItemBuilder;
import dev.razorni.core.util.menu.Button;
import dev.razorni.core.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.extras.global.GlobalInfo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 22/07/2021 / 11:42 PM
 * Core / rip.orbit.gravity.profile.global.menu
 */

@AllArgsConstructor
public class ViewProfileMenu extends Menu {

	private final Profile target;
	
	@Override
	public String getTitle(Player player) {
		return CC.translate("&6" + target.getUsername() + "'s Stats");
	}

	@Override
	public int size(Player player) {
		return 36;
	}

	@Override
	public Map<Integer, Button> getButtons(Player p) {
		Map<Integer, Button> buttons = new HashMap<>();
//		Profile profile = Profile.getByUuid(p.getUniqueId());

		buttons.put(4, new Button() {
			@Override
			public ItemStack getButtonItem(Player player) {
				return new ItemBuilder(Material.FEATHER).name(CC.translate("&6Go Back")).lore(CC.translate("&7Click to go back to the main menu.")).build();
			}

			@Override
			public void clicked(Player player, ClickType clickType) {
				new ProfileMainMenu(target).openMenu(player);
			}
		});
		buttons.put(20, new HCFStatsButton(target));
		buttons.put(22, new KitsStatsButton(target));
		buttons.put(24, new PracticeStatsButton(target));

		return buttons;
	}

	@Override
	public boolean isPlaceholder() {
		return true;
	}
	
	@AllArgsConstructor
	public static class HCFStatsButton extends Button {

		private final Profile profile;
		
		@Override
		public ItemStack getButtonItem(Player player) {
			GlobalInfo info = profile.getGlobalInfo();

			ItemBuilder builder = new ItemBuilder(Material.DIAMOND_SWORD);
			builder.name(CC.translate("&6&lHCF Statistics"));
			builder.lore(CC.translate(Arrays.asList(
					"",
					"&7┃ &fMaps Played: &6" + info.getHcfMapsPlayed(),
					"&7┃ &fKills: &6" + info.getHcfKills(),
					"&7┃ &fDeaths: &6" + info.getHcfDeaths(),
					"&7┃ &fRaidable Teams: &6" + info.getHcfMadeRaidable(),
					"&7┃ &fActive KillStreak: &6" + info.getHcfCurrentKillstreak(),
					"&7┃ &fHighest KillStreak: &6" + info.getHcfHighestKillstreak(),
					"",
					"&7┃ &fKoTH Captures: &6" + info.getHcfKothCaps(),
					"&7┃ &fConquest Captures: &6" + info.getHcfConquestCaps(),
					"&7┃ &fCitadel Captures: &6" + info.getHcfCitadelCaps(),
					"&7┃ &fPast Teams: " + info.getPastFactions().size()
			)));
			info.getPastFactions().forEach(pastFaction -> {
				builder.lore(CC.translate("&7- " + pastFaction.getFaction()));
			});

			return builder.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {

		}
	}

	@AllArgsConstructor
	public static class PracticeStatsButton extends Button {

		private final Profile profile;

		@Override
		public ItemStack getButtonItem(Player player) {

			GlobalInfo info = profile.getGlobalInfo();

			ItemBuilder builder = new ItemBuilder(Material.IRON_SWORD);
			builder.name(CC.translate("&6&lPractice Statistics"));
			builder.lore(CC.translate(Arrays.asList(
					"",
					"&7┃ &fSeasons Played: &6" + info.getPracticeSeasonsPlayed(),
					"&7┃ &fKills: &6" + info.getPracticeKills(),
					"&7┃ &fDeaths: &6" + info.getPracticeDeaths(),
					"&7┃ &fActive WinStreak: &6" + info.getPracticeCurrentWinstreak(),
					"&7┃ &fHighest WinStreak: &6" + info.getPracticeHighestWinstreak(),
					"",
					"&7┃ &fGames Played: &6" + info.getPracticeGamesPlayed(),
					"&7┃ &fGame Wins: &6" + info.getPracticeWins(),
					"&7┃ &fGame Loses: &6" + info.getPracticeLoses()
			)));

			return builder.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {

		}
	}

	@AllArgsConstructor
	public static class KitsStatsButton extends Button {

		private final Profile profile;

		@Override
		public ItemStack getButtonItem(Player player) {
			
			GlobalInfo info = profile.getGlobalInfo();

			ItemBuilder builder = new ItemBuilder(Material.BOW);
			builder.name(CC.translate("&6&lKits Statistics"));
			builder.lore(CC.translate(Arrays.asList(
					"",
					"&7┃ &fSeasons Played: &6" + info.getKitsSeasonsPlayed(),
					"&7┃ &fKills: &6" + info.getKitsKills(),
					"&7┃ &fDeaths: &6" + info.getKitsDeaths(),
					"&7┃ &fActive KillStreak: &6" + info.getKitsCurrentKillstreak(),
					"&7┃ &fHighest KillStreak: &6" + info.getKitsHighestKillstreak(),
					"",
					"&7┃ &fTournament Played: &6" + info.getKitsTournyPlayed(),
					"&7┃ &fTournament Wins: &6" + info.getKitsTournyWins(),
					"&7┃ &fTournament Loses: &6" + info.getKitsTournyLoses()
			)));

			return builder.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {

		}
	}

}
