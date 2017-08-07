package com.grizz.nms.v1_8_R1;

import com.grizz.merchant.MerchantTrade;
import com.grizz.merchant.MerchantWrapper;
import com.grizz.nms.Handler;
import net.minecraft.server.v1_8_R1.*;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

/**
 * Created by Gbtank.
 */
public class NMSHandler implements Handler {

    @Override
    public void displayMerchantGUI(Player player, MerchantWrapper merchant, String name) {
        EntityVillager villager = new EntityVillager(((CraftWorld) merchant.getLocation().getWorld()).getHandle());
        EntityHuman trader = ((CraftPlayer) player).getHandle();

        // Clear Villager Recipe List
        MerchantRecipeList recipeList = villager.getOffers(trader);
        recipeList.clear();

        for(MerchantTrade trade : merchant.getMenuMap().get(name).getTrades()) {
            // Params: CraftItemStack slot1, CraftItemStack slot2, CraftItemStack result, int uses, int maxUses
            MerchantRecipe recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(trade.getFirst()), CraftItemStack.asNMSCopy(trade.getSecond()), CraftItemStack.asNMSCopy(trade.getResult()), 0, 9999);
            try {
                Field rewardExp = recipe.getClass().getDeclaredField("rewardExp");
                rewardExp.setAccessible(true);
                rewardExp.set(recipe, false);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            recipeList.add(recipe);
        }

        // Villager opens trade with player
        villager.a_(trader);
        trader.openTrade(villager);
        // Add player Statistic for talking to villagers
        trader.b(StatisticList.F);
    }

    @Override
    public void displayActionBar(Player player, String text) {
        String msg = ChatColor.translateAlternateColorCodes('&', text.replace("_", " "));
        IChatBaseComponent cbc = ChatSerializer.a("{\"text\": \"" + msg + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(ppoc);
    }

    @Override
    public void displayTitle(Player player, String title, double fadeIn, double stay, double fadeOut) {
        String msg = ChatColor.translateAlternateColorCodes('&', title.replace("_", " "));
        IChatBaseComponent cbc = ChatSerializer.a("{\"text\": \"" + msg + "\"}");
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, cbc);
        PacketPlayOutTitle timePacket = new PacketPlayOutTitle(EnumTitleAction.TIMES, null, (int) Math.round(fadeIn * 20), (int) Math.round(stay * 20), (int) Math.round(fadeOut * 20));

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(timePacket);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket);
    }

    @Override
    public void displaySubTitle(Player player, String subtitle, double fadeIn, double stay, double fadeOut) {
        String msg = ChatColor.translateAlternateColorCodes('&', subtitle.replace("_", " "));
        IChatBaseComponent cbc = ChatSerializer.a("{\"text\": \"" + msg + "\"}");
        PacketPlayOutTitle subTitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, cbc);
        PacketPlayOutTitle timePacket = new PacketPlayOutTitle(EnumTitleAction.TIMES, null, (int) Math.round(fadeIn * 20), (int) Math.round(stay * 20), (int) Math.round(fadeOut * 20));

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(timePacket);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(subTitlePacket);
    }

}
