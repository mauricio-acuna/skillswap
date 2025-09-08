# 🚀 CI/CD Pipeline - SkillSwap
## Continuous Integration & Deployment Strategy

**Última actualización:** 6 de septiembre de 2025  
**Status:** 🟢 Ready for Implementation  
**Target:** Full automation from commit to production

---

## 🎯 CI/CD Overview

### **Pipeline Architecture**
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Commit    │ ──►│   Build     │ ──►│    Test     │ ──►│   Deploy    │
│             │    │             │    │             │    │             │
│ • Backend   │    │ • Spring    │    │ • Unit      │    │ • Staging   │
│ • Frontend  │    │ • React     │    │ • Integration│    │ • Production│
│ • Docs      │    │ • Docker    │    │ • E2E       │    │ • App Store │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
```

### **Technology Stack**
- **CI/CD Platform:** GitHub Actions
- **Container Registry:** Docker Hub / AWS ECR
- **Cloud Provider:** AWS (ECS, RDS, S3)
- **App Distribution:** App Store Connect, Google Play Console
- **Monitoring:** DataDog, Sentry

---

## 🔧 GitHub Actions Workflows

### **1. Backend CI/CD Pipeline**

#### **.github/workflows/backend-ci-cd.yml**
```yaml
name: Backend CI/CD

on:
  push:
    branches: [ main, develop ]
    paths: 
      - 'skillswap-backend/**'
  pull_request:
    branches: [ main ]
    paths:
      - 'skillswap-backend/**'

env:
  REGISTRY: docker.io
  IMAGE_NAME: skillswap/backend

jobs:
  test:
    runs-on: ubuntu-latest
    
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_DB: skillswap_test
          POSTGRES_USER: test
          POSTGRES_PASSWORD: test
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

    steps:
    - name: Checkout code
      uses: actions/checkout@v4

    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'

    - name: Cache Maven dependencies
      uses: actions/cache@v3
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
        restore-keys: ${{ runner.os }}-m2

    - name: Run unit tests
      run: |
        cd skillswap-backend
        ./mvnw test

    - name: Run integration tests
      run: |
        cd skillswap-backend
        ./mvnw verify -P integration-tests
      env:
        SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/skillswap_test
        SPRING_DATASOURCE_USERNAME: test
        SPRING_DATASOURCE_PASSWORD: test

    - name: Generate test coverage report
      run: |
        cd skillswap-backend
        ./mvnw jacoco:report

    - name: Upload coverage to Codecov
      uses: codecov/codecov-action@v3
      with:
        file: skillswap-backend/target/site/jacoco/jacoco.xml

    - name: SonarQube analysis
      uses: sonarqube-quality-gate-action@master
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}

  build-and-push:
    needs: test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'

    steps:
    - name: Checkout code
      uses: actions/checkout@v4

    - name: Set up Docker Buildx
      uses: docker/setup-buildx-action@v3

    - name: Login to Docker Hub
      uses: docker/login-action@v3
      with:
        username: ${{ secrets.DOCKER_USERNAME }}
        password: ${{ secrets.DOCKER_PASSWORD }}

    - name: Extract metadata
      id: meta
      uses: docker/metadata-action@v5
      with:
        images: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}
        tags: |
          type=ref,event=branch
          type=sha,prefix={{branch}}-
          type=raw,value=latest,enable={{is_default_branch}}

    - name: Build and push Docker image
      uses: docker/build-push-action@v5
      with:
        context: ./skillswap-backend
        push: true
        tags: ${{ steps.meta.outputs.tags }}
        labels: ${{ steps.meta.outputs.labels }}
        cache-from: type=gha
        cache-to: type=gha,mode=max

  deploy-staging:
    needs: build-and-push
    runs-on: ubuntu-latest
    environment: staging

    steps:
    - name: Deploy to AWS ECS Staging
      uses: aws-actions/amazon-ecs-deploy-task-definition@v1
      with:
        task-definition: .aws/task-definition-staging.json
        service: skillswap-backend-staging
        cluster: skillswap-staging
        wait-for-service-stability: true

    - name: Run smoke tests
      run: |
        curl -f https://api-staging.skillswap.com/actuator/health
        # Add more smoke tests

  deploy-production:
    needs: deploy-staging
    runs-on: ubuntu-latest
    environment: production
    if: github.ref == 'refs/heads/main'

    steps:
    - name: Deploy to AWS ECS Production
      uses: aws-actions/amazon-ecs-deploy-task-definition@v1
      with:
        task-definition: .aws/task-definition-production.json
        service: skillswap-backend-production
        cluster: skillswap-production
        wait-for-service-stability: true

    - name: Run production health checks
      run: |
        curl -f https://api.skillswap.com/actuator/health
        # Add comprehensive health checks

    - name: Notify deployment success
      uses: 8398a7/action-slack@v3
      with:
        status: success
        text: "🚀 Backend deployed successfully to production!"
      env:
        SLACK_WEBHOOK_URL: ${{ secrets.SLACK_WEBHOOK }}
