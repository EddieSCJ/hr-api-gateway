package com.hrapigatewayzuul.config

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Component
class OAuth2AuthenticationManagerAdapter(
    tokenServices: ResourceServerTokenServices
) : ReactiveAuthenticationManager {

    private val authenticationManager: AuthenticationManager = oauthManager(tokenServices)

    override fun authenticate(token: Authentication): Mono<Authentication> {
        return Mono.just(token).publishOn(Schedulers.elastic()).flatMap { t ->
            return@flatMap try {
                Mono.just(authenticationManager.authenticate(t))
            } catch (x: Exception) {
                Mono.error(BadCredentialsException("Invalid or expired access token presented"))
            }
        }.filter(Authentication::isAuthenticated)
    }

    private fun oauthManager(tokenServices: ResourceServerTokenServices): AuthenticationManager {
        val oauthAuthenticationManager = OAuth2AuthenticationManager()
        oauthAuthenticationManager.setResourceId("")
        oauthAuthenticationManager.setTokenServices(tokenServices)
        return oauthAuthenticationManager
    }
}