# SkillSwap - Especificaciones Técnicas Detalladas
## Marketplace de Intercambio de Habilidades P2P - Mercado Europeo

### 📱 COMPATIBILIDAD DE DISPOSITIVOS EUROPEOS

#### Análisis de Market Share Europa (Q2 2024)

**Distribución por País:**
```
🇪🇸 España:     Android 64.2% | iOS 35.8%
🇫🇷 Francia:     Android 71.8% | iOS 28.2%  
🇩🇪 Alemania:    Android 74.1% | iOS 25.9%
🇬🇧 Reino Unido: Android 51.2% | iOS 48.8%
🇮🇹 Italia:      Android 76.3% | iOS 23.7%
```

#### Top Devices Target (95% Market Coverage)

**Android Devices (Priority Order):**
1. **Samsung Galaxy A54 5G** (15% Android market)
   - RAM: 6-8GB | Storage: 128-256GB | CPU: Exynos 1380
   - Screen: 6.4" Super AMOLED | Android 13
   - **Optimization:** Medium-high performance tier

2. **Samsung Galaxy S23** (12% Android market)
   - RAM: 8GB | Storage: 128-256GB | CPU: Snapdragon 8 Gen 2
   - Screen: 6.1" Dynamic AMOLED | Android 13
   - **Optimization:** High performance tier

3. **Xiaomi Redmi Note 12** (8% Android market)
   - RAM: 4-6GB | Storage: 64-128GB | CPU: Snapdragon 685
   - Screen: 6.67" AMOLED | Android 12
   - **Optimization:** Budget-friendly tier

4. **Google Pixel 7/8** (6% Android market)
   - RAM: 8GB | Storage: 128-256GB | CPU: Google Tensor G2/G3
   - Screen: 6.3" OLED | Android 13/14
   - **Optimization:** AI/ML features priority

5. **OnePlus Nord 3** (5% Android market)
   - RAM: 8-16GB | Storage: 128-256GB | CPU: MediaTek Dimensity 9000
   - Screen: 6.74" Fluid AMOLED | Android 13
   - **Optimization:** Performance enthusiast tier

**iOS Devices (Priority Order):**
1. **iPhone 14** (28% iOS market)
   - RAM: 6GB | Storage: 128-512GB | CPU: A15 Bionic
   - Screen: 6.1" Super Retina XDR | iOS 16+
   - **Optimization:** Current flagship tier

2. **iPhone 13** (24% iOS market)
   - RAM: 4GB | Storage: 128-512GB | CPU: A15 Bionic
   - Screen: 6.1" Super Retina XDR | iOS 15+
   - **Optimization:** Stable flagship tier

3. **iPhone 15** (18% iOS market)
   - RAM: 6GB | Storage: 128-512GB | CPU: A16 Bionic
   - Screen: 6.1" Super Retina XDR | iOS 17+
   - **Optimization:** Latest features priority

4. **iPhone 12** (15% iOS market)
   - RAM: 4GB | Storage: 64-256GB | CPU: A14 Bionic
   - Screen: 6.1" Super Retina XDR | iOS 14+
   - **Optimization:** Minimum recommended tier

5. **iPhone SE 3rd Gen** (8% iOS market)
   - RAM: 4GB | Storage: 64-256GB | CPU: A15 Bionic
   - Screen: 4.7" Retina HD | iOS 15+
   - **Optimization:** Compact form factor considerations

---

### 🔧 ESPECIFICACIONES TÉCNICAS MÍNIMAS

#### Soporte de OS (Target: 95% Coverage)

**Android:**
- **Minimum:** Android 8.0 (API Level 26) - Lanzado 2017
- **Target:** Android 13+ (API Level 33)
- **Reason:** Android 8+ cubre 93% del mercado europeo
- **Features perdidas < Android 8:** Notification channels, background limits

**iOS:**
- **Minimum:** iOS 13.0 - iPhone 6s+ (Lanzado 2019)
- **Target:** iOS 16+
- **Reason:** iOS 13+ cubre 97% del mercado europeo
- **Features perdidas < iOS 13:** Dark mode, Sign in with Apple

#### Hardware Requirements

**RAM Minimum:** 3GB (funcional básico)
**RAM Recommended:** 4GB+ (video calling fluido)
**Storage:** 32GB+ (app + cache + videos)
**Network:** 3G minimum, 4G+ recommended para video

#### React Native Configuration

```json
// react-native.config.js
module.exports = {
  project: {
    ios: {
      sourceDir: 'ios/',
      xcodeProject: {
        name: 'SkillSwap.xcodeproj',
        isWorkspace: false,
      }
    },
    android: {
      sourceDir: 'android/',
      packageName: 'com.skillswap.app'
    }
  },
  assets: ['./src/assets/fonts/'],
};
```

