# RinConvert

A modern Android media converter application built with Jetpack Compose, Hilt, Room, WorkManager, and FFmpeg Kit. This app allows users to convert various media formats (video, audio, images) with ease.

## Features

- 🎥 **Media Conversion**: Convert video, audio, and image files between various formats
- 🎵 **Audio Extraction**: Extract audio from video files
- 🖼️ **Image Conversion**: Convert between image formats
- 📱 **Modern UI**: Built with Jetpack Compose and Material Design 3
- ⚡ **Background Processing**: Uses WorkManager for efficient background processing
- 💾 **History Tracking**: Keeps track of conversion history with Room database
- 🔔 **Notifications**: Notify users when conversions complete
- 🔋 **Battery Efficient**: Uses WorkManager with proper constraints
- 📱 **Foreground Service**: For long-running conversions
- 📥 **URL Download**: Supports downloading media from URLs for conversion
- 🎨 **Modern UI**: Beautiful Material Design 3 interface
- **Virus Test**: [Metadefender Results](https://metadefender.com/results/file/bzI2MDgwNGhiOFJoa1kzczZqTTA4aTlBbXB3_mdaas/overview) , [Virus Total Results](https://www.virustotal.com/gui/file-analysis/MDg0NzAzOGE3ZGRmOWQ2NzdmMWYxM2IzMWUyMGIzOWI6MTc4NTg3NjMwMQ==)

## Screenshots

![App Icon](./app/src/main/res/drawable/app_icon.jpg)


## Features in Detail

### Media Conversion
- Video to video format conversion (MP4, AVI, MKV, MOV, WEBM, etc.)
- Audio extraction from videos (MP3, AAC, WAV, FLAC, OGG, etc.)
- Audio format conversion
- Image format conversion (JPG, PNG, WebP, BMP, GIF, etc.)

### Technical Features
- **Jetpack Compose**: Modern declarative UI toolkit
- **Hilt**: Dependency injection for clean architecture
- **Room Database**: Local storage for conversion history
- **WorkManager**: Reliable background task execution
- **FFmpeg Kit**: Powerful media processing capabilities
- **Coil**: Efficient image loading
- **ExoPlayer**: Media playback capabilities
- **Timber**: Logging utility
- **Navigation Compose**: Navigation between screens

## Architecture

This app follows modern Android architecture principles:

- **Presentation Layer**: Jetpack Compose UI with ViewModels
- **Domain Layer**: Use cases and repository interfaces
- **Data Layer**: Room database, Room DAOs, Repository implementations
- **DI**: Hilt for dependency injection
- **Background Work**: WorkManager for media processing tasks

## Permissions

The app requests the following permissions:
- INTERNET - For downloading media from URLs
- ACCESS_NETWORK_STATE - To check network connectivity
- READ_EXTERNAL_STORAGE / WRITE_EXTERNAL_STORAGE - For accessing storage (legacy)
- MANAGE_EXTERNAL_STORAGE - For Android 11+ scoped storage
- READ_MEDIA_* - For accessing media on Android 13+
- FOREGROUND_SERVICE - For long-running conversion tasks
- WAKE_LOCK - To keep device awake during processing
- POST_NOTIFICATIONS - For conversion completion notifications
- RECEIVE_BOOT_COMPLETED - To reschedule work after device reboot

## Download & Installation

### From GitHub Releases
1. Go to the [Releases](https://github.com/ATKLlama/RinConvert/releases) page to fix this on GitHub.

2. Download the latest APK from the assets section
3. Enable "Install from unknown sources" in your device settings
4. Install the APK

### Building from Source
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/MediaConverterApp.git
   ```
2. Open in Android Studio
3. Build and run on your device or emulator

## FFmpeg Licensing

This app uses [FFmpeg Kit](https://github.com/arthenica/ffmpeg-kit) for media processing. FFmpeg is licensed under the LGPL v2.1 or GPL v2.0.

To comply with FFmpeg licensing:
- The app dynamically links to FFmpeg Kit
- Source code for FFmpeg Kit is available at: https://github.com/arthenica/ffmpeg-kit
- FFmpeg source code is available at: https://ffmpeg.org/download.html

## Privacy Policy

This app respects your privacy:
- No personal data is collected or transmitted
- All media processing happens locally on your device
- Conversion history is stored locally and can be deleted anytime
- Internet permission is only used for downloading media from URLs you specify

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- [Room](https://developer.android.com/training/data-storage/room)
- [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
- [FFmpeg Kit](https://github.com/arthenica/ffmpeg-kit)
- [Coil](https://coil-kt.github.io/coil/)
- [ExoPlayer](https://exoplayer.dev/)
- [Timber](https://github.com/JakeWharton/timber)
- [Material Design 3](https://m3.material.io/)

---

Built with ❤️ for media conversion enthusiasts
