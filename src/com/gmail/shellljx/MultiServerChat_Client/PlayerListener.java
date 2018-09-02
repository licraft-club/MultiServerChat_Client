package com.gmail.shellljx.MultiServerChat_Client;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerListener implements Listener {

    private Client plugin;

    public PlayerListener(Client plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();
        String message = event.getMessage();
        if (!plugin.getSocketUtil().getIsConnected()) {
            plugin.getSocketUtil().connectServer(plugin.getConfig().getInt("setting.Port")
                    , plugin.getConfig().getString("setting.IP"), plugin.getConfig().getString("setting.Name"));
        }
        plugin.getSocketUtil().sendMessage(format(p, message));
    }

    private String format(Player player, String args) {
        String format = plugin.getConfig().getString("setting.format");
        return format.replaceAll("%server%", plugin.getConfig().getString("setting.Name"))
                .replaceAll("%display%", player.getDisplayName()).replaceAll("%msg%", args);
    }

}
