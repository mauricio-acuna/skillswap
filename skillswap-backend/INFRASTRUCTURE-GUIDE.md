# 🚀 SKILLSWAP BACKEND - INFRASTRUCTURE GUIDE

## 📋 **DEPLOYMENT INFRASTRUCTURE COMPLETA**

### **✅ Componentes Implementados:**

#### **🏗️ Core Infrastructure:**
- **Docker Production Stack** (docker-compose.prod.yml)
- **Monitoring Stack** (docker-compose.monitoring.yml) 
- **Automated Deployment Script** (deploy.sh)
- **Environment Configuration** (.env.example)

#### **📊 Monitoring & Observability:**
- **Prometheus** - Metrics collection
- **Grafana** - Dashboards and visualization
- **AlertManager** - Intelligent alerting
- **Elasticsearch + Logstash + Kibana** - Log management
- **Jaeger** - Distributed tracing
- **Node Exporter** - System metrics
- **Redis Exporter** - Cache metrics
- **Postgres Exporter** - Database metrics

---

## 🚀 **DEPLOYMENT COMMANDS**

### **Full Production Deployment:**
```bash
# 1. Clone and setup
git clone <repository>
cd skillswap-backend

# 2. Configure environment
cp .env.example .env
# Edit .env with production values

# 3. Deploy application
./deploy.sh deploy

# 4. Start monitoring (optional)
docker-compose -f docker-compose.monitoring.yml up -d
```

### **Available Deployment Commands:**
```bash
./deploy.sh deploy      # Full deployment
./deploy.sh build       # Build only
./deploy.sh migrate     # Database migrations only
./deploy.sh health      # Health check
./deploy.sh status      # Show status
./deploy.sh logs        # View logs
./deploy.sh stop        # Stop services
./deploy.sh cleanup     # Clean up resources
```

---

## 📊 **MONITORING DASHBOARD URLs**

### **Application Monitoring:**
- **Application**: http://localhost:8080
- **Health Check**: http://localhost:8080/api/health
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Actuator**: http://localhost:8080/actuator

### **Infrastructure Monitoring:**
- **Grafana**: http://localhost:3001 (admin/skillswap_admin)
- **Prometheus**: http://localhost:9090
- **AlertManager**: http://localhost:9093
- **Kibana**: http://localhost:5601
- **Jaeger**: http://localhost:16686

---

## 🔧 **PRODUCTION CONFIGURATION**

### **Required Environment Variables:**
```bash
# Database
DATABASE_URL=jdbc:postgresql://postgres:5432/skillswap_prod
DATABASE_USERNAME=skillswap_user
DATABASE_PASSWORD=secure_password

# Redis
REDIS_HOST=redis
REDIS_PORT=6379
REDIS_PASSWORD=redis_password

# Security
JWT_SECRET=your-256-bit-secret-key
CORS_ALLOWED_ORIGINS=https://app.skillswap.com

# External Services
AGORA_APP_ID=your-agora-app-id
AGORA_APP_CERTIFICATE=your-agora-certificate
SENDGRID_API_KEY=your-sendgrid-key
```

### **Docker Network Setup:**
```bash
# Create network (run once)
docker network create skillswap-network
```

---

## 📈 **ALERTING CONFIGURATION**

### **Alert Severity Levels:**
- **🚨 Critical**: Application down, database failures
- **⚠️ Warning**: High response times, resource usage
- **ℹ️ Info**: Business metrics, low credit balances

### **Alert Channels:**
- **Email**: devops@skillswap.com, backend-team@skillswap.com
- **Slack**: #critical-alerts, #backend-alerts, #backend-info
- **Webhooks**: Custom integrations

### **Key Metrics Monitored:**
- Application health and uptime
- Response times and error rates
- Memory and CPU usage
- Database performance
- Redis cache performance
- Business KPIs (sessions, users, credits)

---

## 🔍 **TROUBLESHOOTING**

### **Common Issues:**

#### **Application Won't Start:**
```bash
# Check logs
docker-compose -f docker-compose.prod.yml logs skillswap-backend

# Check database connection
docker-compose -f docker-compose.prod.yml logs postgres

# Verify environment variables
docker-compose -f docker-compose.prod.yml config
```

#### **Database Migration Issues:**
```bash
# Reset database (CAUTION: Data loss)
docker-compose -f docker-compose.prod.yml down -v
docker-compose -f docker-compose.prod.yml up -d postgres
./deploy.sh migrate
```

#### **Memory Issues:**
```bash
# Check resource usage
docker stats

# Increase memory limits in docker-compose.prod.yml
# Restart services
docker-compose -f docker-compose.prod.yml restart
```

---

## 🔄 **BACKUP & RECOVERY**

### **Database Backup:**
```bash
# Create backup
docker exec skillswap-postgres pg_dump -U skillswap_user skillswap_prod > backup.sql

# Restore backup
docker exec -i skillswap-postgres psql -U skillswap_user skillswap_prod < backup.sql
```

### **Volume Backup:**
```bash
# Backup all data volumes
docker run --rm -v skillswap_postgres_data:/source -v $(pwd):/backup alpine tar czf /backup/postgres_backup.tar.gz -C /source .
docker run --rm -v skillswap_redis_data:/source -v $(pwd):/backup alpine tar czf /backup/redis_backup.tar.gz -C /source .
```

---

## 📚 **MAINTENANCE PROCEDURES**

### **Regular Maintenance:**
```bash
# 1. Update application
git pull
./deploy.sh build
./deploy.sh deploy

# 2. Clean up old Docker images
./deploy.sh cleanup

# 3. Check system health
./deploy.sh health
./deploy.sh status

# 4. Review logs
./deploy.sh logs
```

### **Security Updates:**
```bash
# Update base images
docker-compose pull
./deploy.sh deploy

# Update application dependencies
./mvnw versions:display-dependency-updates
./mvnw versions:use-latest-versions
```

---

## 🎯 **PRODUCTION READINESS CHECKLIST**

### **✅ Infrastructure:**
- [x] Docker production configuration
- [x] Environment variables secured
- [x] Database migrations automated
- [x] Health checks implemented
- [x] Load balancing ready
- [x] SSL/TLS certificates configured

### **✅ Monitoring:**
- [x] Application metrics
- [x] Infrastructure monitoring
- [x] Log aggregation
- [x] Alerting configured
- [x] Dashboards created
- [x] SLA monitoring

### **✅ Security:**
- [x] Authentication implemented
- [x] Authorization configured
- [x] Rate limiting active
- [x] Input validation
- [x] Security headers
- [x] Audit logging

### **✅ Performance:**
- [x] Connection pooling
- [x] Caching strategy
- [x] Query optimization
- [x] Resource limits
- [x] Scaling configuration

---

## 🏆 **DEPLOYMENT SUCCESS CRITERIA**

**The SkillSwap backend is production-ready when:**

✅ All services start successfully  
✅ Health checks pass  
✅ Database migrations complete  
✅ Monitoring dashboards show green  
✅ All alerts are configured  
✅ Performance metrics are within SLA  
✅ Security scans pass  
✅ Load tests complete successfully  

**🚀 Ready for production deployment!**
