package org.example.testsuspend.rest.client

interface RequestInfo {
    val requestId: String
    val customHeaders: Map<String, String>
}
