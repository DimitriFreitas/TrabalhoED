package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DominioTest {
    private Dominio rootDomain;
    private Dominio childDomain1;
    private Dominio childDomain2;

    @BeforeEach
    void setUp() {
        rootDomain = new Dominio("root", 3600, "A");
        childDomain1 = new Dominio("child1", 1800, "NS");
        childDomain2 = new Dominio("child2", 900, "CNAME");
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals("root", rootDomain.getNome());
        assertEquals(3600, rootDomain.getTtl());
        assertEquals("A", rootDomain.getTipo());
        assertTrue(rootDomain.getFilhos().isEmpty());
    }

    @Test
    void testAdicionarFilho() {
        rootDomain.adicionarFilho("child1", childDomain1);
        rootDomain.adicionarFilho("child2", childDomain2);

        Map<String, Dominio> children = rootDomain.getFilhos();
        assertEquals(2, children.size());
        assertSame(childDomain1, children.get("child1"));
        assertSame(childDomain2, children.get("child2"));
    }

    @Test
    void testRemoverFilho() {
        rootDomain.adicionarFilho("child1", childDomain1);
        rootDomain.adicionarFilho("child2", childDomain2);

        rootDomain.removerFilho("child1");

        Map<String, Dominio> children = rootDomain.getFilhos();
        assertEquals(1, children.size());
        assertFalse(children.containsKey("child1"));
        assertTrue(children.containsKey("child2"));
    }

    @Test
    void testRemoverFilhoNonExistent() {
        rootDomain.adicionarFilho("child1", childDomain1);

        // Should not throw exception when removing non-existent child
        assertDoesNotThrow(() -> rootDomain.removerFilho("non-existent"));
        assertEquals(1, rootDomain.getFilhos().size());
    }

    @Test
    void testAdicionarFilhoOverwrite() {
        Dominio anotherChild = new Dominio("another", 1200, "MX");
        rootDomain.adicionarFilho("child1", childDomain1);

        // Overwrite existing child
        rootDomain.adicionarFilho("child1", anotherChild);

        Map<String, Dominio> children = rootDomain.getFilhos();
        assertEquals(1, children.size());
        assertSame(anotherChild, children.get("child1"));
    }
}