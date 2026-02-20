# 📊 Metrics Testing Guide

## ✅ Health Checks (Already Working!)

You've already tested these successfully:
- ✅ http://localhost:8081/actuator/health - Overall health
- ✅ http://localhost:8081/actuator/health/liveness - Liveness probe
- ✅ http://localhost:8081/actuator/health/readiness - Readiness probe

All showing "UP" means your service is healthy! 🎉

---

## 📈 Testing Metrics (3 Options)

### Option 1: View Raw Metrics (No Setup Required) ⚡

#### Step 1: View All Available Metrics
Open in browser:
```
http://localhost:8081/actuator/metrics
```

You'll see a list like:
```json
{
  "names": [
    "auth.login.success",
    "auth.login.failure",
    "auth.registration",
    "http.server.requests",
    "jvm.memory.used",
    ...
  ]
}
```

#### Step 2: View Specific Metric
```
http://localhost:8081/actuator/metrics/auth.login.success
```

Response:
```json
{
  "name": "auth.login.success",
  "measurements": [
    {
      "statistic": "COUNT",
      "value": 15.0
    }
  ]
}
```

#### Step 3: View Prometheus Format
```
http://localhost:8081/actuator/prometheus
```

You'll see:
```
# HELP auth_login_success_total Number of successful login attempts
# TYPE auth_login_success_total counter
auth_login_success_total 15.0

# HELP auth_login_failure_total Number of failed login attempts
# TYPE auth_login_failure_total counter
auth_login_failure_total 3.0

# HELP auth_login_duration_seconds Time taken for login operations
# TYPE auth_login_duration_seconds summary
auth_login_duration_seconds_count 18.0
auth_login_duration_seconds_sum 2.456
```

---

### Option 2: Quick Visualization with Prometheus (Recommended) 📊

#### Step 1: Install Prometheus (5 minutes)

**macOS:**
```bash
brew install prometheus
```

**Or Download:**
https://prometheus.io/download/

#### Step 2: Create Prometheus Config

Create file `prometheus.yml`:
```yaml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'auth-service'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8081']
```

#### Step 3: Start Prometheus
```bash
prometheus --config.file=prometheus.yml
```

#### Step 4: Open Prometheus UI
```
http://localhost:9090
```

#### Step 5: Query Metrics

In the Prometheus UI, try these queries:

**Login Success Rate:**
```
rate(auth_login_success_total[5m])
```

**Login Failure Rate:**
```
rate(auth_login_failure_total[5m])
```

**Average Login Duration:**
```
rate(auth_login_duration_seconds_sum[5m]) / rate(auth_login_duration_seconds_count[5m])
```

**Total Registrations:**
```
auth_registration_total
```

**Memory Usage:**
```
jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"} * 100
```

Click "Graph" tab to see visual charts! 📈

---

### Option 3: Beautiful Dashboards with Grafana (Best Experience) 🎨

#### Step 1: Install Grafana

**macOS:**
```bash
brew install grafana
brew services start grafana
```

**Or Download:**
https://grafana.com/grafana/download

#### Step 2: Open Grafana
```
http://localhost:3000
```

Default credentials:
- Username: `admin`
- Password: `admin`

#### Step 3: Add Prometheus Data Source

1. Click "⚙️ Configuration" → "Data Sources"
2. Click "Add data source"
3. Select "Prometheus"
4. Set URL: `http://localhost:9090`
5. Click "Save & Test"

#### Step 4: Create Dashboard

1. Click "+" → "Dashboard"
2. Click "Add new panel"
3. Enter query (examples below)
4. Click "Apply"

**Example Queries for Panels:**

**Panel 1: Login Success Rate**
```
rate(auth_login_success_total[5m]) * 60
```
Title: "Successful Logins per Minute"

**Panel 2: Login Failure Rate**
```
rate(auth_login_failure_total[5m]) * 60
```
Title: "Failed Logins per Minute"

