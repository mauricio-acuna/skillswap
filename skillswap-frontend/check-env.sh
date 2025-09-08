#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo "🔍 SkillSwap Environment Check"
echo "============================="

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to print check result
print_check() {
    local name="$1"
    local command="$2"
    local required="$3"
    
    if command_exists "$command"; then
        local version=$($command --version 2>/dev/null | head -n 1)
        echo -e "${GREEN}✓${NC} $name: $version"
        return 0
    else
        if [ "$required" = "true" ]; then
            echo -e "${RED}✗${NC} $name: Not found (Required)"
            return 1
        else
            echo -e "${YELLOW}!${NC} $name: Not found (Optional)"
            return 0
        fi
    fi
}

# Track errors
errors=0

echo -e "${BLUE}[INFO]${NC} Checking required tools..."

# Check Node.js
if ! print_check "Node.js" "node" "true"; then
    ((errors++))
fi

# Check npm
if ! print_check "npm" "npm" "true"; then
    ((errors++))
fi

# Check if package.json exists
if [ -f "package.json" ]; then
    echo -e "${GREEN}✓${NC} package.json: Found"
else
    echo -e "${RED}✗${NC} package.json: Not found"
    ((errors++))
fi

# Check if node_modules exists
if [ -d "node_modules" ]; then
    echo -e "${GREEN}✓${NC} node_modules: Found"
else
    echo -e "${YELLOW}!${NC} node_modules: Not found (run 'npm install')"
fi

echo ""
echo -e "${BLUE}[INFO]${NC} Checking optional tools..."

# Check optional tools
print_check "Yarn" "yarn" "false"
print_check "Git" "git" "false"
print_check "Watchman" "watchman" "false"
print_check "CocoaPods" "pod" "false"

# Check React Native CLI
print_check "React Native CLI" "npx react-native" "false"

# Check Expo CLI
print_check "Expo CLI" "npx expo" "false"

echo ""
echo -e "${BLUE}[INFO]${NC} Checking platform-specific tools..."

# macOS specific checks
if [[ "$OSTYPE" == "darwin"* ]]; then
    print_check "Xcode Command Line Tools" "xcode-select" "false"
    print_check "iOS Simulator" "xcrun simctl" "false"
    
    # Check for Android Studio
    if [ -d "/Applications/Android Studio.app" ] || [ -d "$HOME/Applications/Android Studio.app" ]; then
        echo -e "${GREEN}✓${NC} Android Studio: Found"
    else
        echo -e "${YELLOW}!${NC} Android Studio: Not found (Install for Android development)"
    fi
fi

# Check ANDROID_HOME environment variable
if [ -n "$ANDROID_HOME" ]; then
    echo -e "${GREEN}✓${NC} ANDROID_HOME: $ANDROID_HOME"
    
    # Check ADB
    if [ -f "$ANDROID_HOME/platform-tools/adb" ]; then
        echo -e "${GREEN}✓${NC} ADB: Found in ANDROID_HOME"
    else
        echo -e "${YELLOW}!${NC} ADB: Not found in ANDROID_HOME"
    fi
else
    echo -e "${YELLOW}!${NC} ANDROID_HOME: Not set (Set for Android development)"
fi

echo ""
echo -e "${BLUE}[INFO]${NC} Checking project structure..."

# Check important directories
check_dir() {
    if [ -d "$1" ]; then
        echo -e "${GREEN}✓${NC} $1/: Found"
    else
        echo -e "${YELLOW}!${NC} $1/: Not found"
    fi
}

check_dir "src"
check_dir "src/components"
check_dir "src/screens"
check_dir "src/navigation"
check_dir "src/utils"

echo ""

# Final result
if [ $errors -eq 0 ]; then
    echo -e "${GREEN}🎉 Environment check passed!${NC}"
    echo -e "${GREEN}✓${NC} Your environment is ready for SkillSwap development"
    echo ""
    echo -e "${BLUE}Next steps:${NC}"
    echo "1. Run 'npm start' to start Metro bundler"
    echo "2. Run 'npx react-native run-ios' for iOS development"
    echo "3. Run 'npx react-native run-android' for Android development"
    exit 0
else
    echo -e "${RED}❌ Environment check failed with $errors error(s)${NC}"
    echo -e "${YELLOW}Please fix the issues above before continuing${NC}"
    exit 1
fi
