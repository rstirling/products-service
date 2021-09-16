package com.stirling.products

import com.ninjasquad.springmockk.MockkBean
import com.stirling.products.model.Product
import com.stirling.products.services.ProductService
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductHandlerAndRouterTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockkBean
    lateinit var productService: ProductService

    @BeforeEach
    fun setup() {

        val products = listOf(
                Product("sugar", "cristal sugar", BigDecimal(1.00), "1"),
                Product("coffee", "black coffee", BigDecimal(2.00), "2"),
                Product("milk", "dairy milk", BigDecimal(3.00), "3")
        )

        every { productService.getAll() } returns Flux.fromIterable(products)
    }

    @Test
    fun `get all products`() {

        webTestClient.get().uri("/products/")
                .exchange()
                .expectStatus().is2xxSuccessful
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Product::class.java).hasSize(3)
    }

    @Test
    fun `get existing product by id`() {

        every { productService.get("1") } returns Mono.just(Product("sugar", "cristal sugar", BigDecimal(1.00), "1"))

        webTestClient.get()
                .uri("/products/{id}", "1")
                .exchange()
                .expectStatus().is2xxSuccessful
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo("1")
                .jsonPath("$.name").isEqualTo("sugar")
                .jsonPath("$.price").isEqualTo(1.00)
    }

    @Test
    fun `get non-existing product by id`() {

        every { productService.get(any()) } returns Mono.empty()

        webTestClient.get()
                .uri("/products/{id}", "anyId")
                .exchange()
                .expectStatus().isNotFound
    }

    @Test
    fun `add product`() {

        every { productService.create(any()) } returns Mono.just(Product("salt", "marine salt", BigDecimal(0.5), "1"))

        webTestClient.post()
            .uri("/products/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(Product("salt", "marine salt", BigDecimal(0.5))), Product::class.java)
                .exchange()
                .expectStatus().is2xxSuccessful
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo("1")
                .jsonPath("$.name").isEqualTo("salt")
                .jsonPath("$.description").isEqualTo("marine salt")
                .jsonPath("$.price").isEqualTo(0.5)

        verify(exactly = 1) { productService.create(any()) }
    }

    @Test
    fun `update existing product`() {

        val product = Product("sugar", "brown sugar", BigDecimal(1.5), "1")

        every { productService.update(any(), any()) } returns Mono.just(product)

        webTestClient.put()
                .uri("/products/{id}", product.id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(product), Product::class.java)
                .exchange()
                .expectStatus().is2xxSuccessful
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo("1")
                .jsonPath("$.name").isEqualTo("sugar")
                .jsonPath("$.description").isEqualTo("brown sugar")
                .jsonPath("$.price").isEqualTo(1.5)

        verify(exactly = 1) { productService.update(any(), any()) }
    }

    @Test
    fun `update non-existing product`() {

        every { productService.update(any(), any()) } returns Mono.empty()

        webTestClient.put()
                .uri("/products/{id}", "anyId")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound

        verify(exactly = 1) { productService.update(any(), any()) }
    }

    @Test
    fun `remove existing product`() {

        every { productService.get(any()) } returns Mono.just(Product("sugar", "brown sugar", BigDecimal(1.5), "1"))
        every { productService.remove(any()) } returns Mono.empty()

        webTestClient.delete()
                .uri("/products/{id}", "1")
                .exchange()
                .expectStatus().is2xxSuccessful
                .expectHeader().contentType(MediaType.APPLICATION_JSON)

        verify(exactly = 1) { productService.remove(any()) }
    }

    @Test
    fun `remove non-exiting product`() {

        every { productService.get(any()) } returns Mono.empty()

        webTestClient.delete()
                .uri("/products/{id}", "anyId")
                .exchange()
                .expectStatus().isNotFound

        verify(exactly = 0) { productService.remove(any()) }
    }

}