```json
// android/app/build.gradle key settings
android {
    compileSdkVersion 34
    buildToolsVersion "34.0.0"
    
    defaultConfig {
        minSdkVersion 26  // Android 8.0
        targetSdkVersion 34
        multiDexEnabled true
        vectorDrawables.useSupportLibrary = true
    }
    
    splits {
        abi {
            enable true
            reset()
            include "arm64-v8a", "armeabi-v7a", "x86_64"
            universalApk false
        }
    }
}
```

```ruby
# ios/Podfile key settings
platform :ios, '13.0'

target 'SkillSwap' do
  config = use_native_modules!
  use_react_native!(
    :path => config[:reactNativePath],
    :hermes_enabled => true,  # Performance boost
    :fabric_enabled => true   # New Architecture
  )
end
```

---

### 🌍 SOPORTE MULTI-IDIOMA EUROPEO

#### Idiomas Soportados (Launch)
1. **🇬🇧 English** (Base language)
2. **🇪🇸 Español** (España + Latinoamérica)
3. **🇫🇷 Français** (Francia + Francophone)
4. **🇩🇪 Deutsch** (Alemania + Austria + Suiza)
5. **🇮🇹 Italiano** (Italia + Suiza)

#### i18n Implementation

```typescript
// src/i18n/index.ts
import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import * as RNLocalize from 'react-native-localize';

// Language resources
import en from './locales/en.json';
import es from './locales/es.json';
import fr from './locales/fr.json';
import de from './locales/de.json';
import it from './locales/it.json';

const resources = { en: { translation: en }, es: { translation: es }, 
                   fr: { translation: fr }, de: { translation: de }, 
                   it: { translation: it } };

const fallback = { languageTag: 'en', isRTL: false };
const { languageTag } = RNLocalize.findBestAvailableLanguage(
  Object.keys(resources)
) || fallback;

i18n.use(initReactI18next).init({
  resources,
  lng: languageTag,
  fallbackLng: 'en',
  interpolation: { escapeValue: false }
});
```

#### European Localization Considerations

**Date/Time Formats:**
- **UK:** DD/MM/YYYY, 24h format
- **Germany:** DD.MM.YYYY, 24h format  
- **France:** DD/MM/YYYY, 24h format
- **Spain:** DD/MM/YYYY, 24h format
- **Italy:** DD/MM/YYYY, 24h format

**Currency Support:**
- **EUR** (Primary for EU countries)
- **GBP** (UK specific)
- Credit system abstracts currency (1 credit = 1 hour)

**Time Zones:**
- **CET/CEST** (Central European Time)
- **GMT/BST** (Greenwich Mean Time)
- **Automatic detection** via device timezone

---

### 🎥 VIDEO CALLING SPECIFICATIONS

#### Performance Requirements per Device Tier

**Budget Tier (Redmi Note 12, iPhone SE):**
- **Resolution:** 480p maximum
- **Framerate:** 24fps
- **Bitrate:** 500 kbps
- **CPU Usage:** < 40%

**Mid Tier (Galaxy A54, iPhone 13):**
- **Resolution:** 720p standard
- **Framerate:** 30fps  
- **Bitrate:** 1-1.5 Mbps
- **CPU Usage:** < 30%

**High Tier (Galaxy S23, iPhone 14+):**
- **Resolution:** 1080p maximum
- **Framerate:** 30fps
- **Bitrate:** 2-3 Mbps
- **CPU Usage:** < 25%

#### WebRTC Configuration

```typescript
// src/services/VideoService.ts
import { RTCPeerConnection, mediaDevices } from 'react-native-webrtc';

const pcConfiguration = {
  iceServers: [
    { urls: 'stun:stun.l.google.com:19302' },
    { urls: 'stun:stun1.l.google.com:19302' },
    // EU-based TURN servers for optimal routing
    { 
      urls: 'turn:eu-turn.skillswap.com:3478',
      username: 'skillswap',
      credential: process.env.TURN_SECRET
    }
  ],
  iceCandidatePoolSize: 10
};

// Adaptive video constraints based on device capability
const getVideoConstraints = (deviceTier: 'budget' | 'mid' | 'high') => {
  const constraints = {
    budget: { width: 640, height: 480, frameRate: 24 },
    mid: { width: 1280, height: 720, frameRate: 30 },
    high: { width: 1920, height: 1080, frameRate: 30 }
  };
  
  return {
    audio: true,
    video: {
      ...constraints[deviceTier],
      facingMode: 'user'
    }
  };
};
```

---

### 🔒 GDPR & PRIVACY COMPLIANCE

#### Data Protection Implementation

**Privacy by Design:**
- Minimal data collection
- Explicit consent for each data type
- Right to erasure implementation
- Data portability features

