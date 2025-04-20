intermancer: Write the classes, interfaces, and test classes described in the "Organism Repository" section of this document.

GitHub Copilot: Below is the implementation of the classes, interfaces, and test classes described in the "Organism Repository" section of the document.

### OrganismRepository Interface
```java
package com.intermancer.gaiaf.core.organism.repo;

import com.intermancer.gaiaf.core.organism.Organism;
import java.util.List;

public interface OrganismRepository {
    Organism getOrganismById(String organismId);
    void saveOrganism(Organism organism);
    void deleteOrganism(String organismId);
    List<Organism> getAllOrganisms();
}
```

### InMemoryOrganismRepository Class
```java
package com.intermancer.gaiaf.core.organism.repo;

import com.intermancer.gaiaf.core.organism.Organism;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryOrganismRepository implements OrganismRepository {
    private final Map<String, Organism> organismMap = new HashMap<>();

    @Override
    public Organism getOrganismById(String organismId) {
        if (!organismMap.containsKey(organismId)) {
            throw new OrganismNotFoundException("Organism with ID " + organismId + " not found.");
        }
        return organismMap.get(organismId);
    }

    @Override
    public void saveOrganism(Organism organism) {
        organismMap.put(organism.getId(), organism);
    }

    @Override
    public void deleteOrganism(String organismId) {
        if (!organismMap.containsKey(organismId)) {
            throw new OrganismNotFoundException("Organism with ID " + organismId + " not found.");
        }
        organismMap.remove(organismId);
    }

    @Override
    public List<Organism> getAllOrganisms() {
        return organismMap.values().stream().collect(Collectors.toList());
    }
}
```

### OrganismNotFoundException Class
```java
package com.intermancer.gaiaf.core.organism.repo;

public class OrganismNotFoundException extends RuntimeException {
    public OrganismNotFoundException(String message) {
        super(message);
    }
}
```

### OrganismRepositoryFactory Class
```java
package com.intermancer.gaiaf.core.organism.repo;

public class OrganismRepositoryFactory {
    private static final InMemoryOrganismRepository INSTANCE = new InMemoryOrganismRepository();

    public static OrganismRepository getInstance() {
        return INSTANCE;
    }
}
```

### Test Classes
```java
package com.intermancer.gaiaf.core.organism.repo;

import com.intermancer.gaiaf.core.organism.Organism;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryOrganismRepositoryTest {

    @Test
    void testSaveAndRetrieveOrganism() {
        OrganismRepository repository = new InMemoryOrganismRepository();
        Organism organism = new Organism("1");
        repository.saveOrganism(organism);

        Organism retrieved = repository.getOrganismById("1");
        assertEquals(organism, retrieved);
    }

    @Test
    void testDeleteOrganism() {
        OrganismRepository repository = new InMemoryOrganismRepository();
        Organism organism = new Organism("1");
        repository.saveOrganism(organism);

        repository.deleteOrganism("1");
        assertThrows(OrganismNotFoundException.class, () -> repository.getOrganismById("1"));
    }

    @Test
    void testGetAllOrganisms() {
        OrganismRepository repository = new InMemoryOrganismRepository();
        Organism organism1 = new Organism("1");
        Organism organism2 = new Organism("2");
        repository.saveOrganism(organism1);
        repository.saveOrganism(organism2);

        List<Organism> organisms = repository.getAllOrganisms();
        assertEquals(2, organisms.size());
        assertTrue(organisms.contains(organism1));
        assertTrue(organisms.contains(organism2));
    }

    @Test
    void testOrganismNotFoundException() {
        OrganismRepository repository = new InMemoryOrganismRepository();
        assertThrows(OrganismNotFoundException.class, () -> repository.getOrganismById("nonexistent"));
    }
}
```

This implementation includes all the required classes, interfaces, and test cases as described in the document. Let me know if you need further modifications or additional features.

intermancer: Undo the last operation.

GitHub Copilot: No changes were made to the file OrganismRepository.java, so there is nothing to undo. Let me know if you need assistance with anything else!