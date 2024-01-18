package com.amazon.ata.kindlepublishingservice.models.response;

import com.amazon.ata.kindlepublishingservice.models.Book;

import java.util.Objects;

public class RemoveBookFromCatalogResponse {
    private Book book;
    public RemoveBookFromCatalogResponse() {
    }
    public RemoveBookFromCatalogResponse(Builder builder) {
        this.book = builder.book;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public static final class Builder {
        private Book book;
        public Builder withBook (Book bookToUse) {
            this.book = bookToUse;
            return this;
        }
        public RemoveBookFromCatalogResponse build() {
            return new RemoveBookFromCatalogResponse(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RemoveBookFromCatalogResponse that)) return false;
        return Objects.equals(book, that.book);
    }

    @Override
    public int hashCode() {
        return Objects.hash(book);
    }

    @Override
    public String toString() {
        return "RemoveBookFromCatalogResponse{" +
                "book=" + book +
                '}';
    }
}
