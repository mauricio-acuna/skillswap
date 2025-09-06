package com.skillswap.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Configurar broker simple en memoria para temas
        config.enableSimpleBroker("/topic", "/queue");
        
        // Prefijo para destinos de aplicación
        config.setApplicationDestinationPrefixes("/app");
        
        // Prefijo para mensajes dirigidos a usuarios específicos
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint para conexión WebSocket con fallback a SockJS
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // En producción, especificar dominios exactos
                .withSockJS();
        
        // Endpoint adicional sin SockJS para conexiones nativas
        registry.addEndpoint("/ws-native")
                .setAllowedOriginPatterns("*");
    }
}
