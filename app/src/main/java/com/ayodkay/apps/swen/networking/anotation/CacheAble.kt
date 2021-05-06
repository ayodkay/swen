package com.ayodkay.apps.swen.networking.anotation

/**
 * Marks a request for caching
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CacheAble