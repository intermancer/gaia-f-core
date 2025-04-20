intermancer: Create the classes, interfaces, and tests that do not already exist but are needed to implement the "Server Details" section of this document.

GitHub Copilot: To implement the "Server Details" section of the document, we need to create the following components:

1. **OrganismController**: A controller class to handle the endpoints.
2. **Tests for OrganismController**: Unit tests to validate the behavior of the controller.
3. **Endpoint Definitions**: Implement the `/organism`, `/organism/repo`, and `/organism/repo/{organismId}` endpoints.

Below is the scaffolded code for these components:

### 1. OrganismController Class
```java
package com.intermancer.gaiaf.core.controller;

import com.intermancer.gaiaf.core.organism.repo.OrganismRepository;
import com.intermancer.gaiaf.core.organism.repo.OrganismNotFoundException;
import com.intermancer.gaiaf.core.organism.Organism;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/organism")
public class OrganismController {

    private final OrganismRepository organismRepository;

    public OrganismController(OrganismRepository organismRepository) {
        this.organismRepository = organismRepository;
    }

    @GetMapping
    public ResponseEntity<List<Organism>> getAllOrganisms() {
        return ResponseEntity.ok(organismRepository.getAllOrganisms());
    }

    @GetMapping("/repo/{organismId}")
    public ResponseEntity<Organism> getOrganismById(@PathVariable String organismId) {
        try {
            Organism organism = organismRepository.getOrganismById(organismId);
            return ResponseEntity.ok(organism);
        } catch (OrganismNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/repo")
    public ResponseEntity<Void> saveOrganism(@RequestBody Organism organism) {
        organismRepository.saveOrganism(organism);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/repo/{organismId}")
    public ResponseEntity<Void> deleteOrganism(@PathVariable String organismId) {
        try {
            organismRepository.deleteOrganism(organismId);
            return ResponseEntity.noContent().build();
        } catch (OrganismNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
```

### 2. Unit Tests for OrganismController
```java
package com.intermancer.gaiaf.core.controller;

import com.intermancer.gaiaf.core.organism.Organism;
import com.intermancer.gaiaf.core.organism.repo.OrganismRepository;
import com.intermancer.gaiaf.core.organism.repo.OrganismNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrganismControllerTest {

    private OrganismRepository organismRepository;
    private OrganismController organismController;

    @BeforeEach
    void setUp() {
        organismRepository = mock(OrganismRepository.class);
        organismController = new OrganismController(organismRepository);
    }

    @Test
    void testGetAllOrganisms() {
        when(organismRepository.getAllOrganisms()).thenReturn(Collections.emptyList());
        ResponseEntity<List<Organism>> response = organismController.getAllOrganisms();
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testGetOrganismById_Success() {
        Organism organism = new Organism();
        when(organismRepository.getOrganismById("1")).thenReturn(organism);
        ResponseEntity<Organism> response = organismController.getOrganismById("1");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(organism, response.getBody());
    }

    @Test
    void testGetOrganismById_NotFound() {
        when(organismRepository.getOrganismById("1")).thenThrow(new OrganismNotFoundException());
        ResponseEntity<Organism> response = organismController.getOrganismById("1");
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testSaveOrganism() {
        Organism organism = new Organism();
        ResponseEntity<Void> response = organismController.saveOrganism(organism);
        verify(organismRepository, times(1)).saveOrganism(organism);
        assertEquals(201, response.getStatusCodeValue());
    }

    @Test
    void testDeleteOrganism_Success() {
        doNothing().when(organismRepository).deleteOrganism("1");
        ResponseEntity<Void> response = organismController.deleteOrganism("1");
        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    void testDeleteOrganism_NotFound() {
        doThrow(new OrganismNotFoundException()).when(organismRepository).deleteOrganism("1");
        ResponseEntity<Void> response = organismController.deleteOrganism("1");
        assertEquals(404, response.getStatusCodeValue());
    }
}
```

### 3. Additional Notes
- Ensure that the `OrganismRepository` and its implementation (`InMemoryOrganismRepository`) are already in place as described in the document.
- The `Organism` class should be implemented with the necessary properties and methods.
- Add the necessary Spring Boot dependencies in your `pom.xml` or build.gradle if not already included.

Let me know if you'd like further assistance with any of these components!