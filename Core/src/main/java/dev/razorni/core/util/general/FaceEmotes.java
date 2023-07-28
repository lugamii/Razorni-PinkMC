package dev.razorni.core.util.general;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.UUID;

@AllArgsConstructor
@Getter
public enum FaceEmotes {

    LOVE("Love Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDI3MzdlOTllNGMwNTk2YTM3MTJlNzcxMWJhZWNhZThkMWRkYjc3NGFjMWNmNTMxODk2ODYyMzgwNzUzZTE2In19fQ==")),
    COOL("Cool Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmQyMTc1ZWJlOWFlMGUxYTY1OGQ5YWY4MmRhY2ZiODM2OTA1MmQ4MTIxZDRlYTM4ODY3MzhhMWNjYTUifX19")),
    LAUGHING("Laughing Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY1ZWY4Y2M4YzY0NjFhN2M0NTIwM2IyNDlkMTMwZjA2NjA4NmE5YmI4MDMwN2VjYWVkYWU4MzU2ODAwY2VhNCJ9fX0=")),
    FAKE_HAPPINESS("Fake Happiness Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjBlYzNhODBlZDM1YmQ5YmViN2QyMGNiNzVmMWVjZDViOGFiMGQ1NzZmMWRiNjk5ZjdkZWYxMzEzMWZiYzUifX19")),
    PALE_DEAD("Pale Dead Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDM5YzNkZjdhNjI4YWY4ZDc1MWVjY2ExOTc2NDJjZGMxYTA3YzMwZTMyODliMmQzMjYxZjdhNjVjZjM5NWIifX19")),
    NEUTRAL("Neutral Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjY0NjE0YWQ0YmIyZWI2MWIwNmIxYThiNWQ1N2YwMjQ0OGE5NzVhODIxN2VjMTY1NzFmODdjNDkyMjdjYmQifX19")),
    LICKING_NOSE("Licking Nose Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWM0OWJkNDEzODYwM2E5MDRmYWFmZTEzNjZmNmJmYjczZWJkMTY3NDA1OGE4OTg1NjYyOGViMmM5NWMyMCJ9fX0=")),
    SCREAMING("Screaming Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmUxNmE3YWUxODZjM2NmZWFjMzY0ZWFjMGU4M2QzNTI4NzQxYzNkZDllZjgyNzcwODBlMDNkZWFiYzcxNCJ9fX0=")),
    DISGUST("Disgust Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjA3ZWVmOTFhNDUzYTUxNTE0ODdjOWQ2YjlkNGM0MzRkYjdmOGEwMmE0Y2FmMThlZjZmMzM1ODY3N2Y2In19fQ==")),
    SAD("Sad Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ5NjhhYzVhZjMxNDY4MjZmYTJiMGQ0ZGQxMTRmZGExOTdmOGIyOGY0NzUwNTUzZjNmODg4MzZhMjFmYWM5In19fQ==")),
    COOL_SURPRISED("Cool Surprised Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjg1Yzc4OWIxYmFmZWI2Mjc0ZDVjMzMxNGUwMzMzY2NmNmFiOTJkNzMxMmVmMjE0Zjg5NzkzYzk1OWQyNSJ9fX0=")),
    FLUSTERED("Flustered Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmY3ZTZjMDc5ZWZhNjljYjNhMjNkZDNiMTQ3NjQzYzdjYjVlNWM5MTI5Yjc0YWYwY2FiNDdiMDRmMzU1YSJ9fX0=")),
    SURPRISED("Surprised Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzBlNzgyODVkNWFlZTBiMjg3ODdhZDg4YTVkNThmYjA1Y2NmMjI5MThkYWE1MTZlYWQ4NWE2YmY0ZmUwNjgifX19")),
    TERRIFIED("Terrified Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzEyNDNkNjFhN2Q3ZTA0OGY2NGU3ZjdhMmNkY2NlZTE0ZGUyZGQzNjUxOGVjYzc3MWU5NjZmZGRjOWI5N2JlMiJ9fX0=")),
    SNORING("Snoring Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzZmNTQ5NTk3ZmE1NmJmYjhiZTk3NGZmMmExODg3ZjQ2ODYyM2RiNzc0ZjUxY2E3NjBkZmJhMTcyODQyIn19fQ==")),
    DISSAPOINTED("Dissapointed Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2UxMDZlYzY3Y2IyYTIzMmY0ZjI3OTcxYTUyYjNhYjRiOTUwYTMxZWE0NDZlYTkyZWViMjU5MjYzY2ZkMTE5In19fQ==")),
    SMUG("Smug Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDE2OGI3MTYyODE2MzVjZWFmYzMyNjhkZmE3ZDVmNDY0NjZjODAzMmUxMWMxY2ZiN2RiNzExYTlmNjQ3ZCJ9fX0=")),
    IMPRESSED("Impressed Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDE2ZjNkY2U5NzdkOGI3OTdlMWU0NzZmNWFiOTM2MTllZDJmMmIyMWE0OWFjOTM3NDMxNDBjZjY3YTA4OCJ9fX0=")),
    SMILING("Smiling Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDFhYzIxZDkzY2UxN2YyYjdlZTJlMGUwN2E5ODNlZWI0YTUzOWUzNDFjZTVjNzdjMzZjNzIyZjc3YTIyMzUifX19")),
    CONFIDENT("Confident Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDRjOGNkMzFiYWM2NTdhM2YyNmI1MmUyNjJjODNlYjgzMzhkNTY2NTFmZDIyYzE2MzM1NjVmM2RiYmM0NTc3NyJ9fX0=")),
    BIG_SURPRISE("Big Surprise Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDU4Njg1MjlmYmY0YmU2MjkzNzEyNzViMTEzOGRhYjkyOTU3NjAyMTcxNmVlNzM3ZGIxMjYzNGFhMTI1YWYzIn19fQ==")),
    CRAZY("Crazy Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDViMDU5NWE1MTFjNGIzNzc0MzdhNTE2ZmVmYmVhMmZmY2YzNTU4NWFlNTM0NjViMGU4NjAyNGEyMTVlYiJ9fX0=")),
    HAPPY("Happy Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDczZTcyY2MzNzFkZTI1ZjMzMDU2NjU3NjlkZDdlOWZmMTE2MTY5NTI1MmU3OTk2MTI1ODBkZWVlZGQzIn19fQ==")),
    CRYING("Crying Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzMyZmUxMjFhNjNlYWFiZDk5Y2VkNmQxYWNjOTE3OTg2NTJkMWVlODA4NGQyZjkxMjdkOGEzMTVjYWQ1Y2U0In19fQ==")),
    ANGRY("Angry Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjFkNGJlYTM2NmFjYTU4ZGQ1YjIyZTk0MGJjZGQ0YmE0NWJmODg0MjFmNmQ4MzExNThiODc5ZjJjOGFiY2UxOCJ9fX0=")),
    SCARED("Scared Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTk0ZGY2NDJiMjc2MjYyNDQ4MWE1YjJiZTIxYTgxM2Q1ZWE0MTJmNTIxZDEwYzM2M2E3MTRlNDc0OGE5MjY5In19fQ==")),
    THINKING("Thinking Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWZlYTk5YWQ5NWI1NzAxNzVmZGEyNWMzYTY5Nzg4ZDZhOWI4NTRhYTEzZjhhNWZmNjNmNmVmZWRmNTgxZGZiNiJ9fX0=")),
    SUPER_ANGRY("Super Angry Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGZiOTdiMTdjNjM5NTM5MjY1OGYzMjcxOGFhNDZiZWZhMWMzMWQzNTcyNjUxYzMwZjdkMmJmM2I5M2Y2ZWFkOSJ9fX0=")),
    ANGRY_SAD("Angry Sad Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWJkNTlhMTNlNDY5ZjczYWRhMmFmNmRjOWY1MWI1NDgwY2QzNGE5ODExZjY3OTc1NGMxM2ZmYTVmOWY1ZjYifX19")),
    SLEEPING("Sleeping Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWM0YTQxNTU0MzQyYjJjYmZiZDg5NWU5ZWM3MDg5YWU4NjFmZWM4ZjUxNjkzODIyZWMxZWIzY2EzZjE4In19fQ==")),
    NERVOUS("Nervous Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmNjZDU0NzJhNDZmNDZlNGNkZmRhOWFkZWEyMzIwY2NjZmJhZTExMTk4YjZhYWUxNjNkMTdjNGI1YjQ2NjZkIn19fQ==")),
    WINK("Wink Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjRlYTJkNmY5MzlmZWZlZmY1ZDEyMmU2M2RkMjZmYThhNDI3ZGY5MGIyOTI4YmMxZmE4OWE4MjUyYTdlIn19fQ==")),
    MUSTACHE("Mustache Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzYzNmYyNzI0YWE2YWE0ZGU3YWM0NmMxOWYzYzg0NWZiMTQ4NDdhNTE4YzhmN2UwM2Q3OTJjODJlZmZiMSJ9fX0=")),
    EMBARRASSED("Embarrassed Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjcyMGRmOTExYzA1MjM3NzA2NTQwOGRiNzhhMjVjNjc4Zjc5MWViOTQ0YzA2MzkzNWFlODZkYmU1MWM3MWIifX19")),
    DEAD("Dead Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjM3MWU0ZTFjZjZhMWEzNmZkYWUyNzEzN2ZkOWI4NzQ4ZTYxNjkyOTk5MjVmOWFmMmJlMzAxZTU0Mjk4YzczIn19fQ==")),
    KISSY("Kissy Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTQ1YmQxOGEyYWFmNDY5ZmFkNzJlNTJjZGU2Y2ZiMDJiZmJhYTViZmVkMmE4MTUxMjc3Zjc3OWViY2RjZWMxIn19fQ==")),
    ANGEL("Angel Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2UxZGViYzczMjMxZjhlZDRiNjlkNWMzYWMxYjFmMThmMzY1NmE4OTg4ZTIzZjJlMWJkYmM0ZTg1ZjZkNDZhIn19fQ==")),
    AMAZED("Amazed Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2ZTI2YzQ0NjU5ZTgxNDhlZDU4YWE3OWU0ZDYwZGI1OTVmNDI2NDQyMTE2ZjgxYjU0MTVjMjQ0NmVkOCJ9fX0=")),
    HEART("Heart Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjRlYWY4OTQyOGEzNjQ5YzI2ZWRjMWY3MWNjMTlmMjYzZTlmNGViMzFlZDE4Yzk3Njg2YWFjODJmNzY0MjQyIn19fQ==")),
    POOP("Poop Emote", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjY1MDIzMmMxZDhiMzM3OTY1MDg3NjkzNDQ5YzAyYzk2MGNhNDM5M2IwNDgzNDZhNGYwYjJlMThlNThmYSJ9fX0="));

    private String name;
    private ItemStack item;

    public static String getFromPlayerTextures(Player playerBukkit) {
        EntityPlayer playerNMS = ((CraftPlayer) playerBukkit).getHandle();
        GameProfile profile = playerNMS.getProfile();
        Property property = profile.getProperties().get("textures").iterator().next();
        String texture = property.getValue();
        String signature = property.getSignature();
        return texture + "┃" + signature;
    }

    public static String[] getFromPlayer(Player playerBukkit) {
        EntityPlayer playerNMS = ((CraftPlayer) playerBukkit).getHandle();
        GameProfile profile = playerNMS.getProfile();
        Property property = profile.getProperties().get("textures").iterator().next();
        String texture = property.getValue();
        String signature = property.getSignature();
        return new String[]{texture, signature};
    }

    public static String[] getFromName(String name) {
        try {
            URL url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());
            String uuid = new JsonParser().parse(reader_0).getAsJsonObject().get("id").getAsString();
            URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
            JsonObject textureProperty = new JsonParser().parse(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            String texture = textureProperty.get("value").getAsString();
            String signature = textureProperty.get("signature").getAsString();

            return new String[]{texture, signature};
        } catch (IOException e) {
            System.err.println("Could not get skin data from session servers!");
            e.printStackTrace();
            return null;
        }
    }

    public static String[] getFromUUID(UUID uuid) {
        try {
            URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString() + "?unsigned=false");
            InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());

            // TODO: I do it because it give me a problem of it is null
            JsonElement element = new JsonParser().parse(reader_1);

            if (element == null || !element.isJsonObject()) return null;

            JsonObject jsonObject = element.getAsJsonObject();

            if (jsonObject == null) return null;

            element = jsonObject.get("properties");

            if (element == null) return null;

            JsonArray jsonArray = element.getAsJsonArray();

            if (jsonArray == null) return null;

            element = jsonArray.get(0);

            if (element == null) return null;

            JsonObject textureProperty = element.getAsJsonObject();

            if (textureProperty == null) return null;

            String texture = textureProperty.get("value").getAsString();
            String signature = textureProperty.get("signature").getAsString();

            return new String[]{texture, signature};
        } catch (IOException e) {
            System.err.println("Could not get skin data from session servers!");
            return null;
        }
    }

    public static ItemStack createSkull(String texture) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        if (texture.isEmpty())
            return head;

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        profile.getProperties().put("textures", new Property("textures", texture));

        try {
            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException error) {
            error.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
    }
}