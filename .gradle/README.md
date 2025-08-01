# Car Crash Detection MQTT Application

A sophisticated Android application implementing MQTT protocol for emergency car crash detection and response system. The app operates in two modes: **Publisher** (crash victims) and **Subscriber** (emergency responders like police, hospitals, rescue teams).

## 🚨 Features

### Publisher Mode (Crash Victims)
- **Emergency Alert System**: Large SOS button with pulsing animation
- **Medical Profile Management**: Complete medical information setup
- **Location Services**: Real-time GPS location sharing
- **Data Simulation**: Generate realistic crash scenarios for testing
- **MQTT Integration**: Reliable message publishing with QoS

### Subscriber Mode (Emergency Responders)
- **Live Emergency Map**: Real-time crash incident markers
- **Incident Management**: List view with filtering and sorting
- **Navigation Integration**: Direct navigation to incident locations
- **Real-time Updates**: Instant crash alert notifications
- **Medical Information Display**: Victim details and medical data

## 🎨 Design Philosophy

- **Apple-inspired aesthetic**: Clean, minimalist, professional design
- **Premium feel**: Similar to applications used by tech giants
- **Professional emergency app**: Serious, trustworthy, and functional
- **Smooth animations**: 60fps animations with proper optimization

## 🛠 Technical Requirements

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24+ (API Level 24)
- Google Maps API Key
- MQTT Broker (Mosquitto recommended)

### Dependencies
- **MQTT**: Eclipse Paho MQTT Client
- **Maps**: Google Maps API
- **UI**: Material Design 3
- **Animations**: Lottie, Shimmer
- **JSON**: Gson
- **Permissions**: EasyPermissions

## 📱 Installation & Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd CarCrashDetection
```

### 2. Configure Google Maps API
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Enable Maps SDK for Android
4. Create API key
5. Replace `YOUR_GOOGLE_MAPS_API_KEY` in `app/src/main/AndroidManifest.xml`

### 3. Setup MQTT Broker
1. Install Mosquitto MQTT Broker:
   ```bash
   # Ubuntu/Debian
   sudo apt-get install mosquitto mosquitto-clients
   
   # macOS
   brew install mosquitto
   
   # Windows
   # Download from https://mosquitto.org/download/
   ```

2. Start Mosquitto:
   ```bash
   mosquitto -p 1883
   ```

3. Configure broker settings in `MQTTService.kt`:
   ```kotlin
   private const val BROKER_URL = "tcp://localhost:1883"
   ```

### 4. Build and Run
1. Open project in Android Studio
2. Sync Gradle files
3. Build project
4. Run on device or emulator

## 🔧 Configuration

### MQTT Topics
- **Crash Alerts**: `crash/alerts/{region}`
- **Responses**: `crash/responses/{incident_id}`

### Location Settings
- Default location: New York City area
- Location update interval: 10 seconds
- GPS accuracy: High accuracy mode

### Simulation Data
- Random coordinates within city bounds
- Realistic victim information
- Various vehicle types and crash scenarios

## 📋 Usage Guide

### Publisher Mode
1. **Setup Medical Profile**: Complete your medical information
2. **Enable Location**: Allow location permissions
3. **Emergency Alert**: Tap the large SOS button
4. **Simulation**: Use simulation for testing

### Subscriber Mode
1. **View Map**: See real-time crash incidents
2. **Incident Details**: Tap markers for information
3. **Navigation**: Navigate to incident locations
4. **List View**: Switch to list view for detailed information

## 🏗 Architecture

### Core Components
- **Activities**: Main, Publisher, Subscriber, Emergency, Profile
- **Services**: MQTT, Location, Simulation
- **Models**: CrashIncident, VictimInfo
- **UI**: Material Design 3 with custom themes

### Data Flow
1. Publisher detects crash → MQTT publish
2. Subscriber receives alert → Map update
3. Responder navigates → Emergency response

## 🎯 Key Features

### Real-time Communication
- MQTT protocol with QoS 1
- Automatic reconnection
- Message persistence

### Location Services
- High-accuracy GPS
- Background location updates
- Coordinate validation

### Medical Information
- Blood group selection
- Allergies and conditions
- Emergency contacts
- Age and personal details

### Emergency Response
- Instant alert broadcasting
- Location sharing
- Medical data transmission
- Response tracking

## 🔒 Security & Privacy

### Data Protection
- Local data storage
- Secure MQTT connections
- Permission-based access

### Privacy Features
- Optional location sharing
- Medical data encryption
- User consent management

## 🧪 Testing

### Simulation Features
- Random crash generation
- Custom location testing
- Vehicle type selection
- Medical data simulation

### Testing Scenarios
1. **Single Crash**: Basic emergency alert
2. **Multiple Crashes**: Multiple incident handling
3. **Network Issues**: Offline/online scenarios
4. **Location Accuracy**: GPS precision testing

## 📊 Performance

### Optimization
- Efficient MQTT connections
- Battery-optimized location services
- Memory management
- Smooth 60fps animations

### Monitoring
- Connection status indicators
- Location service status
- Message delivery confirmation
- Error handling and logging

## 🚀 Future Enhancements

### Planned Features
- **ESP32 Integration**: Hardware crash detection
- **IoT Sensors**: Real-time vehicle monitoring
- **AI Analysis**: Crash severity prediction
- **Multi-language**: Internationalization
- **Offline Mode**: Local emergency handling

### Scalability
- **Cloud Integration**: AWS/Azure backend
- **Database**: Incident history storage
- **Analytics**: Response time metrics
- **Notifications**: Push notifications

## 🤝 Contributing

1. Fork the repository
2. Create feature branch
3. Make changes
4. Test thoroughly
5. Submit pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the documentation

## ⚠️ Disclaimer

This application is for educational and testing purposes. In real emergency situations, always use official emergency services (911, 112, etc.).

---

**Built with ❤️ for emergency response and public safety** 