**Panel 3: Average Login Duration**
```
rate(auth_login_duration_seconds_sum[5m]) / rate(auth_login_duration_seconds_count[5m])
```
Title: "Average Login Duration (seconds)"

**Panel 4: Total Users**
```
auth_users_total
```
Title: "Total Users"

**Panel 5: Active Sessions**
```
auth_sessions_active
```
Title: "Active Sessions"

**Panel 6: Memory Usage**
```
jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"} * 100
```
Title: "JVM Heap Memory Usage (%)"

---

## 🧪 Generate Test Data

To see metrics in action, generate some traffic:

### Test Script (Bash)

Create `test-metrics.sh`:
```bash
#!/bin/bash

echo "🧪 Generating test traffic..."

# Test successful login (will fail but generate metrics)
for i in {1..10}; do
  echo "Test $i: Login attempt"
  curl -X POST http://localhost:8081/auth/login \
    -H "Content-Type: application/json" \
    -d '{"email":"test@example.com","password":"Test123"}' \
    -s -o /dev/null
  sleep 1
done

# Test registration
for i in {1..5}; do
  echo "Test $i: Registration attempt"
  curl -X POST http://localhost:8081/auth/register \
    -H "Content-Type: application/json" \
    -d "{\"email\":\"user$i@example.com\",\"password\":\"Test123\",\"firstName\":\"Test\",\"lastName\":\"User\",\"role\":\"STUDENT\"}" \
    -s -o /dev/null
  sleep 1
done

echo "✅ Test traffic generated!"
echo "📊 Check metrics at: http://localhost:8081/actuator/prometheus"
```

Run it:
```bash
chmod +x test-metrics.sh
./test-metrics.sh
```

---

## 📊 Quick Metrics Check (No Tools Needed)

### Using curl + jq (Pretty Print)

**View all metrics:**
```bash
curl -s http://localhost:8081/actuator/metrics | jq
```

**View specific metric:**
```bash
curl -s http://localhost:8081/actuator/metrics/auth.login.success | jq
```

**View Prometheus metrics:**
```bash
curl -s http://localhost:8081/actuator/prometheus | grep auth_
```

**Count login successes:**
```bash
curl -s http://localhost:8081/actuator/prometheus | grep "auth_login_success_total"
```

---

## 🎯 What Each Metric Means

### Authentication Metrics

| Metric | Type | Description | Example Query |
|--------|------|-------------|---------------|
| `auth_login_success_total` | Counter | Total successful logins | `auth_login_success_total` |
| `auth_login_failure_total` | Counter | Total failed logins | `auth_login_failure_total` |
| `auth_registration_total` | Counter | Total registrations | `auth_registration_total` |
| `auth_activation_total` | Counter | Total activations | `auth_activation_total` |
| `auth_password_reset_total` | Counter | Total password resets | `auth_password_reset_total` |

### Performance Metrics

| Metric | Type | Description | Example Query |
|--------|------|-------------|---------------|
| `auth_login_duration_seconds` | Timer | Login operation duration | `rate(auth_login_duration_seconds_sum[5m])` |
| `auth_registration_duration_seconds` | Timer | Registration duration | `rate(auth_registration_duration_seconds_sum[5m])` |

### Security Metrics

| Metric | Type | Description | Example Query |
|--------|------|-------------|---------------|
| `auth_ratelimit_exceeded_total` | Counter | Rate limit violations | `auth_ratelimit_exceeded_total` |
| `auth_token_invalid_total` | Counter | Invalid token attempts | `auth_token_invalid_total` |
| `auth_session_suspicious_total` | Counter | Suspicious sessions | `auth_session_suspicious_total` |

### System Metrics (Automatic)

