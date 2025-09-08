#!/bin/bash

# SkillSwap Frontend - Platform Setup Script
# This script sets up the development environment for both iOS and Android

set -e

echo "🚀 SkillSwap Frontend Platform Setup Script"
echo "==========================================="

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if running on macOS
if [[ "$OSTYPE" != "darwin"* ]]; then
    print_error "This script is designed for macOS. For other platforms, please set up manually."
    exit 1
fi

# Check if Node.js is installed
print_status "Checking Node.js installation..."
if ! command -v node &> /dev/null; then
    print_warning "Node.js not found. Installing via Homebrew..."
    
    # Check if Homebrew is installed
    if ! command -v brew &> /dev/null; then
        print_error "Homebrew not found. Please install Homebrew first:"
        echo "https://brew.sh"
        exit 1
    fi
    
    brew install node
    print_success "Node.js installed successfully"
else
    NODE_VERSION=$(node --version)
    print_success "Node.js found: $NODE_VERSION"
fi

# Check if npm is available
if ! command -v npm &> /dev/null; then
    print_error "npm not found. Please reinstall Node.js"
    exit 1
fi

NPM_VERSION=$(npm --version)
print_success "npm found: $NPM_VERSION"

# Check Node.js version requirement
NODE_MAJOR_VERSION=$(node -v | cut -d'.' -f1 | sed 's/v//')
if [ "$NODE_MAJOR_VERSION" -lt 16 ]; then
    print_error "Node.js version 16 or higher is required. Current version: $(node -v)"
    exit 1
fi

# Install npm dependencies
print_status "Installing npm dependencies..."
npm install
print_success "npm dependencies installed"

# Check if we're in a React Native project
if [ ! -f "package.json" ] || ! grep -q "react-native" package.json; then
    print_error "This doesn't appear to be a React Native project"
    exit 1
fi

# iOS Setup
print_status "Setting up iOS development environment..."

# Check if Xcode is installed
if ! command -v xcodebuild &> /dev/null; then
    print_warning "Xcode not found. Please install Xcode from the App Store"
    print_warning "iOS development will not be available until Xcode is installed"
else
    XCODE_VERSION=$(xcodebuild -version | head -n1)
    print_success "Xcode found: $XCODE_VERSION"
    
    # Check if CocoaPods is installed
    if ! command -v pod &> /dev/null; then
        print_warning "CocoaPods not found. Installing..."
        sudo gem install cocoapods
        print_success "CocoaPods installed"
    else
        POD_VERSION=$(pod --version)
        print_success "CocoaPods found: $POD_VERSION"
    fi
    
    # Install iOS dependencies
    if [ -d "ios" ]; then
        print_status "Installing iOS pods..."
        cd ios
        pod install --repo-update
        cd ..
        print_success "iOS pods installed"
    else
        print_warning "iOS directory not found. Run 'npx react-native init' first if this is a new project"
    fi
fi

# Android Setup
print_status "Setting up Android development environment..."

# Check if Android SDK is installed
if [ -z "$ANDROID_HOME" ]; then
    print_warning "ANDROID_HOME not set. Please install Android Studio and set up environment variables:"
    print_warning "export ANDROID_HOME=\$HOME/Library/Android/sdk"
    print_warning "export PATH=\$PATH:\$ANDROID_HOME/emulator"
    print_warning "export PATH=\$PATH:\$ANDROID_HOME/tools"
    print_warning "export PATH=\$PATH:\$ANDROID_HOME/tools/bin"
    print_warning "export PATH=\$PATH:\$ANDROID_HOME/platform-tools"
else
    print_success "ANDROID_HOME found: $ANDROID_HOME"
    
    # Check if Android SDK tools are in PATH
    if command -v adb &> /dev/null; then
        ADB_VERSION=$(adb --version | head -n1)
        print_success "Android SDK tools found: $ADB_VERSION"
    else
        print_warning "Android SDK tools not in PATH. Please add to your PATH"
    fi
    
    # Check Android Gradle setup
    if [ -d "android" ]; then
        print_status "Setting up Android Gradle..."
        cd android
        
        # Clean previous builds
        if [ -f "gradlew" ]; then
            ./gradlew clean
            print_success "Android Gradle setup completed"
        else
            print_warning "gradlew not found in android directory"
        fi
        
        cd ..
    else
        print_warning "Android directory not found"
    fi
fi

# Check if Java is installed (required for Android)
if ! command -v java &> /dev/null; then
    print_warning "Java not found. Please install Java 11 or higher for Android development"
else
    JAVA_VERSION=$(java -version 2>&1 | head -n1)
    print_success "Java found: $JAVA_VERSION"
fi

# Setup development tools
print_status "Setting up development tools..."

# Check if React Native CLI is installed
if ! command -v react-native &> /dev/null; then
    print_status "Installing React Native CLI..."
    npm install -g @react-native-community/cli
    print_success "React Native CLI installed"
else
    RN_CLI_VERSION=$(react-native --version)
    print_success "React Native CLI found: $RN_CLI_VERSION"
fi

# Check if Flipper is available (for debugging)
if command -v flipper &> /dev/null; then
    print_success "Flipper found for debugging"
else
    print_warning "Flipper not found. Consider installing for better debugging experience:"
    print_warning "https://fbflipper.com/"
fi

# Platform-specific optimizations
print_status "Applying platform-specific optimizations..."

# Create iOS optimization script
cat > ios/optimize.sh << 'EOF'
#!/bin/bash
echo "Optimizing iOS build..."

