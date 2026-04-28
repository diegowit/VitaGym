# VitaGym 🏋️

VitaGym is a modern Android fitness application designed to help users track their workouts, log gym check-ins, and manage their fitness journey. Built with Jetpack Compose and Firebase, it offers a seamless, responsive, and visually stunning user experience with a dark theme interface.

## ✨ Features

### 🔐 User Authentication
- Secure login and registration using **Firebase Authentication**
- **Google Sign-In** support for quick access
- User-specific data isolation with secure Firestore rules

### 📊 Workout Tracking
- **Dashboard Overview:** View weekly workout statistics, active days, and total workout time
- **Log Workouts:** Easily add custom workouts or choose from predefined exercises (Squats, Push-ups, Planks)
- **AI-Powered Workouts:** Track AI-generated workout plans with detailed exercise breakdowns including reps and sets
- **Workout History:** View and manage all past workouts with color-coded cards by workout type
- **Interactive Date Picker:** Select workout dates with an intuitive calendar interface
- **Edit & Delete:** Update workout details or remove entries from your history
- **Real-time Stats:** Track total workouts, active days, and cumulative workout time

### 🏢 Gym Check-In System
- **QR Code Scanning:** Check in to gyms by scanning unique QR codes
- **Check-In History:** View all your gym visits with timestamps and location details
- **Multi-Location Support:** Track visits across different gym locations
- QR Format: `GYM_ID|Gym Name` (e.g., `VITAGYM01|VitaGym Central`)

