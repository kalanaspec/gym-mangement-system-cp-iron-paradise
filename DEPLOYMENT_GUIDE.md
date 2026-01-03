# Deployment Guide - Gym Management System

This guide covers deploying both the Spring Boot backend and Angular frontend.

## Table of Contents
1. [Backend Deployment (Spring Boot)](#backend-deployment)
2. [Frontend Deployment (Angular)](#frontend-deployment)
3. [Full Stack Deployment Options](#full-stack-deployment-options)

---

## Backend Deployment (Spring Boot)

### Option 1: Deploy as JAR File (Recommended)

#### Step 1: Build the Application

```bash
# Navigate to project root
cd "D:\New folder"

# Clean and build (creates JAR file)
mvn clean package -DskipTests

# JAR file will be created at:
# target/management-system-0.0.1-SNAPSHOT.jar
```

#### Step 2: Configure Production Properties

Create `application-prod.properties` in `src/main/resources/`:

```properties
spring.application.name=gym
spring.datasource.url=jdbc:mysql://YOUR_DB_HOST:3306/gym_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
spring.jpa.hibernate.ddl-auto=validate
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

jwt.secret=YOUR_PRODUCTION_SECRET_KEY_AT_LEAST_32_CHARACTERS_LONG

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=YOUR_EMAIL@gmail.com
spring.mail.password=YOUR_APP_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html

server.port=8080
```

#### Step 3: Run the JAR

```bash
# Run with production profile
java -jar -Dspring.profiles.active=prod target/management-system-0.0.1-SNAPSHOT.jar

# Or specify port
java -jar -Dspring.profiles.active=prod -Dserver.port=8080 target/management-system-0.0.1-SNAPSHOT.jar
```

#### Step 4: Run as Windows Service (Optional)

Use NSSM (Non-Sucking Service Manager) to run as Windows service:

1. Download NSSM: https://nssm.cc/download
2. Install service:
```bash
nssm install GymManagementSystem "C:\Program Files\Java\jdk-17\bin\java.exe" "-jar -Dspring.profiles.active=prod D:\New folder\target\management-system-0.0.1-SNAPSHOT.jar"
nssm set GymManagementSystem AppDirectory "D:\New folder"
nssm start GymManagementSystem
```

### Option 2: Deploy to Cloud Platforms

#### Heroku

1. **Create `Procfile` in project root:**
```
web: java -Dserver.port=$PORT -jar target/management-system-0.0.1-SNAPSHOT.jar
```

2. **Install Heroku CLI** and login:
```bash
heroku login
```

3. **Create Heroku app:**
```bash
heroku create gym-management-backend
```

4. **Add MySQL database (JawsDB or ClearDB):**
```bash
heroku addons:create jawsdb:kitefin
```

5. **Set environment variables:**
```bash
heroku config:set SPRING_PROFILES_ACTIVE=prod
heroku config:set JWT_SECRET=your_production_secret_key_32_chars_minimum
heroku config:set SPRING_MAIL_USERNAME=your_email@gmail.com
heroku config:set SPRING_MAIL_PASSWORD=your_app_password
```

6. **Deploy:**
```bash
git init
git add .
git commit -m "Initial commit"
heroku git:remote -a gym-management-backend
git push heroku main
```

#### AWS EC2

1. **Launch EC2 instance** (Ubuntu/Amazon Linux)
2. **Install Java 17:**
```bash
sudo yum install java-17-amazon-corretto-headless
```

3. **Install MySQL:**
```bash
sudo yum install mysql-server
sudo systemctl start mysqld
```

4. **Upload JAR file:**
```bash
scp target/management-system-0.0.1-SNAPSHOT.jar ec2-user@your-ec2-ip:/home/ec2-user/
```

5. **Run application:**
```bash
java -jar -Dspring.profiles.active=prod management-system-0.0.1-SNAPSHOT.jar
```

6. **Use systemd service** (create `/etc/systemd/system/gym-management.service`):
```ini
[Unit]
Description=Gym Management System
After=network.target

[Service]
Type=simple
User=ec2-user
WorkingDirectory=/home/ec2-user
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=prod /home/ec2-user/management-system-0.0.1-SNAPSHOT.jar
Restart=always

[Install]
WantedBy=multi-user.target
```

Enable and start:
```bash
sudo systemctl enable gym-management
sudo systemctl start gym-management
```

#### Railway

1. **Connect GitHub repository** to Railway
2. **Add environment variables** in Railway dashboard
3. **Add MySQL database** addon
4. **Deploy automatically** on git push

#### Render

1. **Create new Web Service** on Render
2. **Connect GitHub repository**
3. **Build command:** `mvn clean package -DskipTests`
4. **Start command:** `java -jar target/management-system-0.0.1-SNAPSHOT.jar`
5. **Add environment variables**
6. **Add MySQL database**

---

## Frontend Deployment (Angular)

### Step 1: Build for Production

```bash
cd frontend

# Install dependencies (if not done)
npm install

# Build for production
npm run build

# Output will be in: frontend/dist/gym-management-frontend/
```

### Step 2: Deploy Options

#### Option A: Deploy to Netlify (Easiest)

1. **Install Netlify CLI:**
```bash
npm install -g netlify-cli
```

2. **Build and deploy:**
```bash
cd frontend
npm run build
netlify deploy --prod --dir=dist/gym-management-frontend
```

3. **Or connect GitHub repository:**
   - Go to https://app.netlify.com
   - New site from Git
   - Connect repository
   - Build command: `cd frontend && npm install && npm run build`
   - Publish directory: `frontend/dist/gym-management-frontend`

4. **Update API URL:**
   - Create `src/environments/environment.prod.ts` (see below)
   - Update base URL to your backend URL

#### Option B: Deploy to Vercel

1. **Install Vercel CLI:**
```bash
npm install -g vercel
```

2. **Deploy:**
```bash
cd frontend
vercel --prod
```

3. **Or connect GitHub** through Vercel dashboard

#### Option C: Deploy to GitHub Pages

1. **Install angular-cli-ghpages:**
```bash
npm install -g angular-cli-ghpages
```

2. **Build and deploy:**
```bash
cd frontend
npm run build -- --base-href=/repository-name/
npx angular-cli-ghpages --dir=dist/gym-management-frontend
```

#### Option D: Deploy to AWS S3 + CloudFront

1. **Build the app:**
```bash
cd frontend
npm run build
```

2. **Upload to S3:**
   - Create S3 bucket
   - Enable static website hosting
   - Upload contents of `dist/gym-management-frontend/`

3. **Optional: Setup CloudFront** for CDN

#### Option E: Deploy to Traditional Web Server (Apache/Nginx)

1. **Build the app:**
```bash
cd frontend
npm run build
```

2. **Copy files:**
   - Copy contents of `dist/gym-management-frontend/` to web server directory

3. **Nginx configuration** (`/etc/nginx/sites-available/gym-frontend`):
```nginx
server {
    listen 80;
    server_name your-domain.com;
    root /var/www/gym-frontend;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    # API proxy (optional, if backend on same server)
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

---

## Full Stack Deployment Options

### Option 1: Separate Backend and Frontend

- **Backend:** Deploy to Heroku/Railway/Render
- **Frontend:** Deploy to Netlify/Vercel
- **Update CORS** in backend to allow frontend domain
- **Update API URL** in frontend environment files

### Option 2: Single Server Deployment

- Deploy backend on port 8080
- Deploy frontend build to web server (Nginx/Apache)
- Configure reverse proxy to serve frontend and proxy API requests

### Option 3: Docker Deployment

See `Dockerfile` and `docker-compose.yml` (if created) for containerized deployment.

---

## Important Configuration Updates

### Update Backend CORS

In `SecurityConfig.java`, update allowed origins:
```java
configuration.setAllowedOrigins(Arrays.asList(
    "http://localhost:4200",  // Development
    "https://your-frontend-domain.com"  // Production
));
```

### Update Frontend API URL

Create `src/environments/environment.prod.ts`:
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://your-backend-domain.com/api'
};
```

Update services to use environment variable instead of hardcoded URL.

---

## Database Setup

### Production MySQL Database

1. **Create database:**
```sql
CREATE DATABASE gym_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. **Create user:**
```sql
CREATE USER 'gym_user'@'%' IDENTIFIED BY 'strong_password';
GRANT ALL PRIVILEGES ON gym_db.* TO 'gym_user'@'%';
FLUSH PRIVILEGES;
```

3. **Update connection string** in application properties

---

## Security Checklist

- [ ] Change JWT secret to strong random value (32+ characters)
- [ ] Use strong database passwords
- [ ] Enable HTTPS/SSL
- [ ] Update CORS to only allow your frontend domain
- [ ] Set `spring.jpa.hibernate.ddl-auto=validate` in production
- [ ] Disable `spring.jpa.show-sql` in production
- [ ] Use environment variables for sensitive data
- [ ] Enable database backups
- [ ] Configure firewall rules
- [ ] Set up monitoring and logging

---

## Monitoring and Maintenance

- Monitor application logs
- Set up database backups
- Monitor server resources (CPU, memory, disk)
- Set up error tracking (Sentry, LogRocket)
- Regular security updates
- Database optimization and indexing