```

### **2. Frontend CI/CD Pipeline**

#### **.github/workflows/frontend-ci-cd.yml**
```yaml
name: Frontend CI/CD

on:
  push:
    branches: [ main, develop ]
    paths: 
      - 'skillswap-frontend/**'
  pull_request:
    branches: [ main ]
    paths:
      - 'skillswap-frontend/**'

jobs:
  test:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout code
      uses: actions/checkout@v4

    - name: Setup Node.js
      uses: actions/setup-node@v4
      with:
        node-version: '18'
        cache: 'npm'
        cache-dependency-path: skillswap-frontend/package-lock.json

    - name: Install dependencies
      run: |
        cd skillswap-frontend
        npm ci

    - name: Run linting
      run: |
        cd skillswap-frontend
        npm run lint

    - name: Run type checking
      run: |
        cd skillswap-frontend
        npm run type-check

    - name: Run unit tests
      run: |
        cd skillswap-frontend
        npm run test:coverage

    - name: Upload coverage to Codecov
      uses: codecov/codecov-action@v3
      with:
        file: skillswap-frontend/coverage/lcov.info

    - name: Build for testing
      run: |
        cd skillswap-frontend
        npm run build:staging

  e2e-tests:
    needs: test
    runs-on: ubuntu-latest

    steps:
    - name: Checkout code
      uses: actions/checkout@v4

    - name: Setup Node.js
      uses: actions/setup-node@v4
      with:
        node-version: '18'
        cache: 'npm'
        cache-dependency-path: skillswap-frontend/package-lock.json

    - name: Install dependencies
      run: |
        cd skillswap-frontend
        npm ci

    - name: Start backend services
      run: |
        docker-compose -f docker-compose.test.yml up -d

    - name: Wait for services
      run: |
        npx wait-on http://localhost:8080/actuator/health

    - name: Run E2E tests
      run: |
        cd skillswap-frontend
        npm run test:e2e:ci

    - name: Upload E2E test results
      uses: actions/upload-artifact@v3
      if: always()
      with:
        name: e2e-results
        path: skillswap-frontend/e2e/results/

  build-ios:
    needs: [test, e2e-tests]
    runs-on: macos-latest
    if: github.ref == 'refs/heads/main'

    steps:
    - name: Checkout code
      uses: actions/checkout@v4

    - name: Setup Node.js
      uses: actions/setup-node@v4
      with:
        node-version: '18'
        cache: 'npm'
        cache-dependency-path: skillswap-frontend/package-lock.json

    - name: Install dependencies
      run: |
        cd skillswap-frontend
        npm ci

    - name: Setup Ruby
      uses: ruby/setup-ruby@v1
      with:
        ruby-version: '3.0'
        bundler-cache: true
        working-directory: skillswap-frontend/ios

    - name: Install CocoaPods
      run: |
        cd skillswap-frontend/ios
        pod install

    - name: Build iOS app
      run: |
        cd skillswap-frontend
        npx react-native bundle --platform ios --dev false --entry-file index.js --bundle-output ios/main.jsbundle

    - name: Build for App Store
      run: |
        cd skillswap-frontend/ios
        xcodebuild -workspace SkillSwap.xcworkspace -scheme SkillSwap -configuration Release -archivePath SkillSwap.xcarchive archive

    - name: Upload to App Store Connect
      env:
        APP_STORE_CONNECT_API_KEY: ${{ secrets.APP_STORE_CONNECT_API_KEY }}
      run: |
        cd skillswap-frontend/ios
        xcrun altool --upload-app --type ios --file SkillSwap.ipa --apiKey $APP_STORE_CONNECT_API_KEY

  build-android:
    needs: [test, e2e-tests]
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'

    steps:
    - name: Checkout code
      uses: actions/checkout@v4

    - name: Setup Node.js
      uses: actions/setup-node@v4
      with:
        node-version: '18'
        cache: 'npm'
        cache-dependency-path: skillswap-frontend/package-lock.json

    - name: Setup JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'

    - name: Setup Android SDK
      uses: android-actions/setup-android@v2

    - name: Install dependencies
      run: |
        cd skillswap-frontend
        npm ci

    - name: Build Android APK
      run: |
        cd skillswap-frontend/android
        ./gradlew assembleRelease

    - name: Sign APK
      uses: r0adkll/sign-android-release@v1
      with:
        releaseDirectory: skillswap-frontend/android/app/build/outputs/apk/release
        signingKeyBase64: ${{ secrets.ANDROID_SIGNING_KEY }}
        alias: ${{ secrets.ANDROID_KEY_ALIAS }}
        keyStorePassword: ${{ secrets.ANDROID_KEYSTORE_PASSWORD }}
        keyPassword: ${{ secrets.ANDROID_KEY_PASSWORD }}

    - name: Upload to Google Play
      uses: r0adkll/upload-google-play@v1
      with:
        serviceAccountJsonPlainText: ${{ secrets.GOOGLE_PLAY_SERVICE_ACCOUNT }}
        packageName: com.skillswap.app
        releaseFiles: skillswap-frontend/android/app/build/outputs/apk/release/app-release-signed.apk
        track: production
