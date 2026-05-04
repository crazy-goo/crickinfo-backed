# CrickInfo - Cricket Application with JWT Authentication & Role-Based Access Control

A complete Spring Boot application for managing cricket information with JWT authentication, MongoDB, and role-based access control for Admin and User roles.

---

## 🎯 Features

### 🔐 Authentication & Security
- ✅ **JWT Token Authentication** - Secure token-based authentication
- ✅ **Role-Based Access Control** - Admin and User roles with different permissions
- ✅ **Default Admin Account** - Hardcoded default credentials (admin/admin123)
- ✅ **Admin Credential Management** - Admin can change username and password after login
- ✅ **User Registration** - Users can self-register
- ✅ **Password Encryption** - BCrypt encryption for all passwords
- ✅ **Token Expiration** - 24-hour token validity

### 👨‍💼 Admin Capabilities
- Manage player profiles (add, edit, delete)
- Manage cricket videos (add, edit, delete)
- Update admin credentials
- Full CRUD operations on cricket data

### 👤 User Capabilities
- Browse all players and statistics
- Filter players by country, role, and status
- Browse cricket videos
- Search videos by title and category
- View player and video details
- Automatic view count tracking for videos

### 🏏 Cricket Data Management
- Player information with statistics (runs, wickets, averages)
- Player status tracking (Active, Inactive, Retired)
- Player roles (Batsman, Bowler, All-rounder)
- Cricket videos with categories and metadata
- Video view counting and analytics

---

## 🛠 Technology Stack

| Technology | Version | Purpose |
|-----------|---------|---------|
| **Java** | 25+ | Programming Language |
| **Spring Boot** | 4.0.5 | Framework |
| **MongoDB** | 5.0+ | NoSQL Database |
| **JWT (jjwt)** | 0.13.0 | Authentication |
| **Spring Security** | 4.0.5 | Security Framework |
| **Maven** | 3.9+ | Build Tool |
| **Lombok** | - | Code Generation |
| **BCrypt** | - | Password Encoding |

---

## 📋 Prerequisites

- Java 25 or higher
- Maven 3.6+
- MongoDB 5.0+ (or MongoDB Atlas for cloud)
- Git
- Postman or cURL (for API testing)

---

## 🚀 Installation & Setup

### Step 1: Clone or Download Project
```bash
cd crickInfo
```

### Step 2: Setup MongoDB

#### Using Docker (Recommended)
```bash
# Pull and run MongoDB
docker run -d \
  --name mongodb \
  -p 27017:27017 \
  -e MONGO_INITDB_ROOT_USERNAME=root \
  -e MONGO_INITDB_ROOT_PASSWORD=password \
  mongo:latest

# For local access without auth:
docker run -d -p 27017:27017 --name mongodb mongo:latest
```

#### Using Homebrew (macOS)
```bash
brew install mongodb-community
brew services start mongodb-community
```

#### Using apt (Ubuntu/Debian)
```bash
sudo apt-get install -y mongodb
sudo systemctl start mongod
```

### Step 3: Verify MongoDB Connection
```bash
# Connect to MongoDB CLI
mongosh mongodb://localhost:27017

# List databases
show databases

# Exit
exit
```

### Step 4: Configure Application
Edit `src/main/resources/application.properties`:
```properties
# MongoDB Configuration
spring.mongodb.uri=mongodb://localhost:27017/crickinfo

# JWT Configuration
jwt.secret=DEBU_SECRET_KEY_2026_CRICKINFO_APP
jwt.expiration=86400000

# Server Configuration
server.port=8080
```

### Step 5: Build the Project
```bash
# Build with Maven
mvn clean install

# If you want to skip tests
mvn clean install -DskipTests
```

### Step 6: Run the Application
```bash
# Using Maven
mvn spring-boot:run

# Or run the JAR file
java -jar target/crickInfo-0.0.1-SNAPSHOT.jar
```

### Step 7: Verify Application Started
```
✓ Application should start on http://localhost:8080
✓ MongoDB connection should be established
✓ Default admin account should be created
✓ Sample data should be initialized
```

---

## 🔑 Default Credentials

| Role | Username | Password | Description |
|------|----------|----------|-------------|
| Admin | `admin` | `admin123` | Default hardcoded admin account |

> ⚠️ **Important**: Change default admin password immediately after first login!

---

## 📡 API Endpoints

### 🔓 Public Endpoints (No Authentication Required)

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "role": "ROLE_ADMIN",
  "message": "Login successful"
}
```

---

### 🔐 Admin Endpoints (ROLE_ADMIN Required)

#### Update Admin Credentials
```http
PUT /api/admin/update-credentials?currentPassword=admin123
Authorization: Bearer {token}
Content-Type: application/json

