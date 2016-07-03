/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  org.bukkit.Server
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.Airbornz.ChatManager;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Core
extends JavaPlugin {
    public static String prefix = ChatColor.YELLOW+"<Chat> ";
    public static String Globalprefix = ChatColor.YELLOW+"[G] "+ChatColor.WHITE;
    public static String Worldprefix = ChatColor.YELLOW+"[W] "+ChatColor.WHITE;
    public static String Staffprefix = ChatColor.YELLOW+"[Staff] "+ChatColor.WHITE;
    public static Boolean enabled = true;
    public static int lines = 100;
    public static Boolean multi = true;
    private Boolean reset = false;

    public void onEnable() {
        this.getLogger().info("This server is running Advanced Chat by Airbornz!");
        this.getServer().getPluginManager().registerEvents((Listener)new ChatListener(), (Plugin)this);
        this.getCommand("chat").setExecutor((CommandExecutor)new ChatSwitcher());
        if (!new File(this.getDataFolder(), "config.yml").exists()) {
            this.getLogger().warning("Cannot find config, generating a new one");
            this.saveDefaultConfig();
            this.getLogger().info("Done!");
        }
        lines = this.getConfig().getInt("Lines");
        enabled = this.getConfig().getBoolean("Enabled");
        multi = this.getConfig().getBoolean("MultiChat");
        prefix = this.getConfig().getString("Prefix");
        Globalprefix = this.getConfig().getString("GlobalPrefix");
        Worldprefix = this.getConfig().getString("WorldPrefix");
        Staffprefix = this.getConfig().getString("StaffPrefix");
        this.reset = this.getConfig().getBoolean("Reset");
        if (this.reset.equals(true)) {
            this.getLogger().info("Saving default config...");
            this.saveDefaultConfig();
            this.getLogger().info("Saved!");
        }
    }
}

