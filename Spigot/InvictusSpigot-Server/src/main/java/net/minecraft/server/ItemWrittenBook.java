package net.minecraft.server;

public class ItemWrittenBook extends Item {
    public ItemWrittenBook() {
        this.c(1);
    }

    public static boolean b(NBTTagCompound var0) {
        if (!ItemBookAndQuill.b(var0) || !var0.hasKeyOfType("title", 8)) {
            return false;
        }
        String var = var0.getString("title");
        return var != null && var.length() <= 32 && var0.hasKeyOfType("author", 8);
    }

    public static int h(ItemStack var0) {
        return var0.getTag().getInt("generation");
    }

    @Override
    public String a(ItemStack var1) {
        if (var1.hasTag()) {
            NBTTagCompound var2 = var1.getTag();
            String var3 = var2.getString("title");
            if (!UtilColor.b(var3)) {
                return var3;
            }
        }
        return super.a(var1);
    }

    @Override
    public ItemStack a(ItemStack var1, World var2, EntityHuman var3) {
        this.a(var1, var3);
        var3.openBook(var1);
        return var1;
    }

    private void a(ItemStack var1, EntityHuman var2) {
        if (var1 != null && var1.getTag() != null) {
            NBTTagCompound var3 = var1.getTag();
            if (!var3.getBoolean("resolved")) {
                var3.setBoolean("resolved", true);
                if (b(var3)) {
                    NBTTagList var4 = var3.getList("pages", 8);
                    for (int var5 = 0; var5 < var4.size(); ++var5) {
                        String var6 = var4.getString(var5);
                        IChatBaseComponent var8;
                        try {
                            var8 = ChatComponentUtils.filterForDisplay(var2, IChatBaseComponent.ChatSerializer.a(var6), var2);
                        } catch (Exception var10) {
                            var8 = new ChatComponentText(var6);
                        }
                        var4.a(var5, new NBTTagString(IChatBaseComponent.ChatSerializer.a(var8)));
                    }
                    var3.set("pages", var4);
                    if (var2 instanceof EntityPlayer && var2.bZ() == var1) {
                        ((EntityPlayer) var2).playerConnection.sendPacket(new PacketPlayOutSetSlot(0, var2.activeContainer.getSlot(var2.inventory, var2.inventory.itemInHandIndex).rawSlotIndex, var1));
                    }
                }
            }
        }
    }
}