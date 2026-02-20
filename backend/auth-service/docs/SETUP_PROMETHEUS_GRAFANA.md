# 🚀 Setup Prometheus + Grafana (15 Minutes)

## 📋 What You'll Get

- 📊 Beautiful real-time graphs
- 🎨 Professional dashboards
- 🔔 Alerts for important events
- 📈 Historical data tracking

---

## Step 1: Install Prometheus (5 minutes)

### macOS Installation

```bash
# Install Prometheus
brew install prometheus

# Verify installation
prometheus --version
```

### Create Prometheus Configuration

Create file `prometheus.yml` in your project root:

```bash
cd /path/to/EnglishFlow-PI
nano prometheus.yml
```

Paste this content:

```yaml
# Prometheus Configuration for Auth Service
global:
  scrape_interval: 15s      # Scrape metrics every 15 seconds
  evaluation_interval: 15s  # Evaluate rules every 15 seconds

# Alerting configuration (optional)
alerting:
  alertmanagers:
    - static_configs:
        - targets: []

# Scrape configurations
scrape_configs:
  # Auth Service
  - job_name: 'auth-service'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8081']
        labels:
          application: 'auth-service'
          environment: 'development'

  # Prometheus itself
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']
```

Save and exit (Ctrl+O, Enter, Ctrl+X)

### Start Prometheus

```bash
# Start Prometheus with your config
prometheus --config.file=prometheus.yml

# You should see:
# Server is ready to receive web requests.
```

### Verify Prometheus is Running

Open in browser:
```
http://localhost:9090
```

You should see the Prometheus UI! 🎉

### Test Prometheus is Scraping Your Service

1. Go to: http://localhost:9090/targets
2. You should see `auth-service` with status **UP** (green)
3. If it's red, make sure your auth-service is running on port 8081

---

## Step 2: Install Grafana (5 minutes)

### macOS Installation

```bash
# Install Grafana
brew install grafana

# Start Grafana service
brew services start grafana

# Verify it's running
brew services list | grep grafana
```

### Access Grafana

Open in browser:
```
http://localhost:3000
```

**Default credentials:**
- Username: `admin`
- Password: `admin`

You'll be asked to change the password. Choose a new one or skip.

---

## Step 3: Connect Grafana to Prometheus (2 minutes)

### Add Prometheus Data Source

1. **Click the gear icon (⚙️)** on the left sidebar
2. **Click "Data sources"**
3. **Click "Add data source"**
4. **Select "Prometheus"**
5. **Configure:**
   - Name: `Prometheus`
   - URL: `http://localhost:9090`
   - Access: `Server (default)`
6. **Click "Save & Test"**

You should see: ✅ "Data source is working"

---

## Step 4: Create Your First Dashboard (3 minutes)

### Option A: Import Pre-Built Dashboard (Easiest)

I'll create a complete dashboard for you. Create file `auth-service-dashboard.json`:

```json
{
  "dashboard": {
    "title": "Auth Service Monitoring",
    "tags": ["auth", "monitoring"],
    "timezone": "browser",
    "panels": [
      {
        "id": 1,
        "title": "Login Success Rate (per minute)",
        "type": "graph",
        "gridPos": {"h": 8, "w": 12, "x": 0, "y": 0},
        "targets": [
          {
            "expr": "rate(auth_login_success_total[5m]) * 60",
            "legendFormat": "Successful Logins"
          }
        ]
      },
      {
        "id": 2,
        "title": "Login Failure Rate (per minute)",
        "type": "graph",
        "gridPos": {"h": 8, "w": 12, "x": 12, "y": 0},
        "targets": [
          {
            "expr": "rate(auth_login_failure_total[5m]) * 60",
            "legendFormat": "Failed Logins"
          }
        ]
      },
      {
        "id": 3,
        "title": "Total Registrations",
        "type": "stat",
        "gridPos": {"h": 4, "w": 6, "x": 0, "y": 8},
        "targets": [
          {
            "expr": "auth_registration_total"
          }
        ]
      },
      {
        "id": 4,
        "title": "Total Users",
        "type": "stat",
        "gridPos": {"h": 4, "w": 6, "x": 6, "y": 8},
        "targets": [
          {
            "expr": "auth_users_total"
          }
        ]
      },
      {
        "id": 5,
        "title": "Active Sessions",
        "type": "stat",
        "gridPos": {"h": 4, "w": 6, "x": 12, "y": 8},
        "targets": [
          {
            "expr": "auth_sessions_active"
          }
        ]
      },
      {
        "id": 6,
        "title": "Memory Usage (%)",
        "type": "gauge",
        "gridPos": {"h": 4, "w": 6, "x": 18, "y": 8},
        "targets": [
          {
            "expr": "jvm_memory_used_bytes{area=\"heap\"} / jvm_memory_max_bytes{area=\"heap\"} * 100"
          }
        ]
      }
    ]
  }
}
```

