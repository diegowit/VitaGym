# VitaGym

VitaGym is a modern Android application designed to help users track their workouts and manage their fitness journey. Built with Jetpack Compose and Firebase, it offers a seamless and responsive user experience.

## Features

- **User Authentication:** Secure login and registration using Firebase Authentication, including Google Sign-In support.
- **Workout Dashboard:** Easily add new workouts with details such as title, duration, and date.
- **Workout History:** View and manage your past workouts.
- **Edit & Delete:** Update workout details or remove entries from your history.
- **Date Picker:** Interactive date selection for both adding and editing workouts.
- **Location Services:** (In progress) Integration for gym locations or workout spots.

## Tech Stack

- **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose) for a modern, declarative UI.
- **Architecture:** MVVM (Model-View-ViewModel) for clean separation of concerns.
- **Backend:** [Firebase](https://firebase.google.com/) (Auth & Firestore) for real-time data and authentication.
- **Maps:** Google Maps Compose for location-based features.
- **Language:** Kotlin.
- **DI/Networking/Utils:** Timber for logging, Coil for image loading.

## Getting Started

### Prerequisites

- Android Studio Iguana or newer.
- A Firebase project set up in the Firebase Console.
- `google-services.json` file placed in the `app/` directory.

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/vitagym.git