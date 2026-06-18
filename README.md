# 🧾 SplitBill AI - Smart Bill Splitting App

SplitBill is a Java-based Android application designed to simplify the process of splitting restaurant bills among friends. It leverages **Google Gemini AI** to automatically parse receipts and **Firebase Firestore** to store transaction histories and track the payment status of each member.

## ✨ Key Features

* 🤖 **AI Receipt Scanner (OCR):** Upload a photo of your receipt, and the AI will automatically extract the restaurant name, date, list of items, prices, tax, service charge, discounts, and delivery fees.

* 👯 **Friend Management:** Easily add friends who are sharing the bill. Friend data is saved for quick searching via an auto-suggest feature.

* 🍕 **Dynamic Item Splitting:** Assign specific items to specific friends using checkboxes. A single item can even be split equally among multiple people.

* 🧮 **Proportional Calculation:** Taxes, delivery fees, and other extra charges are divided fairly and proportionally based on each person's share of the total item cost.

* ☁️ **Cloud History:** All transaction histories are securely saved to the cloud using Firebase Firestore.

* ✅ **Payment Tracking:** Keep track of who has settled their debt and who hasn't. (History cards turn pastel green when everyone has paid, and remain pastel red if someone still owes money).

## 🛠️ Tech Stack

* **Language:** Java
* **Platform:** Android SDK
* **Backend & Database:** Firebase Firestore (NoSQL)
* **AI & Machine Learning:** Google Gemini 2.5 Flash API
* **Networking:** OkHttp3 (for handling API requests)
* **UI Components:** RecyclerView, ConstraintLayout, CardView, Activity Result API (PickVisualMedia)

## 📸 App Flow

1. **Home Screen (History):** View a list of your previous bill-splitting transactions.
2. **Add New Transaction:**
   * Search for or add the names of friends participating in the bill.
   * Upload a photo of the receipt.
   * Wait a moment as the AI reads the receipt and displays the itemized details.
   * Check the boxes next to friends' names for the items they ordered/shared.
   * Tap **Hitung Patungan Final** (Calculate Final Split).
   * Save the transaction to your Firebase history.
3. **Transaction Details:** Tap on any history record to view the exact amount each person owes. Check a person's name to mark their portion as "Paid".

## 🏗️ Project Architecture

This project follows an Object-Oriented Programming (OOP) approach, separated into the following packages:

* `api/` : Contains `GeminiService` to manage OkHttp connections to the Google Gemini API.
* `data/` : Contains Repositories (`TransactionRepository`, `FriendRepository`) for Firebase Firestore CRUD operations, and `SplitBillCalculator` for the splitting math logic.
* `model/` : Contains data structure objects such as `Transaction`, `ReceiptItem`, and `PersonSummary`.
* `ui/` : Contains the main application Activities (`MainActivity`, `TambahTransaksiActivity`, `DetailTransaksiActivity`).
* `ui/adapter/` : Contains various RecyclerView Adapters (`HistoryAdapter`, `ReceiptItemAdapter`, etc.) to manage list view UI.
* ImageHelper : A utility class to handle image processing, including compressing the bitmap and converting the image to a Base64 string for the Gemini API request.