{
  "username": "newadmin",
  "password": "newpassword123"
}
```

#### Player Management

**Add Player**
```http
POST /api/admin/players
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "Virat Kohli",
  "country": "India",
  "role": "Batsman",
  "jerseyNumber": 18,
  "status": "Active",
  "runs": 14000,
  "wickets": 0,
  "battingAverage": 58.5,
  "bowlingAverage": 0
}
```

**Update Player**
```http
PUT /api/admin/players/{playerId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "Virat Kohli",
  "runs": 15000,
  "battingAverage": 59.2
}
```

**Delete Player**
```http
DELETE /api/admin/players/{playerId}
Authorization: Bearer {token}
```

#### Video Management

**Add Video**
```http
POST /api/admin/videos
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "India vs Australia Highlights",
  "description": "Best moments from the match",
  "videoUrl": "https://example.com/video.mp4",
  "thumbnailUrl": "https://example.com/thumb.jpg",
  "category": "Match Highlights",
  "duration": "10:30"
}
```

**Update Video**
```http
PUT /api/admin/videos/{videoId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Updated Title",
  "viewCount": 100
}
```

**Delete Video**
```http
DELETE /api/admin/videos/{videoId}
Authorization: Bearer {token}
```

---

### 👤 User Endpoints (ROLE_USER Required)

#### Player Information

**Get All Players**
```http
GET /api/user/players
Authorization: Bearer {token}
```

**Get Player by ID**
```http
GET /api/user/players/{playerId}
Authorization: Bearer {token}
```

**Get Players by Country**
```http
GET /api/user/players/country/India
Authorization: Bearer {token}
```

**Get Players by Role**
```http
GET /api/user/players/role/Batsman
Authorization: Bearer {token}
```

**Get Players by Status**
```http
GET /api/user/players/status/Active
Authorization: Bearer {token}
```

#### Video Information

**Get All Videos**
```http
GET /api/user/videos
Authorization: Bearer {token}
```

**Get Video by ID** (Increments view count)
```http
GET /api/user/videos/{videoId}
Authorization: Bearer {token}
```

**Get Videos by Category**
```http
GET /api/user/videos/category/Match Highlights
Authorization: Bearer {token}
```

**Search Videos**
```http
GET /api/user/videos/search/India
Authorization: Bearer {token}
```

---

## 📊 Database Models

### User Collection
```json
{
  "_id": "ObjectId",
  "name": "Administrator",
  "email": "admin@example.com",
  "username": "admin",
  "password": "$2a$10$encrypted_password",
  "role": "ROLE_ADMIN"
}
```

### Player Collection
```json
{
  "_id": "ObjectId",
  "name": "Virat Kohli",
  "country": "India",
  "role": "Batsman",
  "jerseyNumber": 18,
  "imageUrl": "https://example.com/image.jpg",
  "status": "Active",
  "runs": 14000,
  "wickets": 0,
  "battingAverage": 58.5,
  "bowlingAverage": 0.0
}
```

### Video Collection
```json
{
  "_id": "ObjectId",
  "title": "India vs Pakistan Highlights",
  "description": "Match highlights",
  "videoUrl": "https://example.com/video.mp4",
  "thumbnailUrl": "https://example.com/thumb.jpg",
  "category": "Match Highlights",
  "uploadedAt": "2026-04-19T10:30:00",
  "viewCount": 150,
  "duration": "10:30"
}
```

---

## 🧪 Testing with cURL

### Login as Admin
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

### Register New User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Smith",
    "email": "jane@example.com",
    "password": "password123"
  }'
```

### Add Player (Admin)
```bash
TOKEN="your_admin_token_here"

curl -X POST http://localhost:8080/api/admin/players \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Rohit Sharma",
    "country": "India",
    "role": "Batsman",
    "jerseyNumber": 45,
    "status": "Active",
    "runs": 10000,
    "wickets": 0,
    "battingAverage": 51.2,
    "bowlingAverage": 0
  }'
```

### Get All Players (User)
```bash
TOKEN="your_user_token_here"

curl -X GET http://localhost:8080/api/user/players \
  -H "Authorization: Bearer $TOKEN"
```

### Search Videos (User)
```bash
TOKEN="your_user_token_here"

curl -X GET http://localhost:8080/api/user/videos/search/highlights \
  -H "Authorization: Bearer $TOKEN"
```

---

## 📁 Project Structure

```
crickInfo/
├── src/
│   ├── main/
│   │   ├── java/com/debu/crickInfo/
│   │   │   ├── config/
│   │   │   │   ├── DataInitializer.java      (Sample data initialization)
│   │   │   │   └── SecurityConfig.java       (Spring Security configuration)
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java       (Login & Registration)
│   │   │   │   ├── AdminController.java      (Admin operations)
│   │   │   │   └── UserController.java       (User operations)
│   │   │   ├── dto/
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── LoginResponse.java
│   │   │   │   ├── RegisterRequest.java
│   │   │   │   └── AdminUpdateRequest.java
│   │   │   ├── model/
│   │   │   │   ├── User.java
│   │   │   │   ├── Player.java
│   │   │   │   └── Video.java
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── PlayerRepository.java
│   │   │   │   └── VideoRepository.java
│   │   │   ├── security/
│   │   │   │   ├── JwtUtil.java              (JWT token operations)
│   │   │   │   └── JwtFilter.java            (JWT validation filter)
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java          (Authentication logic)
│   │   │   │   ├── PlayerService.java        (Player operations)
│   │   │   │   └── VideoService.java         (Video operations)
│   │   │   └── CrickInfoApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/...
├── pom.xml
├── HELP.md
├── API_DOCUMENTATION.md
├── README.md
└── mvnw
```

