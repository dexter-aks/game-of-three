package com.dexter.gameofthree.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@ConfigurationProperties(prefix = "websocket")
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {

    lateinit var registryUrl: String
    lateinit var applicationPrefixUrl: String
    lateinit var relayTopicUrl: String
    lateinit var relayQueueUrl: String
    lateinit var relayHost: String
    val relayPort = 61613
    lateinit var clientUsername: String
    lateinit var clientPassword: String

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint(registryUrl).withSockJS()
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableStompBrokerRelay(relayTopicUrl, relayQueueUrl)
            .setRelayHost(relayHost)
            .setRelayPort(relayPort)
            .setClientLogin(clientUsername)
            .setClientPasscode(clientPassword)
        registry.setApplicationDestinationPrefixes(applicationPrefixUrl)
    }
}