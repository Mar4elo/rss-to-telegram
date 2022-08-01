/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.investsoft.rss.to.telegram.engine;

import com.investsoft.rss.to.telegram.Application;
import com.investsoft.rss.to.telegram.model.ConfigRssItem;
import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

/**
 *
 * @author SMS
 */
public class TimerTaskJob extends TimerTask {

	private final Logger log = Application.getLogger(this.getClass().getSimpleName());

	private final ConfigRssItem config;
	private final Consumer<ConfigRssItem> restartTimer;
	private String FULL_ARTICLE_LINK = "Link";
	private String FULL_ARTICLE_LINK_CAPTION = "Full article";
	private String LANGUAGE_CODE = "en";

	public TimerTaskJob(ConfigRssItem config, Consumer<ConfigRssItem> restartTimer) {
		this.config = config;
		this.restartTimer = restartTimer;
		if (Application.CONFIG.constants != null) {
			this.FULL_ARTICLE_LINK = Application.CONFIG.constants.fullArticleLink;
			this.FULL_ARTICLE_LINK_CAPTION = Application.CONFIG.constants.fullArticleLinkCaption;
			this.LANGUAGE_CODE = Application.CONFIG.constants.languageCode;
		}
	}

	@Override
	public void run() {
		try {
			SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(this.config.url)));
			List<SyndEntryImpl> newEntries = ((List<SyndEntryImpl>) feed.getEntries())
				.stream()
				.filter(entry -> !State.getInstance().exists(this.config.url, entry.getLink()))
				.sorted((a, b) -> a.getLink().compareTo(b.getLink()))
				.collect(Collectors.toList());
			log.info(this.config.url + " got " + newEntries.size() + " entries");
			newEntries.forEach(entry -> {
				String text = new StringBuilder()
					.append(entry.getTitle())
					.append("\r\n").append("\r\n")
					.append(Jsoup.parse(entry.getDescription().getValue()).text())
					.append("\r\n").append("\r\n")
					.append(entry.getCategories().stream().map(a -> ((SyndCategory) a).getName()).map(a -> ((String) a).replace(" ", "_")).collect(Collectors.joining(" #", "#", "")))
					.append("\r\n").append("\r\n")
					.append(FULL_ARTICLE_LINK)
					.toString();
				SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
				message.setChatId(this.config.telegramChatId);
				//message.enableMarkdown(true);
				message.setText(text);
				message.setEntities(new ArrayList<>());

				message.getEntities().add(new MessageEntity("bold", 0, entry.getTitle().length(), entry.getLink(), null, "ru", entry.getTitle()));
				message.getEntities().add(new MessageEntity("text_link",
					text.length() - FULL_ARTICLE_LINK.length(),
					FULL_ARTICLE_LINK.length(), entry.getLink(),
					null, LANGUAGE_CODE, FULL_ARTICLE_LINK_CAPTION));
				Sender.getInstance().send(message);
				State.getInstance().save(this.config.url, entry.getLink());
			});

		} catch (Exception ex) {
		}
		this.restartTimer.accept(this.config);
	}

}
