# Pesa Mjanja — Full Build

Everything from the brief is implemented in this pass — all 8 build-order
steps done at once, per your request. This is a complete, runnable Android
Studio project.

## What's built

**Data layer**
- Room database with `Transaction`, `Category` (10 seeded defaults),
  `BudgetPlan` + `CategorySplit`, `SplitBill` + `SplitBillParticipant`, `Debt`
- A DAO + thin Repository per entity group
- `PesaMjanjaApplication` exposes the DB and all repositories as singletons

**Screens (all wired into Compose Navigation)**
- **Onboarding (3a)** — welcome copy, notification-listener permission
  button (`ACTION_NOTIFICATION_LISTENER_SETTINGS`), skip option, and a link
  to the Privacy Policy placeholder
- **HELB/Upkeep Setup (3b)** — pre-fills from an auto-detected "received"
  transaction if one exists, otherwise manual entry; weeks input; adjustable
  percentage sliders (defaulting to the brief's suggested split); "Lock Plan"
  validates the split sums to 100% before saving
- **Home Dashboard (3c)** — weeks remaining, total budget, % spent progress
  bar, amount left, daily spendable amount, category breakdown with progress
  bars, alert banners (fires when a category crosses 80% before the plan's
  midpoint), Ghost Mode toggle (masks amounts, in-memory only, not
  persisted), pending auto-detected transactions as dismissible cards, FAB
  for manual entry
- **Notification Listener + Parser (3d/4)** — `MPesaNotificationListenerService`
  filters by Safaricom package name, hands text to `MpesaMessageParser`
  (all regex in one file, easy to retune), classifies RECEIVED / SENT /
  AIRTIME / BUNDLES / WITHDRAWAL, and inserts an unconfirmed `Transaction`.
  Unmatched notifications are logged (`Log.w`), never crash. Merchant→category
  matching lives in `MerchantCategoryMap` as an editable `Map`, kept separate
  from parsing logic per the brief
- **Confirm/Edit Transaction Card (3e)** — used inline on the dashboard and
  in a dedicated Review screen; one-tap confirm, category dropdown, discard
- **Manual Add Expense (3f)** — amount, category, optional note; saves as
  already-confirmed, same data path as confirmed auto-detected entries
- **Split Bill (3g)** — create a bill with title/total/names, even split
  auto-calculated, paid/unpaid checkboxes per person, "Send Reminder" opens
  a share-intent (SMS/WhatsApp/etc.) with a prefilled message — no STK Push,
  no automated payment
- **Den / Debt Tracker (3h)** — Owed-to-me / I-owe tabs, add debt with
  person/amount/note, mark settled, same share-intent reminder pattern
- **Privacy Policy placeholder** — required for the sensitive-permission
  Play Store listing, linked from Onboarding

**Manifest / permissions**
- `BIND_NOTIFICATION_LISTENER_SERVICE` service declared correctly
- No `READ_SMS` / `RECEIVE_SMS` requested, per the brief

## Explicitly out of scope (left as `// TODO: Phase X` where relevant)
Survival Mode auto-trigger, hustle/income tracker, Chama goal-savings,
meal planning, any Daraja API integration (STK Push, balance checks, or
real money movement), iOS.

## How to run
1. Open the `PesaMjanja/` folder in Android Studio (Koala or newer).
2. Let Gradle sync (wrapper targets Gradle 8.7 — if prompted to regenerate
   `gradlew`/`gradlew.bat`, accept it; those launcher scripts weren't
   generated here since this environment has no network access).
3. Run on an emulator or device, API 26+.
4. Flow to test: Onboarding → (grant or skip notification access) → HELB
   Setup (enter an amount + weeks, adjust sliders to sum to 100%, Lock Plan)
   → Dashboard. Tap the "+" to add a manual expense and watch the category
   bar and top stats update. Toggle Ghost Mode (eye icon) to mask amounts.
   Try Split Bill and the Debt Tracker from the top bar icons.
5. To test real auto-detection you'll need a device/emulator that actually
   receives Safaricom M-Pesa notifications, or you can simulate one by
   posting a test notification from `com.safaricom.mpesa` with body text
   matching the patterns in `MpesaMessageParser.kt`.

## Known follow-ups worth doing before a real release
- Real launcher icon/art (current one is a simple placeholder vector)
- Expand `MpesaMessageParser` regex coverage against real message samples —
  wording varies more than a handful of patterns can fully cover
- Persist Ghost Mode preference if you want it to survive app restarts
  (currently in-memory only, matching the brief)
- Fill in the real privacy policy text before any Play Store submission
