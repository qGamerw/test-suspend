package org.example.testsuspend.rest.client

import org.example.testsuspend.rest.dto.Result

interface RestClient<Request, Response> {

    suspend fun postCall(request: Request): Result<Response>
}