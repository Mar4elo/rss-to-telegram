# rss-to-telegram
RSS poster to telegram chat

## Requrements
- java8

## Usage
```shell
usage: java -jar rss_to_telegram.jar <arg>
Options
   <arg>     Path to config file
```

## Configuration file
```javascript
{
    "botToken": "bot-TOKEN",  // required
    "botName": "bot-NAME",    // required
    "constants": {
        "fullArticleLink": "Link",
        "fullArticleLinkCaption": "Full article",
        "languageCode": "en"
    },
    "rssData": [
        {
            "telegramChatId": "CHAT_ID",   // required
            "url": "RSS_URL",              // required
            "interval": 10000              // required, milliseconds
        }
    ]
}
```
