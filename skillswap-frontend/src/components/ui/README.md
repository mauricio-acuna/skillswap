# 🎨 UI Components Library

Una librería completa de componentes UI reutilizables para SkillSwap, construida con React Native y TypeScript.

## 📋 Componentes Disponibles

### 🔘 Button
Botón versátil con múltiples variantes, estados y tamaños.

```typescript
import { Button } from '@components/ui';

<Button
  title="Click me"
  onPress={handlePress}
  variant="primary" // primary | secondary | outline | ghost | danger
  size="medium"     // small | medium | large
  loading={false}
  disabled={false}
  fullWidth={false}
  icon={<Icon name="star" />}
  iconPosition="left" // left | right
/>
```

**Características:**
- ✅ 5 variantes de estilo
- ✅ 3 tamaños diferentes
- ✅ Estados de loading y disabled
- ✅ Soporte para iconos
- ✅ Opción full width
- ✅ Accesibilidad completa

### 🃏 Card
Contenedor flexible para agrupar contenido relacionado.

```typescript
import { Card } from '@components/ui';

<Card
  variant="elevated"  // default | elevated | outlined | filled
  padding="medium"    // none | small | medium | large
  margin="medium"     // none | small | medium | large
  onPress={handlePress} // Opcional: hace la card clickeable
>
  <Text>Card content</Text>
</Card>
```

**Características:**
- ✅ 4 variantes visuales
- ✅ Padding y margin configurables
- ✅ Soporte para interacción táctil
- ✅ Sombras y elevaciones
- ✅ Bordes y backgrounds personalizables

### 👤 Avatar
Componente para mostrar avatares de usuario con iniciales o imágenes.

```typescript
import { Avatar } from '@components/ui';

<Avatar
  source={{ uri: 'https://example.com/avatar.jpg' }}
  name="John Doe"
  size="medium"     // xs | small | medium | large | xl
  variant="circular" // circular | rounded | square
  onPress={handlePress}
  badge={<Badge count={5} />}
  badgePosition="top-right" // top-right | bottom-right | top-left | bottom-left
/>
```

**Características:**
- ✅ Imágenes o iniciales automáticas
- ✅ 5 tamaños diferentes
- ✅ 3 formas (circular, rounded, square)
- ✅ Colores de fondo automáticos basados en nombre
- ✅ Soporte para badges
- ✅ Interacción táctil opcional

### 🏷️ Badge
Indicadores pequeños para mostrar contadores, estados o etiquetas.

```typescript
import { Badge } from '@components/ui';

<Badge
  label="New"          // Texto personalizado
  count={5}           // Número
  variant="success"    // default | success | warning | error | info | secondary
  size="medium"       // small | medium | large
  shape="rounded"     // rounded | pill | square
  showZero={false}    // Mostrar cuando count = 0
  maxCount={99}       // Mostrar "99+" cuando excede
/>
```

**Características:**
- ✅ Texto personalizado o contadores
- ✅ 6 variantes de color
- ✅ 3 tamaños
- ✅ 3 formas diferentes
- ✅ Dots automáticos para contadores pequeños
- ✅ Límite máximo configurable

### ⏳ Loading
Componentes para estados de carga con múltiples estilos.

```typescript
import { Loading, LoadingButton, LoadingDots, LoadingSkeleton } from '@components/ui';

// Loading básico
<Loading
  visible={true}
  message="Loading..."
  size="large"       // small | large
  color="#2196F3"
  overlay={false}    // Modal overlay
/>

// Loading en botones
<LoadingButton loading={true} />

// Dots animados
<LoadingDots visible={true} />

// Skeleton placeholder
<LoadingSkeleton 
  width="100%" 
  height={20} 
  borderRadius={4} 
/>
```

**Características:**
- ✅ Loading con overlay modal
- ✅ Loading inline
- ✅ Loading específico para botones
- ✅ Dots animados
- ✅ Skeleton placeholders
- ✅ Mensajes personalizables

## 🎨 Sistema de Diseño

### Colores
Los componentes utilizan automáticamente el sistema de colores del tema:

```typescript
// Colores principales
colors.primary[500]    // Azul principal
colors.secondary[500]  // Púrpura secundario
colors.success        // Verde
colors.warning        // Naranja
colors.error          // Rojo
colors.info           // Azul info

// Colores de texto
colors.text.primary   // Negro principal
colors.text.secondary // Gris secundario

// Colores de fondo
colors.background.primary   // Blanco
colors.background.secondary // Gris claro
```

### Espaciado
Sistema de espaciado consistente:

