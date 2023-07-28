package dev.razorni.core.extras.tag;

import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.FrozenCommandHandler;
import dev.razorni.core.Core;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TagHandler {

    private final Core plugin = Core.getInstance();

    private final MongoCollection<Document> customCollection, symbolcollection, textcollection, countrycollection;

    @Getter
    private final List<Tag> customtags;
    @Getter
    private final List<Tag> symboltags;
    @Getter
    private final List<Tag> texttags;
    @Getter
    private final List<Tag> countrytags;

    public TagHandler() {
        this.customtags = new ArrayList<>();
        this.symboltags = new ArrayList<>();
        this.texttags = new ArrayList<>();
        this.countrytags = new ArrayList<>();
        this.customCollection = plugin.getMongoHandler().getMongoDatabase().getCollection("tags1");
        this.symbolcollection = plugin.getMongoHandler().getMongoDatabase().getCollection("tags2");
        this.textcollection = plugin.getMongoHandler().getMongoDatabase().getCollection("tags3");
        this.countrycollection = plugin.getMongoHandler().getMongoDatabase().getCollection("tags4");

        FrozenCommandHandler.registerParameterType(Tag.class, new TagParameterType());
        loadTags();
    }

    public Tag customgetTagByName(String search) {
        return this.customtags.stream().filter(tag -> tag.getName().equalsIgnoreCase(search)).findFirst().orElse(null);
    }
    public Tag symbolgetTagByName(String search) {
        return this.symboltags.stream().filter(tag -> tag.getName().equalsIgnoreCase(search)).findFirst().orElse(null);
    }
    public Tag textgetTagByName(String search) {
        return this.texttags.stream().filter(tag -> tag.getName().equalsIgnoreCase(search)).findFirst().orElse(null);
    }
    public Tag countrygetTagByName(String search) {
        return this.countrytags.stream().filter(tag -> tag.getName().equalsIgnoreCase(search)).findFirst().orElse(null);
    }

    public Tag getDefault() {
        return null;

    }

    private void loadTags() {
        for (Document document : customCollection.find()) {
            Tag tag = new Tag(document.getString("name"));
            tag.setPrefix(CC.translate(document.getString("prefix")));
            tag.setCategory("custom");
            tag.setWeight(document.getInteger("weight"));
            customsaveTag(tag);
        }
        for (Document document : symbolcollection.find()) {
            Tag tag = new Tag(document.getString("name"));
            tag.setPrefix(CC.translate(document.getString("prefix")));
            tag.setCategory("symbol");
            tag.setWeight(document.getInteger("weight"));
            symbolsaveTag(tag);
        }
        for (Document document : textcollection.find()) {
            Tag tag = new Tag(document.getString("name"));
            tag.setPrefix(CC.translate(document.getString("prefix")));
            tag.setCategory("text");
            tag.setWeight(document.getInteger("weight"));
            textsaveTag(tag);
        }
        for (Document document : countrycollection.find()) {
            Tag tag = new Tag(document.getString("name"));
            tag.setPrefix(CC.translate(document.getString("prefix")));
            tag.setCategory("country");
            tag.setWeight(document.getInteger("weight"));
            countrysaveTag(tag);
        }
    }

    public Optional<Document> customgetTagDocFromDb(String name) {
        return Optional.ofNullable(customCollection.find(Filters.eq("name", name)).first());
    }
    public Optional<Document> symbolgetTagDocFromDb(String name) {
        return Optional.ofNullable(symbolcollection.find(Filters.eq("name", name)).first());
    }
    public Optional<Document> textgetTagDocFromDb(String name) {
        return Optional.ofNullable(textcollection.find(Filters.eq("name", name)).first());
    }
    public Optional<Document> countrygetTagDocFromDb(String name) {
        return Optional.ofNullable(countrycollection.find(Filters.eq("name", name)).first());
    }


    public void customloadPrefixByName(String name) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            customgetTagDocFromDb(name).ifPresent(document -> {
                Tag tag = customgetTagByName(name);

                if (tag != null) {
                    tag.setPrefix(ChatColor.translateAlternateColorCodes('&', document.getString("prefix")));
                    tag.setWeight(document.getInteger("weight"));
                } else {
                    tag = new Tag(document.getString("name"));
                    tag.setPrefix(ChatColor.translateAlternateColorCodes('&', document.getString("prefix")));
                    tag.setWeight(document.getInteger("weight"));
                    customtags.remove(tag);
                    customtags.add(tag);
                }
            });
        });
    }

    public void customsaveTag(Tag tag) {
        if (!customtags.contains(tag)) {
            customtags.add(tag);
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Document document = new Document();
            document.put("name", tag.getName());
            document.put("category", tag.getCategory());
            document.put("prefix", tag.getPrefix().replace(ChatColor.COLOR_CHAR, '&'));
            document.put("weight", tag.getWeight());

            customCollection.replaceOne(Filters.eq("name", tag.getName()), document, new ReplaceOptions().upsert(true));
        });
    }
    public void textsaveTag(Tag tag) {
        if (!texttags.contains(tag)) {
            texttags.add(tag);
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Document document = new Document();
            document.put("name", tag.getName());
            document.put("category", tag.getCategory());
            document.put("prefix", tag.getPrefix().replace(ChatColor.COLOR_CHAR, '&'));
            document.put("weight", tag.getWeight());

            textcollection.replaceOne(Filters.eq("name", tag.getName()), document, new ReplaceOptions().upsert(true));
        });
    }
    public void symbolsaveTag(Tag tag) {
        if (!symboltags.contains(tag)) {
            symboltags.add(tag);
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Document document = new Document();
            document.put("name", tag.getName());
            document.put("category", tag.getCategory());
            document.put("prefix", tag.getPrefix().replace(ChatColor.COLOR_CHAR, '&'));
            document.put("weight", tag.getWeight());

            symbolcollection.replaceOne(Filters.eq("name", tag.getName()), document, new ReplaceOptions().upsert(true));
        });
    }
    public void countrysaveTag(Tag tag) {
        if (!countrytags.contains(tag)) {
            countrytags.add(tag);
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Document document = new Document();
            document.put("name", tag.getName());
            document.put("category", tag.getCategory());
            document.put("prefix", tag.getPrefix().replace(ChatColor.COLOR_CHAR, '&'));
            document.put("weight", tag.getWeight());

            countrycollection.replaceOne(Filters.eq("name", tag.getName()), document, new ReplaceOptions().upsert(true));
        });
    }

    public void customremoveTag(Tag tag) {
        customtags.remove(tag);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            customCollection.deleteOne(Filters.eq("name", tag.getName())); // Deletes the prefix
//            Core.getInstance().getPacketBase().sendPacket(new PacketDeletePrefix(prefix.getName()));
        });
    }
    public void textremoveTag(Tag tag) {
        texttags.remove(tag);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            textcollection.deleteOne(Filters.eq("name", tag.getName())); // Deletes the prefix
//            Core.getInstance().getPacketBase().sendPacket(new PacketDeletePrefix(prefix.getName()));
        });
    }
    public void symbolremoveTag(Tag tag) {
        symboltags.remove(tag);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            symbolcollection.deleteOne(Filters.eq("name", tag.getName())); // Deletes the prefix
//            Core.getInstance().getPacketBase().sendPacket(new PacketDeletePrefix(prefix.getName()));
        });
    }
    public void countryremoveTag(Tag tag) {
        countrytags.remove(tag);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            countrycollection.deleteOne(Filters.eq("name", tag.getName())); // Deletes the prefix
//            Core.getInstance().getPacketBase().sendPacket(new PacketDeletePrefix(prefix.getName()));
        });
    }
}
