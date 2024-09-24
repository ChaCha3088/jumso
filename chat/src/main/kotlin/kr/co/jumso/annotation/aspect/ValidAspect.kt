package kr.co.jumso.annotation.aspect

import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import jakarta.validation.Validator
import kr.co.jumso.annotation.Valid
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature

@Aspect
class ValidAspect(
    private val validator: Validator
) {
    @Around("execution(* *(.., @kr.co.jumso.annotation.Valid (*), ..))")
    fun around(joinPoint: ProceedingJoinPoint): Any? {
        val methodSignature = joinPoint.signature as MethodSignature
        val method = methodSignature.method

        val parameters = method.parameters
        var targetIndex = -1
        var targetAnnotation: Valid? = null
        for (i in parameters.indices) {
            val annotation = parameters[i].getAnnotation(Valid::class.java)
            if (annotation != null) {
                targetIndex = i
                targetAnnotation = annotation
                break
            }
        }

        val targetObject = joinPoint.args[targetIndex]

        val result: Set<ConstraintViolation<Any>> = validator.validate(targetObject, *targetAnnotation!!.value.map { it.java }.toTypedArray())

        if (result.isNotEmpty()) {
            throw ConstraintViolationException(result)
        }

        return joinPoint.proceed()
    }
}