```typescript
spacing.xs   // 4px
spacing.sm   // 8px
spacing.md   // 16px
spacing.lg   // 24px
spacing.xl   // 32px
```

### Tipografía
Escalas tipográficas predefinidas:

```typescript
typography.h1        // Títulos principales
typography.h2        // Títulos secundarios
typography.h3        // Títulos terciarios
typography.subtitle1 // Subtítulos
typography.body1     // Texto principal
typography.body2     // Texto secundario
typography.caption   // Texto pequeño
```

## 🚀 Ejemplos de Uso

### Card de Skill
```typescript
<Card variant="elevated" padding="large">
  <View style={styles.skillHeader}>
    <Avatar name="Sarah Wilson" size="medium" />
    <View style={styles.skillInfo}>
      <Text style={styles.skillTitle}>React Native Development</Text>
      <Text style={styles.skillInstructor}>by Sarah Wilson</Text>
    </View>
    <Badge label="Pro" variant="success" />
  </View>
  
  <Text style={styles.skillDescription}>
    Learn to build amazing mobile apps with React Native.
  </Text>
  
  <View style={styles.skillFooter}>
    <Text style={styles.skillPrice}>$25/hour</Text>
    <Button
      title="Book Session"
      onPress={handleBooking}
      size="small"
      variant="primary"
    />
  </View>
</Card>
```

### Profile Header
```typescript
<Card variant="filled" padding="large">
  <View style={styles.profileHeader}>
    <Avatar 
      name="John Doe" 
      size="xl"
      badge={<Badge count={3} variant="success" />}
      onPress={handleAvatarPress}
    />
    <View style={styles.profileInfo}>
      <Text style={styles.profileName}>John Doe</Text>
      <Badge label="Expert" variant="secondary" />
    </View>
  </View>
  
  <View style={styles.profileActions}>
    <Button
      title="Message"
      onPress={handleMessage}
      variant="outline"
      size="small"
      style={styles.actionButton}
    />
    <Button
      title="Book Session"
      onPress={handleBookSession}
      variant="primary"
      size="small"
      style={styles.actionButton}
    />
  </View>
</Card>
```

## ♿ Accesibilidad

Todos los componentes incluyen:

- ✅ **Screen reader support** con `accessibilityLabel` y `accessibilityRole`
- ✅ **Focus management** para navegación por teclado
- ✅ **State announcements** para estados de loading y disabled
- ✅ **Touch targets** de mínimo 44x44 pts
- ✅ **High contrast support** con colores accesibles

## 🧪 Testing

Los componentes incluyen:

- ✅ **TestIDs** para testing automatizado
- ✅ **Props de testing** personalizables
- ✅ **Estados consistentes** para testing de estados
- ✅ **Accessibility testing** integrado

```typescript
// Ejemplo de testing
<Button
  title="Test Button"
  onPress={mockHandler}
  testID="submit-button"
/>

// En tests
const button = screen.getByTestId('submit-button');
fireEvent.press(button);
```

## 📱 Responsividad

Los componentes son responsivos y se adaptan a:

- ✅ **Diferentes tamaños de pantalla** (phone, tablet)
- ✅ **Orientación** (portrait, landscape)
- ✅ **Text scaling** del sistema
- ✅ **Dark mode** (preparado para implementación)

## 🔧 Personalización

### Extending Components
```typescript
// Crear variantes personalizadas
const CustomButton: React.FC<ButtonProps> = (props) => (
  <Button
    {...props}
    style={[{ borderRadius: 20 }, props.style]}
  />
);

// Usar con temas personalizados
const ThemedCard: React.FC<CardProps> = (props) => {
  const theme = useTheme();
  return (
    <Card
      {...props}
      style={[{ backgroundColor: theme.colors.custom }, props.style]}
    />
  );
};
```

### Custom Styling
```typescript
// Override de estilos específicos
<Button
  title="Custom Button"
  style={{
    borderRadius: 20,
    paddingHorizontal: 30,
  }}
  textStyle={{
    fontSize: 18,
    fontWeight: 'bold',
  }}
/>
```

## 📚 Próximos Componentes

Próximos componentes en desarrollo:

- 🔄 **Input** - Campo de entrada mejorado
- 🔄 **Modal** - Modal reutilizable
- 🔄 **Toast** - Notificaciones temporales
- 🔄 **Switch** - Toggle switch
- 🔄 **Slider** - Control deslizante
- 🔄 **TabBar** - Navegación por pestañas
- 🔄 **BottomSheet** - Panel deslizable
- 🔄 **Calendar** - Selector de fechas

---

**¡La librería de componentes UI está lista para crear interfaces increíbles! 🎉**
