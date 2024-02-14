package com.amazon.ata.kindlepublishingservice.dao;

import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.publishing.KindleFormattedBook;
import com.amazon.ata.kindlepublishingservice.utils.KindlePublishingUtils;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import javax.inject.Inject;

public class CatalogDao {

    private final DynamoDBMapper dynamoDbMapper;

    /**
     * Instantiates a new CatalogDao object.
     *
     * @param dynamoDbMapper The {@link DynamoDBMapper} used to interact with the catalog table.
     */
    @Inject
    public CatalogDao(DynamoDBMapper dynamoDbMapper) {
        this.dynamoDbMapper = dynamoDbMapper;
    }

    /**
     * Returns the latest version of the book from the catalog corresponding to the specified book id.
     * Throws a BookNotFoundException if the latest version is not active or no version is found.
     * @param bookId Id associated with the book.
     * @return The corresponding CatalogItem from the catalog table.
     */
    public CatalogItemVersion getBookFromCatalog(String bookId) {
        CatalogItemVersion book = getLatestVersionOfBook(bookId);

        if (book == null || book.isInactive()) {
            throw new BookNotFoundException(String.format("No book found for id: %s", bookId));
        }

        return book;
    }

    // Returns null if no version exists for the provided bookId
    private CatalogItemVersion getLatestVersionOfBook(String bookId) {
        CatalogItemVersion book = new CatalogItemVersion();
        book.setBookId(bookId);

        DynamoDBQueryExpression<CatalogItemVersion> queryExpression = new DynamoDBQueryExpression()
            .withHashKeyValues(book)
            .withScanIndexForward(false)
            .withLimit(1);

        List<CatalogItemVersion> results = dynamoDbMapper.query(CatalogItemVersion.class, queryExpression);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    public CatalogItemVersion removeBookFromCatalog(String bookId) {
        if(bookId.isEmpty()) throw new BookNotFoundException("The book id can not be empty");

        CatalogItemVersion itemVersion = getLatestVersionOfBook(bookId);
        if (itemVersion == null || itemVersion.isInactive()) {
            throw new BookNotFoundException(String.format("Book with the %s id is not found!", bookId));
        }
        itemVersion.setInactive(true);
        dynamoDbMapper.save(itemVersion);
        return itemVersion;
    }

    public void validateBookExists(String bookId) {
        CatalogItemVersion book = getLatestVersionOfBook(bookId);
        if (book == null) {
            throw new BookNotFoundException("The book with the given Id was not found.");
        }
    }

    public CatalogItemVersion createOrUpdateBook(KindleFormattedBook formattedBook) {
        CatalogItemVersion itemVersion;
        String bookId = formattedBook.getBookId();
        if(bookId == null) {
            bookId = KindlePublishingUtils.generateBookId();
            itemVersion = new CatalogItemVersion();
            itemVersion.setBookId(bookId);
            itemVersion.setVersion(1);
            itemVersion.setInactive(false);
        } else {
            itemVersion = getBookFromCatalog(bookId);
            itemVersion.setBookId(formattedBook.getBookId());
            int version = itemVersion.getVersion();
            itemVersion.setVersion(version+1);
            itemVersion.setInactive(false);

        }
        itemVersion.setGenre(formattedBook.getGenre());
        itemVersion.setAuthor(formattedBook.getAuthor());
        itemVersion.setTitle(formattedBook.getTitle());
        itemVersion.setText(formattedBook.getText());
        dynamoDbMapper.save(itemVersion);
        return itemVersion;
    }
}
