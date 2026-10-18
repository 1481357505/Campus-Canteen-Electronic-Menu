# Campus Canteen Electronic Menu App

A comprehensive Android application designed to connect students and canteen managers through a unified dining platform. This app provides real-time access to dining information, crowd monitoring, and review systems specifically tailored for campus environments.

## Features

### For Students
- **Multi-Canteen Menu Browsing**: View dishes from different campus canteens
- **Window-Level Mapping**: See which window serves each dish for easy navigation
- **Real-Time Crowd Monitoring**: Check crowd density and estimated wait times for each window
- **Dish Reviews & Ratings**: Read and write reviews for dishes with 1-5 star ratings
- **Search Functionality**: Find dishes by name, category, or ingredients
- **Favorites System**: Save favorite dishes and windows for quick access
- **Personal Dashboard**: View review history and favorite dishes

### For Managers
- **Dish Management**: Add, edit, and delete dishes with detailed information
- **Window Management**: Manage window information and availability
- **Crowd Status Control**: Update real-time crowd levels and wait times
- **Review Management**: Reply to student reviews and address feedback
- **Negative Review Alerts**: Get notifications for low-rated dishes (≤2 stars)
- **Analytics Dashboard**: Monitor canteen performance and student feedback

## Technical Architecture

### Database Structure
- **Room Database**: Local SQLite database for offline functionality
- **Entities**: User, Canteen, Window, Dish, Review, WindowCrowdStatus, Favorite
- **Relationships**: Proper foreign key relationships between entities

### Key Components
- **Authentication System**: Dual-role login (Student/Manager) with SharedPreferences
- **Repository Pattern**: Centralized data access layer
- **MVVM Architecture**: ViewModels for UI state management
- **Jetpack Compose**: Modern declarative UI framework
- **Navigation Component**: Type-safe navigation between screens
- **WorkManager**: Background tasks for notifications

### Unique Features
1. **Dish-Window Precise Mapping**: Each dish is linked to a specific window for easy location
2. **Window-Level Crowd Monitoring**: Real-time crowd data at window level, not just canteen level
3. **Dual-Role Permission System**: Separate interfaces for students and managers
4. **Integrated Review System**: Reviews linked to specific dishes with manager reply functionality

## Installation & Setup

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24+ (Android 7.0)
- Kotlin 1.9+

### Build Instructions
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Build and run on device/emulator

### Dependencies
- Jetpack Compose for UI
- Room for local database
- Navigation Compose for navigation
- Hilt for dependency injection
- WorkManager for background tasks
- Coil for image loading

## Usage

### Student Workflow
1. **Login/Register**: Create account as student
2. **Browse Canteens**: View available canteens and their status
3. **Select Canteen**: Choose canteen to see windows and dishes
4. **Check Crowd Status**: View real-time crowd levels and wait times
5. **Browse Dishes**: See all dishes with ratings and reviews
6. **Add Reviews**: Rate and comment on dishes
7. **Manage Favorites**: Save favorite dishes and windows

### Manager Workflow
1. **Manager Login**: Access manager dashboard
2. **Dish Management**: Add/edit/delete dishes with detailed information
3. **Window Management**: Manage window information and crowd status
4. **Review Management**: Monitor and reply to student reviews
5. **Crowd Control**: Update real-time crowd levels and wait times
6. **Notifications**: Receive alerts for negative reviews

## Database Schema

### Core Entities
- **Users**: Student and manager accounts with role-based permissions
- **Canteens**: Campus dining locations with operating hours
- **Windows**: Individual service windows within canteens
- **Dishes**: Menu items with pricing, categories, and nutritional info
- **Reviews**: Student ratings and comments with manager replies
- **CrowdStatus**: Real-time window crowd levels and wait times
- **Favorites**: Student-saved dishes and windows

## Key Improvements Over Existing Apps

1. **Campus-Specific Design**: Tailored for university dining environments
2. **Window-Level Precision**: Unlike general dining apps that only show canteen-level info
3. **Integrated Management**: Managers can directly respond to feedback
4. **Real-Time Crowd Data**: Helps students avoid long waits
5. **Comprehensive Review System**: Dish-specific reviews with manager engagement

## Future Enhancements

- **Push Notifications**: Real-time alerts for menu changes and special offers
- **Nutritional Analysis**: Detailed nutritional information and dietary filters
- **Social Features**: Share reviews and dining experiences with friends
- **Analytics Dashboard**: Detailed insights for canteen managers
- **Integration**: Connect with campus card systems and payment methods

## Contributing

This project is designed as a comprehensive campus dining solution. Contributions are welcome for:
- UI/UX improvements
- Additional features
- Performance optimizations
- Bug fixes

## License

This project is developed for educational purposes and campus dining management.
