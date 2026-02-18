# Troubleshooting Guide - No Data Display Issue

## Problem
Frontend is not displaying courses and create functionality is not working.

## Step-by-Step Diagnosis

### 1. Check Backend Services

Run the `start-backend.bat` script to check if all services are running:
```bash
start-backend.bat
```

Expected output:
- ✅ Eureka Server on port 8761
- ✅ Config Server on port 8888  
- ✅ API Gateway on port 8088
- ✅ Courses Service on port 8082

### 2. Test API Directly

Open browser and navigate to:
```
http://localhost:8088/api/courses
```

You should see JSON data with 4 courses.

### 3. Check Browser Console

1. Open your Angular app: `http://localhost:4200`
2. Open Browser DevTools (F12)
3. Go to Console tab
4. Look for errors

Common errors and solutions:

#### Error: "Failed to load resource: net::ERR_CONNECTION_REFUSED"
**Solution:** Backend is not running. Start all backend services.

#### Error: "CORS policy: No 'Access-Control-Allow-Origin' header"
**Solution:** API Gateway CORS is misconfigured. Verify `application.yml` has:
```yaml
globalcors:
  cors-configurations:
    '[/**]':
      allowed-origins: "http://localhost:4200"
```

#### Error: "404 Not Found"
**Solution:** API Gateway routing is wrong. Check routes in `application.yml`.

### 4. Test with test-api.html

Open `frontend/test-api.html` in your browser. This will test the API connection directly.

### 5. Check Network Tab

1. Open DevTools → Network tab
2. Reload the page
3. Look for requests to `http://localhost:8088/api/courses`
4. Check:
   - Status code (should be 200)
   - Response data
   - Request headers

### 6. Verify Database

Connect to PostgreSQL and run:
```sql
SELECT * FROM courses;
```

You should see 4 courses created by DataInitializer.

## Quick Fixes

### Fix 1: Restart All Services

```bash
# Stop all Java processes
taskkill /F /IM java.exe

# Start in order:
cd backend/eureka-server
start mvn spring-boot:run

# Wait 30 seconds, then:
cd ../config-server
start mvn spring-boot:run

# Wait 30 seconds, then:
cd ../api-gateway  
start mvn spring-boot:run

# Wait 30 seconds, then:
cd ../courses-service
start mvn spring-boot:run
```

### Fix 2: Clear Browser Cache

1. Open DevTools (F12)
2. Right-click refresh button
3. Select "Empty Cache and Hard Reload"

### Fix 3: Rebuild Frontend

```bash
cd frontend
npm run build
ng serve
```

## Verification Checklist

- [ ] PostgreSQL is running
- [ ] Database `englishflow_courses` exists
- [ ] Eureka Server is running (http://localhost:8761)
- [ ] API Gateway is running (http://localhost:8088)
- [ ] Courses Service is running (http://localhost:8082)
- [ ] API returns data: http://localhost:8088/api/courses
- [ ] Frontend is running (http://localhost:4200)
- [ ] Browser console shows no errors
- [ ] Network tab shows successful API calls

## Still Not Working?

Check the console output when you:
1. Navigate to courses list page
2. Try to create a course

The console will show:
- API URL being called
- Request payload
- Response or error details

Share this information for further debugging.
