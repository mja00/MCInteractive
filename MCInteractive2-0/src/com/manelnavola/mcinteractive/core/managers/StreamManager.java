package com.manelnavola.mcinteractive.core.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.manelnavola.mcinteractive.core.utils.ChatUtils;
import com.manelnavola.mcinteractive.core.wrappers.WPlayer;

/**
 * Singleton class for managing player-TwitchBotX connections
 * @author Manel Navola
 *
 */
public class StreamManager extends Manager {
	
	private static StreamManager INSTANCE;
	private StreamManager() {}
	
	private boolean enabled = false;
	private Map<String, Collection<WPlayer<?>>> channelToPlayers;
	private Map<WPlayer<?>, String> playerToChannel;
	
	/**
	 * Gets the singleton object
	 * @return The singleton object
	 */
	public static StreamManager getInstance() {
		if (INSTANCE == null) INSTANCE = new StreamManager();
		return INSTANCE;
	}
	
	/**
	 * Connects a player to a Twitch channel
	 * @param wp The player to connect
	 * @param channel The name of the Twitch channel, in lowercase
	 */
	public void joinChannel(WPlayer<?> wp, String channel) {
		if (!enabled) return;
		
		Collection<WPlayer<?>> currentPlayers = channelToPlayers.get(channel);
		if (currentPlayers == null) {
			currentPlayers = new ArrayList<WPlayer<?>>();
			BotManager.getInstance().joinChannel(channel);
		}
		
		playerToChannel.put(wp, channel);
		currentPlayers.add(wp);
	}
	
	/**
	 * Disconnects a player from any Twitch channel
	 * @param wp The player to disconnect
	 */
	public void leaveChannel(WPlayer<?> wp) {
		if (!enabled) return;
		
		String playerChannel = getPlayerChannel(wp);
		if (playerChannel == null) return;
		
		Collection<WPlayer<?>> currentPlayers = channelToPlayers.get(playerChannel);
		if (currentPlayers != null) {
			currentPlayers.remove(wp);
			playerToChannel.remove(wp);
			if (currentPlayers.isEmpty()) {
				channelToPlayers.remove(playerChannel);
				BotManager.getInstance().leaveChannel(playerChannel);
			}
		}
	}
	
	/**
	 * Gets the channel of a connected player
	 * @return The name of the connected channel
	 */
	public String getPlayerChannel(WPlayer<?> wp) {
		if (!enabled) return null;
		
		return playerToChannel.get(wp);
	}
	
	/**
	 * Checks if the player is connected
	 * @return True if the player is connected
	 */
	public boolean isPlayerConnected(WPlayer<?> wp) {
		return playerToChannel.containsKey(wp);
	}
	
	/**
	 * Gets a collection of players currently listening to a channel
	 * @param channelName The name of the channel
	 * @return A collection of players
	 */
	public Collection<WPlayer<?>> getChannelPlayers(String channelName) {
		return channelToPlayers.get(channelName);
	}
	
	@Override
	public void start() {
		channelToPlayers = new HashMap<>();
		playerToChannel = new HashMap<>();
		enabled = true;
	}
	
	@Override
	public void stop() {
		ChatUtils.broadcastError(playerToChannel.keySet(), "You have been disconnected from Twitch!");
		
		channelToPlayers = null;
		playerToChannel = null;
		enabled = false;
		INSTANCE = null;
	}
	
	/**
	 * Returns a lowercase collection of the currently running streams
	 * @return Collection of strings
	 */
	public Collection<String> getRunningStreams() {
		return channelToPlayers.keySet();
	}
	
	/**
	 * Returns whether a stream is currently ongoing or not
	 * @param channelName The stream to check
	 * @return True if the stream is ongoing
	 */
	public boolean streamExists(String channelName) {
		return channelToPlayers.containsKey(channelName);
	}
	
	/**
	 * Returns whether a player has registered a stream in their account
	 * @param wp The player to check
	 * @return True if the player has registered a Twitch channel
	 */
	public static boolean isRegistered(WPlayer<?> wp) {
		
	}
	
}