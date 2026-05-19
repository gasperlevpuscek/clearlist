# Pastelist

Pastelist is a lightweight Android task management application designed for straightforward organization and cloud synchronization. It features a structured, boxy UI aesthetic for clear and efficient daily planning.

## Screenshots

| Task Management | Cloud Features | Subtasks |
|---|---|---|
| *Task list with overdue indicators* | *Firebase login & sync* | *Nested task hierarchy* |
| [Screenshot 1 Placeholder] | [Screenshot 2 Placeholder] | [Screenshot 3 Placeholder] |

## Installation & Setup

1.  **Clone the Repository**:
    ```bash
    git clone https://github.com/yourusername/pastelist.git
    ```
2.  **Open in Android Studio**: Launch Android Studio and open the cloned folder.
3.  **Build & Run**: Sync Gradle and run the project on an emulator or physical device.

## Architecture Overview

Pastelist follows a modular fragment-based architecture for seamless navigation and clear separation of concerns.

*   **View Layer**: `MainActivity` uses a `ViewPager2` to manage `TaskFragment`, `UpcomingFragment`, and `SettingsFragment`.
*   **Data Layer**:
    *   **SQLite**: Primary local storage using `DatabaseInsert.java` for offline capabilities.
    *   **Firebase**: Cloud backend for multi-device synchronization.
*   **Navigation**: Modal bottom sheets (`AddTaskSheet.java`, `DateSheet.java`, `ReminderSheet.java`) handle user input without cluttering the main UI.

## Tech Stack

![Java](https://img.shields.io/badge/Language-Java-orange)
![Android SDK](https://img.shields.io/badge/Platform-Android-green)
![SQLite](https://img.shields.io/badge/Database-SQLite-blue)
![Firebase](https://img.shields.io/badge/Backend-Firebase-ffca28)

## Features

### Task Management
*   **Hierarchical Subtasks**: Tasks can be broken down into subtasks with a completion counter and persistent state.
*   **Time & Date Scheduling**: Supports exact date and hour assignment for all tasks.
*   **Fragment-Based Views**:
    *   **Tasks View**: Primary inbox for all active tasks.
    *   **Upcoming View**: Chronological list of tasks with overdue indicators.
    *   **Settings View**: Account management and backup controls.

### Notifications & Reminders
*   **Custom Alerts**: Schedule reminders for the time of the event, 1 hour before, or 1 day before.
*   **Dynamic Labels**: Buttons display selected configurations in real-time.

### Cloud Integration
*   **Authentication**: Email-based login system.
*   **Sync**: One-tap backup and restore between local SQLite and Firebase.

## Known Limitations

*   **Offline Mode**: While tasks are saved locally, an initial connection is required for cloud features.

## Future Improvements

*   [ ] Dark Mode support/color themes.
*   [ ] Real-time background synchronization.
*   [ ] Task categories/folders.
*   [ ] Integration with system Calendar API.

---

## Core Interaction
Tasks are added via a bottom sheet and can be managed through a contextual menu. Completed tasks are handled persistently and cleaned up during app lifecycle events to maintain performance.
