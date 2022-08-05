/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.investsoft.rss.to.telegram.engine;

import com.investsoft.rss.to.telegram.Application;
import com.investsoft.rss.to.telegram.model.ConfigRssItem;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import org.apache.log4j.Logger;

/**
 *
 * @author SMS
 */
public class Engine {

	private final Logger log = Application.getLogger(this.getClass().getSimpleName());

	private final Map<ConfigRssItem, Timer> timerJobs = new HashMap<>();
	private final Timer timerGc = new Timer();

	public void start() {
		Application.CONFIG.rssData
			.forEach(item -> {
				Timer timer = new Timer();
				timer.schedule(new TimerTaskJob(item, this.restartTimer), item.interval);
				this.timerJobs.put(item, timer);
			});
		// Periodical call garbage collector, to collect wasted jobs
		this.timerGc.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				System.gc();
			}
		}, 0, 60000);
	}

	private Consumer<ConfigRssItem> restartTimer = conf
		-> timerJobs.get(conf).schedule(new TimerTaskJob(conf, this.restartTimer), conf.interval);
}
