package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ArvoreDNSTest {

    private ArvoreDNS arvore;

    @BeforeEach
    public void setUp() {
        arvore = new ArvoreDNS();
    }

    @Test
    public void testConstructor() {
        assertNotNull(arvore);
    }

    @Test
    public void testInserirDominio_SingleLevel() {
        arvore.inserirDominio("com", 3600, "A");
        assertTrue(arvore.buscarDominio("com"));
    }

    @Test
    public void testInserirDominio_MultiLevel() {
        arvore.inserirDominio("example.com", 3600, "A");
        assertTrue(arvore.buscarDominio("example.com"));
        assertTrue(arvore.buscarDominio("com"));
    }

    @Test
    public void testInserirDominio_Duplicate() {
        arvore.inserirDominio("example.com", 3600, "A");
        arvore.inserirDominio("example.com", 7200, "MX");
        assertTrue(arvore.buscarDominio("example.com"));
    }

    @Test
    public void testBuscarDominio_Exists() {
        arvore.inserirDominio("test.org", 3600, "A");
        assertTrue(arvore.buscarDominio("test.org"));
    }

    @Test
    public void testBuscarDominio_NotExists() {
        assertFalse(arvore.buscarDominio("nonexistent.com"));
    }

    @Test
    public void testBuscarDominio_PartialPath() {
        arvore.inserirDominio("sub.example.com", 3600, "A");
        assertTrue(arvore.buscarDominio("example.com"));
        assertTrue(arvore.buscarDominio("com"));
    }

    @Test
    public void testRemoverDominio_LeafNode() {
        arvore.inserirDominio("to.remove", 3600, "A");
        assertTrue(arvore.removerDominio("to.remove"));
        assertFalse(arvore.buscarDominio("to.remove"));
    }

    @Test
    public void testRemoverDominio_MiddleNode() {
        arvore.inserirDominio("sub.example.com", 3600, "A");
        assertTrue(arvore.removerDominio("example.com"));
        assertFalse(arvore.buscarDominio("sub.example.com"));
        assertFalse(arvore.buscarDominio("example.com"));
        assertTrue(arvore.buscarDominio("com"));
    }

    @Test
    public void testRemoverDominio_NotExists() {
        assertFalse(arvore.removerDominio("nonexistent.domain"));
    }

    @Test
    public void testRemoverDominio_Root() {
        assertFalse(arvore.removerDominio("raiz"));
    }

    @Test
    public void testExibirArvore_Empty() {
        arvore.exibirArvore();
    }

    @Test
    public void testExibirArvore_WithNodes() {
        arvore.inserirDominio("test1.com", 3600, "A");
        arvore.inserirDominio("test2.org", 7200, "MX");
        arvore.exibirArvore();
    }

    @Test
    public void testExportarZonas_Empty() {

        arvore.exportarZonas();
    }

    @Test
    public void testExportarZonas_WithNodes() {
        arvore.inserirDominio("zone1.com", 3600, "A");
        arvore.inserirDominio("sub.zone2.org", 7200, "MX");
        arvore.exportarZonas();
    }

    @Test
    public void testMultipleOperations() {
        arvore.inserirDominio("example.com", 3600, "A");
        arvore.inserirDominio("test.example.com", 1800, "CNAME");
        assertTrue(arvore.buscarDominio("example.com"));
        assertTrue(arvore.buscarDominio("test.example.com"));

        assertTrue(arvore.removerDominio("test.example.com"));
        assertFalse(arvore.buscarDominio("test.example.com"));
        assertTrue(arvore.buscarDominio("example.com"));
    }
}