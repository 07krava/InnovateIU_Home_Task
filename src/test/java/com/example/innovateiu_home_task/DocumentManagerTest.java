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

    /**
    * This test method is designed to verify the behavior of the save method
    * in the DocumentManager class when a new document is created without an ID
    */
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

    /**
     * This test method verifies the behavior of the save method in the
     * DocumentManager class when attempting to save a document with
     * an existing ID. It first creates and saves an initial document
     * with a unique ID and a specified creation time. Then, it attempts
     * to save a new document with the same ID but different content and
     * title. The test asserts that the ID remains the same, the
     * creation time does not change, and the title is updated to the
     * new value provided in the second document.
     */
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

    /**
     * This test method verifies the behavior of the save method in the
     * DocumentManager class when a document is saved without a specified
     * creation date. It first creates a document with a unique ID,
     * title, and content, but with the creation date set to null.
     * After saving the document, the test asserts that the creation date
     * is automatically set (i.e., not null) by the save method.
     * It also verifies that the saved document can be found in storage
     * and matches the original document.
     */
    @Test
    void testSaveDocumentWithoutCreatedDate() {
        String documentId = UUID.randomUUID().toString();
        DocumentManager.Document document = new DocumentManager.Document(documentId, "Test title", "Test content", new DocumentManager.Author("1", "Tom"), null);
        DocumentManager.Document saveDocument = documentManager.save(document);
        assertNotNull(saveDocument.getCreated());
        assertEquals(documentManager.findById(documentId).get(), saveDocument);
    }

    /**
     * This test method verifies the behavior of the save method in the
     * DocumentManager class when a document is saved with an empty title.
     * It creates a document with a unique ID, an empty title, content,
     * and an author. After saving the document, the test asserts that
     * the document has been assigned a valid ID and that the creation
     * date has been set (i.e., is not null), even though the title is empty.
     */
    @Test
    void testSaveDocumentWithEmptyTitle() {
        DocumentManager.Document document = new DocumentManager.Document(null, "", "content", new DocumentManager.Author("1", "Tom"), null);
        DocumentManager.Document savedDocument = documentManager.save(document);
        assertNotNull(savedDocument.getId());
        assertNotNull(savedDocument.getCreated());
    }

    /**
     * This test method verifies the behavior of the search method in the
     * DocumentManager class when the storage is empty. It initializes a
     * new DocumentManager instance with no documents stored and creates
     * a search request with all fields set to null. After invoking the
     * search method, the test asserts that the results list is empty,
     * confirming that the method behaves as expected when there are no
     * documents to search through.
     */
    @Test
    void testSearchWithEmptyStorage() {
        documentManager = new DocumentManager();
        DocumentManager.SearchRequest searchRequest = new DocumentManager.SearchRequest(null, null, null, null, null);
        List<DocumentManager.Document> results = documentManager.search(searchRequest);
        assertTrue(results.isEmpty(), "Should return an empty list for empty storage.");
    }

    /**
     * This test method verifies the behavior of the search method in the
     * DocumentManager class when searching for documents by title prefix.
     * It first adds two documents to the storage with titles "title1"
     * and "title2". Then, it creates a search request looking for titles
     * that start with "title1". After performing the search, the test
     * asserts that exactly one document is returned and that the title
     * of the found document matches "title1", confirming that the
     * search functionality works correctly for title prefixes.
     */
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

    /**
     * This test method verifies the behavior of the search method in the
     * DocumentManager class when searching for documents by content.
     * It first saves two documents to the storage, each with distinct
     * content: "content1" and "content2". Then, it creates a search
     * request looking for documents that contain the content "content2".
     * After performing the search, the test asserts that exactly one
     * document is returned and that the content of the found document
     * matches "content2", confirming that the search functionality works
     * correctly for document contents.
     */
    @Test
    void testSearchByContent() {
        documentManager.save(new DocumentManager.Document("1", "title1", "content1", new DocumentManager.Author("1", "Tom"), Instant.now()));
        documentManager.save(new DocumentManager.Document("2", "title2", "content2", new DocumentManager.Author("2", "Jack"), Instant.now()));
        DocumentManager.SearchRequest searchRequest = new DocumentManager.SearchRequest(null, Collections.singletonList("content2"), null, null, null);
        List<DocumentManager.Document> results = documentManager.search(searchRequest);
        assertEquals(1, results.size(), "Should return 1 document with content 'content2'");
        assertEquals("content2", results.get(0).getContent());
    }

    /**
     * This test method verifies the behavior of the search method in the
     * DocumentManager class when all search fields are set to null.
     * It first saves three distinct documents to the storage, each with
     * unique titles and contents. Then, it creates a search request with
     * all fields as null, which should result in a search that returns
     * all documents in the storage. The test asserts that the size of the
     * returned list is equal to 3, confirming that all documents are
     * correctly retrieved when no specific search criteria are provided.
     */
    @Test
    void testSearchWithAllNullFields() {
        documentManager.save(new DocumentManager.Document("1", "title1", "content1", new DocumentManager.Author("1", "Tom"), Instant.now()));
        documentManager.save(new DocumentManager.Document("2", "title2", "content2", new DocumentManager.Author("2", "Jack"), Instant.now()));
        documentManager.save(new DocumentManager.Document("3", "title3", "content3", new DocumentManager.Author("3", "Mike"), Instant.now()));
        DocumentManager.SearchRequest searchRequest = new DocumentManager.SearchRequest(null, null, null, null, null);
        List<DocumentManager.Document> results = documentManager.search(searchRequest);
        assertEquals(3, results.size(), "Should return all documents when all search fields are null.");
    }

    /**
     * This test method verifies the behavior of the search method in the
     * DocumentManager class when searching for documents by a specific
     * author ID. It first saves two documents with different authors to
     * the storage. Then, it creates a search request targeting the author
     * ID "1". The test asserts that the returned list contains exactly one
     * document and that the author ID of that document matches the expected
     * value "1", confirming that the search function correctly filters
     * documents by the specified author.
     */
    @Test
    void testSearchByAuthorId() {
        documentManager.save(new DocumentManager.Document("1", "title1", "content1", new DocumentManager.Author("1", "Tom"), Instant.now()));
        documentManager.save(new DocumentManager.Document("2", "title2", "content2", new DocumentManager.Author("2", "Jack"), Instant.now()));
        DocumentManager.SearchRequest searchRequest = new DocumentManager.SearchRequest(null, null, Collections.singletonList("1"), null, null);
        List<DocumentManager.Document> results = documentManager.search(searchRequest);
        assertEquals("1", results.get(0).getAuthor().getId(), "The author ID of the document should be '1'");
        assertEquals(1, results.size(), "Should return 1 document with author ID '1'");
    }

    /**
     * This test method verifies the behavior of the search method in the
     * DocumentManager class when searching for documents within a specified
     * date range. It first saves two documents, one with a creation date
     * that falls within the given range and another that does not. The
     * method then creates a search request with a start and end date,
     * and checks that the returned list contains exactly one document.
     * Finally, the test asserts that the title of the returned document
     * matches the expected value "title1", confirming that the search
     * function correctly filters documents based on their creation dates.
     */
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

    /**
     * This test method verifies the behavior of the search method in the
     * DocumentManager class when multiple search criteria are applied.
     * It saves three documents with different titles, contents, and authors.
     * The method then creates a search request with specific title, content,
     * and author ID criteria. It checks that the returned list contains
     * exactly one document that matches all specified criteria. Finally,
     * the test asserts that the title, content, and author ID of the
     * returned document match the expected values, confirming that the
     * search function correctly filters documents based on multiple criteria.
     */
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

    /**
     * This test method verifies the behavior of the findById method in the
     * DocumentManager class when searching for an existing document.
     * It creates a new document with a specific ID, title, content,
     * and author, then saves it using the document manager.
     * The method subsequently attempts to retrieve the document by its ID.
     * The test checks that the retrieved document is present, and it
     * asserts that the ID and title of the found document match the
     * expected values, confirming that the findById function correctly
     * retrieves the document from storage.
     */
    @Test
    void testFindByExistingDocument() {
        DocumentManager.Document document = new DocumentManager.Document("1", "title1", "content1", new DocumentManager.Author("1", "Tom"), Instant.now());
        documentManager.save(document);
        Optional<DocumentManager.Document> foundDocument = documentManager.findById("1");
        assertEquals("1", foundDocument.get().getId(), "The document ID '1' should be found.");
        assertTrue(foundDocument.isPresent(), "Document with ID '1' should be found.");
        assertEquals("title1", foundDocument.get().getTitle(), "The document title should be 'title1'.");
    }

    /**
     * This test method verifies the behavior of the findById method in the
     * DocumentManager class when searching for a non-existing document.
     * It attempts to retrieve a document using an ID that does not exist
     * in the storage. The test checks that the result is an empty
     * Optional, confirming that the findById function correctly
     * handles the case where the requested document ID is not present
     * in the storage.
     */
    @Test
    void testFindByIdNonExistingDocument() {
        Optional<DocumentManager.Document> foundDocument = documentManager.findById("non-existing-id");
        assertFalse(foundDocument.isPresent(), "Document with non-existing ID showId not be found.");
    }

    /**
     * This test method verifies the behavior of the findById method in the
     * DocumentManager class when searching for a document with a null ID.
     * It attempts to retrieve a document using a null ID and checks that
     * the result is an empty Optional. This confirms that the findById
     * function correctly handles cases where the provided ID is null,
     * ensuring that it does not attempt to search for a document in
     * such scenarios.
     */
    @Test
    void testFindByIdNull() {
        Optional<DocumentManager.Document> foundDocument = documentManager.findById(null);
        assertFalse(foundDocument.isPresent(), "Searching for document with null ID should return empty Optional.");
    }

    /**
     * This test method verifies the behavior of the findById method in the
     * DocumentManager class when searching for a document with an empty string
     * as the ID. It attempts to retrieve a document using an empty string and
     * checks that the result is an empty Optional. This ensures that the
     * findById function correctly handles cases where the provided ID is
     * an empty string, preventing any attempts to find a document with
     * an invalid identifier.
     */
    @Test
    void testFindByIdWithEmptyString() {
        Optional<DocumentManager.Document> foundDocument = documentManager.findById("");
        assertFalse(foundDocument.isPresent(), "Should return empty Optional for empty ID.");
    }

    /**
     * This test method verifies the behavior of the save method in the
     * DocumentManager class when updating an existing document with null
     * fields. It creates a document with initial values and then attempts
     * to update it by providing null values for the title, content,
     * author, and creation date. The test ensures that the original title
     * and content remain unchanged after the update, confirming that
     * the save method correctly handles null fields by retaining the
     * original values for fields that are not specified.
     */
    @Test
    void testUpdateDocumentWithNullFields() {
        String documentId = UUID.randomUUID().toString();
        documentManager.save(new DocumentManager.Document(documentId, "Initial Title", "Initial Content", new DocumentManager.Author("1", "Tom"), Instant.now()));

        DocumentManager.Document updatedDocument = new DocumentManager.Document(documentId, null, null, null, null);
        DocumentManager.Document savedDocument = documentManager.save(updatedDocument);

        assertEquals("Initial Title", savedDocument.getTitle(), "Title should not change.");
        assertEquals("Initial Content", savedDocument.getContent(), "Content should not change.");
    }
}
