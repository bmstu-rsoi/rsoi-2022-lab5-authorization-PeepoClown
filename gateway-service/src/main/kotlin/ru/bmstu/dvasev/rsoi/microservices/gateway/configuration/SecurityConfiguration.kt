package ru.bmstu.dvasev.rsoi.microservices.gateway.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfiguration {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            authorizeRequests {
                authorize("/api/v1/callback", permitAll)
                authorize(anyRequest, authenticated)
            }
            oauth2ResourceServer { jwt {} }
        }
        return http.build()
    }
}
