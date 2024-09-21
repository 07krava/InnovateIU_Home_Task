package com.example.innovateiu_home_task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
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
}
