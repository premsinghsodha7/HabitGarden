# HabitGarden

<p align="center"><img src="https://raw.githubusercontent.com/premsinghsodha7/HabitGarden/main/brand-logo.png" alt="HabitGarden logo" width="150"></p>

> A gentle daily habit tracker made with Kotlin and Jetpack Compose.

Set a small daily intention, check in when it's done, and watch a streak grow. Your habits are stored in private app preferences on the device; no account or cloud service is involved.
## App preview

![HabitGarden running on the Android emulator](https://raw.githubusercontent.com/premsinghsodha7/HabitGarden/main/screenshots/app-preview.jpg)

*Live screenshot captured from a Pixel 9 Pro emulator running Android 15 (API 35).*


## Features

- Create and remove habits with a name, emoji, and time goal.
- Check in or undo today's completion.
- Track a consecutive-day streak and today's completion progress.
- View weekly completion insights with a per-day progress chart.
- Choose among Meadow, Sky, and Dusk color themes.
- Pause or enable the in-app reminder preference.
- Keep habit data across app restarts with local preferences.
- Kotlin-only sources under `app/src/main/kotlin`.

## Run

Open this project in Android Studio (JDK 17, Android SDK 35) and run the `app` configuration. From a terminal, run `./gradlew :app:installDebug`. Unit tests: `./gradlew testDebugUnitTest`.

## Structure

```text
app/src/main/kotlin/com/premsingh/habitgarden/
  data/       Local preferences repository and ViewModel
  domain/     Habit model, streak and progress rules
  MainActivity.kt  Today, Insights, Settings, and habit editor
```

GitHub Actions runs unit tests and assembles the debug app on pushes and pull requests.

## Scope

HabitGarden is a personal planning tool, not a health service. It does not collect or send habit data. Streaks are a lightweight motivation aid; a missed day is just a missed day.

## License

MIT. See [LICENSE](LICENSE).
