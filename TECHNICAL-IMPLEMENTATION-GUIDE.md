# 🔥 URGENT: TECHNICAL IMPLEMENTATION GUIDE
**Para:** Backend & Frontend Agents  
**Prioridad:** CRÍTICA - Implementar antes que competencia  
**Timeline:** 2-3 semanas para MVP de features revolucionarios

---

## 🎯 **BACKEND AGENT - NUEVAS APIS CRÍTICAS**

### **1. SKILL STORIES API (Priority 1)**
```java
// Agregar a SkillService.java
@RestController
@RequestMapping("/api/skill-stories")
public class SkillStoryController {
    
    @PostMapping("/upload")
    public ResponseEntity<SkillStory> uploadStory(
        @RequestParam("video") MultipartFile video,
        @RequestParam("skillId") Long skillId,
        @RequestParam("duration") Integer duration) {
        // Video processing, thumbnail generation
        // Auto-transcription for accessibility
        return ResponseEntity.ok(storyService.create(video, skillId, duration));
    }
    
    @GetMapping("/feed")
    public ResponseEntity<List<SkillStory>> getStoryFeed(
        @RequestParam("userId") Long userId,
        @RequestParam("location") String location) {
        // AI-powered feed based on user preferences
        // Hyperlocal filtering
        return ResponseEntity.ok(storyService.getFeed(userId, location));
    }
    
    @PostMapping("/{storyId}/duet")
    public ResponseEntity<SkillStory> createDuet(
        @PathVariable Long storyId,
        @RequestParam("video") MultipartFile responseVideo) {
        // Link videos for duet learning
        return ResponseEntity.ok(storyService.createDuet(storyId, responseVideo));
    }
}
```

### **2. SKILL DNA MATCHING API (Priority 2)**
```java
// Nuevo servicio revolucionario
@Service
public class SkillDNAService {
    
    public CompatibilityScore calculateCompatibility(User teacher, User student) {
        // Algoritmo propietario de matching
        LearningStyle teacherStyle = analyzeTeachingStyle(teacher);
        LearningStyle studentStyle = analyzeLearningStyle(student);
        
        double compatibility = calculateScientificMatch(teacherStyle, studentStyle);
        
        return CompatibilityScore.builder()
            .score(compatibility)
            .explanation(generateExplanation(teacherStyle, studentStyle))
            .tips(generateLearningTips(teacherStyle, studentStyle))
            .build();
    }
    
    private double calculateScientificMatch(LearningStyle teacher, LearningStyle student) {
        // Algoritmo basado en research de psicología educativa
        double paceCompatibility = calculatePaceMatch(teacher.pace, student.pace);
        double styleCompatibility = calculateStyleMatch(teacher.methods, student.preferences);
        double personalityMatch = calculatePersonalityMatch(teacher.mbti, student.mbti);
        
        return (paceCompatibility * 0.4) + (styleCompatibility * 0.4) + (personalityMatch * 0.2);
    }
}
```

### **3. HYPERLOCAL API (Priority 3)**
```java
@RestController
@RequestMapping("/api/hyperlocal")
public class HyperlocalController {
    
    @GetMapping("/skills-nearby")
    public ResponseEntity<List<LocalSkill>> getSkillsNearby(
        @RequestParam("lat") Double latitude,
        @RequestParam("lng") Double longitude,
        @RequestParam("radius") Integer radiusMeters,
        @RequestParam("context") String context) { // "cafe", "office", "park"
        
        List<LocalSkill> skills = hyperlocalService.findSkillsWithContext(
            latitude, longitude, radiusMeters, context
        );
        
        return ResponseEntity.ok(skills);
    }
    
    @PostMapping("/context-aware-match")
    public ResponseEntity<List<SmartMatch>> getContextAwareMatches(
        @RequestBody ContextRequest request) {
        // Considera: ubicación, hora, clima, calendario, mood
        return ResponseEntity.ok(hyperlocalService.findContextMatches(request));
    }
}
```

---

## 📱 **FRONTEND AGENT - NUEVAS PANTALLAS REVOLUCIONARIAS**

### **1. SKILL STORIES SCREEN (Priority 1)**
```typescript
// screens/SkillStoriesScreen.tsx
import React, { useState, useRef } from 'react';
import { Camera } from 'expo-camera';
import { Video } from 'expo-av';

interface SkillStory {
  id: string;
  videoUrl: string;
  skillName: string;
  author: User;
  duration: number;
  likes: number;
  isLiked: boolean;
  canDuet: boolean;
}

export const SkillStoriesScreen: React.FC = () => {
  const [stories, setStories] = useState<SkillStory[]>([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [isRecording, setIsRecording] = useState(false);
  
  // TikTok-style vertical swipe navigation
  const handleSwipeUp = () => {
    if (currentIndex < stories.length - 1) {
      setCurrentIndex(currentIndex + 1);
    }
  };
  
  const recordSkillStory = async () => {
    // Implementar grabación de video vertical
    // Auto-generate captions
    // Apply skill-specific AR filters
  };
  
  const createDuet = async (originalStoryId: string) => {
    // Split screen recording with original video
    // Sync audio/video timing
  };
  
  return (
    <GestureDetector gesture={swipeGesture}>
      <View style={styles.container}>
        {/* Video player full screen */}
        {/* Overlay UI with skills info */}
        {/* Action buttons (like, duet, share) */}
        {/* Record button (always visible) */}
      </View>
    </GestureDetector>
  );
};
```

