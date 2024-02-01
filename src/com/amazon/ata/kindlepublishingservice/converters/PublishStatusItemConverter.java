package com.amazon.ata.kindlepublishingservice.converters;

import com.amazon.ata.coral.converter.CoralConverterUtil;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.models.PublishingStatusRecord;

import java.util.List;
import java.util.stream.Collectors;

public class PublishStatusItemConverter {
    private PublishStatusItemConverter(){}
    public static List<PublishingStatusRecord> toStatusRecord(List<PublishingStatusItem> publishingStatusItemList) {
        return publishingStatusItemList.stream()
                .map(item -> toPublishingStatusRecord(item))
                .collect(Collectors.toList());
    }
    public static PublishingStatusRecord toPublishingStatusRecord(PublishingStatusItem publishingStatusItem) {
        return PublishingStatusRecord.builder()
                .withBookId(publishingStatusItem.getBookId())
                .withStatusMessage(publishingStatusItem.getStatusMessage())
                .withStatus(publishingStatusItem.getStatus().toString())
                .build();
    }


}
