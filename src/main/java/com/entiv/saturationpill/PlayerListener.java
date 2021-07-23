package com.entiv.saturationpill;

import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerListener implements Listener {


    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {

        Configuration config = SaturationPill.getInstance().getConfig();

        Player player = event.getEntity() instanceof Player ? ((Player) event.getEntity()) : null;

        System.out.println(event.getFoodLevel());
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
}
