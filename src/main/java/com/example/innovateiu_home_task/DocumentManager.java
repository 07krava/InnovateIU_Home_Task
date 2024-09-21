package com.example.innovateiu_home_task;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

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

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if (StringUtils.isEmpty(document.getId())) {
            String newId;
            do {
                newId = UUID.randomUUID().toString();
            } while (storage.containsKey(newId));
            document.setId(newId);
        }

        Document existingDocument = storage.get(document.getId());
        if (existingDocument != null) {
            // Сохраняем созданное время
            document.setCreated(existingDocument.getCreated());

            // Обновляем только если новые значения не равны null
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

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        if (request == null ||
                (request.getTitlePrefixes() == null &&
                        request.getContainsContents() == null &&
                        request.getAuthorIds() == null &&
                        request.getCreatedFrom() == null &&
                        request.getCreatedTo() == null)) {
            return new ArrayList<>(storage.values()); // Возвращаем все документы, если все поля null
        }

        return storage.values().stream()
                .filter(document -> matchesTitlePrefixes(document, request.getTitlePrefixes()))
                .filter(document -> containsContents(document, request.getContainsContents()))
                .filter(document -> matchesAuthorIds(document, request.getAuthorIds()))
                .filter(document -> isWithinCreateRange(document, request.getCreatedFrom(), request.getCreatedTo()))
                .collect(Collectors.toList());
    }


    private boolean matchesAuthorIds(Document document, List<String> authorIds) {
        return authorIds == null || authorIds.isEmpty() || authorIds.contains(document.getAuthor().getId());
    }

    private boolean containsContents(Document document, List<String> containsContents) {
        return containsContents == null || containsContents.isEmpty() ||
                containsContents.stream().anyMatch(document.getContent()::contains);
    }

    private boolean matchesTitlePrefixes(Document document, List<String> titlePrefixes) {
        return titlePrefixes == null || titlePrefixes.isEmpty() ||
                titlePrefixes.stream().anyMatch(prefix -> document.getTitle().startsWith(prefix));
    }

    private boolean isWithinCreateRange(Document document, Instant createdFrom, Instant createdTo) {
        Instant created = document.getCreated();
        return (createdFrom == null || !created.isBefore(createdFrom)) &&
                (createdTo == null || !created.isAfter(createdTo));
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
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