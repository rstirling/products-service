package com.stirling.products.handlers

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router

@Configuration
class ProductRouter {

    @Bean
    fun routeProduct(productHandler: ProductHandler) = router {
        accept(MediaType.APPLICATION_JSON).nest {
            "/products".nest {
                GET("/", productHandler::listProducts)
                GET("/{id}", productHandler::getProduct)
                POST("/", productHandler::addProduct)
                PUT("/{id}", productHandler::updateProduct)
                DELETE("/{id}", productHandler::deleteProduct)
            }
        }
    }

}