```

### **3. Documentation CI Pipeline**

#### **.github/workflows/docs-ci.yml**
```yaml
name: Documentation CI

on:
  push:
    branches: [ main ]
    paths: 
      - 'shared-docs/**'
  pull_request:
    branches: [ main ]
    paths:
      - 'shared-docs/**'

jobs:
  validate-docs:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout code
      uses: actions/checkout@v4

    - name: Setup Node.js
      uses: actions/setup-node@v4
      with:
        node-version: '18'

    - name: Install markdown linter
      run: npm install -g markdownlint-cli

    - name: Lint markdown files
      run: |
        markdownlint shared-docs/**/*.md

    - name: Validate API contracts
      run: |
        npx swagger-cli validate shared-docs/api-specs/skillswap-api.yaml

    - name: Check links
      run: |
        npx markdown-link-check shared-docs/**/*.md

  deploy-docs:
    needs: validate-docs
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'

    steps:
    - name: Checkout code
      uses: actions/checkout@v4

    - name: Deploy to GitHub Pages
      uses: peaceiris/actions-gh-pages@v3
      with:
        github_token: ${{ secrets.GITHUB_TOKEN }}
        publish_dir: ./shared-docs/technical-wiki
        cname: docs.skillswap.com

  notify-updates:
    needs: deploy-docs
    runs-on: ubuntu-latest

    steps:
    - name: Notify team of documentation updates
      uses: 8398a7/action-slack@v3
      with:
        status: success
        text: "📚 Documentation updated: https://docs.skillswap.com"
      env:
        SLACK_WEBHOOK_URL: ${{ secrets.SLACK_WEBHOOK }}
```

---

## 🐳 Docker Configuration

### **Backend Dockerfile**
```dockerfile
# skillswap-backend/Dockerfile
FROM openjdk:17-jdk-slim as builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN ./mvnw clean package -DskipTests

FROM openjdk:17-jre-slim

RUN groupadd -r skillswap && useradd -r -g skillswap skillswap

WORKDIR /app
COPY --from=builder /app/target/skillswap-backend-*.jar app.jar

RUN chown -R skillswap:skillswap /app
USER skillswap

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### **Frontend Dockerfile**
```dockerfile
# skillswap-frontend/Dockerfile
FROM node:18-slim as builder

WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production

COPY . .
RUN npm run build

FROM nginx:alpine

COPY --from=builder /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:80 || exit 1

CMD ["nginx", "-g", "daemon off;"]
```

---

## ☁️ AWS Infrastructure

### **ECS Task Definition**
```json
{
  "family": "skillswap-backend-production",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "512",
  "memory": "1024",
  "executionRoleArn": "arn:aws:iam::ACCOUNT:role/ecsTaskExecutionRole",
  "taskRoleArn": "arn:aws:iam::ACCOUNT:role/ecsTaskRole",
  "containerDefinitions": [
    {
      "name": "skillswap-backend",
      "image": "skillswap/backend:latest",
      "portMappings": [
        {
          "containerPort": 8080,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "production"
        }
      ],
      "secrets": [
        {
          "name": "DATABASE_URL",
          "valueFrom": "arn:aws:secretsmanager:region:account:secret:skillswap/database-url"
        },
        {
          "name": "JWT_SECRET",
          "valueFrom": "arn:aws:secretsmanager:region:account:secret:skillswap/jwt-secret"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/skillswap-backend",
          "awslogs-region": "eu-west-1",
          "awslogs-stream-prefix": "ecs"
        }
      },
      "healthCheck": {
        "command": [
          "CMD-SHELL",
          "curl -f http://localhost:8080/actuator/health || exit 1"
        ],
        "interval": 30,
        "timeout": 5,
        "retries": 3,
        "startPeriod": 60
      }
    }
  ]
}
```

---

## 📱 App Store Deployment

