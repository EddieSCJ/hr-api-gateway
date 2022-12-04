package com.hrapigatewayzuul.config

import org.apache.commons.lang.StringUtils
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.*
import java.util.function.Function

@Component
class OAuthTokenConverter : Function<ServerWebExchange, Mono<Authentication>> {
    override fun apply(exchange: ServerWebExchange): Mono<Authentication> {
        val token = extractToken(exchange.getRequest())
        return if (token != null) {
            Mono.just(PreAuthenticatedAuthenticationToken(token, ""))
        } else Mono.empty()
    }

    private fun extractToken(request: ServerHttpRequest): String? {
        val token: String? = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION)
        return if (StringUtils.isBlank(token) || !token?.lowercase(Locale.getDefault())?.startsWith(BEARER)!!) {
            null
        } else token.substring(BEARER.length)
    }

    companion object {
        private const val BEARER = "bearer "
    }
}