# PebbleNotifier

PebbleNotifier is a simple application which forwards *all* of your devices notifications to your Pebble smartwatch.
With a intuitive user interface you can easily mute notifications of applications you're not interested in, so they won't appear on your Pebble.
It is also possible to mute notifications alltogether when the screen is on, or ignore multiple subsequent notifications when they're sent within 60 seconds of eachother.

Do you have a feature request, or a pull request? Don't hesitate to open an issue!

Obviously, Pebble Notifier needs access to your notifications to work. It also needs the official Pebble app to be installed.

## Contributing

You can contribute to this project! Have an amazing feature you'd like to add, or a fix to some bug? Just fork this project, and issue a pull request!
Remember to review the [Contributing guidelines](https://raw.githubusercontent.com/nhaarman/PebbleNotifier/master/CONTRIBUTING.md) before you do.

### Specific notification strategies

Pebble Notifier is easily set up to create custom notification text strategies to extract text from the notification to send to your Pebble. For example, take a look at the
[SpotifyNotificationStrategy](https://github.com/nhaarman/PebbleNotifier/blob/master/app/src/main/java/com/haarman/pebblenotifier/notifications
/strategies/SpotifyNotificationStrategy.java) class. It handles its own logic specific for Spotify notifications, to get the best experience.

You can create your own strategy for a specific app by:

 - Creating a `AppNotificationStrategy` class in the `strategies` package, which implements `NotificationTextStrategy`;
 - Modifying `NotificationTextStrategyFactory` to return a new instance of your class when the package matches.

Created a cool new strategy? Submit a pull request!


## Created by

 - Niek Haarman

## License

    Copyright 2014 Niek Haarman

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.