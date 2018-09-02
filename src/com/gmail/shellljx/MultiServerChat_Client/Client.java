package com.gmail.shellljx.MultiServerChat_Client;

import java.io.File;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Client extends JavaPlugin {

    private PluginManager pluginManager;
    private Logger log = Logger.getLogger("MultiServerChat");
    private PlayerListener pl = new PlayerListener(this);
    private FileConfiguration config;
    private SocketUtil socketUtil = new SocketUtil(this);
    private Economy econ = null;

    @Override
    public void onEnable() {

        if (!setupEconomy()) {
            this.log.info("[level] Plugin Vault not found! " + this.econ.getName());
            return;
        }
        pluginManager = Bukkit.getServer().getPluginManager();
        this.log.info("[level] Plugin Vault is found! " + this.econ.getName());
        config = getConfig();
        if (!new File(getDataFolder() + File.separator + "config.yml").exists()) {
            config.createSection("menu.title");
            config.createSection("setting.Name");
            config.createSection("setting.IP");
            config.createSection("setting.Port");
            config.createSection("setting.price");
            config.set("setting.format", "[%server%]<%display%>%msg%");
            config.options().copyDefaults(true);
            saveConfig();
        }
        reloadConfig();
        socketUtil.connectServer(config.getInt("setting.Port"), config.getString("setting.IP"), config.getString("setting.Name"));
        pluginManager.registerEvents(pl, this);
        this.getCommand("wechat").setExecutor(new MainCommand(this));

        log.info("=====================================================");
        log.info(ChatColor.translateAlternateColorCodes('&', "&5 auther shell"));
        log.info("======================================================");
    }

    @Override
    public void onDisable() {
        reloadConfig();
        saveConfig();
        socketUtil.closeConnection();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (rsp == null) {
            return false;
        }
        this.econ = rsp.getProvider();
        return this.econ != null;
    }

    public Economy getEcon() {
        return this.econ;
    }

    public SocketUtil getSocketUtil() {
        return this.socketUtil;
    }

}
