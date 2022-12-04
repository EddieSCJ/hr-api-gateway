package com.hrapigatewayzuul.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore
import org.springframework.security.web.server.authentication.AuthenticationWebFilter

@Configuration
class OAuth2Config {
    @Bean("oauthAuthenticationWebFilter")
    fun oauthAuthenticationWebFilter(
        authManager: OAuth2AuthenticationManagerAdapter?, tokenConverter: OAuthTokenConverter?
    ): AuthenticationWebFilter {
        val filter = AuthenticationWebFilter(authManager)
        filter.setAuthenticationConverter(tokenConverter)
        return filter
    }

    @Bean
    fun tokenServices(): ResourceServerTokenServices {
        val tokenServices = DefaultTokenServices()
        tokenServices.setTokenStore(tokenStore())
        tokenServices.setSupportRefreshToken(false)
        return tokenServices
    }

    @Bean
    fun accessTokenConverter(): JwtAccessTokenConverter {
        val tokenConverter = JwtAccessTokenConverter()
        tokenConverter.setSigningKey("MY-SECRET-KEY")

        return tokenConverter
    }

    @Bean
    fun tokenStore() = JwtTokenStore(accessTokenConverter())
}