package com.amazon.ata.kindlepublishingservice.publishing;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
@Singleton
public class BookPublishRequestManager {
    private ConcurrentLinkedDeque<BookPublishRequest> publishRequests;
    @Inject
    public BookPublishRequestManager() {
        publishRequests = new ConcurrentLinkedDeque<>();
    }
    public void addBookPublishRequest(BookPublishRequest bookPublishRequest) {
        publishRequests.add(bookPublishRequest);
    }
    public BookPublishRequest getBookPublishRequestToProcess() {
        return publishRequests.poll();
    }


}
