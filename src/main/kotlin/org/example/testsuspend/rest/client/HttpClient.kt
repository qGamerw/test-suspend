package org.example.testsuspend.rest.client

interface HttpClient<Request : java.io.Serializable, Response : Any> : RestClient<Request, Response>
