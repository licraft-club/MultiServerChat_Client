package com.gmail.shellljx.MultiServerChat_Client;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainCommand implements CommandExecutor {

    private Client plugin;
    Double price = 0D;
    Double balance = 0D;

    public MainCommand(Client plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label,
                             String[] args) {

        if (args[0].equalsIgnoreCase("help") && args.length == 1) {
            sender.sendMessage(ChatColor.YELLOW + "-----------------.[" + ChatColor.RED + this.plugin.getConfig().getString("menu.title") + ChatColor.YELLOW + "].-----------------");
            sender.sendMessage(ChatColor.AQUA + "/wechat [聊天信息]  --发送跨服聊天信息");
            sender.sendMessage(ChatColor.YELLOW + "------------------------------------------------------------------");
            return true;
        }

        if (args.length == 1 && sender.hasPermission("msc.talk") && sender instanceof Player) {
            price = plugin.getConfig().getDouble("setting.price");
            balance = Double.valueOf(plugin.getEcon().getBalance(sender.getName()));
            if (balance < price) {
                sender.sendMessage(ChatColor.RED + "你的游戏币不足,无法发送跨服信息");
                return true;
            }
            plugin.getEcon().withdrawPlayer(sender.getName(), price);
            if (plugin.getSocketUtil().getIsConnected()) {
                Player p = (Player) sender;
                plugin.getSocketUtil().sendMessage(format(p, args));
            } else {
                Player p = (Player) sender;
                plugin.getSocketUtil().connectServer(plugin.getConfig().getInt("setting.Port"), plugin.getConfig().getString("setting.IP"), plugin.getConfig().getString("setting.Name"));
                plugin.getSocketUtil().sendMessage(format(p, args));
            }
            return true;
        }

        sender.sendMessage(ChatColor.RED + "/wechat help 查看帮助");
        return false;
    }

    private String format(Player player, String[] args) {
        String format = plugin.getConfig().getString("setting.format");
        return format.replaceAll("%server%", plugin.getConfig().getString("setting.Name"))
                .replaceAll("%display%", player.getDisplayName()).replaceAll("%msg%", args[0]);
    }
}
