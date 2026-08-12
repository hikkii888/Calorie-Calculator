# 🧾 Calorie Tracker

A calorie tracker built two ways: a **Java Swing desktop app** (with a live dashboard, daily goal tracking, and CSV save/load) and a **web version** you can open instantly in any browser — styled like a grocery receipt and an FDA nutrition label.

**https://hikkii888.github.io/Calorie-Calculator/** — no install needed.
---

## What's in here

| Folder | What it is |
|---|---|
| `docs/` | The web app — plain HTML/CSS/JS, deployable straight to GitHub Pages |
| `java/` | The Swing desktop app |

Both versions share the same food-tracking model: an abstract `FoodItem` with `Breakfast`, `Lunch`, `Dinner`, and `Snack` subclasses overriding `getCategory()`.

## Features

- Log food with a name, calorie count, and category
- Edit or delete any entry
- Set a daily calorie goal and see live progress
- Dashboard broken down by category (calories + % of daily total)
- **Java version only:** save/load your log as a CSV file
- **Web version only:** your log is saved automatically in the browser (`localStorage`) — nothing is sent anywhere


## Running the Java app locally

Requires a JDK (17+).

```bash
cd java
javac CalorieTrackerGUI.java
java CalorieTrackerGUI
```

## Tech

- **Web:** HTML5, CSS3 (Grid/Flexbox, no framework), vanilla JavaScript (ES classes)
- **Desktop:** Java, Swing (`JFrame`, `JTable`, `GridBagLayout`)
- **OOP concepts demonstrated:** abstraction, inheritance, polymorphism, encapsulation

## License

MIT — feel free to fork and adapt for your own coursework or portfolio.
