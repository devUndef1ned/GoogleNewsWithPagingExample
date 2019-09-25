package com.devundefined.googlenewswithpagingexample.domain.repository

import java.util.concurrent.TimeUnit

interface CacheValidator {
    fun isValid(timestamp: Long): Boolean
}

class CacheValidatorImpl : CacheValidator {
    companion object {
        private val VALID_CACHE_DURATION = TimeUnit.MINUTES.toMillis(15L) // 15 minutes
    }

    override fun isValid(timestamp: Long): Boolean {
        return System.currentTimeMillis() - timestamp < VALID_CACHE_DURATION
    }
}