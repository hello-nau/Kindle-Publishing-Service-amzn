package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;

import javax.inject.Inject;
import java.util.Queue;

public final class BookPublishTask implements Runnable {

  private final BookPublishRequestManager bookPublishRequestManager;
  private final PublishingStatusDao publishingStatusDao;
  private final CatalogDao catalogDao;

  @Inject
  public BookPublishTask(BookPublishRequestManager bookPublishRequestManager, PublishingStatusDao publishingStatusDao,
                         CatalogDao catalogDao) {
    this.bookPublishRequestManager = bookPublishRequestManager;
    this.publishingStatusDao = publishingStatusDao;
    this.catalogDao = catalogDao;
  }

  @Override
  public void run() {
    BookPublishRequest publishRequest = bookPublishRequestManager.getBookPublishRequestToProcess();

    while (publishRequest == null) {
      try {
        Thread.sleep(1000);
        publishRequest = bookPublishRequestManager.getBookPublishRequestToProcess();

      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }

      publishingStatusDao.setPublishingStatus(publishRequest.getPublishingRecordId(),
              PublishingRecordStatus.IN_PROGRESS, publishRequest.getBookId());
      KindleFormattedBook formattedBook = KindleFormatConverter.format(publishRequest);

      try {
        CatalogItemVersion itemVersion = catalogDao.createOrUpdateBook(formattedBook);
        publishingStatusDao.setPublishingStatus(publishRequest.getPublishingRecordId(), PublishingRecordStatus.SUCCESSFUL,
                itemVersion.getBookId());

      } catch(BookNotFoundException e) {
        publishingStatusDao.setPublishingStatus(publishRequest.getPublishingRecordId(), PublishingRecordStatus.FAILED,
                e.getMessage());
      }
    }
  }
}
