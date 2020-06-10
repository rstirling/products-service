package com.stirling.products.repository

import com.stirling.products.model.Product
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface ProductRepository : ReactiveMongoRepository<Product, String> {
    fun findByName(name: String): Flux<Product>
}