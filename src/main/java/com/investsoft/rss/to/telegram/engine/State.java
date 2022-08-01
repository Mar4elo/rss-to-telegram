/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.investsoft.rss.to.telegram.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.investsoft.rss.to.telegram.Application;
import com.investsoft.rss.to.telegram.model.StateEntries;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author SMS
 */
public class State {

	private final static String FILE_NAME = "state.json";
	private final static State INSTANCE = new State();

	public static State getInstance() {
		return INSTANCE;
	}

	private final org.apache.log4j.Logger log = Application.getLogger(this.getClass().getSimpleName());
	
	private StateEntries stateEntries;
	private AtomicBoolean hasNew = new AtomicBoolean();
	private final Timer persister = new Timer();

	private State() {
		try {
			this.stateEntries = new ObjectMapper().readValue(new File(FILE_NAME), StateEntries.class);
		} catch (Exception ex) {
			this.stateEntries = new StateEntries();
		}
		if (this.stateEntries.entries == null) {
			this.stateEntries.entries = new ConcurrentHashMap<>();
		}
		this.persister.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (hasNew.get()) {
					try ( OutputStream os = new FileOutputStream(new File(FILE_NAME))) {
						new ObjectMapper().writeValue(os, stateEntries);
						hasNew.set(false);
					} catch (Exception ex) {
						log.error("{}", ex);
					}
				}
			}
		}, 5000, 5000);
	}

	public boolean exists(String url, String link) {
		if (!this.stateEntries.entries.containsKey(url)) {
			this.stateEntries.entries.put(url, new ArrayList<>());
		}
		return this.stateEntries.entries.get(url).contains(link);
	}

	public void save(String url, String entry) {
		if (!this.stateEntries.entries.containsKey(url)) {
			this.stateEntries.entries.put(url, new ArrayList<>());
		}
		this.stateEntries.entries.get(url).add(entry);
		this.hasNew.set(true);
	}

}
