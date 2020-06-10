package com.stirling.products.services

import com.stirling.products.model.Product
import com.stirling.products.repository.ProductRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ProductService(private val repository: ProductRepository) {

    fun create(product: Mono<Product>) = product.flatMap { repository.save(it) }
    fun remove(product: Mono<Product>) = product.flatMap { repository.delete(it) }
    fun getAll() = repository.findAll()
    fun getByName(name: String) = repository.findByName(name)
    fun get(id: String) = repository.findById(id)

    fun update(product: Mono<Product>, id: String) = repository.findById(id)
            .zipWith(product)
            .flatMap {
                it.t1.name = it.t2.name
                it.t1.description = it.t2.description
                it.t1.price = it.t2.price
                return@flatMap repository.save(it.t1)
            }
            .switchIfEmpty(Mono.empty())
}