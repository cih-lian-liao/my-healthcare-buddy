#!/bin/bash

# My Healthcare Buddy - Screenshot Capture Script
# This script helps you capture high-quality screenshots for portfolio

echo "🚀 My Healthcare Buddy - Screenshot Capture Setup"
echo "================================================="
echo ""

# Create screenshots directory
mkdir -p screenshots
echo "📁 Created screenshots directory"

# Build the project
echo "🔨 Building the project..."
mvn clean package -q

if [ $? -eq 0 ]; then
    echo "✅ Project built successfully"
else
    echo "❌ Build failed. Please check the error messages above."
    exit 1
fi

echo ""
echo "📸 Screenshot Capture Instructions"
echo "=================================="
echo ""
echo "Now the application will start. Please capture screenshots in this order:"
echo ""
echo "1.  LOGIN SCREEN - Show the authentication interface"
echo "     • Username: test"
echo "     • Password: test123"
echo ""
echo "2.  DASHBOARD/HOMEPAGE - Main navigation hub"
echo "     • Show the welcome message"
echo "     • Show the menu options"
echo ""
echo "3.  HEALTH DATA ENTRY - Form with validation"
echo "     • Show data entry form"
echo "     • Demonstrate input validation"
echo ""
echo "4.  DAILY HABITS - Habit tracking interface"
echo "     • Show calendar selection"
echo "     • Show habit entry form"
echo ""
echo "5.  DATA ANALYSIS - Charts and visualization"
echo "     • Show different chart types"
echo "     • Show trend analysis"
echo ""
echo "6.  PROFILE SETTINGS - User management"
echo "     • Show profile form"
echo "     • Show settings options"
echo ""
echo "💡 Screenshot Tips:"
echo "   • Use high resolution (1920x1080 or higher)"
echo "   • Ensure clear, clean window appearance"
echo "   • Remove any personal information from screenshots"
echo "   • Save as PNG files for best quality"
echo "   • Name files descriptively (e.g., login-screen.png)"
echo ""
echo "🎯 Save all screenshots in the 'screenshots' folder"
echo ""

# Start the application
echo "🚀 Starting application..."
echo ""
echo "Login credentials:"
echo "Username: test"
echo "Password: test123"
echo ""
echo "💡 Press Ctrl+C to stop the application when you're done"
echo ""

java -jar target/my-healthcare-buddy-1.0-SNAPSHOT.jar
