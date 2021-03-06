package com.manelnavola.mcinteractive.generic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.manelnavola.mcinteractive.Main;
import com.manelnavola.mcinteractive.voting.Vote;
import com.manelnavola.mcinteractive.voting.VoteManager;
import com.manelnavola.twitchbotx.domain.TwitchUser;
import com.manelnavola.twitchbotx.domain.TwitchUser.Badge;
import com.manelnavola.twitchbotx.events.TwitchMessageEvent;

public class ChatManager {
	
	private static final String DEFAULT_TAG = ChatColor.GRAY + "[MCI]";
	private static final int[] TITLE_DURATION = {10, 70, 10};
	private static final Map<String, ChatColor> USER_CHAT_COLORS = new HashMap<>();
	private static final ChatColor[] ALLOWED_CHAT_COLORS = new ChatColor[] {
			ChatColor.AQUA, ChatColor.BLUE, ChatColor.DARK_AQUA, ChatColor.DARK_GREEN, ChatColor.DARK_PURPLE, ChatColor.DARK_RED,
			ChatColor.GOLD, ChatColor.GREEN, ChatColor.LIGHT_PURPLE, ChatColor.RED, ChatColor.YELLOW
	};
	private static int randomChatColorIndex = 0;
	
	// Optional tag
	public static String parseUserTag(TwitchUser tu) {
		String tag = DEFAULT_TAG;

		if (tu.hasBadge(Badge.BROADCASTER)) {
			tag = ChatColor.AQUA + "[Streamer]";
		} else if (tu.hasBadge(Badge.ADMIN)) {
			tag = ChatColor.YELLOW + "[Admin]";
		} else if (tu.hasBadge(Badge.STAFF)) {
			tag = ChatColor.YELLOW + "[Staff]";
		} else if (tu.hasBadge(Badge.MODERATOR)) {
			tag = ChatColor.RED + "[Mod]";
		} else if (tu.hasBadge(Badge.GLOBAL_MOD)) {
			tag = ChatColor.RED + "[GlobalMod]";
		} else if (tu.hasBadge(Badge.SUBSCRIBER)) {
			tag = ChatColor.LIGHT_PURPLE + "[Sub]";
		}

		return tag;
	}

	// Username tag
	public static String parseUsername(PlayerData pd, TwitchUser tu, boolean tags) {
		if (!USER_CHAT_COLORS.containsKey(tu.getUserId())) {
			randomChatColorIndex++;
			if (randomChatColorIndex >= ALLOWED_CHAT_COLORS.length) randomChatColorIndex = 0;
			USER_CHAT_COLORS.put(tu.getUserId(), ALLOWED_CHAT_COLORS[randomChatColorIndex]);
		}
		String name = USER_CHAT_COLORS.get(tu.getUserId()) + tu.getDisplayName();
		if (tags) {
			ChatColor tagsColor;
			if (tu.hasBadge(Badge.SUBSCRIBER)) {
				if (pd.getConfig("highlight")) {
					tagsColor = ChatColor.WHITE;
				} else {
					tagsColor = ChatColor.GRAY;
				}
			} else {
				if (tu.hasBadge(Badge.BROADCASTER) || tu.hasBadge(Badge.ADMIN) || tu.hasBadge(Badge.STAFF)
						|| tu.hasBadge(Badge.MODERATOR) || tu.hasBadge(Badge.GLOBAL_MOD)) {
					tagsColor = ChatColor.WHITE;
				} else {
					tagsColor = ChatColor.GRAY;
				}
			}
			return tagsColor + "<" + name + "" + tagsColor + ">";
		} else {
			return name;
		}
	}

	// Message color
	public static String parseMessage(PlayerData pd, TwitchUser tu, String message) {
		if (tu.hasBadge(Badge.SUBSCRIBER)) {
			if (pd.getConfig("highlight")) {
				return ChatColor.WHITE + message;
			} else {
				return ChatColor.GRAY + message;
			}
		} else {
			if (tu.hasBadge(Badge.BROADCASTER) || tu.hasBadge(Badge.ADMIN) || tu.hasBadge(Badge.STAFF)
					|| tu.hasBadge(Badge.MODERATOR) || tu.hasBadge(Badge.GLOBAL_MOD)) {
				return ChatColor.WHITE + message;
			} else {
				return ChatColor.GRAY + message;
			}
		}
	}
	
	public static void sendMessage(final List<Player> pl, final TwitchMessageEvent tme) {
		Bukkit.getScheduler().runTask(Main.plugin, new Runnable() {
			@Override
			public void run() {
				for (Player p : pl) {
					PlayerData pd = PlayerManager.getPlayerData(p);
					
					Vote v = VoteManager.getVote(p);
					if (v != null && v.isValidOption(tme.getMessage())) return; // Is a vote, handle automatically
					
					if (pd.getConfig("showchat")) {
						TwitchUser tu = tme.getTwitchUser();
						String tag = parseUserTag(tu);
						String user = parseUsername(pd, tu, true);
						String msg = parseMessage(pd, tu, tme.getMessage());
						p.sendMessage(tag + " " + user + " " + msg);
					}
				}
			}
		});
	}

	public static void sendNotice(List<Player> pl, String msg) {
		for (Player p : pl) {
			sendNotice(p, msg);
		}
	}

	public static void sendNotice(Player p, String msg) {
		p.sendMessage(msg);
		if (PlayerManager.getPlayerData(p).getConfig("noticetitle")) {
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
			if (!VoteManager.isActive(p)) {
				p.sendTitle("", msg, TITLE_DURATION[0], TITLE_DURATION[1], TITLE_DURATION[2]);
			}
		}
	}
	
}
