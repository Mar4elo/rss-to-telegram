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

    public TimerTaskJob(ConfigRssItem config, Consumer<ConfigRssItem> restartTimer) {
        this.config = config;
        this.restartTimer = restartTimer;
    }

    @Override
    public void run() {
        try {
            SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(this.config.url)));
            List<SyndEntryImpl> newEntries = ((List<SyndEntryImpl>) feed.getEntries())
                    .stream()
                    .filter(entry -> !State.getInstance().exists(this.config.url, entry.getLink()))
                    //.sorted((a, b) -> a.getLink().compareTo(b.getLink()))
                    .collect(Collectors.toList());
            log.info(this.config.url + " got " + newEntries.size() + " entries");
            newEntries.forEach(entry -> {
                SendMessage message = new SendMessageBuilder(this.config.telegramChatId)
                        .setTitleText(entry.getTitle())
                        .setCrLf().setCrLf()
                        .setText(Jsoup.parse(entry.getDescription().getValue()).text())
                        .setCrLf().setCrLf()
                        .setHashTag((List) entry.getCategories().stream().map(a -> ((SyndCategory) a).getName()).collect(Collectors.toList()))
                        .setCrLf().setCrLf()
                        .setLink(entry.getLink())
                        .build();

                Sender.getInstance().send(message);
                State.getInstance().save(this.config.url, entry.getLink());
            });

        } catch (Exception ex) {
            log.error(ex);
        }
        this.restartTimer.accept(this.config);
    }

}
