package com.stirling.products.handlers

import com.stirling.products.model.Product
import com.stirling.products.services.ProductService
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono

@Component
class ProductHandler(private val service: ProductService) {

    fun addProduct(request: ServerRequest) = ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(service.create(request.bodyToMono(Product::class.java)))

    fun updateProduct(request: ServerRequest): Mono<ServerResponse> {
        val productRequest = request.bodyToMono(Product::class.java)
        return service.update(productRequest, request.pathVariable("id"))
                .flatMap { p ->
                    ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(Mono.just(p))
                }
                .switchIfEmpty(ServerResponse.notFound().build())
    }

    fun listProducts(request: ServerRequest) = ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(service.getAll())

    fun getProduct(request: ServerRequest) =
            service.get(request.pathVariable("id"))
                    .flatMap {
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(Mono.just(it))
                    }
                    .switchIfEmpty(ServerResponse.notFound().build())

    fun deleteProduct(request: ServerRequest) =
            service.get(request.pathVariable("id"))
                    .flatMap {
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(service.remove(Mono.just(it)))
                    }
                    .switchIfEmpty(ServerResponse.notFound().build())

}