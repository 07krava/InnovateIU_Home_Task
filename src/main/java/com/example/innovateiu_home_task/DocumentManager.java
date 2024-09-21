package com.example.innovateiu_home_task;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {

    private final Map<String, Document> storage = new HashMap<>();

    // TODO:
    //     * Implementation of this method should upsert the document to your storage
    //     * And generate unique id if it does not exist, don't change [created] field
    //     *
    //     * @param document - document content and author data
    //     * @return saved document /**

    /**
     * This method saves a document in the storage. If the document does not
     * have an ID, a new unique ID is generated and assigned to it. If the
     * document already exists in the storage, its creation time is retained,
     * and only the fields that are not set to null (i.e., title and content)
     * are updated. If the document is new and does not have a creation time
     * specified, the current timestamp is set as its creation time. Finally,
     * the document is stored in the storage map and returned.
     */
    public Document save(Document document) {
        if (StringUtils.isEmpty(document.getId())) {
            String newId;
            //Handling duplicate ID generation
            do {
                newId = UUID.randomUUID().toString();
            } while (storage.containsKey(newId));
            document.setId(newId);
        }

        Document existingDocument = storage.get(document.getId());
        if (existingDocument != null) {
            document.setCreated(existingDocument.getCreated());

            if (document.getTitle() == null) {
                document.setTitle(existingDocument.getTitle());
            }
            if (document.getContent() == null) {
                document.setContent(existingDocument.getContent());
            }
        } else if (document.getCreated() == null) {
            document.setCreated(Instant.now());
        }

        storage.put(document.getId(), document);
        return document;
    }

    // TODO: Implement this method to find documents that match the search request.
    //     * Each field in the request could be null.
    //     *
    //     * @param request - search request
    //     * @return list of matched documents /**

    /**
     * Searches for documents that match the criteria specified in the search request.
     * If the request is null or all fields in the request are null, it returns all documents in storage.
     *
     * @param request - the search request containing criteria for filtering documents.
     *                Each field in the request can be null.
     * @return a list of documents that match the search criteria, or all documents if no criteria are specified.
     */
    public List<Document> search(SearchRequest request) {
        if (request == null ||
                (request.getTitlePrefixes() == null &&
                        request.getContainsContents() == null &&
                        request.getAuthorIds() == null &&
                        request.getCreatedFrom() == null &&
                        request.getCreatedTo() == null)) {
            return new ArrayList<>(storage.values());
        }

        return storage.values().stream()
                .filter(document -> matchesTitlePrefixes(document, request.getTitlePrefixes()))
                .filter(document -> containsContents(document, request.getContainsContents()))
                .filter(document -> matchesAuthorIds(document, request.getAuthorIds()))
                .filter(document -> isWithinCreateRange(document, request.getCreatedFrom(), request.getCreatedTo()))
                .collect(Collectors.toList());
    }

    /**
     * Checks if a document's author ID matches any of the provided author IDs.
     * If the author IDs list is null or empty, the method returns true, indicating
     * that the document matches by default.
     *
     * @param document  - the document to check for a matching author ID.
     * @param authorIds - a list of author IDs to match against. Can be null or empty.
     * @return true if the document's author ID matches one of the provided author IDs,
     * or if the authorIds list is null or empty; false otherwise.
     */
    private boolean matchesAuthorIds(Document document, List<String> authorIds) {
        return authorIds == null || authorIds.isEmpty() || authorIds.contains(document.getAuthor().getId());
    }

    /**
     * Checks if a document's content contains any of the specified keywords.
     * If the containsContents list is null or empty, the method returns true,
     * indicating that the document matches by default.
     *
     * @param document         - the document to check for matching content.
     * @param containsContents - a list of keywords to check for within the document's content.
     *                         Can be null or empty.
     * @return true if the document's content contains any of the specified keywords,
     * or if the containsContents list is null or empty; false otherwise.
     */
    private boolean containsContents(Document document, List<String> containsContents) {
        return containsContents == null || containsContents.isEmpty() ||
                containsContents.stream().anyMatch(document.getContent()::contains);
    }

    /**
     * Checks if a document's title starts with any of the specified prefixes.
     * If the titlePrefixes list is null or empty, the method returns true,
     * indicating that the document matches by default.
     *
     * @param document      - the document to check for matching title prefixes.
     * @param titlePrefixes - a list of prefixes to check against the document's title.
     *                      Can be null or empty.
     * @return true if the document's title starts with any of the specified prefixes,
     * or if the titlePrefixes list is null or empty; false otherwise.
     */
    private boolean matchesTitlePrefixes(Document document, List<String> titlePrefixes) {
        return titlePrefixes == null || titlePrefixes.isEmpty() ||
                titlePrefixes.stream().anyMatch(prefix -> document.getTitle().startsWith(prefix));
    }

    /**
     * Checks if a document's creation date is within the specified range.
     * If the createdFrom or createdTo parameters are null, they are ignored
     * in the comparison. A document is considered to be within the range
     * if its creation date is not before createdFrom and not after createdTo.
     *
     * @param document    - the document whose creation date is to be checked.
     * @param createdFrom - the start of the creation date range; can be null.
     * @param createdTo   - the end of the creation date range; can be null.
     * @return true if the document's creation date is within the specified range,
     * or if the range boundaries are null; false otherwise.
     */
    private boolean isWithinCreateRange(Document document, Instant createdFrom, Instant createdTo) {
        Instant created = document.getCreated();
        return (createdFrom == null || !created.isBefore(createdFrom)) &&
                (createdTo == null || !created.isAfter(createdTo));
    }

//     TODO: 21.09.2024 Implementation this method should find document by id;
//      @param id - document id
//      @return optional document

    /**
     * Retrieves a document by its ID from the storage.
     * If the document with the given ID exists, it returns an Optional
     * containing the document. If no document is found, it returns
     * an empty Optional.
     *
     * @param id - the ID of the document to be retrieved; can be null.
     * @return an Optional containing the document if found, or an empty
     * Optional if no document with the given ID exists.
     */
    public Optional<Document> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}