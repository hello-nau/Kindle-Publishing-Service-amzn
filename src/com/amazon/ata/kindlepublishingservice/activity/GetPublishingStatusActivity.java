package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.converters.PublishStatusItemConverter;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.exceptions.KindlePublishingClientException;
import com.amazon.ata.kindlepublishingservice.exceptions.PublishingStatusNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.PublishingStatus;
import com.amazon.ata.kindlepublishingservice.models.requests.GetPublishingStatusRequest;
import com.amazon.ata.kindlepublishingservice.models.response.GetPublishingStatusResponse;
import com.amazonaws.services.lambda.runtime.Context;

import javax.inject.Inject;
import java.util.List;

public class GetPublishingStatusActivity {
    PublishingStatusDao publishingStatusDao;
    @Inject
    public GetPublishingStatusActivity(PublishingStatusDao publishingStatusDao) {
        this.publishingStatusDao = publishingStatusDao;
    }

    public GetPublishingStatusResponse execute(GetPublishingStatusRequest publishingStatusRequest) {
        List<PublishingStatusItem> publishingStatusItemList;
    try {
         publishingStatusItemList =
                publishingStatusDao.getPublishingStatuses(publishingStatusRequest.getPublishingRecordId());
    } catch(PublishingStatusNotFoundException e) {
        throw new PublishingStatusNotFoundException("The Publishing Status was not found.");
    }


        return GetPublishingStatusResponse.builder()
                .withPublishingStatusHistory(PublishStatusItemConverter.toStatusRecord(publishingStatusItemList))
                .build();
    }

}
