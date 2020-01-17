# TomcatEchoBot

This is a simple **webhook** bot that sends back JSON it received.

## Run

To run example type in terminal:

```shell
./run.sh --host %HOSTNAME% --port %PORT% --token %BOT_ACCESS_TOKEN%
```

…where `%BOT_ACCESS_TOKEN%` is the bot's token. In case you don't have one, talk to [@PrimeBot](https://tt.me/primebot) to get it.

It will start Tomcat web server on given port.

To make this example working you must have publicly available IP address.
If you do not have this take a look at [ngrok](https://ngrok.com).