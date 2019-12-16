# TamTam Bot SDK

TamTam Bot SDK is a simple library built on top of [tamtam-bot-api](https://github.com/tamtam-chat/tamtam-bot-api) that
helps you to develop bots for [TamTam](https://tamtam.chat) quickly.

Both long-polling and webhook are supported.

## Requirements

Minimum required version of Java is 8.

## Long-polling

Long-polling is the easiest way to receive updates for your bot because it does not require running web server.

To start your bot just extends `LongPollingBot` class and implement `onUpdate` method.

For example, simple bot that just does print incoming *update* to system console:

```
public class LoggingBot extends LongPollingBot {
    public LoggingBot(TamTamBotAPI api) {
        super(api, LongPollingBotOptions.DEFAULT);
    }

    @Override
    public void onUpdate(Update update) {
        System.out.println(update);
    }
}
```

As soon as you created instance of this class you should `start` it:

```java
TamTamBotAPI api = TamTamBotAPI.create("%ACCESS_TOKEN%");
LoggingBot bot = new LoggingBot(api);
bot.start();
```
This method starts separated *non-daemon* thread that polls Bot API in cycle.
Call `stop` as soon as you ready to shutdown it:

```java
bot.stop();
```

Check out [EchoBot]() for more complete example.

## Webhooks

TamTamIntegrationTest.setUp

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License

This project is licensed under the [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0).