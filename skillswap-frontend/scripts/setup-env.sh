#!/bin/bash

# SkillSwap Frontend Setup Script
# This script sets up the development environment for SkillSwap React Native app

echo "🚀 Setting up SkillSwap Frontend Development Environment..."

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo "❌ Node.js is not installed. Please install Node.js 16+ first."
    echo "Visit: https://nodejs.org/"
    exit 1
fi

# Check Node.js version
NODE_VERSION=$(node -v | cut -d'v' -f2 | cut -d'.' -f1)
if [ "$NODE_VERSION" -lt 16 ]; then
    echo "❌ Node.js version must be 16 or higher. Current version: $(node -v)"
    exit 1
fi

echo "✅ Node.js version: $(node -v)"

# Install dependencies
echo "📦 Installing dependencies..."
npm install

# Install CocoaPods dependencies for iOS (if on macOS)
if [[ "$OSTYPE" == "darwin"* ]]; then
    echo "🍎 Installing iOS dependencies..."
    cd ios && pod install && cd ..
fi

# Set up pre-commit hooks (optional)
echo "🔧 Setting up development tools..."

# Create .env file if it doesn't exist
if [ ! -f ".env" ]; then
    echo "📝 Creating .env file from template..."
    cp .env.example .env
fi

echo ""
echo "🎉 Setup complete! Next steps:"
echo ""
echo "1. Configure your .env file with actual API keys"
echo "2. Start Metro bundler: npm start"
echo "3. Run on iOS: npm run ios"
echo "4. Run on Android: npm run android"
echo ""
echo "📱 For device testing:"
echo "- iOS: Open ios/SkillSwap.xcworkspace in Xcode"
echo "- Android: Open android/ folder in Android Studio"
echo ""
echo "🚀 Happy coding!"
