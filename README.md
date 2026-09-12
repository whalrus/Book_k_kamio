# Kamio Ledger

A simple native Android app (Kotlin + Jetpack Compose) for tracking the household's
larger monthly expenses and seeing how much is left to save each month.

## Features
- Log expenses with amount, category, note, and date (stored locally in a Room/SQLite database — nothing leaves the phone)
- Set an expected monthly income; the dashboard shows **Income − Tracked spend = Left to save**
- Donut chart of spending by category for the selected month
- Bar chart of total spend over the last 6 months
- Swipe between months, view/delete individual entries

## Opening the project
1. Unzip this project.
2. Open **Android Studio** (Koala/2024.1 or newer) → **Open** → select the `BookKKamio` folder.
3. Android Studio will offer to generate the Gradle wrapper JAR automatically the first time it syncs — accept that (or run `gradle wrapper` once if you have Gradle installed locally). This project only ships `gradle-wrapper.properties`, not the binary `gradle-wrapper.jar`, since it was written outside of Android Studio.
4. Let Gradle sync, then Run ▶ on an emulator or your phone (minSdk 26 / Android 8.0+).

## Project structure
```
app/src/main/java/com/kamio/expensetracker/
  data/        Room entities, DAO, database, repository
  viewmodel/   ExpenseViewModel — exposes dashboard + trend state
  ui/screens/  Dashboard, Add Expense, History, Settings
  ui/components/ Reusable PieChart and BarChart (plain Compose Canvas, no external chart library)
  ui/theme/    Colors and Material 3 theme
```

## Pushing to your GitHub repo
From inside the unzipped `BookKKamio` folder:
```bash
git init
git add .
git commit -m "Initial Kamio Ledger app"
git branch -M main
git remote add origin https://github.com/whalrus/Book_k_kamio.git
git push -u origin main
```
If the remote already has a commit (e.g. an auto-created README from GitHub), run
`git pull --rebase origin main` before the push, or push with `--force` only if you're
sure the remote has nothing you want to keep.

## Ideas for later
- Multi-currency / EUR-JPY split given the Germany + Japan household
- CSV export of a month's expenses
- A shared cloud backend (e.g. Firebase) if you and Yuka want the data synced across two phones
