package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;

import java.util.Queue;

public final class BookPublishTask implements Runnable {
  //  processes a publish request from the BookPublishRequestManager.
  //  If the BookPublishRequestManager has no publishing requests the BookPublishTask should return immediately
  //  without taking action.
    private final BookPublishRequestManager bookPublishRequestManager;
    private final PublishingStatusDao publishingStatusDao;
    private final CatalogDao catalogDao;

    public BookPublishTask(BookPublishRequestManager bookPublishRequestManager, PublishingStatusDao publishingStatusDao, CatalogDao catalogDao) {
        this.bookPublishRequestManager = bookPublishRequestManager;
        this.publishingStatusDao = publishingStatusDao;
        this.catalogDao = catalogDao;
    }

    public void getBookPublishRequestToProcess() {
      Queue<BookPublishRequest> publishRequests = bookPublishRequestManager.getPublishRequests();
      if (!publishRequests.isEmpty()) {

        for (BookPublishRequest request : publishRequests) {
          String recordId = request.getPublishingRecordId();
          String bookId = request.getBookId();
          PublishingStatusItem item = publishingStatusDao.setPublishingStatus(recordId,
                  PublishingRecordStatus.IN_PROGRESS, bookId);
          KindleFormattedBook formattedBook = KindleFormatConverter.format(request);
          CatalogItemVersion itemVersion;
          try {
           itemVersion =  catalogDao.createOrUpdateBook(formattedBook);
           item = publishingStatusDao.setPublishingStatus(item.getPublishingRecordId(), PublishingRecordStatus.SUCCESSFUL,
                   itemVersion.getBookId());
          } catch (BookNotFoundException e) {
            item = publishingStatusDao.setPublishingStatus(recordId, PublishingRecordStatus.FAILED, e.getMessage());
          }

        }

      }
    }
    @Override
    public void run() {
      getBookPublishRequestToProcess();
    }
}