### **iOS App Store Connect**
```yaml
# .github/workflows/ios-release.yml
name: iOS Release

on:
  push:
    tags:
      - 'v*.*.*'

jobs:
  release-ios:
    runs-on: macos-latest

    steps:
    - name: Checkout code
      uses: actions/checkout@v4

    - name: Setup Xcode
      uses: maxim-lobanov/setup-xcode@v1
      with:
        xcode-version: latest-stable

    - name: Install dependencies
      run: |
        cd skillswap-frontend
        npm ci
        cd ios && pod install

    - name: Build and archive
      run: |
        cd skillswap-frontend/ios
        xcodebuild -workspace SkillSwap.xcworkspace \
                   -scheme SkillSwap \
                   -configuration Release \
                   -archivePath SkillSwap.xcarchive \
                   archive

    - name: Export IPA
      run: |
        cd skillswap-frontend/ios
        xcodebuild -exportArchive \
                   -archivePath SkillSwap.xcarchive \
                   -exportPath . \
                   -exportOptionsPlist ExportOptions.plist

    - name: Upload to App Store Connect
      env:
        APPLE_ID: ${{ secrets.APPLE_ID }}
        APPLE_APP_SPECIFIC_PASSWORD: ${{ secrets.APPLE_APP_SPECIFIC_PASSWORD }}
      run: |
        cd skillswap-frontend/ios
        xcrun altool --upload-app \
                     --type ios \
                     --file SkillSwap.ipa \
                     --username $APPLE_ID \
                     --password $APPLE_APP_SPECIFIC_PASSWORD
```

### **Android Google Play**
```yaml
# .github/workflows/android-release.yml
name: Android Release

on:
  push:
    tags:
      - 'v*.*.*'

jobs:
  release-android:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout code
      uses: actions/checkout@v4

    - name: Setup JDK
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'

    - name: Setup Node.js
      uses: actions/setup-node@v4
      with:
        node-version: '18'

    - name: Install dependencies
      run: |
        cd skillswap-frontend
        npm ci

    - name: Build Android bundle
      run: |
        cd skillswap-frontend/android
        ./gradlew bundleRelease

    - name: Sign bundle
      uses: r0adkll/sign-android-release@v1
      with:
        releaseDirectory: skillswap-frontend/android/app/build/outputs/bundle/release
        signingKeyBase64: ${{ secrets.ANDROID_SIGNING_KEY }}
        alias: ${{ secrets.ANDROID_KEY_ALIAS }}
        keyStorePassword: ${{ secrets.ANDROID_KEYSTORE_PASSWORD }}
        keyPassword: ${{ secrets.ANDROID_KEY_PASSWORD }}

    - name: Upload to Google Play
      uses: r0adkll/upload-google-play@v1
      with:
        serviceAccountJsonPlainText: ${{ secrets.GOOGLE_PLAY_SERVICE_ACCOUNT }}
        packageName: com.skillswap.app
        releaseFiles: skillswap-frontend/android/app/build/outputs/bundle/release/app-release.aab
        track: production
        status: completed
```

---

## 📊 Monitoring & Alerts

### **Health Checks**
```yaml
# docker-compose.monitoring.yml
version: '3.8'

services:
  backend:
    image: skillswap/backend:latest
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s

  prometheus:
    image: prom/prometheus:latest
    ports:
      - "9090:9090"
    volumes:
      - ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml

  grafana:
    image: grafana/grafana:latest
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
    volumes:
      - ./monitoring/grafana/dashboards:/var/lib/grafana/dashboards

  alertmanager:
    image: prom/alertmanager:latest
    ports:
      - "9093:9093"
    volumes:
      - ./monitoring/alertmanager.yml:/etc/alertmanager/alertmanager.yml
```

### **Slack Notifications**
```yaml
# monitoring/alertmanager.yml
global:
  slack_api_url: '$SLACK_WEBHOOK_URL'

route:
  group_by: ['alertname']
  group_wait: 10s
  group_interval: 10s
  repeat_interval: 1h
  receiver: 'slack-notifications'

receivers:
- name: 'slack-notifications'
  slack_configs:
  - channel: '#skillswap-alerts'
    title: '🚨 SkillSwap Alert'
    text: '{{ range .Alerts }}{{ .Annotations.summary }}{{ end }}'
    send_resolved: true
```

---

## ✅ CI/CD Success Metrics

### **Quality Gates**
- ✅ **Test Coverage:** > 85% for all components
- ✅ **Build Time:** < 10 minutes for full pipeline
- ✅ **Deployment Time:** < 5 minutes to production
- ✅ **Zero Downtime:** Blue-green deployment strategy

### **Performance Targets**
- ✅ **API Response Time:** < 500ms p95
- ✅ **App Launch Time:** < 3 seconds
- ✅ **Error Rate:** < 0.1% in production
- ✅ **Availability:** 99.9% uptime SLA

---

*Última actualización: 6 de septiembre de 2025*  
*Próxima revisión: Con cada release major*

## 🎯 Ready for Production!

Complete CI/CD pipeline ensuring quality, security, and seamless deployment from development to production across all platforms.

**🚀 Let's automate everything! ⚙️**
