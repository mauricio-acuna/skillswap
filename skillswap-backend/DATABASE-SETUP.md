# SkillSwap Backend - Test Data & Database Setup

## 🗄️ Base de Datos con Flyway

### Estado Actual
- ✅ **15 Migraciones** Flyway completamente funcionales (V1-V15)
- ✅ **Datos de Prueba** completos y realistas insertados
- ✅ **PostgreSQL/H2** soporte dual para desarrollo y producción
- ✅ **Script automatizado** para setup completo

### Migraciones Disponibles

| Versión | Descripción | Estado |
|---------|-------------|--------|
| V1-V13 | Schema completo | ✅ Activo |
| V14 | Datos de ejemplo principales | ✅ Nuevo |
| V15 | Datos adicionales de testing | ✅ Nuevo |

## 👥 Usuarios de Prueba Creados

### Perfiles Completos con Skills Reales

1. **Juan Pérez** - `juan.perez@email.com`
   - 🎯 **Expertise:** JavaScript, React.js, Node.js
   - 📚 **Quiere aprender:** Machine Learning, Docker
   - 💰 **Créditos:** 28

2. **María García** - `maria.garcia@email.com`
   - 🎯 **Expertise:** UI/UX Design, Figma
   - 📚 **Quiere aprender:** JavaScript, React.js
   - 💰 **Créditos:** 40

3. **Carlos Rodríguez** - `carlos.rodriguez@email.com`
   - 🎯 **Expertise:** Python, Machine Learning, SQL
   - 📚 **Quiere aprender:** Node.js
   - 💰 **Créditos:** 75

4. **Ana López** - `ana.lopez@email.com`
   - 🎯 **Expertise:** Digital Marketing, SEO
   - 📚 **Quiere aprender:** UI/UX Design, Python
   - 💰 **Créditos:** 20

5. **David Martín** - `david.martin@email.com`
   - 🎯 **Expertise:** Docker, Kubernetes, AWS
   - 📚 **Quiere aprender:** Machine Learning
   - 💰 **Créditos:** 55

6. **Lucía Fernández** - `lucia.fernandez@email.com`
   - 🎯 **Expertise:** Product Management, Agile/Scrum
   - 📚 **Quiere aprender:** UI/UX Design, SQL
   - 💰 **Créditos:** 22

7. **Miguel Santos** - `miguel.santos@email.com`
   - 🎯 **Expertise:** Docker, Kubernetes, AWS
   - 📚 **Quiere aprender:** Python
   - 💰 **Créditos:** 45

8. **Elena Ruiz** - `elena.ruiz@email.com`
   - 🎯 **Expertise:** JavaScript, React.js
   - 📚 **Quiere aprender:** Node.js, Python
   - 💰 **Créditos:** 20

**Credenciales:** Todos los usuarios usan `password123`

## 🤝 Matches y Sesiones Activas

### Matches en Diferentes Estados

1. **JavaScript Teaching** (María ← Juan)
   - 📅 Estado: `PENDING`
   - 🕐 Programada: 20 Ene 15:00
   - 💬 Conversación activa

2. **Machine Learning** (Juan ← Carlos)
   - 📅 Estado: `COMPLETED`
   - ⭐ Rating: 4/5 estrellas
   - 📹 Video grabado disponible

3. **UX Design** (Ana ← María)
   - 📅 Estado: `ACCEPTED`
   - 📍 Presencial en Madrid
   - 💬 Coordinando lugar

4. **Docker Training** (Juan ← David)
   - 📅 Estado: `COMPLETED`
   - ⭐ Rating: 5/5 estrellas
   - ✅ Homework completado

5. **Python Backend** (Elena ← Carlos)
   - 📅 Estado: `IN_PROGRESS`
   - 🔴 Sesión live activa
   - 📚 Flask API en desarrollo

## 💬 Conversaciones de Chat Realistas

- **15+ mensajes** de ejemplo
- Coordinación de horarios
- Feedback post-sesión
- Intercambio de recursos
- Seguimiento de homework

## 🎥 Sesiones de Video y Aprendizaje

### Video Sessions
- Zoom, Google Meet, Teams
- Estados: Programadas, En curso, Completadas
- URLs de grabaciones

### Learning Sessions
- Notas detalladas de cada sesión
- Homework asignado y completado
- Ratings bidireccionales
- Duración y progreso tracking

## 💰 Sistema de Créditos Funcional

### Transacciones Reales
- Créditos de bienvenida (20-45)
- Ganancia por enseñar (12-18 por sesión)
- Gasto por aprender (8-12 por sesión)
- Bonus por homework (5 créditos)
- Verificación de perfil (10 créditos)

## 🚀 Setup Rápido

### Opción 1: Script Automatizado
```bash
./setup-database.sh
```

### Opción 2: Manual con Maven
```bash
# H2 Database (desarrollo)
mvn flyway:migrate -Dspring.profiles.active=dev

# PostgreSQL (producción)  
mvn flyway:migrate -Dspring.profiles.active=prod

# Docker PostgreSQL
docker-compose up -d postgres
mvn flyway:migrate -Dspring.profiles.active=docker
```

### Opción 3: Con Docker Compose
```bash
docker-compose up -d
```

## 🔧 Testing y Validación

### Endpoints para Probar
- `GET /api/users` - Ver usuarios
- `GET /api/skills` - Listar skills
- `GET /api/matches` - Ver matches activos
- `POST /api/auth/login` - Login con usuarios test
- `GET /api/chat/conversations` - Conversaciones

### Swagger UI
- **URL:** http://localhost:8080/swagger-ui/index.html
- **Testing interactivo** de todos los endpoints
- **Documentación completa** de API

### H2 Console (Desarrollo)
- **URL:** http://localhost:8080/h2-console
- **JDBC URL:** jdbc:h2:mem:skillswap
- **Username:** sa
- **Password:** (vacío)

## 📊 Datos de Ejemplo Incluidos

### 15 Skills Categorizadas
- **Programming:** JavaScript, React.js, Node.js, Python
- **Data Science:** Machine Learning, SQL
- **Design:** UI/UX Design, Figma
- **DevOps:** Docker, Kubernetes, AWS
- **Marketing:** Digital Marketing, SEO
- **Business:** Product Management, Agile/Scrum

### Casos de Uso Reales
- ✅ Developer quiere aprender ML
- ✅ Designer quiere aprender código
- ✅ Marketer quiere UX skills
- ✅ DevOps engineer enseña containers
- ✅ Data scientist comparte Python
- ✅ Product manager mentoriza

## 🎯 Próximos Pasos

1. **Ejecutar el setup:** `./setup-database.sh`
2. **Iniciar aplicación:** `mvn spring-boot:run`
3. **Probar endpoints:** Swagger UI
4. **Login con usuarios test**
5. **Conectar frontend** para testing E2E

¡La base de datos está lista para **demostración completa** y **testing integral**! 🚀
