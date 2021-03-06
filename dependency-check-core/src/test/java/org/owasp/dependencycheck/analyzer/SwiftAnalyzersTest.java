package org.owasp.dependencycheck.analyzer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;
import org.owasp.dependencycheck.analyzer.exception.AnalysisException;
import org.owasp.dependencycheck.dependency.Dependency;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

import java.io.File;
import org.owasp.dependencycheck.dependency.EvidenceType;

/**
 * Unit tests for CocoaPodsAnalyzer.
 *
 * @author Bianca Jiang
 */
public class SwiftAnalyzersTest extends BaseTest {

    /**
     * The analyzer to test.
     */
    private CocoaPodsAnalyzer podsAnalyzer;
    private SwiftPackageManagerAnalyzer spmAnalyzer;

    /**
     * Correctly setup the analyzer for testing.
     *
     * @throws Exception thrown if there is a problem
     */
    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        podsAnalyzer = new CocoaPodsAnalyzer();
        podsAnalyzer.initialize(getSettings());
        podsAnalyzer.setFilesMatched(true);
        podsAnalyzer.prepare(null);

        spmAnalyzer = new SwiftPackageManagerAnalyzer();
        spmAnalyzer.initialize(getSettings());
        spmAnalyzer.setFilesMatched(true);
        spmAnalyzer.prepare(null);
    }

    /**
     * Cleanup the analyzer's temp files, etc.
     *
     * @throws Exception thrown if there is a problem
     */
    @After
    @Override
    public void tearDown() throws Exception {
        podsAnalyzer.close();
        podsAnalyzer = null;

        spmAnalyzer.close();
        spmAnalyzer = null;

        super.tearDown();
    }

    /**
     * Test of getName method, of class CocoaPodsAnalyzer.
     */
    @Test
    public void testPodsGetName() {
        assertThat(podsAnalyzer.getName(), is("CocoaPods Package Analyzer"));
    }

    /**
     * Test of getName method, of class SwiftPackageManagerAnalyzer.
     */
    @Test
    public void testSPMGetName() {
        assertThat(spmAnalyzer.getName(), is("SWIFT Package Manager Analyzer"));
    }

    /**
     * Test of supportsFiles method, of class CocoaPodsAnalyzer.
     */
    @Test
    public void testPodsSupportsFiles() {
        assertThat(podsAnalyzer.accept(new File("test.podspec")), is(true));
    }

    /**
     * Test of supportsFiles method, of class SwiftPackageManagerAnalyzer.
     */
    @Test
    public void testSPMSupportsFiles() {
        assertThat(spmAnalyzer.accept(new File("Package.swift")), is(true));
    }

    /**
     * Test of analyze method, of class CocoaPodsAnalyzer.
     *
     * @throws AnalysisException is thrown when an exception occurs.
     */
    @Test
    public void testCocoaPodsAnalyzer() throws AnalysisException {
        final Dependency result = new Dependency(BaseTest.getResourceAsFile(this,
                "swift/cocoapods/EasyPeasy.podspec"));
        podsAnalyzer.analyze(result, null);
        final String vendorString = result.getEvidence(EvidenceType.VENDOR).toString();

        assertThat(vendorString, containsString("Carlos Vidal"));
        assertThat(vendorString, containsString("https://github.com/nakiostudio/EasyPeasy"));
        assertThat(result.getEvidence(EvidenceType.PRODUCT).toString(), containsString("EasyPeasy"));
        assertThat(result.getEvidence(EvidenceType.VERSION).toString(), containsString("0.2.3"));
        assertThat(result.getName(), equalTo("EasyPeasy"));
        assertThat(result.getVersion(), equalTo("0.2.3"));
        assertThat(result.getDisplayFileName(), equalTo("EasyPeasy:0.2.3"));
        assertThat(result.getLicense(), containsString("MIT"));
        assertThat(result.getEcosystem(), equalTo(CocoaPodsAnalyzer.DEPENDENCY_ECOSYSTEM));
    }

    /**
     * Test of analyze method, of class SwiftPackageManagerAnalyzer.
     *
     * @throws AnalysisException is thrown when an exception occurs.
     */
    @Test
    public void testSPMAnalyzer() throws AnalysisException {
        final Dependency result = new Dependency(BaseTest.getResourceAsFile(this,
                "swift/Gloss/Package.swift"));
        spmAnalyzer.analyze(result, null);

        assertThat(result.getEvidence(EvidenceType.PRODUCT).toString(), containsString("Gloss"));
        assertThat(result.getName(), equalTo("Gloss"));
        //TODO: when version processing is added, update the expected name.
        assertThat(result.getDisplayFileName(), equalTo("Gloss"));
        assertThat(result.getEcosystem(), equalTo(SwiftPackageManagerAnalyzer.DEPENDENCY_ECOSYSTEM));
    }
}
