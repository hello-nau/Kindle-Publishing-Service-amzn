package com.amazon.ata.kindlepublishingservice.exceptions;

public class KindlePublishingClientException extends RuntimeException{



    public KindlePublishingClientException(String message) {
        super(message);
    }

    public KindlePublishingClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
