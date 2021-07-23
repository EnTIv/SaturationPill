package com.entiv.saturationpill;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class SaturationPill extends JavaPlugin {

    private static SaturationPill plugin;

    @Override
    public void onEnable() {

        plugin = this;

        String[] message = {
                "§e§l" + getName() + "§a 插件§e v" + getDescription().getVersion() + " §a已启用",
                "§a插件制作作者:§e EnTIv §aQQ群:§e 600731934"
        };
        Message.sendConsole(message);
        saveDefaultConfig();

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {
        String[] message = {
                "§e§l" + getName() + "§a 插件§e v" + getDescription().getVersion() + " §a已卸载",
                "§a插件制作作者:§e EnTIv §aQQ群:§e 600731934"
        };
        Message.sendConsole(message);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender.isOp()) {
            reloadConfig();
            Message.send(sender, "&9&l" + getName() + "&6&l >> &a插件已重载完毕");
        }

        return true;
    }

    public static SaturationPill getInstance() {
        return plugin;
    }
}