| Metric | Type | Description | Example Query |
|--------|------|-------------|---------------|
| `jvm_memory_used_bytes` | Gauge | JVM memory usage | `jvm_memory_used_bytes{area="heap"}` |
| `http_server_requests_seconds` | Timer | HTTP request duration | `rate(http_server_requests_seconds_sum[5m])` |
| `system_cpu_usage` | Gauge | CPU usage | `system_cpu_usage` |

---

## 🔥 Real-World Monitoring Scenarios

### Scenario 1: Detect Brute Force Attack

**Alert when login failures spike:**
```
rate(auth_login_failure_total[5m]) > 10
```

If this triggers, someone might be trying to brute force accounts.

### Scenario 2: Monitor Performance Degradation

**Alert when login takes too long:**
```
rate(auth_login_duration_seconds_sum[5m]) / rate(auth_login_duration_seconds_count[5m]) > 2
```

If average login time > 2 seconds, investigate performance issues.

### Scenario 3: Track User Growth

**Daily new registrations:**
```
increase(auth_registration_total[24h])
```

Shows how many users registered in the last 24 hours.

### Scenario 4: Security Monitoring

**Suspicious activity:**
```
rate(auth_ratelimit_exceeded_total[5m]) > 5
OR
rate(auth_token_invalid_total[5m]) > 10
```

High rate of rate limits or invalid tokens = potential attack.

---

## 📱 Mobile-Friendly Monitoring

### Option: Prometheus Alertmanager + Slack/Email

1. Install Alertmanager
2. Configure alerts
3. Get notifications on your phone!

Example alert config:
```yaml
groups:
  - name: auth-service
    rules:
      - alert: HighLoginFailureRate
        expr: rate(auth_login_failure_total[5m]) > 10
        annotations:
          summary: "High login failure rate!"
          description: "{{ $value }} failures per second"
```

---

## 🎨 Pre-Built Grafana Dashboard (Copy-Paste)

Create a new dashboard and import this JSON:

```json
{
  "dashboard": {
    "title": "Auth Service Monitoring",
    "panels": [
      {
        "title": "Login Success Rate",
        "targets": [
          {
            "expr": "rate(auth_login_success_total[5m]) * 60"
          }
        ]
      },
      {
        "title": "Login Failure Rate",
        "targets": [
          {
            "expr": "rate(auth_login_failure_total[5m]) * 60"
          }
        ]
      },
      {
        "title": "Average Login Duration",
        "targets": [
          {
            "expr": "rate(auth_login_duration_seconds_sum[5m]) / rate(auth_login_duration_seconds_count[5m])"
          }
        ]
      }
    ]
  }
}
```

---

## ✅ Quick Verification Checklist

- [x] Health checks working (you already did this!)
- [ ] Can access `/actuator/metrics`
- [ ] Can access `/actuator/prometheus`
- [ ] Can see metric values change after generating traffic
- [ ] (Optional) Prometheus installed and scraping
- [ ] (Optional) Grafana dashboard created

---

## 🚀 Next Steps

1. **Now**: Test raw metrics with curl
2. **5 min**: Install Prometheus for basic graphs
3. **15 min**: Install Grafana for beautiful dashboards
4. **30 min**: Set up alerts for production

---

## 💡 Pro Tips

1. **Bookmark these URLs:**
   - Health: http://localhost:8081/actuator/health
   - Metrics: http://localhost:8081/actuator/metrics
   - Prometheus: http://localhost:8081/actuator/prometheus

2. **Use browser extensions:**
   - JSONView for Chrome/Firefox (pretty JSON)
   - Prometheus extension for quick queries

3. **Monitor in production:**
   - Set up Grafana Cloud (free tier)
   - Configure alerts to Slack/Email
   - Create dashboards for your team

4. **Performance:**
   - Metrics have minimal overhead (<1% CPU)
   - Async logging prevents blocking
   - Health checks are cached

---

**Need Help?**
- Prometheus docs: https://prometheus.io/docs/
- Grafana docs: https://grafana.com/docs/
- Micrometer docs: https://micrometer.io/docs/
