package org.example.testsuspend.rest

import org.example.testsuspend.rest.client.RestClient

interface HttpRestClientDelegate<Request, Response> : RestClient<Request, Response>