# Clean derived data
rm -rf ~/Library/Developer/Xcode/DerivedData

# Clear iOS cache
rm -rf ~/Library/Caches/com.apple.dt.Xcode

# Update pods
pod repo update
pod install --clean-install

echo "iOS optimization complete"
EOF

chmod +x ios/optimize.sh

# Create Android optimization script
cat > android/optimize.sh << 'EOF'
#!/bin/bash
echo "Optimizing Android build..."

# Clean gradle cache
./gradlew clean
rm -rf .gradle
rm -rf build
rm -rf app/build

# Clear gradle caches
rm -rf ~/.gradle/caches

echo "Android optimization complete"
EOF

chmod +x android/optimize.sh

# Create platform-specific run scripts
print_status "Creating platform run scripts..."

# iOS run script
cat > run-ios.sh << 'EOF'
#!/bin/bash
echo "🍎 Starting iOS Development Server..."

# Check if iOS simulator is available
if ! command -v xcrun &> /dev/null; then
    echo "❌ Xcode not found. Please install Xcode to run iOS simulator"
    exit 1
fi

# Start Metro bundler in background
npm start &
METRO_PID=$!

# Wait for Metro to start
sleep 5

# Run iOS app
npx react-native run-ios --simulator="iPhone 14"

# Cleanup
trap "kill $METRO_PID" EXIT
EOF

chmod +x run-ios.sh

# Android run script
cat > run-android.sh << 'EOF'
#!/bin/bash
echo "🤖 Starting Android Development Server..."

# Check if Android SDK is available
if [ -z "$ANDROID_HOME" ]; then
    echo "❌ ANDROID_HOME not set. Please install Android Studio"
    exit 1
fi

# Check if emulator is running
if ! adb devices | grep -q "emulator"; then
    echo "📱 Starting Android emulator..."
    $ANDROID_HOME/emulator/emulator -avd Pixel_4_API_30 &
    
    # Wait for emulator to boot
    echo "⏳ Waiting for emulator to boot..."
    adb wait-for-device
    sleep 10
fi

# Start Metro bundler in background
npm start &
METRO_PID=$!

# Wait for Metro to start
sleep 5

# Run Android app
npx react-native run-android

# Cleanup
trap "kill $METRO_PID" EXIT
EOF

chmod +x run-android.sh

# Create environment check script
cat > check-env.sh << 'EOF'
#!/bin/bash
echo "🔍 SkillSwap Environment Check"
echo "==============================="

# Node.js
if command -v node &> /dev/null; then
    echo "✅ Node.js: $(node --version)"
else
    echo "❌ Node.js: Not installed"
fi

# npm
if command -v npm &> /dev/null; then
    echo "✅ npm: $(npm --version)"
else
    echo "❌ npm: Not installed"
fi

# React Native CLI
if command -v react-native &> /dev/null; then
    echo "✅ React Native CLI: $(react-native --version | head -n1)"
else
    echo "❌ React Native CLI: Not installed"
fi

# iOS Tools
if command -v xcodebuild &> /dev/null; then
    echo "✅ Xcode: $(xcodebuild -version | head -n1)"
else
    echo "❌ Xcode: Not installed"
fi

if command -v pod &> /dev/null; then
    echo "✅ CocoaPods: $(pod --version)"
else
    echo "❌ CocoaPods: Not installed"
fi

# Android Tools
if [ -n "$ANDROID_HOME" ]; then
    echo "✅ ANDROID_HOME: $ANDROID_HOME"
else
    echo "❌ ANDROID_HOME: Not set"
fi

if command -v adb &> /dev/null; then
    echo "✅ Android SDK: $(adb --version | head -n1)"
else
    echo "❌ Android SDK: Not in PATH"
fi

if command -v java &> /dev/null; then
    echo "✅ Java: $(java -version 2>&1 | head -n1)"
else
    echo "❌ Java: Not installed"
fi

echo ""
echo "📱 Platform Status:"
if [ -d "ios" ] && [ -f "ios/Podfile.lock" ]; then
    echo "✅ iOS: Ready for development"
else
    echo "❌ iOS: Needs setup (run: cd ios && pod install)"
fi

if [ -d "android" ] && [ -f "android/gradlew" ]; then
    echo "✅ Android: Ready for development"
else
    echo "❌ Android: Needs setup"
fi

echo ""
echo "🛠  Available Commands:"
echo "  npm start          - Start Metro bundler"
echo "  ./run-ios.sh       - Run iOS app"
echo "  ./run-android.sh   - Run Android app"
echo "  ./check-env.sh     - Check environment"
echo "  npm run lint       - Run code linting"
echo "  npm test           - Run tests"
EOF

chmod +x check-env.sh

# Final setup verification
print_status "Running final verification..."
./check-env.sh

print_success "🎉 Platform setup completed!"
echo ""
echo "📋 Next Steps:"
echo "  1. Run './check-env.sh' to verify your environment"
echo "  2. Run './run-ios.sh' to start iOS development"
echo "  3. Run './run-android.sh' to start Android development"
echo "  4. Open the project in your favorite editor"
echo ""
echo "📚 Documentation:"
echo "  - Platform Config: docs/PLATFORM-CONFIG.md"
echo "  - Current Progress: docs/CURRENT-PROGRESS-PLATFORM.md"
echo "  - Component Library: src/components/ui/README.md"
echo ""
echo "🚀 Happy coding!"