---

## 🔒 Security Features

### JWT Token Management
- **Generation**: Tokens include username and role as claims
- **Validation**: Every protected endpoint validates token signature and expiration
- **Expiration**: Tokens expire after 24 hours (86400000 ms)
- **Encoding**: HS256 algorithm with secret key

### Password Security
- **Encryption**: BCrypt with 10 rounds of hashing
- **Validation**: Password verified against encrypted hash
- **Storage**: Never stored in plaintext

### Role-Based Access Control
- **Admin Routes**: `/api/admin/**` - Requires ROLE_ADMIN
- **User Routes**: `/api/user/**` - Requires ROLE_USER
- **Public Routes**: `/api/auth/**` - No authentication required

### CORS Configuration
- Cross-Origin requests enabled for development
- Can be restricted in production

---

## ⚙️ Configuration

### JWT Settings (`application.properties`)
```properties
jwt.secret=DEBU_SECRET_KEY_2026_CRICKINFO_APP
jwt.expiration=86400000  # 24 hours in milliseconds
```

### MongoDB Settings
```properties
spring.mongodb.uri=mongodb://localhost:27017/crickinfo
spring.data.mongodb.auto-index-creation=true
```

### Server Settings
```properties
server.port=8080
server.servlet.context-path=/
spring.application.name=crickinfo
```

---

## 🐛 Troubleshooting

### MongoDB Connection Error
```
Error: Cannot connect to MongoDB
```
**Solution:**
- Verify MongoDB is running: `mongosh`
- Check URI in `application.properties`
- Ensure port 27017 is not blocked
- Restart MongoDB service

### JWT Token Expired
```
Error: Token has expired
```
**Solution:**
- Get a new token by logging in again
- Default expiration: 24 hours
- Increase expiration in `application.properties` if needed

### 403 Forbidden - Access Denied
```
Error: Access is denied
```
**Solution:**
- Verify your token's role matches the endpoint
- Admin endpoints need ROLE_ADMIN
- User endpoints need ROLE_USER
- Use correct token in Authorization header

### Port Already in Use
```
Error: Address already in use :8080
```
**Solution:**
- Change port: `server.port=8081`
- Or kill process: `lsof -i :8080` then `kill -9 <PID>`

### Database Not Found
```
Error: Database 'crickinfo' not found
```
**Solution:**
- Database will be created automatically on first write
- Ensure MongoDB is running
- Check connection string format

---

## 🚀 Deployment

### Docker Deployment
```dockerfile
FROM openjdk:25
COPY target/crickInfo-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Cloud Deployment (Heroku)
```bash
git init
heroku create your-app-name
git push heroku main
```

### Production Checklist
- [ ] Change default admin password
- [ ] Update JWT secret key
- [ ] Use MongoDB Atlas or secure MongoDB instance
- [ ] Enable HTTPS/SSL
- [ ] Restrict CORS origins
- [ ] Implement rate limiting
- [ ] Set up logging and monitoring
- [ ] Enable MongoDB authentication
- [ ] Use environment variables for secrets

---

## 📈 Performance Optimization

1. **Indexing**: Automatic indexing on frequently queried fields
2. **Pagination**: Can be added for large result sets
3. **Caching**: Redis can be integrated for token caching
4. **Connection Pooling**: MongoDB connection pooling enabled
5. **Lazy Loading**: View counts updated asynchronously

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

---

## 📝 License

This project is open source and available under the MIT License.

---

## 📞 Support & Documentation

- **Full API Documentation**: See `API_DOCUMENTATION.md`
- **Quick Start Guide**: See `HELP.md`
- **Issues**: Report bugs via GitHub issues
- **Discussions**: Use GitHub discussions for questions

---

## 🎓 Learning Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [MongoDB Documentation](https://docs.mongodb.com)
- [JWT Introduction](https://jwt.io)
- [Spring Security Guide](https://spring.io/guides/gs/securing-web/)
- [RESTful API Best Practices](https://restfulapi.net)

---

## 📊 Statistics & Metrics

Track your application:
- User registrations
- API request counts
- Video view analytics
- Player performance metrics
- System uptime and response times

---

## 🗺️ Roadmap

- [ ] Frontend UI (React/Vue)
- [ ] Live match scoring
- [ ] Email notifications
- [ ] Advanced analytics dashboard
- [ ] Player performance prediction
- [ ] Video streaming optimization
- [ ] Multi-language support
- [ ] Mobile app

---

## 🙏 Acknowledgments

- Spring Boot team
- MongoDB community
- JWT creators
- All contributors

---

**Happy Cricket Coding! 🏏**

For more information, visit the [project documentation](./API_DOCUMENTATION.md)

