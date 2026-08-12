# 🧾 Calorie Tracker

A calorie tracker built two ways: a **Java Swing desktop app** (with a live dashboard, daily goal tracking, and CSV save/load) and a **web version** you can open instantly in any browser — styled like a grocery receipt and an FDA nutrition label.

**[▶ Open the live demo](https://YOUR-USERNAME.github.io/YOUR-REPO/)** — no install needed.

*(Replace the link above with your GitHub Pages URL — see [Deploying the web demo](#deploying-the-web-demo) below.)*

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

## Deploying the web demo

1. Push this repo to GitHub.
2. Go to **Settings → Pages**.
3. Under **Build and deployment**, choose **Deploy from a branch**.
4. Pick your default branch (e.g. `main`) and the **`/docs`** folder, then **Save**.
5. GitHub gives you a URL like `https://YOUR-USERNAME.github.io/YOUR-REPO/` within a minute or two — drop that into the top of this README and into your GitHub profile.

No build step, no dependencies — it's static HTML/CSS/JS.

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
