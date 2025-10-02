# My Healthcare Buddy - Installation Guide

## 📋 System Requirements

### Required Software
- **Java Runtime Environment (JRE) 11** or higher
- **Java Development Kit (JDK) 11** (for developers)
- **Apache Maven 3.6+** (for developers)

### Supported Operating Systems
- Windows 10/11
- macOS 10.15+
- Linux (Ubuntu 18+, CentOS 7+, etc.)

## 🚀 Installation Steps

### Method 1: Using Pre-compiled Version (Recommended)

1. **Download JAR File**
   ```bash
   # Download latest version from GitHub Releases
   wget https://github.com/cih-lian-liao/my-healthcare-buddy/releases/latest/download/my-healthcare-buddy-1.0-SNAPSHOT.jar
   ```

2. **Run Application**
   ```bash
   java -jar my-healthcare-buddy-1.0-SNAPSHOT.jar
   ```

### Method 2: Compile from Source

1. **Clone Repository**
   ```bash
   git clone https://github.com/cih-lian-liao/my-healthcare-buddy.git
   cd my-healthcare-buddy
   ```

2. **Build Project**
   ```bash
   mvn clean package
   ```

3. **Run Application**
   ```bash
   # Using Maven
   mvn exec:java -Dexec.mainClass="com.healthbuddy.Main"
   
   # Or run JAR directly
   java -jar target/my-healthcare-buddy-1.0-SNAPSHOT.jar
   ```

## 🛠️ Development Environment Setup

### IDE Setup

#### IntelliJ IDEA
1. Open Project: `File → Open`
2. Select project's `pom.xml` file
3. IDE will automatically recognize it as Maven project
4. Wait for dependencies to download

#### Eclipse
1. Import Project: `File → Import → Maven → Existing Maven Projects`
2. Select project directory
3. Click `Finish`

#### Visual Studio Code
1. Install Java Extension Pack
2. Open project folder
3. Press `Ctrl+Shift+P`, select `Java: Reload Projects`

### Development Tools Recommended
- **JDK**: Oracle JDK 11 or OpenJDK 11
- **Maven**: Auto-install or manually install 3.6+
- **Git**: For version control

## ⚙️ Configuration Options

### Application Configuration
Edit `src/main/resources/application.properties`:

```properties
# Database Settings
database.name=health_buddy
database.path=health_buddy.db

# Application Settings
app.name=My Healthcare Buddy
app.version=1.0.0
app.mode=production

# Security Settings
security.password.min-length=6
security.password.require-special-chars=false

# Data Validation
validation.weight.min=20.0
validation.weight.max=300.0
validation.height.min=100.0
validation.height.max=250.0
```

## 🔧 Troubleshooting

### Common Issues

#### 1. Java Version Problem
```bash
# Check Java version
java -version

# Should display version 11 or higher
# If not, please install correct Java version
```

#### 2. Maven Issues
```bash
# Clean and recompile
mvn clean
mvn compile
```

#### 3. Database Connection Issues
- Ensure write permissions in project directory
- Check if folder is blocked by antivirus software
- Ensure `health_buddy.db` file is not corrupted

#### 4. Out of Memory
```bash
# Increase JVM memory
java -Xmx512m -jar target/my-healthcare-buddy-1.0-SNAPSHOT.jar
```

### Windows-Specific Issues
- Make sure Windows Defender allows the application
- Run as administrator if permission issues occur

### macOS-Specific Issues
- You may need to allow the application in Security & Privacy settings
- If Java not found, run: `export JAVA_HOME=/usr/libexec/java_home`

### Linux-Specific Issues
- Install OpenJDK: `sudo apt-get install openjdk-11-jdk` (Ubuntu/Debian)
- Set JAVA_HOME: `export JAVA_HOME=/usr/lib/jvm/java-11-openjdk`

## 📱 User Guide

### First Use
1. Launch the application
2. Login with default account:
   - Username: `test`
   - Password: `test123`
3. Or click "Sign Up" to create new account
4. Set up your profile in Profile Settings

### Data Entry
1. **Health Data**: Weight, BMI, Steps, Blood Pressure, Heart Rate
2. **Daily Habits**: Water intake, Diet choices, Sleep hours
3. **Goal Setting**: Set targets in Profile Settings

### Data Analysis
- View chart trends
- Export data as CSV format
- Compare data across different periods

## 🔒 Security & Privacy

### Data Privacy
- All data is stored locally on your device
- Your health data is not shared with third parties
- Passwords are protected with SHA-256 hashing and salt

### Data Backup
Recommended to regularly backup `health_buddy.db` file:
```bash
# Backup database
cp health_buddy.db health_buddy_backup_$(date +%Y%m%d).db
```

### Password Security
- Minimum 6 characters for passwords
- Passwords are hashed with SHA-256 and random salt
- Consider using complex passwords with mixed characters

## 🐛 Debug Mode

### Enable Development Mode
```bash
# Run with debug information
java -Dapp.mode=development -jar target/my-healthcare-buddy-1.0-SNAPSHOT.jar
```

### Log Files
- Application logs are saved in `healthcare-buddy.log`
- Database operations are logged automatically
- Error details are recorded for troubleshooting

## 🔄 Updates & Maintenance

### Checking for Updates
- Visit the GitHub repository for latest releases
- Follow release notes for breaking changes

### Database Migration
- Current version creates database automatically
- Future versions will include migration scripts
- Always backup your data before updates

## 📞 Technical Support

### Getting Help
1. Check this installation guide
2. Review log files: `healthcare-buddy.log`
3. Search existing issues on GitHub
4. Create new issue with:
   - OS version
   - Java version
   - Error messages
   - Steps to reproduce

### Known Limitations
- Single user per database file
- No cloud sync functionality
- Manual backup required
- Required Java 11+ runtime

## 🚀 Performance Optimization

### System Recommendations
- **RAM**: Minimum 2GB, Recommended 4GB+
- **Storage**: At least 100MB free space
- **CPU**: Any modern processor (2015+)

### JVM Tuning
```bash
# For better performance with large datasets
java -Xmx1024m -XX:+UseG1GC -jar target/my-healthcare-buddy-1.0-SNAPSHOT.jar
```

### Database Optimization
- Database is automatically optimized
- Use SSD storage for better performance
- Avoid running multiple instances simultaneously

## 📊 Features Overview

### Current Features
- ✅ User authentication with secure passwords
- ✅ Health metrics tracking
- ✅ Daily habits monitoring
- ✅ Data visualization with charts
- ✅ Data export functionality
- ✅ Goal setting and tracking
- ✅ Input validation and error handling

### Upcoming Features
- Password recovery functionality
- Data backup and restore
- Multi-language support
- Dark theme option

## 🏆 Success Tips

### Best Practices
1. **Regular Backups**: Backup your database weekly
2. **Consistent Data Entry**: Enter data regularly for better trends
3. **Realistic Goals**: Set achievable health targets
4. **Security**: Use strong, unique passwords
5. **Updates**: Keep the application updated

### Data Management
- Export data monthly for external analysis
- Review charts weekly to track progress
- Set reminder alerts for data entry
- Use consistent units of measurement

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Built with Java Swing GUI framework
- Powered by SQLite database
- Charts provided by JFreeChart library
- Icons and UI elements designed for accessibility

---

**Thank you for using My Healthcare Buddy! Stay healthy! 🌟**

For the most up-to-date information, visit our [GitHub repository](https://github.com/cih-lian-liao/my-healthcare-buddy).