package kr.co.jumso.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.EXPRESSION)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class Valid(
    val value: Array<KClass<*>> = []
)