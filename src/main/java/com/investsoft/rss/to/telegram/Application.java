/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package com.investsoft.rss.to.telegram;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.investsoft.rss.to.telegram.bot.PosterBot;
import com.investsoft.rss.to.telegram.engine.Engine;
import com.investsoft.rss.to.telegram.engine.Sender;
import com.investsoft.rss.to.telegram.model.Config;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 *
 * @author SMS
 */
public class Application {

	public static Config CONFIG;
	public static TelegramLongPollingBot BOT;
	public static Engine ENGINE;

	private final static Logger log = getLogger(Application.class.getSimpleName());

	public static void main(String[] args) throws TelegramApiException, IOException, InterruptedException {
		log.info("Starting application");
		CONFIG = getConfig(args);
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
		telegramBotsApi.registerBot(new PosterBot());
		Sender.getInstance();
		ENGINE = new Engine();
		ENGINE.start();
	}

	private static Config getConfig(String[] args) throws IOException {
		return new ObjectMapper().readValue(new File(args[0]), Config.class);
	}

	public static Logger getLogger(String name) {
		name = name == null ? "default" : name;
		try {
			Properties log4jProperties = new Properties();
			log4jProperties.setProperty("log4j.logger." + name, "DEBUG, myConsoleAppender");
			log4jProperties.setProperty("log4j.appender.myConsoleAppender", "org.apache.log4j.ConsoleAppender");
			log4jProperties.setProperty("log4j.appender.myConsoleAppender.layout", "org.apache.log4j.PatternLayout");
			log4jProperties.setProperty("log4j.appender.myConsoleAppender.layout.ConversionPattern", "%d{yyyy-MM-dd HH:mm:ss} %-5p [%t] %c{1} - %m%n");
			PropertyConfigurator.configure(log4jProperties);
			return Logger.getLogger(name);
		} catch (Exception e) {
			return null;
		}
	}
}
