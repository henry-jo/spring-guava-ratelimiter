package com.henry.springguavaratelimiter.sample
import com.google.common.util.concurrent.RateLimiter
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

@Suppress("UnstableApiUsage")
class RateLimiterSpec : DescribeSpec({

    fun doSomething() {
        println("doSomething")
    }

    describe("1초에 1번 실행") {
        val rateLimiter = RateLimiter.create(1.0)
        repeat(10) {
            val startTime = ZonedDateTime.now().second
            rateLimiter.acquire() // = rateLimiter.acquire(1)
            doSomething()
            val elapsedTimeSeconds = ZonedDateTime.now().second - startTime
            println("elapsedTimeSeconds: $elapsedTimeSeconds")
        }
    }

    describe("2초에 1번 실행 (1)") {
        val rateLimiter = RateLimiter.create(1.0)
        repeat(10) {
            val startTime = ZonedDateTime.now().second
            rateLimiter.acquire(2)
            doSomething()
            val elapsedTimeSeconds = ZonedDateTime.now().second - startTime
            println("elapsedTimeSeconds: $elapsedTimeSeconds")
        }
    }

    describe("2초에 1번 실행 (2)") {
        val rateLimiter = RateLimiter.create(0.5)
        repeat(10) {
            val startTime = ZonedDateTime.now().second
            rateLimiter.acquire(1)
            doSomething()
            val elapsedTimeSeconds = ZonedDateTime.now().second - startTime
            println("elapsedTimeSeconds: $elapsedTimeSeconds")
        }
    }

    describe("첫번째 요청은 딜레이 없이 바로 실행된다.") {
        val rateLimiter = RateLimiter.create(1.0)

        val startTime = ZonedDateTime.now().second
        rateLimiter.acquire(5)
        doSomething()
        val elapsedTimeSeconds = ZonedDateTime.now().second - startTime
        println("elapsedTimeSeconds: $elapsedTimeSeconds")
    }

    describe("acquire()와 tryAcquire()의 동작 차이") {
        val rateLimiter = RateLimiter.create(1.0)

        val startTime = ZonedDateTime.now().second

        // When
        rateLimiter.acquire(1)
        val result = rateLimiter.tryAcquire()

        val elapsedTimeSeconds = ZonedDateTime.now().second - startTime

        // Then
        result shouldBe false
        println("elapsedTimeSeconds: $elapsedTimeSeconds")

        // acquire()는 허가를 받을 때까지 blocking하며 기다린다.
        // tryAcquire()는 허가를 요청하지만, 현재 허가를 받지 못하는 상황이면 false를 리턴한다.
    }

    describe("acquire() 연속으로 호출") {
        val rateLimiter = RateLimiter.create(1.0)

        val startTime = ZonedDateTime.now().second

        // When
        rateLimiter.acquire(1)
        rateLimiter.acquire(1)
        rateLimiter.acquire(1)
        // acquire(3)과 동작이 다르다.

        val elapsedTimeSeconds = ZonedDateTime.now().second - startTime

        // Then
        println("elapsedTimeSeconds: $elapsedTimeSeconds")
    }

    describe("tryAcquire()가 예상대로 작동안되는 경우") {
        val rateLimiter = RateLimiter.create(100.0) // 1초에 100번 실행
        repeat(10) {
            val startTime = ZonedDateTime.now().second
            if (rateLimiter.tryAcquire()) {
                // 1초에 100번 실행하도록 허용했으므로, 10번 모두 실행되길 기대했지만, 첫번째 요청말고 실행되지 않는다.
                // tryAcquire()는 1초에 적절히 분산되서 실행시키므로, 연속으로 계속 요청할 경우 실행되지 않는다.
                doSomething()
            }
            val elapsedTimeSeconds = ZonedDateTime.now().second - startTime
            println("elapsedTimeSeconds: $elapsedTimeSeconds")
        }
    }

    describe("tryAcquire() wait time 추가") {
        val rateLimiter = RateLimiter.create(100.0) // 1초에 100번 실행
        repeat(10) {
            val startTime = ZonedDateTime.now().second
            // 대기 시간을 주면 모두 실행된다.
            if (rateLimiter.tryAcquire(1, 100, TimeUnit.MILLISECONDS)) {
                doSomething()
            }
            val elapsedTimeSeconds = ZonedDateTime.now().second - startTime
            println("elapsedTimeSeconds: $elapsedTimeSeconds")
        }
    }
})
