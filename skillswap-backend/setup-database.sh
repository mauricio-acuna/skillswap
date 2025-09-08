# 🚀 Script para ejecutar migraciones Flyway y poblar base de datos

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}   SkillSwap - Database Setup Script    ${NC}"
echo -e "${BLUE}========================================${NC}"

# Verificar que Java está instalado
echo -e "\n${YELLOW}📋 Verificando prerequisitos...${NC}"
if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ Java no está instalado. Por favor instala Java 17+${NC}"
    exit 1
fi

java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$java_version" -lt 17 ]; then
    echo -e "${RED}❌ Java 17+ es requerido. Versión actual: $java_version${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Java instalado correctamente${NC}"

# Verificar Maven
if ! command -v mvn &> /dev/null; then
    echo -e "${YELLOW}⚠️  Maven no encontrado, usando Maven wrapper si está disponible...${NC}"
    if [ ! -f "./mvnw" ]; then
        echo -e "${RED}❌ Maven wrapper no encontrado. Instalando Maven...${NC}"
        # Para macOS con Homebrew
        if command -v brew &> /dev/null; then
            brew install maven
        else
            echo -e "${RED}❌ Por favor instala Maven manualmente${NC}"
            exit 1
        fi
    else
        MVN_CMD="./mvnw"
    fi
else
    MVN_CMD="mvn"
    echo -e "${GREEN}✅ Maven instalado correctamente${NC}"
fi

# Función para ejecutar comandos Maven
run_maven() {
    echo -e "${BLUE}🔧 Ejecutando: $MVN_CMD $1${NC}"
    $MVN_CMD $1
    if [ $? -ne 0 ]; then
        echo -e "${RED}❌ Error ejecutando: $MVN_CMD $1${NC}"
        exit 1
    fi
}

# Limpiar y compilar proyecto
echo -e "\n${YELLOW}🧹 Limpiando proyecto...${NC}"
run_maven "clean"

echo -e "\n${YELLOW}🔨 Compilando proyecto...${NC}"
run_maven "compile"

# Mostrar información de Flyway
echo -e "\n${YELLOW}📊 Información de migraciones Flyway...${NC}"
run_maven "flyway:info"

# Pregunta si quiere continuar con las migraciones
echo -e "\n${YELLOW}¿Deseas ejecutar las migraciones de base de datos? (y/n):${NC}"
read -r response
if [[ "$response" =~ ^([yY][eE][sS]|[yY]|[sS]|[sí])$ ]]; then
    
    # Preguntar por el perfil de base de datos
    echo -e "\n${YELLOW}Selecciona el perfil de base de datos:${NC}"
    echo -e "1) ${GREEN}H2 (desarrollo/testing)${NC}"
    echo -e "2) ${BLUE}PostgreSQL (producción)${NC}"
    echo -e "3) ${YELLOW}Docker PostgreSQL${NC}"
    echo -e "\nElige una opción (1-3): "
    read -r db_choice
    
    case $db_choice in
        1)
            echo -e "\n${GREEN}🗄️  Usando H2 Database (en memoria)${NC}"
            PROFILE="-Dspring.profiles.active=dev"
            ;;
        2)
            echo -e "\n${BLUE}🐘 Usando PostgreSQL local${NC}"
            echo -e "${YELLOW}Asegúrate de que PostgreSQL esté ejecutándose localmente${NC}"
            PROFILE="-Dspring.profiles.active=prod"
            ;;
        3)
            echo -e "\n${BLUE}🐳 Iniciando PostgreSQL con Docker...${NC}"
            if command -v docker &> /dev/null; then
                docker-compose up -d postgres
                echo -e "${GREEN}✅ PostgreSQL iniciado en Docker${NC}"
                sleep 5
            else
                echo -e "${RED}❌ Docker no está instalado${NC}"
                exit 1
            fi
            PROFILE="-Dspring.profiles.active=docker"
            ;;
        *)
            echo -e "${RED}❌ Opción inválida${NC}"
            exit 1
            ;;
    esac
    
    # Ejecutar migraciones
    echo -e "\n${YELLOW}🚀 Ejecutando migraciones Flyway...${NC}"
    run_maven "flyway:migrate $PROFILE"
    
    echo -e "\n${GREEN}✅ Migraciones ejecutadas correctamente${NC}"
    
    # Mostrar información final
    echo -e "\n${YELLOW}📋 Estado final de migraciones:${NC}"
    run_maven "flyway:info $PROFILE"
    
    # Verificar datos insertados
    echo -e "\n${GREEN}📊 Datos de ejemplo insertados:${NC}"
    echo -e "   • ${BLUE}8 usuarios${NC} con perfiles completos"
    echo -e "   • ${BLUE}15 skills${NC} en diferentes categorías"
    echo -e "   • ${BLUE}6 matches${NC} en diferentes estados"
    echo -e "   • ${BLUE}Conversaciones de chat${NC} realistas"
    echo -e "   • ${BLUE}Sesiones de video${NC} y aprendizaje"
    echo -e "   • ${BLUE}Sistema de créditos${NC} funcional"
    echo -e "   • ${BLUE}Reviews y ratings${NC} de ejemplo"
    
else
    echo -e "${YELLOW}⏭️  Saltando migraciones de base de datos${NC}"
fi

# Opciones adicionales
echo -e "\n${YELLOW}¿Deseas ejecutar los tests para validar la funcionalidad? (y/n):${NC}"
read -r test_response
if [[ "$test_response" =~ ^([yY][eE][sS]|[yY]|[sS]|[sí])$ ]]; then
    echo -e "\n${YELLOW}🧪 Ejecutando tests...${NC}"
    run_maven "test $PROFILE"
    echo -e "\n${GREEN}✅ Tests completados${NC}"
fi

echo -e "\n${YELLOW}¿Deseas iniciar la aplicación? (y/n):${NC}"
read -r start_response
if [[ "$start_response" =~ ^([yY][eE][sS]|[yY]|[sS]|[sí])$ ]]; then
    echo -e "\n${GREEN}🚀 Iniciando aplicación SkillSwap...${NC}"
    echo -e "${BLUE}La aplicación estará disponible en: http://localhost:8080${NC}"
    echo -e "${BLUE}Swagger UI: http://localhost:8080/swagger-ui/index.html${NC}"
    echo -e "${BLUE}H2 Console (si usas H2): http://localhost:8080/h2-console${NC}"
    echo -e "\n${YELLOW}Presiona Ctrl+C para detener la aplicación${NC}"
    
    run_maven "spring-boot:run $PROFILE"
fi

echo -e "\n${GREEN}========================================${NC}"
echo -e "${GREEN}   ✅ Setup completado exitosamente!    ${NC}"
echo -e "${GREEN}========================================${NC}"
echo -e "\n${BLUE}Credenciales de usuarios de prueba:${NC}"
echo -e "📧 ${YELLOW}Email:${NC} juan.perez@email.com | ${YELLOW}Password:${NC} password123"
echo -e "📧 ${YELLOW}Email:${NC} maria.garcia@email.com | ${YELLOW}Password:${NC} password123"
echo -e "📧 ${YELLOW}Email:${NC} carlos.rodriguez@email.com | ${YELLOW}Password:${NC} password123"
echo -e "\n${BLUE}Próximos pasos:${NC}"
echo -e "1. 🌐 Abrir http://localhost:8080/swagger-ui/index.html"
echo -e "2. 🔐 Hacer login con cualquier usuario de prueba"
echo -e "3. 🔍 Explorar los endpoints disponibles"
echo -e "4. 📱 Conectar el frontend para testing completo"
