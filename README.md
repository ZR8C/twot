# TWOT
A personal project for automatically grabbing latest tweets. Currently employs screen scraping to fetch latest tweets from users and search terms. There is both a multiplatform desktop client and an android client.

## Development
This project is largely unfinished and is probably full of bugs. I intend for this program to only be used by myself. If you are interested in basic screen scraping for twitter then check out com/twot/core/providers/JsoupProvider.kt

## Screenshots

![desktop](https://i.imgur.com/kSy602I.png)
![android](https://i.imgur.com/r1worT9.png)
![android](https://i.imgur.com/auLYPuY.png)

## Stack
This project is written in Kotlin.
- (https://github.com/edvin/tornadofx) TornadoFX is a JavaFX framework for Kotlin, filled with brilliant idiomatic builders and more.
- (https://github.com/requery/requery) Requery for object mapping / sql generation. The query syntax is really nice.
- (https://github.com/ReactiveX/RxJava) RXJava is used on top of Requery to listen for table changes and update UI elements in a reactive fashion.
- (https://github.com/jhy/jsoup) JSoup for scraping twitter data.
- (https://twitrss.me/) TwitRss although not hooked up currently the intention is to utilise this twitter rss service to avoid being rate limited.

