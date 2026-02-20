# 📊 Auth Service - Monitoring & Observability Guide

## Overview

This guide covers the complete monitoring and observability setup for the Auth Service, including Prometheus metrics, Grafana dashboards, health checks, and structured logging.

---

## Table of Contents

1. [Architecture](#architecture)
2. [Metrics](#metrics)
3. [Health Checks](#health-checks)
4. [Logging](#logging)
5. [Prometheus Setup](#prometheus-setup)
6. [Grafana Setup](#grafana-setup)
7. [Alerts](#alerts)
8. [Testing](#testing)

---

## Architecture

### Components

- **Prometheus**: Metrics collection and storage
- **Grafana**: Metrics visualization and dashboards
- **Micrometer**: Metrics instrumentation library
- **Spring Boot Actuator**: Health checks and metrics endpoints
- **Logback**: Structured logging

### Endpoints

- `/actuator/health` - Overall health status
- `/actuator/health/liveness` - Kubernetes liveness probe
- `/actuator/health/readiness` - Kubernetes readiness probe
- `/actuator/prometheus` - Prometheus metrics endpoint
- `/actuator/metrics` - All available metrics

---

## Metrics

### Business Metrics

#### Authentication Metrics
- `auth_login_success_total` - Successful login attempts
- `auth_login_failure_total` - Failed login attempts
- `auth_login_duration_seconds` - Login operation duration
- `auth_oauth2_login_total` - OAuth2 login attempts

#### Registration Metrics
- `auth_registration_total` - User registrations
- `auth_registration_duration_seconds` - Registration operation duration
- `auth_activation_total` - Account activations

#### Session Metrics
- `auth_session_created_total` - Sessions created
- `auth_session_terminated_total` - Sessions terminated
- `auth_sessions_active` - Currently active sessions (gauge)
- `auth_session_suspicious_total` - Suspicious sessions detected

#### Security Metrics
- `auth_ratelimit_exceeded_total` - Rate limit violations
- `auth_token_invalid_total` - Invalid token attempts
- `auth_password_reset_total` - Password reset requests

#### Email Metrics
- `auth_email_sent_total` - Emails sent successfully
- `auth_email_failed_total` - Email sending failures

#### User Metrics
- `auth_users_total` - Total users in system (gauge)
- `auth_invitation_sent_total` - Invitations sent
- `auth_invitation_accepted_total` - Invitations accepted

### System Metrics (Automatic)

- `jvm_memory_used_bytes` - JVM memory usage
- `jvm_memory_max_bytes` - JVM max memory
- `http_server_requests_seconds` - HTTP request metrics
- `process_cpu_usage` - CPU usage
- `system_cpu_usage` - System CPU usage
- `jdbc_connections_active` - Active database connections

---

## Health Checks

### Custom Health Indicators

#### Database Health Check
Verifies database connectivity by executing a simple query.

```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        // Checks database connection
        return Health.up().withDetail("database", "Connected").build();
    }
}
```

#### Email Service Health Check
Verifies email service configuration and connectivity.

```java
@Component
public class EmailServiceHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        // Checks email service
        return Health.up().withDetail("email", "Configured").build();
    }
}
```

#### Application Memory Health Check
Monitors JVM memory usage and warns if threshold exceeded.

```java
@Component
public class ApplicationMemoryHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        // Checks memory usage
        double usagePercent = (usedMemory / maxMemory) * 100;
        if (usagePercent > 90) {
            return Health.down().withDetail("memory", "High usage").build();
        }
        return Health.up().withDetail("memory", usagePercent + "%").build();
    }
}
```

### Health Check Endpoints

- **Liveness**: `/actuator/health/liveness` - Returns UP if application is running
- **Readiness**: `/actuator/health/readiness` - Returns UP if application is ready to serve traffic
- **Overall**: `/actuator/health` - Aggregated health status

---

## Logging

### Structured Logging

The service uses structured JSON logging for production environments:

```json
{
  "timestamp": "2026-02-20T10:15:30.123Z",
  "level": "INFO",
  "logger": "com.englishflow.auth.service.AuthService",
  "message": "User logged in successfully",
  "userId": "123",
  "email": "user@example.com",
  "ipAddress": "192.168.1.1"
}
```

### Log Levels

- **ERROR**: Exceptions and critical errors
- **WARN**: Warning conditions (rate limits, suspicious activity)
- **INFO**: Important business events (login, registration)
- **DEBUG**: Detailed debugging information

### Log Files

- `logs/auth-service.log` - Human-readable logs (development)
- `logs/auth-service.json` - JSON logs (production)
- Rolling policy: 30 days retention, 1GB max size

---

## Prometheus Setup

### Installation (macOS)

```bash
brew install prometheus
```

### Configuration

Create `prometheus.yml` in project root:

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'auth-service'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8081']
        labels:
          application: 'auth-service'
          environment: 'development'
```

### Start Prometheus

```bash
prometheus --config.file=prometheus.yml
```

Access Prometheus UI: http://localhost:9090

### Verify Metrics Collection

1. Go to http://localhost:9090/targets
2. Verify `auth-service` target is UP (green)
3. Query metrics: `auth_login_success_total`

---

## Grafana Setup

### Installation (macOS)

```bash
brew install grafana
brew services start grafana
```

Access Grafana: http://localhost:3000 (admin/admin)

### Add Prometheus Data Source

1. Go to Configuration → Data sources
2. Click "Add data source"
3. Select "Prometheus"
4. URL: `http://localhost:9090`
5. Click "Save & Test"

### Create Dashboard

#### Recommended Panels

**1. Login Success Rate**
```promql
rate(auth_login_success_total[5m]) * 60
```

**2. Login Failure Rate**
```promql
rate(auth_login_failure_total[5m]) * 60
```

**3. Average Login Duration**
```promql
rate(auth_login_duration_seconds_sum[5m]) / rate(auth_login_duration_seconds_count[5m])
```

**4. Active Sessions**
```promql
auth_sessions_active
```

**5. Memory Usage (%)**
```promql
jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"} * 100
```

**6. HTTP Request Rate**
```promql
rate(http_server_requests_seconds_count[5m])
```

---

## Alerts

### Alert Rules

Create `alert-rules.yml`:

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
        annotations:
          summary: "High login failure rate detected"
          description: "Login failures: {{ $value }}/sec"

      # High memory usage
      - alert: HighMemoryUsage
        expr: (jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"}) * 100 > 90
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "High memory usage"
          description: "Memory usage: {{ $value }}%"

      # Service down
      - alert: ServiceDown
        expr: up{job="auth-service"} == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Auth service is down"
```

Update `prometheus.yml`:

```yaml
rule_files:
  - "alert-rules.yml"
```

View alerts: http://localhost:9090/alerts

---

## Testing

### Manual Testing

#### Test Health Checks

```bash
curl http://localhost:8081/actuator/health
curl http://localhost:8081/actuator/health/liveness
curl http://localhost:8081/actuator/health/readiness
```

#### View Metrics

```bash
curl http://localhost:8081/actuator/prometheus | grep auth_
```

#### Query Specific Metric

```bash
curl http://localhost:8081/actuator/metrics/auth.login.success
```

### Generate Test Traffic

Use Swagger UI (http://localhost:8081/swagger-ui.html) to:
1. Register users
2. Attempt logins
3. Test various endpoints

Watch metrics update in Grafana dashboard.

---

## Production Deployment

### Checklist

- [ ] Configure persistent storage for Prometheus
- [ ] Set up Grafana authentication
- [ ] Configure alert notifications (email/Slack)
- [ ] Set up log aggregation (ELK/Loki)
- [ ] Configure metrics retention policy
- [ ] Set up backup for Grafana dashboards
- [ ] Configure SSL/TLS for endpoints
- [ ] Set up monitoring for Prometheus/Grafana themselves

### Security Considerations

1. **Secure Actuator Endpoints**: Restrict access to `/actuator/*` endpoints
2. **Authentication**: Enable authentication for Prometheus and Grafana
3. **Network Security**: Use firewall rules to restrict access
4. **Sensitive Data**: Avoid logging sensitive information (passwords, tokens)

---

## Troubleshooting

### Prometheus Not Scraping

**Problem**: Target shows as DOWN in Prometheus

**Solutions**:
1. Verify service is running: `curl http://localhost:8081/actuator/health`
2. Check metrics endpoint: `curl http://localhost:8081/actuator/prometheus`
3. Verify Prometheus config: `prometheus --config.file=prometheus.yml --config.check`

### Grafana Shows "No Data"

**Problem**: Dashboard panels show "No data"

**Solutions**:
1. Verify Prometheus data source is connected
2. Check time range (set to "Last 15 minutes")
3. Generate traffic to create metrics
4. Verify query syntax in panel

### High Memory Usage

**Problem**: Memory usage alert triggered

**Solutions**:
1. Check for memory leaks in application logs
2. Increase JVM heap size: `-Xmx2g`
3. Analyze heap dump: `jmap -dump:live,format=b,file=heap.bin <pid>`
4. Review database connection pool settings

---

## Best Practices

1. **Metric Naming**: Use consistent naming conventions (prefix with `auth_`)
2. **Labels**: Use labels for dimensions (environment, service, etc.)
3. **Cardinality**: Avoid high-cardinality labels (user IDs, emails)
4. **Retention**: Configure appropriate retention periods
5. **Dashboards**: Create role-specific dashboards (ops, dev, business)
6. **Alerts**: Set meaningful thresholds and avoid alert fatigue
7. **Documentation**: Keep this guide updated with changes

---

## References

- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)
- [Micrometer Documentation](https://micrometer.io/docs/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)

---

**Last Updated**: February 20, 2026  
**Version**: 1.0  
**Status**: Production Ready