### **2. SKILL DNA SCREEN (Priority 2)**
```typescript
// screens/SkillDNAScreen.tsx
export const SkillDNAScreen: React.FC = () => {
  const [dnaData, setDnaData] = useState<SkillDNA | null>(null);
  
  const SkillDNAVisualization = () => (
    <Svg height="300" width="300">
      {/* Circular DNA-like visualization */}
      {/* Each skill as a node with connections */}
      {/* Color-coded by proficiency level */}
      {/* Animated connections showing learning paths */}
    </Svg>
  );
  
  const CompatibilityMeter = ({ score, explanation }: CompatibilityProps) => (
    <View style={styles.compatibilityContainer}>
      <CircularProgress 
        percentage={score * 100}
        color={getCompatibilityColor(score)}
      />
      <Text style={styles.explanation}>{explanation}</Text>
      <TouchableOpacity onPress={() => showDetailedAnalysis()}>
        <Text style={styles.seeDetails}>Ver análisis detallado</Text>
      </TouchableOpacity>
    </View>
  );
  
  return (
    <ScrollView>
      <SkillDNAVisualization />
      {/* Learning style preferences */}
      {/* Compatibility with potential matches */}
      {/* Personalized learning recommendations */}
    </ScrollView>
  );
};
```

### **3. MOOD-BASED UI SYSTEM**
```typescript
// hooks/useMoodAdaptiveUI.ts
export const useMoodAdaptiveUI = () => {
  const [userMood, setUserMood] = useState<UserMood>('neutral');
  
  const detectMoodFromUsage = (interactionPattern: InteractionData) => {
    // ML model que detecta mood basado en:
    // - Velocidad de swipes
    // - Tiempo en pantallas
    // - Tipos de skills que busca
    // - Hora del día
    return moodDetectionService.predict(interactionPattern);
  };
  
  const adaptUIToMood = (mood: UserMood) => {
    switch(mood) {
      case 'energetic':
        return {
          colorScheme: 'vibrant',
          animationSpeed: 'fast',
          suggestedSkills: 'challenging'
        };
      case 'calm':
        return {
          colorScheme: 'soft',
          animationSpeed: 'slow',
          suggestedSkills: 'relaxing'
        };
      case 'focused':
        return {
          colorScheme: 'minimal',
          animationSpeed: 'none',
          suggestedSkills: 'technical'
        };
    }
  };
  
  return { userMood, adaptUIToMood };
};
```

---

## 🎨 **UI/UX SPECIFICATIONS**

### **Color Psychology por Mood:**
```typescript
const moodThemes = {
  energetic: {
    primary: '#FF6B6B',    // Energizing red
    secondary: '#4ECDC4',  // Vibrant teal
    accent: '#FFE66D',     // Sunny yellow
    animation: 'bounce'
  },
  calm: {
    primary: '#A8E6CF',    // Soft green
    secondary: '#B4A7D6',  // Lavender
    accent: '#F7D794',     // Warm beige
    animation: 'fade'
  },
  focused: {
    primary: '#2C3E50',    // Deep blue
    secondary: '#34495E',  // Slate gray
    accent: '#3498DB',     // Clear blue
    animation: 'none'
  }
};
```

### **Micro-Interactions Específicas:**
```typescript
// Haptic feedback patterns por skill category
const skillHaptics = {
  music: 'rhythm',        // Vibración rítmica
  sports: 'strong',       // Vibración intensa
  art: 'gentle',         // Vibración suave
  tech: 'sharp',         // Vibración precisa
  cooking: 'warm'        // Vibración gradual
};
```

---

## ⚡ **IMPLEMENTATION TIMELINE**

### **Week 1: Foundation**
- **Backend:** Skill Stories API + video upload
- **Frontend:** Basic video recording screen
- **Both:** Mood detection infrastructure

### **Week 2: Core Features**
- **Backend:** Skill DNA matching algorithm
- **Frontend:** TikTok-style stories interface
- **Both:** Hyperlocal positioning system

### **Week 3: Polish & Integration**
- **Backend:** Performance optimization + caching
- **Frontend:** Mood-adaptive UI system
- **Both:** End-to-end testing + bug fixes

---

## 🚨 **CRITICAL SUCCESS FACTORS**

1. **Video Processing:** Implement efficient compression for mobile
2. **Real-time Matching:** Sub-second response times for DNA matching
3. **Battery Optimization:** Background location tracking without drain
4. **Accessibility:** Auto-captions for all video content
5. **Data Privacy:** GDPR-compliant mood/location tracking

---

**NEXT ACTION:** Backend agent debe implementar SkillStory API inmediatamente. Frontend agent debe crear video recording screen. Estas features nos dan 6 meses de ventaja sobre competencia.
