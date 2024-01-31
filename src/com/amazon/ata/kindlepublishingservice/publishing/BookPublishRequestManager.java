package com.amazon.ata.kindlepublishingservice.publishing;

import java.util.LinkedList;
import java.util.Queue;

public class BookPublishRequestManager {
    Queue<BookPublishRequest> publishRequests;

    public BookPublishRequestManager() {
        publishRequests = new LinkedList<>();
    }
    public void addBookPublishRequest(BookPublishRequest bookPublishRequest) {
        publishRequests.add(bookPublishRequest);
    }
    public BookPublishRequest getBookPublishRequestToProcess() {
        if(!publishRequests.isEmpty()) {
            return publishRequests.remove();
        }
        return null;
    }
}
