package com.devundefined.googlenewswithpagingexample.domain.repository

interface CacheValidator {
    fun isValid(timestamp: Long): Boolean
}

class CacheValidatorImpl : CacheValidator {
    companion object {
        private const val VALID_CACHE_DURATION = 1000 * 60 * 60 * 60 // 1 hour
    }

    override fun isValid(timestamp: Long): Boolean {
        return System.currentTimeMillis() - timestamp < VALID_CACHE_DURATION
    }
}