# SkillSwap - Marketplace de Intercambio de Habilidades

## 🎯 Proyecto Seleccionado
**SkillSwap** - Aplicación multiplataforma para intercambio de habilidades P2P, optimizada para el mercado europeo.

## 📁 Estructura del Proyecto

```
skillswap-app/
├── mobile/                 # React Native App (iOS + Android)
├── backend/                # Spring Boot API
├── docs/                   # Documentación del proyecto
└── docker/                 # Containerización y deployment
```

## 📋 Documentación

- **[PRD.md](./PRD.md)** - Product Requirements Document completo
- **[MercadoObjetivo.md](./MercadoObjetivo.md)** - Análisis de mercado y 3 propuestas
- **[SkillSwap-TechnicalSpecs.md](./SkillSwap-TechnicalSpecs.md)** - Especificaciones técnicas detalladas

## 🚀 Quick Start

### Requisitos Previos
- Node.js 18+
- Java 17+
- Xcode (para iOS)
- Android Studio (para Android)
- Docker (opcional)

### Setup Desarrollo
```bash
# Clonar el proyecto
cd skillswap-app

# Setup Backend
cd backend
./mvnw spring-boot:run

# Setup Mobile
cd ../mobile
npm install
npx react-native run-ios     # Para iOS
npx react-native run-android # Para Android
```

## 📊 Market Focus - Europa

### Target Countries (Phase 1)
- 🇪🇸 **España** - 47M población, 64% Android
- 🇫🇷 **Francia** - 68M población, 72% Android  
- 🇩🇪 **Alemania** - 84M población, 74% Android
- 🇬🇧 **Reino Unido** - 67M población, 51% Android
- 🇮🇹 **Italia** - 59M población, 76% Android

### Device Compatibility
- **Android:** 8.0+ (93% coverage)
- **iOS:** 13.0+ (97% coverage)
- **Optimizado para:** Galaxy A54, iPhone 13/14, Pixel 7

## 🛠️ Tech Stack

### Frontend (Mobile)
- **React Native 0.72+** con TypeScript
- **React Navigation 6** (navegación)
- **Redux Toolkit** (estado global)
- **React Native WebRTC** (video calling)

### Backend
- **Spring Boot 3.1+** con Java 17
- **PostgreSQL** (producción) / H2 (desarrollo)
- **Redis** (caching y sessions)
- **JWT + OAuth2** (autenticación)

### DevOps
- **Docker** (containerización)
- **GitHub Actions** (CI/CD)
- **Cloud deployment** (GCP/AWS)

## 🎯 Core Features

### MVP (Sprints 1-6)
- ✅ Autenticación y perfiles de usuario
- ✅ Gestión de habilidades (skill inventory)
- ✅ Algoritmo de matching P2P
- ✅ Sistema de créditos (1 hora = 1 crédito)
- ✅ Programación de sesiones
- ✅ Video calling integrado

### Advanced (Sprints 7-12)
- 🔄 Algoritmo de matching con ML
- 🔄 Verificación de habilidades
- 🔄 Sesiones grupales
- 🔄 Learning paths colaborativos
- 🔄 Analytics y reportes

## 📈 Business Model

### Monetización
- **Freemium:** Básico gratis, Premium €14.99/mes
- **Enterprise:** €199/mes para empresas
- **Target Revenue Year 2:** €1.8M

### Success Metrics
- **1000+ usuarios activos** en 6 meses
- **60% matches → sesiones programadas**
- **15% conversión a premium** en 3 meses
- **Rating 4.2+ estrellas** en stores

## 🌍 Localization

### Idiomas Soportados
- 🇬🇧 English (base)
- 🇪🇸 Español 
- 🇫🇷 Français
- 🇩🇪 Deutsch
- 🇮🇹 Italiano

### GDPR Compliance
- ✅ Consentimiento explícito
- ✅ Right to erasure
- ✅ Data portability
- ✅ Privacy by design

## 📅 Timeline

**Total Duration:** 8 meses (16 sprints)
- **MVP:** 3 meses (Sprints 1-6)
- **Beta:** 2 meses (Sprints 7-10) 
- **Launch:** 3 meses (Sprints 11-16)

## 🎬 Next Steps

1. **Setup inicial** del proyecto React Native + Spring Boot
2. **Implementación MVP** siguiendo roadmap de sprints
3. **Testing** en dispositivos europeos reales
4. **Beta launch** con 100 usuarios en Madrid
5. **Scale** a 5 países europeos

---

**Status:** ✨ Ready to start development
**Last Updated:** 6 septiembre 2025
**Team:** 1 Full-Stack Developer + 1 Designer (part-time)