### 🎨 Modern UI/UX
- **Dark Theme Design:** Eye-friendly dark interface with cyan (#00E5FF) accents
- **Color-Coded Workouts:** Visual distinction between workout types (Squats = Cyan, Push-ups = Red, Planks = Orange)
- **Smooth Animations:** Polished transitions and loading states
- **Responsive Layout:** Adapts beautifully to different screen sizes
- **Material Design 3:** Following Google's latest design guidelines

## 🛠️ Tech Stack

### Frontend
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern declarative UI toolkit
- **Language:** Kotlin 100%
- **Architecture:** MVVM (Model-View-ViewModel) with Repository pattern
- **Navigation:** Jetpack Compose Navigation

### Backend & Services
- **Authentication:** [Firebase Authentication](https://firebase.google.com/products/auth) with Google Sign-In
- **Database:** [Cloud Firestore](https://firebase.google.com/products/firestore) for real-time data storage
- **Security:** Production-ready Firestore Security Rules with user-scoped access control
- **Indexing:** Composite indexes for efficient querying (userId + date/timestamp)

### Libraries & Tools
- **Image Loading:** Coil for efficient image loading and caching
- **Logging:** Timber for structured logging
- **Maps:** Google Maps Compose for location-based features (in progress)
- **Date/Time:** Kotlinx DateTime for date handling
- **QR Codes:** ML Kit Barcode Scanning for check-in functionality

## 🚀 Getting Started

### Prerequisites

- **Android Studio:** Ladybug or newer (2024.2.1+)
- **Minimum SDK:** API 24 (Android 7.0)
- **Target SDK:** API 34 (Android 14)
- **JDK:** Version 17 or higher
- **Firebase Project:** Set up in the [Firebase Console](https://console.firebase.google.com/)

### Firebase Configuration

1. **Create a Firebase Project:**
    - Go to [Firebase Console](https://console.firebase.google.com/)
    - Create a new project or select an existing one
    - Enable **Authentication** (Email/Password + Google Sign-In)
    - Enable **Cloud Firestore**

2. **Download Configuration File:**
    - In Firebase Console, add an Android app
    - Download `google-services.json`
    - Place it in the `app/` directory

3. **Configure Firestore Security Rules:**
   ```javascript
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       // Workouts collection
       match /workouts/{workoutId} {
         allow read: if request.auth != null && resource.data.userId == request.auth.uid;
         allow create: if request.auth != null && request.resource.data.userId == request.auth.uid;
         allow update, delete: if request.auth != null && resource.data.userId == request.auth.uid;
       }
       
       // Check-ins collection
       match /checkins/{checkinId} {
         allow read: if request.auth != null && resource.data.userId == request.auth.uid;
         allow create: if request.auth != null && request.resource.data.userId == request.auth.uid;
         allow update, delete: if request.auth != null && resource.data.userId == request.auth.uid;
       }
     }
   }
   ```

4. **Create Firestore Composite Indexes:**

   **For Workouts:**
    - Collection: `workouts`
    - Fields: `userId` (Ascending), `date` (Descending)

   **For Check-ins:**
    - Collection: `checkins`
    - Fields: `userId` (Ascending), `timestamp` (Descending)

   *Note: Firebase will provide direct links to create these indexes when you first run queries.*

### Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/MAD-SETU/mad2-ca2-70-diegowit.git
   cd mad2-ca2-70-diegowit
   ```

2. **Open in Android Studio:**
    - Open Android Studio
    - Select "Open an Existing Project"
    - Navigate to the cloned repository

3. **Add `google-services.json`:**
    - Place your Firebase configuration file in `app/google-services.json`

4. **Sync Gradle:**
    - Let Android Studio sync Gradle dependencies
    - Wait for the build to complete

5. **Run the app:**
    - Connect an Android device or start an emulator
    - Click the "Run" button in Android Studio

## 📱 App Structure

```
com.example.vitagym/
├── data/
│   ├── model/           # Data models (Workout, CheckIn, User)
│   └── repository/      # Repository pattern for data operations
├── presentation/
│   ├── auth/            # Authentication screens (Login, Register)
│   ├── dashboard/       # Dashboard with stats and navigation
│   ├── workouts/        # Workout logging and history
│   ├── checkin/         # QR code scanning and check-in history
│   └── theme/           # UI theme and styling
└── viewmodel/           # ViewModels for business logic
```

## 🎯 Key Features Explained

### Workout Types

**Predefined Workouts:**
- **Squats** (Cyan) - Lower body strength
- **Push-ups** (Red) - Upper body strength
- **Planks** (Orange) - Core stability

**Custom Workouts:**
- Create your own workout routines
- Track duration and date
- Add detailed descriptions

**AI-Generated Workouts:**
- Receive personalized workout plans
- Track exercises with specific reps and sets
- View detailed exercise breakdowns

### Dashboard Statistics

- **Total Workouts:** Cumulative count of all logged workouts
- **Active Days:** Number of unique days with workout activity
- **Total Time:** Sum of all workout durations in minutes
- **Weekly Calendar:** Visual representation of workout days

### Security & Privacy

- **User Isolation:** Each user can only access their own data
- **Authenticated Requests:** All database operations require authentication
- **Secure Rules:** Production-ready Firestore security rules prevent unauthorized access
- **Data Validation:** Client and server-side validation for data integrity

## 🔧 Configuration

### Build Variants

- **Debug:** Development build with debugging enabled
- **Release:** Production build with ProGuard optimization

### Gradle Dependencies

Key dependencies include:
- Firebase BoM 33.7.0
- Jetpack Compose BoM 2024.12.01
- Kotlin 2.1.0
- Material3 1.3.1
- Coil 2.5.0

## 🐛 Troubleshooting

### Common Issues

**1. "Repository not found" when pushing to GitHub:**
- Ensure you have proper authentication (Personal Access Token)
- Verify repository URL is correct
- Check your GitHub permissions

**2. "PERMISSION_DENIED" from Firestore:**
- Verify Firestore Security Rules are published
- Ensure user is authenticated
- Check that rules match your collection structure

**3. "FAILED_PRECONDITION" - Index required:**
- Click the link in the error message to create the index
- Wait 1-5 minutes for index to build
- Restart the app after index is enabled

**4. Workouts not appearing in history:**
- Verify Firestore composite indexes are created and enabled
- Check that `userId` field matches authenticated user
- Restart the app to refresh data


## 👤 Author

**Diego**
- GitHub: [@diegowit](https://github.com/diegowit)
- Project: [VitaGym](https://github.com/MAD-SETU/mad2-ca2-70-diegowit)

## 🙏 Acknowledgments

- Firebase for backend infrastructure
- Jetpack Compose for modern UI development
- Material Design for design guidelines
- Google Maps for location services


### Planned Features
- [ ] Workout statistics graphs and charts
- [ ] Social features (share workouts with friends)
- [ ] Workout reminders and notifications
- [ ] Integration with fitness wearables
- [ ] Nutrition tracking
- [ ] Personal trainer AI recommendations
- [ ] Offline mode with local caching
- [ ] Export workout data (CSV, PDF)

### Recent Updates
- ✅ Firebase Firestore security rules implementation
- ✅ Composite indexes for efficient querying
- ✅ QR code check-in system
- ✅ Modern dark theme UI redesign
- ✅ AI workout tracking with exercise details
- ✅ Dashboard statistics and weekly calendar
- ✅ Google Sign-In integration

---

**Made with ❤️ for fitness enthusiasts**