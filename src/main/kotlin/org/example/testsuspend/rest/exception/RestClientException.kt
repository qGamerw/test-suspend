package org.example.testsuspend.rest.exception

/** Базовое исключение для технических ошибок внутри пайплайна REST-клиента. */
open class RestClientException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

/** Возникает, когда HTTP-запрос не удалось выполнить успешно. */
class TransportException(message: String, cause: Throwable? = null) : RestClientException(message, cause)

/** Возникает, когда тело ответа нельзя преобразовать в ожидаемый DTO. */
class ResponseMappingException(message: String, cause: Throwable? = null) : RestClientException(message, cause)
