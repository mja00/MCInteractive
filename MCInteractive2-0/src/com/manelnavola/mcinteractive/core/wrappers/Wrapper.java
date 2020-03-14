package com.manelnavola.mcinteractive.core.wrappers;

import java.util.function.Consumer;

/**
 * Singleton class for general wrapping purposes
 * @author Manel Navola
 *
 */
public class Wrapper {
	
	private static Wrapper INSTANCE;
	
	private WServer<?> wServer;
	
	/**
	 * Gets the singleton object
	 * @return The singleton object
	 */
	public static Wrapper getInstance() {
		if (INSTANCE == null) INSTANCE = new Wrapper();
		return INSTANCE;
	}
	
	/**
	 * Sets the wrapping of the current server
	 * @param wServer The server wrapping to set
	 */
	public void setServer(WServer<?> wServer) {
		this.wServer = wServer;
	}
	
	/**
	 * Gets the current wrapped server
	 * @return The server wrapping
	 */
	public WServer<?> getServer() {
		return wServer;
	}
	
	/**
	 * Runs code on the server side
	 * @param consumer The code to run
	 */
	public void runOnServer(Consumer<WServer<?>> consumer) {
		wServer.runOnServer(consumer);
	}
	
}
