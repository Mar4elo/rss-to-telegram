/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.investsoft.rss.to.telegram.engine;

import com.investsoft.rss.to.telegram.Application;
import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

/**
 *
 * @author SMS
 */
public class SendMessageBuilder {

    private final SendMessage message = new SendMessage();
    private boolean SHOW_FULL_ARTICLE_LINK = false;
    private String FULL_ARTICLE_LINK = "Link";
    private String LANGUAGE_CODE = "en";

    public SendMessageBuilder(String chatId) {
        this.message.setChatId(chatId);
        this.message.setEntities(new ArrayList<>());
        if (Application.CONFIG.constants != null) {
            this.SHOW_FULL_ARTICLE_LINK = Application.CONFIG.constants.showFullArticleLink;
            this.FULL_ARTICLE_LINK = Application.CONFIG.constants.fullArticleLink;
            this.LANGUAGE_CODE = Application.CONFIG.constants.languageCode;
        }
    }

    public SendMessageBuilder setTitleText(String title) {
        String _text = this.getText();
        int start_index = _text.length();
        _text = _text.concat(title);
        message.setText(_text);
        message.getEntities().add(new MessageEntity("bold", start_index, title.length(), null, null, LANGUAGE_CODE, title));
        return this;
    }

    public SendMessageBuilder setCrLf() {
        return this.setText("\r\n");
    }

    public SendMessageBuilder setText(String text) {
        String _text = this.getText();
        _text = _text.concat(text);
        message.setText(_text);
        return this;
    }

    public SendMessageBuilder setHashTag(List collect) {
        if (collect != null && !collect.isEmpty()) {
            this.setText("🔖 ️");
            collect.forEach(entry -> {
                String hashTag = "#" + entry.toString().replace(" ", "_");
                String _text = this.getText();
                int start_index = _text.length();
                _text = _text.concat(hashTag);
                message.setText(_text + " ");
                message.getEntities().add(new MessageEntity("hashtag", start_index, hashTag.length(), null, null, LANGUAGE_CODE, hashTag));
            });
        }
        return this;
    }

    public SendMessageBuilder setLink(String link) {
        if (link != null && !link.trim().isEmpty()) {
            String articleLinkTitle = this.getArticleLinkTitle(link);
            String _text = this.getText() + "🔗 ";
            int start_index = _text.length();
            _text = _text.concat(articleLinkTitle);
            message.setText(_text);
            message.getEntities().add(new MessageEntity("bold", start_index, articleLinkTitle.length(), link, null, LANGUAGE_CODE, link));
        }
        return this;
    }

    private String getArticleLinkTitle(String url) {
        return SHOW_FULL_ARTICLE_LINK ? url : FULL_ARTICLE_LINK;
    }

    private String getText() {
        return this.message.getText() == null ? "" : this.message.getText();
    }

    public SendMessage build() {
        return this.message;
    }
}
