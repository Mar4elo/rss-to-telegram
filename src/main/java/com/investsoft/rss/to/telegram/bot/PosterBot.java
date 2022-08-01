/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.investsoft.rss.to.telegram.bot;

import com.investsoft.rss.to.telegram.Application;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 *
 * @author SMS
 */
public class PosterBot extends TelegramLongPollingBot {

	private final Logger log = Application.getLogger(this.getClass().getSimpleName());
	
	@Override
	public String getBotToken() {
		return Application.CONFIG.botToken;
	}

	@Override
	public String getBotUsername() {
		return Application.CONFIG.botName;
	}

	@Override
	public void onUpdateReceived(Update update) {

	}

	@Override
	public void onRegister() {
		Application.BOT = this;
		this.log.info("Bot registered");
	}

}
