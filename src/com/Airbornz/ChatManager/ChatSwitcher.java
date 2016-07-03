/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Sound
 *  org.bukkit.World
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.Airbornz.ChatManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatSwitcher
implements CommandExecutor {
    public static Map<Player, ChatLobbys> playersStored = new HashMap<Player, ChatLobbys>();
    public static List<Player> globalChat = new ArrayList<Player>();
    public static List<Player> worldChat = new ArrayList<Player>();
    public static List<Player> staffChat = new ArrayList<Player>();
    public static List<Player> noneChat = new ArrayList<Player>();
    private static List<Player> mutedPlayers = new ArrayList<Player>();

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("clear")) {
                    if (player.hasPermission("chatmanager.clear")) {
                        int lines = 100;
                        while (lines > 0) {
                            Bukkit.broadcastMessage((String)" ");
                            --lines;
                        }
                    } else {
                        player.sendMessage("§cAccess Denied!");
                    }
                } else if (args[0].equalsIgnoreCase("toggle")) {
                    if (player.hasPermission("chatmanager.toggle")) {
                        if (Core.enabled.booleanValue()) {
                            Core.enabled = false;
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                p.sendMessage(String.valueOf(Core.prefix) + "§eAll chats have been frozen!");
                            }
                        } else {
                            Core.enabled = true;
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                p.sendMessage(String.valueOf(Core.prefix) + "§eAll chats have been resumed!");
                            }
                        }
                    } else {
                        player.sendMessage("§cAccess Denied!");
                    }
                } else if (args[0].equalsIgnoreCase("Help")) {
                    player.sendMessage("§e---Chat Manager Help---");
                    if (Core.multi.booleanValue()) {
                        player.sendMessage("§9Switch yourself:§e /chat (Global/World/Staff/None)");
                        if (player.hasPermission("chatmanager.switch")) {
                            player.sendMessage("§9Switch Others:§e /chat switch (playername) (Global/World/Staff/None)");
                        }
                    }
                    if (player.hasPermission("chatmanager.clear")) {
                        player.sendMessage("§9Clear chats:§e /chat clear");
                    }
                    if (player.hasPermission("chatmanager.toggle")) {
                        player.sendMessage("§9Toggle All Chats:§e /chat toggle");
                    }
                    if (player.hasPermission("chatmanager.mute")) {
                        player.sendMessage("§9Mute and Unmute Players:§e /chat mute (playername)");
                    }
                    player.sendMessage("§9This screen:§e /chat help");
                } else if (Core.multi.booleanValue()) {
                    String stringlobby = args[0];
                    ChatLobbys lobby = ChatSwitcher.determineLobby(stringlobby);
                    if (lobby.equals((Object)ChatLobbys.Staff) && !player.hasPermission("chatmanager.staffchat")) {
                        player.sendMessage(String.valueOf(Core.prefix) + "Sorry You Can't Join That Lobby!");
                    } else {
                        playersStored.remove((Object)player);
                        if (globalChat.contains((Object)player)) {
                            globalChat.remove((Object)player);
                        } else if (worldChat.contains((Object)player)) {
                            worldChat.remove((Object)player);
                        } else if (noneChat.contains((Object)player)) {
                            noneChat.remove((Object)player);
                        } else if (staffChat.contains((Object)player)) {
                            staffChat.remove((Object)player);
                        }
                        if (lobby.equals((Object)ChatLobbys.Error)) {
                            ChatSwitcher.setLobby(player, ChatLobbys.Global);
                        } else {
                            ChatSwitcher.setLobby(player, lobby);
                        }
                        if (lobby.equals((Object)ChatLobbys.Global)) {
                            ChatSwitcher.joinGlobal(player);
                        } else if (lobby.equals((Object)ChatLobbys.World)) {
                            ChatSwitcher.joinWorld(player);
                        } else if (lobby.equals((Object)ChatLobbys.None)) {
                            ChatSwitcher.joinNone(player);
                        } else if (lobby.equals((Object)ChatLobbys.Staff)) {
                            ChatSwitcher.joinStaff(player);
                        } else {
                            player.sendMessage("§cBad Lobby Name! Moving You To Global...");
                            ChatSwitcher.joinGlobal(player);
                        }
                    }
                } else {
                    player.sendMessage("§cSorry, there is only one chat channel right now!");
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("mute")) {
                    if (player.hasPermission("chatmanager.mute")) {
                        @SuppressWarnings("deprecation")
						Player target = Bukkit.getPlayer((String)args[1]);
                        if (target != null) {
                            if (ChatSwitcher.isMuted(target).booleanValue()) {
                                ChatSwitcher.unmutePlayer(target);
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    if (!p.hasPermission("chatmanager.hear") && !p.getUniqueId().equals(player.getUniqueId())) continue;
                                    p.sendMessage(String.valueOf(Core.prefix) + target.getName() + " Has Been Unmuted By " + player.getName() + "!");
                                }
                            } else {
                                ChatSwitcher.mutePlayer(target);
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    if (!p.hasPermission("chatmanager.hear") && !p.getUniqueId().equals(player.getUniqueId())) continue;
                                    p.sendMessage(String.valueOf(Core.prefix) + target.getName() + " Has Been Muted By " + player.getName() + "!");
                                }
                            }
                        } else {
                            player.sendMessage("§cThat player could not be found, did you type it right?");
                        }
                    } else {
                        player.sendMessage("§cAccess Denied!");
                    }
                }
            } else if (args.length == 3) {
                if (args[0] == "switch") {
                    if (Core.multi.booleanValue()) {
                        if (player.hasPermission("chatmanager.switch")) {
                            if (args[1] != null && args[2] != null) {
                                @SuppressWarnings("deprecation")
								Player target = Bukkit.getPlayer((String)args[1]);
                                if (target != null) {
                                    ChatLobbys lobby = ChatSwitcher.determineLobby(args[2]);
                                    playersStored.remove((Object)target);
                                    if (globalChat.contains((Object)target)) {
                                        globalChat.remove((Object)target);
                                    } else if (worldChat.contains((Object)target)) {
                                        worldChat.remove((Object)target);
                                    } else if (noneChat.contains((Object)target)) {
                                        noneChat.remove((Object)target);
                                    } else if (staffChat.contains((Object)target)) {
                                        staffChat.remove((Object)target);
                                    }
                                    ChatSwitcher.setLobby(target, lobby);
                                    if (lobby.equals((Object)ChatLobbys.Global)) {
                                        ChatSwitcher.joinGlobal(target);
                                    } else if (lobby.equals((Object)ChatLobbys.World)) {
                                        ChatSwitcher.joinWorld(target);
                                    } else if (lobby.equals((Object)ChatLobbys.None)) {
                                        ChatSwitcher.joinNone(target);
                                    } else if (lobby.equals((Object)ChatLobbys.Staff)) {
                                        ChatSwitcher.joinStaff(target);
                                    }
                                    player.sendMessage(String.valueOf(Core.prefix) + "Sucessfully Moved " + args[1] + " To " + ChatListener.getLobbyName(lobby));
                                    target.playSound(target.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.0f);
                                    target.sendMessage(String.valueOf(Core.prefix) + "You have been moved to chat lobby " + ChatListener.getLobbyName(lobby) + "!");
                                } else {
                                    player.sendMessage("§cAccess Denied!");
                                }
                            } else {
                                player.sendMessage("§cBad Arguments! /chat switch (playername) (Global/World/Staff/None)");
                            }
                        } else {
                            player.sendMessage("§cAccess Denied!");
                        }
                    } else {
                        player.sendMessage("§cSorry this command is only enabled with multiple chat rooms!");
                    }
                }
            } else {
                player.sendMessage("§cSorry that command is invaild, for help use §e'/chat help'!");
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("clear")) {
                int linestr = Core.lines;
                Bukkit.broadcastMessage((String)"§cClearing chat...");
                while (linestr > 0) {
                    Bukkit.broadcastMessage((String)"");
                    --linestr;
                }
            } else if (args[0].equalsIgnoreCase("toggle")) {
                if (Core.enabled.booleanValue()) {
                    Core.enabled = false;
                    Bukkit.broadcastMessage((String)(String.valueOf(Core.prefix) + "§eAll chats have been frozen!"));
                } else {
                    Core.enabled = true;
                    Bukkit.broadcastMessage((String)(String.valueOf(Core.prefix) + "§eAll chats have been resumed!"));
                }
            } else {
                sender.sendMessage("Invaild arguments! Usage: /chat (toggle/clear)");
            }
        }
        return true;
    }

    public static void setLobby(Player player, ChatLobbys lobby) {
        playersStored.put(player, lobby);
    }

    private static ChatLobbys determineLobby(String lobby) {
        if (lobby.equalsIgnoreCase("Global") || lobby.equalsIgnoreCase("G")) {
            return ChatLobbys.Global;
        }
        if (lobby.equalsIgnoreCase("World") || lobby.equalsIgnoreCase("W")) {
            return ChatLobbys.World;
        }
        if (lobby.equalsIgnoreCase("Staff") || lobby.equalsIgnoreCase("S")) {
            return ChatLobbys.Staff;
        }
        if (lobby.equalsIgnoreCase("None") || lobby.equalsIgnoreCase("N")) {
            return ChatLobbys.None;
        }
        return ChatLobbys.Error;
    }

    public static void joinGlobal(Player player) {
        player.sendMessage(Core.prefix + "Welcome to Global Chat!");
        player.sendMessage(Core.prefix + "Here Only People In 'Global' Can Hear You!");
        for (Player p : globalChat) {
            p.sendMessage(Core.Globalprefix + player.getName() + " Has Joined Global Chat!");
        }
        globalChat.add(player);
    }

    public static void joinWorld(Player player) {
        player.sendMessage(Core.prefix + "Welcome to World Chat!");
        player.sendMessage(Core.prefix + "Here Only People In This World (And In World Chat) Can Hear You !");
        for (Player p : worldChat) {
            if (!p.getWorld().equals((Object)player.getWorld())) continue;
            p.sendMessage(Core.Worldprefix + player.getName() + " Has Joined World Chat!");
        }
        worldChat.add(player);
    }

    public static void joinNone(Player player) {
        player.sendMessage(Core.prefix + "Welcome to None Chat!");
        player.sendMessage(Core.prefix + "Nobody Can Hear You, And You Can Hear Nobody!");
        noneChat.add(player);
    }

    public static void joinStaff(Player player) {
        player.sendMessage(Core.prefix + "Welcome to Staff Chat!");
        player.sendMessage(Core.prefix + "You Can Only Talk To Your Fellow Staff!");
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission("chatmanager.staffchat")) continue;
            p.sendMessage(Core.Staffprefix + player.getName() + " Has Joined Staff Chat!");
        }
        staffChat.add(player);
    }

    public static ChatLobbys getLobby(Player player) {
        return playersStored.get((Object)player);
    }

    public static Boolean isMuted(Player player) {
        if (mutedPlayers.contains((Object)player)) {
            return true;
        }
        return false;
    }

    public static void mutePlayer(Player player) {
        if (!ChatSwitcher.isMuted(player).booleanValue()) {
            mutedPlayers.add(player);
            player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_THUNDER, 1.0f, 1.0f);
            player.sendMessage(String.valueOf(Core.prefix) + "You Have Been Muted!");
            player.sendMessage(String.valueOf(Core.prefix) + "You Can No Longer Chat In ANY Channels!");
        }
    }

    public static void unmutePlayer(Player player) {
        if (ChatSwitcher.isMuted(player).booleanValue()) {
            mutedPlayers.remove((Object)player);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            player.sendMessage(String.valueOf(Core.prefix) + "You Have Been Unmuted!");
            player.sendMessage(String.valueOf(Core.prefix) + "You Can Now Resume Chat!");
        }
    }
}