```typescript
// src/services/GDPRService.ts
export class GDPRService {
  static async requestConsent(dataType: 'profile' | 'location' | 'video') {
    // Show consent modal with clear explanation
    return await ConsentModal.show({
      title: t(`gdpr.${dataType}.title`),
      description: t(`gdpr.${dataType}.description`),
      purposes: t(`gdpr.${dataType}.purposes`),
      canReject: true
    });
  }
  
  static async exportUserData(userId: string) {
    // Generate downloadable JSON with all user data
    const data = await API.getUserData(userId);
    return FileUtils.downloadJSON(data, `skillswap-data-${userId}.json`);
  }
  
  static async deleteUserData(userId: string) {
    // Irreversible data deletion
    await API.deleteUser(userId);
    await AsyncStorage.clear();
    await AuthService.logout();
  }
}
```

#### European Cookie Law Compliance

```typescript
// src/components/CookieConsent.tsx
import CookieManager from '@react-native-cookies/cookies';

export const CookieConsent = () => {
  const [showBanner, setShowBanner] = useState(false);
  
  useEffect(() => {
    checkCookieConsent();
  }, []);
  
  const checkCookieConsent = async () => {
    const consent = await AsyncStorage.getItem('cookie_consent');
    if (!consent) setShowBanner(true);
  };
  
  const acceptCookies = async () => {
    await AsyncStorage.setItem('cookie_consent', 'accepted');
    setShowBanner(false);
  };
  
  // ... render banner with accept/reject options
};
```

---

### 📊 PERFORMANCE MONITORING

#### European CDN Strategy

**CDN Locations:**
- **Frankfurt** (Primary EU)
- **London** (UK specific)
- **Paris** (France/South EU)
- **Amsterdam** (Netherlands/North EU)

#### Monitoring Stack

```yaml
# docker-compose.monitoring.yml
version: '3.8'
services:
  prometheus:
    image: prom/prometheus
    ports: ['9090:9090']
    volumes: ['./prometheus.yml:/etc/prometheus/prometheus.yml']
    
  grafana:
    image: grafana/grafana
    ports: ['3000:3000']
    environment:
      GF_SECURITY_ADMIN_PASSWORD: ${GRAFANA_PASSWORD}
      
  redis-exporter:
    image: oliver006/redis_exporter
    environment:
      REDIS_ADDR: "redis:6379"
      
  postgres-exporter:
    image: prometheuscommunity/postgres-exporter
    environment:
      DATA_SOURCE_NAME: "postgresql://user:pass@postgres:5432/skillswap?sslmode=disable"
```

#### Key Metrics to Track

**Mobile App Performance:**
- App launch time per device model
- Memory usage per session type
- Battery drain during video calls
- Network usage efficiency
- Crash rates by OS version

**Backend Performance:**
- API response times by endpoint
- Database query performance
- Matching algorithm execution time
- Video call connection success rate
- European server latency

**Business Metrics:**
- User acquisition by country
- Feature adoption rates
- Session completion rates
- Revenue per user by tier
- Churn rate analysis

---

### 🚀 DEPLOYMENT STRATEGY

#### Multi-Region European Deployment

```dockerfile
# Dockerfile.backend
FROM openjdk:17-jdk-slim as builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM openjdk:17-jre-slim
WORKDIR /app
COPY --from=builder /app/target/skillswap-backend.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
```

```yaml
# docker-compose.prod.yml
version: '3.8'
services:
  app:
    build: .
    ports: ['8080:8080']
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_HOST: ${DB_HOST}
      REDIS_HOST: ${REDIS_HOST}
      JWT_SECRET: ${JWT_SECRET}
    depends_on: [postgres, redis]
    
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: skillswap
      POSTGRES_USER: ${DB_USER}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes: ['postgres_data:/var/lib/postgresql/data']
    
  redis:
    image: redis:7-alpine
    command: redis-server --appendonly yes
    volumes: ['redis_data:/data']
    
  nginx:
    image: nginx:alpine
    ports: ['80:80', '443:443']
    volumes: 
      - './nginx.conf:/etc/nginx/nginx.conf'
      - '/etc/letsencrypt:/etc/letsencrypt'
    depends_on: [app]

volumes:
  postgres_data:
  redis_data:
```

#### CI/CD Pipeline

```yaml
# .github/workflows/deploy.yml
name: Deploy SkillSwap

on:
  push:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with: { distribution: 'temurin', java-version: '17' }
      - run: ./mvnw test
      
  build-and-deploy:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - run: docker build -t skillswap-backend .
      - run: docker tag skillswap-backend eu.gcr.io/${{ secrets.GCP_PROJECT }}/skillswap-backend
      - uses: google-github-actions/setup-gcloud@v0
        with: { service_account_key: '${{ secrets.GCP_SA_KEY }}' }
      - run: gcloud docker -- push eu.gcr.io/${{ secrets.GCP_PROJECT }}/skillswap-backend
      - run: gcloud run deploy skillswap-backend --image eu.gcr.io/${{ secrets.GCP_PROJECT }}/skillswap-backend --region europe-west1
```

---

*Especificaciones técnicas para SkillSwap - Mercado Europeo*
*Última actualización: 6 de septiembre de 2025*
