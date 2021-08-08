package com.entiv.saturationpill;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerListener implements Listener {

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {

        Configuration config = SaturationPill.getInstance().getConfig();

        Player player = event.getEntity() instanceof Player ? ((Player) event.getEntity()) : null;

        if (player == null) return;

        int foodLevel = event.getFoodLevel();

        if (foodLevel < 20) {
            for (ItemStack itemStack : player.getInventory()) {

                AtomicBoolean find = new AtomicBoolean(false);

                Optional.ofNullable(itemStack)
                        .map(ItemStack::getItemMeta)
                        .map(ItemMeta::getLore)
                        .ifPresent(lore -> {
                            LoreChecker loreChecker = new LoreChecker(itemStack);
                            Integer i = loreChecker.getIndex(config.getString("检测关键字"));

                            if (i == null) return;

                            find.set(true);
                            String value = lore.get(i);

                            int itemFoodLevel = loreChecker.getInt(i);
                            int nextItemFoodLevel = itemFoodLevel - config.getInt("消耗饱食度");

                            event.setFoodLevel(20);
                            player.setSaturation(20);

                            if (nextItemFoodLevel <= 0) {

                                itemStack.setAmount(itemStack.getAmount() - 1);

                            } else {

                                String newValue = value.replace(String.valueOf(itemFoodLevel), String.valueOf(nextItemFoodLevel));
                                loreChecker.replaceLore(i, newValue);

                            }

                        });

                if (find.get()) return;
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(BlockBreakEvent event) {
        Player player = event.getPlayer();
        SaturationPill plugin = SaturationPill.getInstance();
        FileConfiguration config = plugin.getConfig();

        int checkFoodLevel = config.getInt("饥饿惩罚.检测值");
        boolean denyBlock = config.getBoolean("饥饿惩罚.禁止破坏方块");

        if (!config.getBoolean("饥饿惩罚.开启")) return;
        if (player.getFoodLevel() > checkFoodLevel) return;

        if (denyBlock) {
            event.setCancelled(true);
            Message.send(player, config.getString("提示消息.禁止破坏方块"));
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        SaturationPill plugin = SaturationPill.getInstance();
        FileConfiguration config = plugin.getConfig();

        int checkFoodLevel = config.getInt("饥饿惩罚.检测值");
        boolean denyAttack = config.getBoolean("饥饿惩罚.禁止攻击");

        if (!config.getBoolean("饥饿惩罚.开启")) return;
        if (player.getFoodLevel() > checkFoodLevel) return;

        if (denyAttack) {
            event.setCancelled(true);
            Message.send(player, config.getString("提示消息.禁止攻击"));
        }
    }
}
