# Document Manager

This project is a simple implementation of a document management system in Java, where documents can be saved, retrieved by ID, and searched using various search criteria.

## Description

The `DocumentManager` class manages the saving, searching, and retrieving of documents from an in-memory collection (in this case, a `HashMap` is used).

### Main Features:
- **Saving Documents**: If a document doesn't have an ID, a unique identifier is assigned to it. If the document already exists, only the fields that are not `null` are updated.
- **Document Search**: Documents can be searched by several criteria, such as title prefixes, keywords in content, author IDs, and creation date ranges.
- **Retrieving by ID**: Documents can be retrieved from storage using their unique ID.

## Usage

### Methods

- **`Document save(Document document)`**
  This method saves a document to storage. If the document doesn't have an ID, a unique one is generated. If the document already exists, only its fields that are not `null` will be updated.

- **`List<Document> search(SearchRequest request)`**
  This method returns a list of documents that match the search criteria specified in the `SearchRequest`.

- **`Optional<Document> findById(String id)`**
  This method retrieves a document by its ID. If the document exists, it returns an `Optional` containing the document; otherwise, it returns `Optional.empty()`.

### Example

```java
DocumentManager manager = new DocumentManager();

// Creating a document
Document doc = Document.builder()
    .title("Example Title")
    .content("Example Content")
    .author(Author.builder().id("1").name("Author Name").build())
    .build();

// Saving the document
Document savedDoc = manager.save(doc);

// Finding the document by ID
Optional<Document> foundDoc = manager.findById(savedDoc.getId());

// Searching for documents by criteria
SearchRequest request = SearchRequest.builder()
    .titlePrefixes(Collections.singletonList("Example"))
    .build();
List<Document> searchResults = manager.search(request);

Installation and Running

1. Clone the repository:
git clone https://github.com/07krava/InnovateIU_Home_Task

2. Navigate to the project directory:
cd InnovateIU_Home_Task

3. Build the project using Maven:
mvn clean install

4. Run automated tests:
mvn test

Requirements
Java 11 or newer
Apache Maven 3.8.4 or newer