**To import:**
1. Click "+" on left sidebar → "Import"
2. Click "Upload JSON file"
3. Select `auth-service-dashboard.json`
4. Click "Import"

### Option B: Create Dashboard Manually

1. **Click "+" on left sidebar → "Dashboard"**
2. **Click "Add new panel"**
3. **Configure first panel:**
   - Query: `rate(auth_login_success_total[5m]) * 60`
   - Title: "Successful Logins per Minute"
   - Panel type: Graph
4. **Click "Apply"**
5. **Click "Add panel"** to add more panels
6. **Click "Save dashboard"** (disk icon top right)

---

## Step 5: Test Everything (2 minutes)

### Generate Test Traffic

Run the test script:
```bash
cd backend/auth-service
./test-metrics.sh
```

Or manually test via Swagger:
```
http://localhost:8081/swagger-ui.html
```

### Watch Metrics Update

1. **In Prometheus** (http://localhost:9090):
   - Go to "Graph" tab
   - Query: `auth_login_failure_total`
   - Click "Execute"
   - See the graph update!

2. **In Grafana** (http://localhost:3000):
   - Open your dashboard
   - Watch the graphs update in real-time! 📈

---

## 🎨 Recommended Dashboard Panels

### Panel 1: Login Success Rate
```
Query: rate(auth_login_success_total[5m]) * 60
Type: Graph
Title: Successful Logins per Minute
```

### Panel 2: Login Failure Rate
```
Query: rate(auth_login_failure_total[5m]) * 60
Type: Graph
Title: Failed Logins per Minute
Color: Red
```

### Panel 3: Average Login Duration
```
Query: rate(auth_login_duration_seconds_sum[5m]) / rate(auth_login_duration_seconds_count[5m])
Type: Graph
Title: Average Login Duration (seconds)
```

### Panel 4: Registration Rate
```
Query: rate(auth_registration_total[5m]) * 60
Type: Graph
Title: Registrations per Minute
```

### Panel 5: Total Users (Stat)
```
Query: auth_users_total
Type: Stat
Title: Total Users
```

### Panel 6: Active Sessions (Stat)
```
Query: auth_sessions_active
Type: Stat
Title: Active Sessions
```

### Panel 7: Memory Usage (Gauge)
```
Query: jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"} * 100
Type: Gauge
Title: JVM Heap Memory Usage (%)
Thresholds: 75 (yellow), 90 (red)
```

### Panel 8: Email Success Rate
```
Query: rate(auth_email_sent_total[5m]) / (rate(auth_email_sent_total[5m]) + rate(auth_email_failed_total[5m])) * 100
Type: Gauge
Title: Email Success Rate (%)
```

### Panel 9: Rate Limit Violations
```
Query: rate(auth_ratelimit_exceeded_total[5m]) * 60
Type: Graph
Title: Rate Limit Violations per Minute
Color: Orange
```

### Panel 10: HTTP Request Rate
```
Query: rate(http_server_requests_seconds_count[5m])
Type: Graph
Title: HTTP Requests per Second
```

---

## 🔔 Setup Alerts (Optional but Recommended)

### Create Alert Rules in Prometheus

Create file `alert-rules.yml`:

```yaml
groups:
  - name: auth-service-alerts
    interval: 30s
    rules:
      # High login failure rate
      - alert: HighLoginFailureRate
        expr: rate(auth_login_failure_total[5m]) > 10
        for: 5m
        labels:
          severity: warning
          service: auth-service
        annotations:
          summary: "High login failure rate detected"
          description: "Login failure rate is {{ $value }} per second (threshold: 10)"

      # High memory usage
      - alert: HighMemoryUsage
        expr: (jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"}) * 100 > 90
        for: 5m
        labels:
          severity: critical
          service: auth-service
        annotations:
          summary: "High memory usage"
          description: "Memory usage is {{ $value }}% (threshold: 90%)"

      # Service down
      - alert: ServiceDown
        expr: up{job="auth-service"} == 0
        for: 1m
        labels:
          severity: critical
          service: auth-service
        annotations:
          summary: "Auth service is down"
          description: "Auth service has been down for more than 1 minute"

      # High rate limit violations
      - alert: HighRateLimitViolations
        expr: rate(auth_ratelimit_exceeded_total[5m]) > 5
        for: 5m
        labels:
          severity: warning
          service: auth-service
        annotations:
          summary: "High rate limit violations"
          description: "Rate limit exceeded {{ $value }} times per second"

      # Email sending issues
      - alert: HighEmailFailureRate
        expr: rate(auth_email_failed_total[5m]) > 1
        for: 5m
        labels:
          severity: warning
          service: auth-service
        annotations:
          summary: "High email failure rate"
          description: "Email failure rate is {{ $value }} per second"
```

Update `prometheus.yml` to include alert rules:

```yaml
rule_files:
  - "alert-rules.yml"
```

Restart Prometheus to load the rules.

### View Alerts in Prometheus

Go to: http://localhost:9090/alerts

You'll see all your alert rules and their current status!

---

## 📱 Setup Grafana Alerts (Optional)

### Configure Alert Notification Channel

1. **Go to Alerting → Notification channels**
2. **Click "Add channel"**
3. **Choose type:**
   - Email
   - Slack
   - Discord
   - Webhook
4. **Configure and test**

### Add Alert to Panel

1. **Edit any panel**
2. **Go to "Alert" tab**
3. **Click "Create Alert"**
4. **Set conditions:**
   - WHEN: `avg()` OF `query(A, 5m, now)` IS ABOVE `10`
5. **Set notification channel**
6. **Save**

---

## 🎯 Quick Reference

### URLs to Bookmark

| Service | URL | Purpose |
|---------|-----|---------|
| Auth Service | http://localhost:8081 | Your application |
| Swagger | http://localhost:8081/swagger-ui.html | API testing |
| Health Check | http://localhost:8081/actuator/health | Service health |
| Metrics | http://localhost:8081/actuator/prometheus | Raw metrics |
| Prometheus | http://localhost:9090 | Metrics database |
| Grafana | http://localhost:3000 | Dashboards |

### Useful Prometheus Queries

```promql
# Login success rate (per minute)
rate(auth_login_success_total[5m]) * 60

# Login failure rate (per minute)
rate(auth_login_failure_total[5m]) * 60

# Average login duration
rate(auth_login_duration_seconds_sum[5m]) / rate(auth_login_duration_seconds_count[5m])

# Memory usage percentage
jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"} * 100

# HTTP request rate
rate(http_server_requests_seconds_count[5m])

# Email success rate
rate(auth_email_sent_total[5m]) / (rate(auth_email_sent_total[5m]) + rate(auth_email_failed_total[5m])) * 100
```

---

## 🐛 Troubleshooting

### Prometheus can't scrape auth-service

**Problem:** Target shows as "DOWN" in http://localhost:9090/targets

**Solutions:**
1. Make sure auth-service is running: `curl http://localhost:8081/actuator/health`
2. Check metrics endpoint: `curl http://localhost:8081/actuator/prometheus`
3. Check Prometheus config: `prometheus --config.file=prometheus.yml --config.check`

### Grafana shows "No data"

**Problem:** Panels show "No data" message

**Solutions:**
1. Check Prometheus data source is working (Settings → Data sources → Test)
2. Generate some traffic to create metrics
3. Check time range (top right) - set to "Last 15 minutes"
4. Verify query syntax in panel

### Metrics are all zero

**Problem:** All counters show 0

**Solution:** Generate traffic! Use the test script or Swagger to create some activity.

---

## 🚀 Next Steps

### For Development
- ✅ Keep Prometheus and Grafana running
- ✅ Create custom dashboards for your needs
- ✅ Set up alerts for important metrics

### For Production
- 📦 Deploy Prometheus with persistent storage
- 🔒 Secure Grafana with proper authentication
- 🌐 Use Grafana Cloud (free tier available)
- 📧 Configure email/Slack alerts
- 💾 Set up long-term metrics storage

---

## 💡 Pro Tips

1. **Dashboard Organization:**
   - Create separate dashboards for: Overview, Performance, Security, Business Metrics
   - Use folders to organize dashboards

2. **Time Ranges:**
   - Use "Last 15 minutes" for real-time monitoring
   - Use "Last 24 hours" for daily trends
   - Use "Last 7 days" for weekly analysis

3. **Variables:**
   - Add dashboard variables for environment, service, etc.
   - Makes dashboards reusable

4. **Annotations:**
   - Add annotations for deployments
   - Mark important events on graphs

5. **Sharing:**
   - Export dashboards as JSON
   - Share with your team
   - Version control your dashboards

---

## ✅ Verification Checklist

- [ ] Prometheus installed and running on port 9090
- [ ] Grafana installed and running on port 3000
- [ ] Prometheus successfully scraping auth-service
- [ ] Grafana connected to Prometheus
- [ ] At least one dashboard created
- [ ] Test traffic generated
- [ ] Metrics visible in graphs
- [ ] (Optional) Alerts configured

---

**Congratulations!** 🎉

You now have professional-grade monitoring for your auth-service!
