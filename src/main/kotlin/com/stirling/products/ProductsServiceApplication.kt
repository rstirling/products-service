package com.stirling.products

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ProductsServiceApplication

fun main(args: Array<String>) {
    runApplication<ProductsServiceApplication>(*args)
}
