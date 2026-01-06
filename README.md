# 🎬 Streaming App Design (Android)

A modern, high-performance **Android video streaming app UI** built using **Java**.  
This project focuses on **complex UI layouts**, **custom RecyclerView adapters**, and **smooth navigation**, designed specifically for the Android ecosystem.

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Android Studio](https://img.shields.io/badge/Android_Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white)

---

## 📱 Features

- **Dynamic Hero Slider**  
  Auto-scrolling / manual banner for featured content using `ViewPager` / `SliderView`

- **Categorized Content Layout**  
  Nested `RecyclerView` structure  
  - Vertical list for sections  
  - Horizontal rows for categories (Trending, Popular, Genres)

- **Movie Detail Screen**  
  Immersive UI with:
  - Movie poster
  - Description
  - Ratings
  - “Watch Now” action

- **Custom ViewHolders**  
  Optimized for image-heavy lists and smooth scrolling

- **Clean Material Design UI**  
  - CardView-based layouts  
  - Custom shapes & gradients  
  - Dark cinematic theme

---

## 🛠 Tech Stack

**Language**
- Java (Native Android)

**UI**
- XML Layouts  
  - ConstraintLayout  
  - RelativeLayout

**Android Components**
- RecyclerView
- CardView

**Image Loading**
- Glide
- Picasso

**SDK Versions**
- Min SDK: **21** (Android 5.0 Lollipop)
- Target SDK: **33+** (Android 13 / 14)

---

## 📂 Project Structure

app/src/main/java/com/example/streamingapp/
├── Activities/ # Screens (Home, Detail)
├── Adapters/ # RecyclerView Adapters
├── Models/ # POJO classes (Movie, Category)
└── Utils/ # Helper & utility classes

app/src/main/res/
├── layout/ # Activity & item XML layouts
├── drawable/ # Shapes, gradients, icons
└── values/ # Themes, colors, strings


---

## 🚀 Getting Started

### Prerequisites
- Android Studio (latest recommended)
- JDK 11 or higher
- Android Emulator or physical device

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Ragav63/Streaming-App-Design-.git

2. **Open in Android Studio**

File → Open

Select the cloned project folder

3. **Build the project**

Wait for Gradle sync to finish

4. **Run the app**

Click the ▶ Run button

## 📸 Screen Previews

<img src="https://github.com/user-attachments/assets/5431372c-0686-4f65-81b7-816ff8e7594c" alt="Screenshot"/>

<img src="https://github.com/user-attachments/assets/314f63fc-c7e6-438c-a678-1756eaad3959" alt="Screenshot"/>

<img src="https://github.com/user-attachments/assets/a7de2b3c-a906-4628-854b-b37fccd100ba" alt="Screenshot"/>

<img src="https://github.com/user-attachments/assets/b94e4ed0-1b59-4d41-b53f-c58b67dc0c37" alt="Screenshot"/>

<img src="https://github.com/user-attachments/assets/92e81803-70b6-4a56-80fa-5ca7c9d699c4" alt="Screenshot"/>


	
## 🏗 Future Enhancements

TMDB API integration for real-time movie data

ExoPlayer integration for video playback

Search functionality

User profile & watchlist

Room / SQLite persistence

## 📦 Dependencies
dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.9.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.recyclerview:recyclerview:1.3.0'
    implementation 'androidx.cardview:cardview:1.0.0'
    implementation 'com.github.bumptech.glide:glide:4.15.1'
    implementation 'com.squareup.picasso:picasso:2.71828'
}

## 📊 Project Analysis
### ✅ Strengths

Clean project structure

Proper separation of concerns

Efficient RecyclerView usage

Material Design compliant UI

### ⚠️ Improvements Needed

Add real screenshots

Add GIF demo

Improve Gradle documentation

Add versioned dependency list

### 🤝 Contributing

Contributions are welcome.
Consider adding:

CONTRIBUTING.md

Issue templates (bug / feature request)

## 👤 Author

Ragav

GitHub: @Ragav63

## 📄 License

This project is licensed under the MIT License.
See the LICENSE file for details.
