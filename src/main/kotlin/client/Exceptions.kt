package client

class UnexpectedStatusCodeException(override val message: String?): RuntimeException(message)