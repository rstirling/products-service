package com.stirling.products.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal

@Document
data class Product(var name: String,
                   var description: String,
                   var price: BigDecimal? = BigDecimal.ZERO,
                   @Id var id: String? = null)