package com.example.innovateiu_home_task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentManagerTest {

    DocumentManager documentManager = new DocumentManager();

    @BeforeEach
    void setUp() {
        DocumentManager documentManager = new DocumentManager();
        documentManager.save(new DocumentManager.Document("1", "title1", "content1", new DocumentManager.Author("1", "Tom"), Instant.now()));
        documentManager.save(new DocumentManager.Document("2", "title2", "content2", new DocumentManager.Author("2", "Jack"), Instant.now()));
        documentManager.save(new DocumentManager.Document("3", "title3", "content3", new DocumentManager.Author("3", "Mike"), Instant.now()));
    }

    @Test
    void testSaveDocumentWithoutId() {
        DocumentManager.Document document = new DocumentManager.Document(null, "test title", "test content", new DocumentManager.Author("1", "Tom"), null);
        DocumentManager.Document savedDocument = documentManager.save(document);

        assertNotNull(savedDocument.getId(), "ID should be generated");
        assertFalse(savedDocument.getId().isEmpty(), "ID should not be empty");
        assertNotNull(savedDocument.getCreated(), "Creation date should be set");

        Optional<DocumentManager.Document> foundDocument = documentManager.findById(savedDocument.getId());
        assertTrue(foundDocument.isPresent(), "Document should be present in storage");
        assertEquals(savedDocument, foundDocument.get(), "Saved document should be in storage");
    }

    @Test
    void testSaveDocumentWithExistingId() {
        String documentId = UUID.randomUUID().toString();
        Instant initialCreatedTime = Instant.now().minusSeconds(60);
        DocumentManager.Document initialDocument = new DocumentManager.Document(documentId, "initial title", "initial Content", new DocumentManager.Author("1", "Tom"), initialCreatedTime);
        documentManager.save(initialDocument);
        DocumentManager.Document newDocument = new DocumentManager.Document(documentId, "UpdateTitle", "Update Content", new DocumentManager.Author("1", "UpdateTom"), null);
        DocumentManager.Document savedDocument = documentManager.save(newDocument);
        assertEquals(documentId, savedDocument.getId());
        assertEquals(initialCreatedTime, savedDocument.getCreated(), "Creation time should not change");
        assertEquals("UpdateTitle", savedDocument.getTitle(), "Title should be updated");
    }

    @Test
    void testSaveDocumentWithoutCreatedDate() {
        String documentId = UUID.randomUUID().toString();
        DocumentManager.Document document = new DocumentManager.Document(documentId, "Test title", "Test content", new DocumentManager.Author("1", "Tom"), null);
        DocumentManager.Document saveDocument = documentManager.save(document);
        assertNotNull(saveDocument.getCreated());
        assertEquals(documentManager.findById(documentId).get(), saveDocument);
    }

    @Test
    void testSearchWithEmptyStorage() {
        documentManager = new DocumentManager();
        DocumentManager.SearchRequest searchRequest = new DocumentManager.SearchRequest(null, null, null, null, null);
        List<DocumentManager.Document> results = documentManager.search(searchRequest);
        assertTrue(results.isEmpty(), "Should return an empty list for empty storage.");
    }

    @Test
    void testSearchByTitlePrefix() {
        // Добавляем документы в хранилище
        documentManager.save(new DocumentManager.Document("1", "title1", "content1", new DocumentManager.Author("1", "Tom"), Instant.now()));
        documentManager.save(new DocumentManager.Document("2", "title2", "content2", new DocumentManager.Author("2", "Jack"), Instant.now()));
        // Поиск по заголовку "title1"
        DocumentManager.SearchRequest searchRequest = new DocumentManager.SearchRequest(Collections.singletonList("title1"), null, null, null, null);
        List<DocumentManager.Document> results = documentManager.search(searchRequest);
        // Проверка, что найден 1 документ
        assertEquals(1, results.size(), "Should return 1 document with title starting with 'title1'");
        assertEquals("title1", results.get(0).getTitle(), "The title of the document should be 'title1'");
    }

    @Test
    void testSearchByContent() {
        documentManager.save(new DocumentManager.Document("1", "title1", "content1", new DocumentManager.Author("1", "Tom"), Instant.now()));
        documentManager.save(new DocumentManager.Document("2", "title2", "content2", new DocumentManager.Author("2", "Jack"), Instant.now()));
        DocumentManager.SearchRequest searchRequest = new DocumentManager.SearchRequest(null, Collections.singletonList("content2"), null, null, null);
        List<DocumentManager.Document> results = documentManager.search(searchRequest);
        assertEquals(1, results.size(), "Should return 1 document with content 'content2'");
        assertEquals("content2", results.get(0).getContent());
    }

    @Test
    void testSearchByAuthorId() {
        documentManager.save(new DocumentManager.Document("1", "title1", "content1", new DocumentManager.Author("1", "Tom"), Instant.now()));
        documentManager.save(new DocumentManager.Document("2", "title2", "content2", new DocumentManager.Author("2", "Jack"), Instant.now()));
        DocumentManager.SearchRequest searchRequest = new DocumentManager.SearchRequest(null, null, Collections.singletonList("1"), null, null);
        List<DocumentManager.Document> results = documentManager.search(searchRequest);
        assertEquals("1", results.get(0).getAuthor().getId(), "The author ID of the document should be '1'");
        assertEquals(1, results.size(), "Should return 1 document with author ID '1'");
    }

    @Test
    void testSearchByDateRange() {
        Instant now = Instant.parse("2024-08-19T10:15:30Z");
        Instant start = Instant.parse("2024-07-19T10:15:30Z");
        Instant end = Instant.parse("2024-09-19T10:15:30Z");
        documentManager.save(new DocumentManager.Document("1", "title1", "content1", new DocumentManager.Author("1", "Tom"), now));
        documentManager.save(new DocumentManager.Document("2", "title2", "content2", new DocumentManager.Author("2", "Jack"), Instant.parse("2024-06-19T10:15:30Z")));
        DocumentManager.SearchRequest searchRequest = new DocumentManager.SearchRequest(null, null, null, start, end);
        List<DocumentManager.Document> results = documentManager.search(searchRequest);
        assertEquals(1, results.size(), "Should return 1 document within the specified date range.");
        assertEquals("title1", results.get(0).getTitle(), "The title of the document should be 'title1'");
    }

    @Test
    void testSearchWithMultipleCriteria() {
        Instant now = Instant.now();
        documentManager.save(new DocumentManager.Document("1", "title1", "content1", new DocumentManager.Author("1", "Tom"), now));
        documentManager.save(new DocumentManager.Document("2", "title2", "content2", new DocumentManager.Author("2", "Jack"), now));
        documentManager.save(new DocumentManager.Document("3", "title3", "searchable content", new DocumentManager.Author("1", "Tom"), now));
        DocumentManager.SearchRequest searchRequest = new DocumentManager.SearchRequest(
                Collections.singletonList("title3"), Collections.singletonList("searchable content"),
                Collections.singletonList("1"), null, null
        );
        List<DocumentManager.Document> results = documentManager.search(searchRequest);
        assertEquals(1, results.size(), "Should return 1 document matching all search criteria.");
        assertEquals("title3", results.get(0).getTitle(), "The title of the document should be 'title3'");
        assertEquals("searchable content", results.get(0).getContent(), "The content of the should be 'searchable content'");
        assertEquals("1", results.get(0).getAuthor().getId(), "The author ID of the document should be '1'");
    }
}
