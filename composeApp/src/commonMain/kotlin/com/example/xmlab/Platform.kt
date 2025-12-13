package com.example.xmlab

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform