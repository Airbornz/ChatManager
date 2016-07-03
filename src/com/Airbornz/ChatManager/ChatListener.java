/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Sound
 *  org.bukkit.World
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.AsyncPlayerChatEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 */
package com.Airbornz.ChatManager;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class ChatListener
implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (Core.multi.booleanValue()) {
            if (ChatSwitcher.playersStored.containsKey(player.getUniqueId())) {
                ChatSwitcher.setLobby(player, ChatSwitcher.playersStored.get(player.getUniqueId()));
                ChatLobbys lobby = ChatSwitcher.getLobby(player);
                if (lobby.equals((Object)ChatLobbys.Global)) {
                    ChatSwitcher.joinGlobal(player);
                } else if (lobby.equals((Object)ChatLobbys.World)) {
                    ChatSwitcher.joinWorld(player);
                } else if (lobby.equals((Object)ChatLobbys.Staff)) {
                    ChatSwitcher.joinStaff(player);
                } else if (lobby.equals((Object)ChatLobbys.None)) {
                    ChatSwitcher.joinNone(player);
                } else {
                    player.sendMessage("§cFound Corrupt Chat Save For You!");
                    player.sendMessage("§cAll Fixed Now, Just Tell Airbornz!");
                    ChatSwitcher.joinGlobal(player);
                }
            } else {
                ChatSwitcher.setLobby(player, ChatLobbys.World);
                ChatSwitcher.joinWorld(player);
            }
            player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.0f);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (Core.multi.booleanValue()) {
            if (Core.enabled.booleanValue() || player.hasPermission("chatmanager.talk")) {
                ChatLobbys lobby = ChatSwitcher.getLobby(player);
                if (ChatSwitcher.isMuted(player).booleanValue()) {
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_GROWL, 1.0f, 1.0f);
                    player.sendMessage("§e[§cChat§e]§c You Can Not Chat, You Are §l§cMUTED!");
                } else if (lobby.equals((Object)ChatLobbys.Global)) {
                    ChatListener.sendGlobalMessage(player.getDisplayName(), event.getMessage());
                } else if (lobby.equals((Object)ChatLobbys.World)) {
                    ChatListener.sendWorldMessage(player, event.getMessage());
                } else if (lobby.equals((Object)ChatLobbys.Staff)) {
                    ChatListener.sendStaffMessage(player, event.getMessage());
                } else if (lobby.equals((Object)ChatLobbys.None)) {
                    player.sendMessage("§eHey, In 'None' Chat Nobody Can Hear You! Switch Chats By Doing:");
                    player.sendMessage("§e'/chat g");
                } else {
                    player.sendMessage("§cOh No! You Shouldn't Be Here! Tell Airbornz This Right Away!");
                }
                event.setCancelled(true);
            } else {
                event.setCancelled(true);
                player.sendMessage("§cSorry, chat is disabled right now!");
            }
        }
    }

    private static void sendGlobalMessage(String sender, String message) {
        for (Player p : ChatSwitcher.globalChat) {
            p.sendMessage(String.valueOf(Core.Globalprefix) + "<" + sender + "§f> " + message);
        }
    }

    private static void sendWorldMessage(Player sender, String message) {
        for (Player p : ChatSwitcher.worldChat) {
            if (!p.getWorld().equals((Object)sender.getWorld())) continue;
            p.sendMessage(String.valueOf(Core.Worldprefix) + "<" + sender.getDisplayName() + "§f> " + message);
        }
    }

    private static void sendStaffMessage(Player sender, String message) {
        for (Player p : ChatSwitcher.staffChat) {
            p.sendMessage(String.valueOf(Core.Staffprefix) + "<" + sender.getName() + "§f> " + message);
        }
    }

    public static String getLobbyName(ChatLobbys lobby) {
        if (lobby.equals((Object)ChatLobbys.Global)) {
            return "Global";
        }
        if (lobby.equals((Object)ChatLobbys.World)) {
            return "World";
        }
        if (lobby.equals((Object)ChatLobbys.None)) {
            return "None";
        }
        if (lobby.equals((Object)ChatLobbys.Staff)) {
            return "Staff";
        }
        return "Unknown";
    }
}

