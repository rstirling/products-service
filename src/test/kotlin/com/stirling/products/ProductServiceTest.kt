package com.stirling.products

import com.stirling.products.model.Product
import com.stirling.products.repository.ProductRepository
import com.stirling.products.services.ProductService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.test.StepVerifier
import java.math.BigDecimal

@SpringBootTest
class ProductServiceTest {

    @Autowired
    lateinit var productService: ProductService

    @Autowired
    lateinit var productRepository: ProductRepository

    private val products = listOf(
            Product("Milk", "Dairy Milk", BigDecimal(4.51)),
            Product("Coffee", "Black Coffee", BigDecimal(9.32)),
            Product("Sugar", "Cristal Sugar", BigDecimal(3.84)),
            Product("Butter", "Salted Butter", BigDecimal(4.72))
    )

    @BeforeEach
    fun `setup`() {
        productRepository.deleteAll()
                .thenMany(Flux.fromIterable(products))
                .flatMap { productRepository.save(it) }
                .then()
                .block()
    }


    @Test
    fun `test create product`() {
        val product = Mono.just(Product("honey", "Raw Honey"))
        StepVerifier.create(productService.create(product))
                .expectNextMatches {
                    it.id != null
                    it.name == "honey"
                    it.description == "Raw Honey"
                }
                .verifyComplete()
    }

    @Test
    fun `test get by name`() {
        StepVerifier.create(productService.getByName("Milk"))
                .expectNextMatches { it.id != null && it.name == "Milk" }
                .expectNextCount(0)
                .verifyComplete()
    }

    @Test
    fun `test remove product`() {
        val byName = productService.getByName("Milk")
        StepVerifier.create(productService.remove(byName.toMono()))
                .verifyComplete()
    }

    @Test
    fun `test get all products`() {
        StepVerifier.create(productService.getAll())
                .expectNextCount(4)
                .verifyComplete()
    }


    @Test
    fun `test get products by id`() {
        val product = productService.getByName("Milk")
                .next()
                .block()
        StepVerifier.create(productService.get(product?.id!!))
                .expectNextMatches { it.name == "Milk" }
                .expectNextCount(0)
                .verifyComplete()
    }

    @Test
    fun `update existing product`() {

        val product = productService.getByName("Milk")
                .next()
                .block()

        val update = Mono.just(Product("Milk", "Delicious", BigDecimal(3.59)))
        StepVerifier.create(productService.update(update, product?.id!!))
                .expectNextMatches {
                    it.id == product.id &&
                            it.name == "Milk" &&
                            it.price == BigDecimal(3.59)
                }
                .verifyComplete()
    }

    @Test
    fun `update non existing product`() {
        StepVerifier.create(productService.update(Mono.empty(), "null"))
                .expectNextCount(0)
                .verifyComplete()
    }

}