package org.example.testsuspend.rest.client

interface RestClient<Rq : Any, Rs : Any> {
    suspend fun call(request: Rq): Rs
}
