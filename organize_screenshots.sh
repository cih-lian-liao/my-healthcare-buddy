#!/bin/bash

# My Healthcare Buddy - Screenshot Organization Script

echo "📸 Screenshot Organization Helper"
echo "================================="

# Create screenshot directories
mkdir -p docs/screenshots
mkdir -p screenshots

echo "📁 Created screenshot directories:"
echo "   📂 docs/screenshots/ (for README.md)"
echo "   📂 screenshots/ (for general use)  "

echo ""
echo "🎯 Recommended screenshot workflow:"
echo ""
echo "1. Take screenshots of your application:"
echo "   • Login screen"
echo "   • Main dashboard" 
echo "   • Health data entry"
echo "   • Data analysis charts"
echo "   • Daily habits page"
echo "   • Profile settings"
echo ""
echo "2. Name them descriptively:"
echo "   • login-screen.png"
echo "   • main-dashboard.png"
echo "   • health-data-form.png"
echo "   • data-visualization.png"
echo "   • daily-habits.png"
echo "   • profile-settings.png"
echo ""
echo "3. Place them in docs/screenshots/ for README.md"
echo ""
echo "📏 Recommended size: 1200x800 pixels"
echo "💾 Recommended format: PNG"
echo ""
echo "🎨 Screenshot tips:"
echo "   • Use clean, professional backgrounds"
echo "   • Remove personal information"
echo "   • Show clear, readable text"
echo "   • Capture complete interfaces"
echo "   • Use consistent screenshot dimensions"
echo ""
echo "📋 After adding screenshots, run:"
echo "   git add docs/screenshots/"
echo "   git commit -m '📸 Add application screenshots'"
echo "   git push origin main"
echo ""
echo "Ready for screenshot organization! 🎉"
