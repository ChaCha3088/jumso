package kr.co.jumso.annotation

import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER
import kotlin.reflect.KClass

@Target(VALUE_PARAMETER)
@Retention(SOURCE)
@MustBeDocumented
annotation class Valid(
    val value: Array<KClass<*>> = []
)
