package com.hrapigatewayzuul.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers


@Configuration
@EnableWebFluxSecurity
class ResourceServerConfig {

    private val operator: List<String> = listOf("/workers-api/**")
    private val admin: List<String> = listOf("/payroll-api/**", "/users-api/**", "/oauth-api/users**")

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity, oauthAuthenticationWebFilter: AuthenticationWebFilter): SecurityWebFilterChain {
        http
            .csrf { csrf: CsrfSpec -> csrf.disable() }
            .addFilterAt(oauthAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHORIZATION)
            .authorizeExchange()
            .pathMatchers("/oauth-api/oauth/**")
            .permitAll()
            .pathMatchers(HttpMethod.GET, *operator.toTypedArray())
            .hasAnyRole("OPERATOR", "ADMIN")
            .pathMatchers(*admin.toTypedArray())
            .hasRole("ADMIN")
            .anyExchange()
            .authenticated()

        return http.build()
    }

    private fun matches(vararg routes: String): ServerWebExchangeMatcher? {
        return ServerWebExchangeMatchers.pathMatchers(*routes)
    }

    private fun notMatches(vararg routes: String): ServerWebExchangeMatcher? {
        return NegatedServerWebExchangeMatcher(matches(*routes))
    }

}