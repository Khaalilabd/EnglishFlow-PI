# Monitoring Configuration

This directory contains the monitoring configuration files for the EnglishFlow Auth Service.

## Files

- `prometheus.yml` - Prometheus configuration
- `alert-rules.yml` - Prometheus alert rules
- `grafana-dashboard-working.json` - Grafana dashboard

## Quick Start

### 1. Start Prometheus

```bash
cd monitoring
prometheus --config.file=prometheus.yml
```

Access: http://localhost:9090

### 2. Start Grafana

```bash
brew services start grafana
```

Access: http://localhost:3000 (admin/admin)

### 3. Import Dashboard

1. Go to Grafana → Dashboards → Import
2. Upload `grafana-dashboard-working.json`
3. Select Prometheus data source
4. Click Import

## Documentation

See `backend/auth-service/docs/MONITORING_GUIDE.md` for complete documentation.
