package dev.razorni.core.extras.rank.command;
import dev.razorni.core.util.CC;
import dev.razorni.core.database.redis.packets.rank.RankRenamePacket;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.command.CommandSender;
import dev.razorni.core.util.Locale;
import dev.razorni.core.extras.rank.Rank;

public class RankRenameCommand {

    @Command(names = "rank rename", permission = "gravity.command.rank", async = true, description = "Changes the name of an existing rank.")
    public static void rename(CommandSender sender, @Param(name = "rank") Rank rank, @Param(name = "newName") String newName) {
        if (rank == null){
            sender.sendMessage(CC.RED + Locale.RANK_NOT_FOUND);
            return;
        }

//        new RankUpdatePacket(new JsonBuilder()
//                .addProperty("uuid", rank.getUuid().toString())
//                .addProperty("create", false)
//                .addProperty("addPerm", false)
//                .addProperty("removePerm", false)
//                .addProperty("addInherit", false)
//                .addProperty("rename", true)
//                .addProperty("newName", newName)
//                .addProperty("removeInherit", false)
//                .addProperty("delete", false)
//                .addProperty("name", rank.getDisplayName()))
//                .send();
//
//
        rank.setDisplayName(newName);
        rank.save();

        new RankRenamePacket(rank, newName).send();

        sender.sendMessage(CC.GREEN + "Set rank name to " + newName + '.');
    }
}
