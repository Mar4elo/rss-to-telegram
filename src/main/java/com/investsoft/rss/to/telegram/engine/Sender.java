/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.investsoft.rss.to.telegram.engine;

import com.investsoft.rss.to.telegram.Application;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 *
 * @author SMS
 */
public class Sender {

	private final static Sender INSTANCE = new Sender();

	public static Sender getInstance() {
		return INSTANCE;
	}

	private final Logger log = Application.getLogger(this.getClass().getSimpleName());
	
	private final BlockingQueue<SendMessage> queue = new LinkedBlockingQueue<>();
	private final SenderJob job = new SenderJob();
	private final Thread jobThread;

	private Sender() {
		this.jobThread = new Thread(this.job);
		this.jobThread.start();
		this.log.info("Started sender");
	}

	void send(SendMessage message) {
		this.queue.add(message);
	}

	public class SenderJob implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					SendMessage message = queue.take();
					Application.BOT.execute(message);
					log.info("sent message");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
