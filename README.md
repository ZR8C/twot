# TWOT
A personal project for automatically grabbing latest tweets. Currently employs screen scraping to fetch latest tweets from users and search terms. There is both a multiplatform desktop client and an android client.

## Development
This project is largely unfinished and is probably full of bugs. I intend for this program to only be used by myself. If you are interested in basic screen scraping for twitter then check out [JsoupProvider](https://github.com/ZR8C/twot/blob/master/core/src/main/kotlin/com/twot/core/providers/JsoupProvider.kt)

## Screenshots

![desktop](https://i.imgur.com/kSy602I.png)
![android](https://i.imgur.com/r1worT9.png)
![android](https://i.imgur.com/auLYPuY.png)

## Stack
This project is written in Kotlin.
- [TornadoFX] (https://github.com/edvin/tornadofx) is a JavaFX framework for Kotlin, filled with brilliant idiomatic builders and more.
- [Requery] (https://github.com/requery/requery) for object mapping / sql generation. The query syntax is really nice.
- [RXJava] (https://github.com/ReactiveX/RxJava) is used on top of Requery to listen for table changes and update UI elements in a reactive fashion.
- [JSoup] (https://github.com/jhy/jsoup) for scraping twitter data.
- [TwitRss] (https://twitrss.me/) although not hooked up currently the intention is to utilise this twitter rss service to avoid being rate limited.

