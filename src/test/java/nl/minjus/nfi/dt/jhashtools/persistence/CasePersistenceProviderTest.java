/*
 * Copyright (c) 2010 Erwin van Eijk <erwin.vaneijk@gmail.com>.
 *
 * Licensed under the EUPL, Version 1.2 only (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *     https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the Licence is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the Licence for the specific language
 * governing permissions and limitations under the Licence.
 *
 * SPDX-License-Identifier: EUPL-1.2
 */

package nl.minjus.nfi.dt.jhashtools.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import nl.minjus.nfi.dt.jhashtools.AccessTimes;
import nl.minjus.nfi.dt.jhashtools.Digest;
import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;
import nl.minjus.nfi.dt.jhashtools.exceptions.PersistenceException;

public class CasePersistenceProviderTest
{
    private static final String SHA256_OF_ABC = "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad";

    private static final String MD5_OF_ABC = "900150983cd24fb0d6963f7d28e17f72";

    private static final String CONTEXT = """
        "@context": {
            "core": "https://ontology.unifiedcyberontology.org/uco/core/",
            "observable": "https://ontology.unifiedcyberontology.org/uco/observable/",
            "types": "https://ontology.unifiedcyberontology.org/uco/types/"
        }""";

    private final CasePersistenceProvider provider = new CasePersistenceProvider();

    @Test
    public void testPersistWritesUcoStructure() throws PersistenceException, IOException {
        final AccessTimes accessTimes = new AccessTimes(FileTime.from(Instant.parse("2001-02-03T04:05:06Z")),
            FileTime.from(Instant.parse("2001-02-03T04:05:07Z")));
        final DirHasherResult result = createResult();
        result.putAccessTimes(new File("dir/abc.txt"), accessTimes);

        final JsonNode document = new ObjectMapper().readTree(persist(result));

        assertThat(document.path("@context").path("observable").asText())
            .isEqualTo("https://ontology.unifiedcyberontology.org/uco/observable/");
        assertThat(document.path("@context").path("case-investigation").asText())
            .isEqualTo("https://ontology.caseontology.org/case/investigation/");
        final JsonNode file = nodeOfType(document, "observable:File");
        final JsonNode fileFacet = file.path("core:hasFacet").get(0);
        assertThat(fileFacet.path("@type").asText()).isEqualTo("observable:FileFacet");
        assertThat(fileFacet.path("observable:filePath").asText()).isEqualTo("dir/abc.txt");
        assertThat(fileFacet.path("observable:fileName").asText()).isEqualTo("abc.txt");
        assertThat(fileFacet.path("observable:accessedTime").path("@type").asText()).isEqualTo("xsd:dateTime");
        assertThat(fileFacet.path("observable:accessedTime").path("@value").asText())
            .isEqualTo("2001-02-03T04:05:06Z");
        final JsonNode contentDataFacet = file.path("core:hasFacet").get(1);
        assertThat(contentDataFacet.path("@type").asText()).isEqualTo("observable:ContentDataFacet");
        final List<String> hashes = new ArrayList<>();
        for (final JsonNode hash : contentDataFacet.path("observable:hash")) {
            assertThat(hash.path("@type").asText()).isEqualTo("types:Hash");
            assertThat(hash.path("types:hashValue").path("@type").asText()).isEqualTo("xsd:hexBinary");
            hashes.add(hash.path("types:hashMethod").asText() + ":" + hash.path("types:hashValue").path("@value")
                .asText());
        }
        assertThat(hashes).containsExactlyInAnyOrder("MD5:" + MD5_OF_ABC, "SHA256:" + SHA256_OF_ABC);
        final JsonNode action = nodeOfType(document, "case-investigation:InvestigativeAction");
        assertThat(action.path("action:result").get(0).path("@id").asText()).isEqualTo(file.path("@id").asText());
        assertThat(action.path("action:instrument").path("@id").asText())
            .isEqualTo(nodeOfType(document, "tool:AnalyticTool").path("@id").asText());
        assertThat(action.path("action:performer").path("@id").asText())
            .isEqualTo(nodeOfType(document, "identity:Person").path("@id").asText());
        assertThat(action.path("action:startTime").path("@type").asText()).isEqualTo("xsd:dateTime");
        assertThat(nodeOfType(document, "tool:AnalyticTool").path("core:name").asText()).isEqualTo("jHashtools");
    }

    @Test
    public void testPersistGivesEveryNodeAUniqueUuidIri() throws PersistenceException, IOException {
        final JsonNode document = new ObjectMapper().readTree(persist(createResult()));

        final List<String> ids = document.findValuesAsText("@id");
        assertThat(ids).allMatch(id -> id.matches("urn:uuid:[0-9a-f-]{36}"));
        // Every node has an @id; the action refers to the tool, the performer and the file once more.
        assertThat(new HashSet<>(ids)).hasSize(ids.size() - 3);
    }

    @Test
    public void testPersistAndLoadRoundTrip() throws PersistenceException {
        final DirHasherResult result = createResult();
        result.put("other.bin", new DigestResult(new Digest("ripemd160", "8eb208f7e05d987a9b044a8e98c6b087f15a0bfc")));

        final DirHasherResult loaded = this.provider.load(new StringReader(persist(result)), DirHasherResult.class);

        assertThat(loaded).isEqualTo(result);
        assertThat(loaded.get("other.bin").getAlgorithms()).containsExactly("ripemd160");
    }

    @Test
    public void testLoadAcceptsSingleValuesAndPlainHashValues() throws PersistenceException {
        final String json = "{" + CONTEXT + """
            , "@graph": [
                { "@type": "observable:ObservableObject" },
                {
                    "@type": ["observable:File"],
                    "core:hasFacet": [
                        { "@type": "observable:FileFacet", "observable:filePath": "dir/abc.txt" },
                        {
                            "@type": "observable:ContentDataFacet",
                            "observable:hash": { "types:hashMethod": "sha256", "types:hashValue": "%s" }
                        }
                    ]
                },
                {
                    "@type": "observable:File",
                    "core:hasFacet": { "@type": "observable:FileFacet", "observable:filePath": "no-hashes.txt" }
                }
            ]}""".formatted(SHA256_OF_ABC);

        final DirHasherResult loaded = this.provider.load(new StringReader(json), DirHasherResult.class);

        assertThat(loaded.getContent()).containsOnlyKeys(new File("dir/abc.txt"));
        assertThat(loaded.get("dir/abc.txt").getHexDigest("sha-256")).isEqualTo(SHA256_OF_ABC);
    }

    @Test
    public void testLoadHashtreeOutput() throws PersistenceException, IOException {
        final DirHasherResult loaded;
        // hashtree-case.jsonld was written by hashtree with `create --all -s case basic_tree`.
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("hashtree-case.jsonld"),
            StandardCharsets.UTF_8))
        {
            loaded = this.provider.load(reader, DirHasherResult.class);
        }

        assertThat(loaded.getContent()).containsOnlyKeys(new File("basic_tree/alpha.txt"),
            new File("basic_tree/nested/beta.txt"), new File("basic_tree/nested/deeper/gamma.txt"));
        assertThat(loaded.get("basic_tree/alpha.txt").getAlgorithms()).containsExactlyInAnyOrder("md5", "sha-1",
            "sha-256", "sha-384", "sha-512", "ripemd160", "sha3-256", "sha3-384", "sha3-512");
        assertThat(loaded.get("basic_tree/alpha.txt").getHexDigest("sha-1"))
            .isEqualTo("0770de18130c1aee37d32be349970813f2e2bdd8");
        assertThat(loaded.get("basic_tree/alpha.txt").getHexDigest("sha-256"))
            .isEqualTo("b76b08e074143b424a51915549ce7a4ada10b90e569995d23a9fa4dedead091b");
        assertThat(loaded.get("basic_tree/nested/beta.txt").getHexDigest("sha-256"))
            .isEqualTo("68aff8968ef4f932111707a0021bf69c5aa0f449494c703ddd8c9edf9fe3d98e");
        assertThat(loaded.get("basic_tree/nested/deeper/gamma.txt").getHexDigest("sha-256"))
            .isEqualTo("ae9a6306a205417afddd14316cc1d0d5e04a98f1be10865dce643925ee070ce2");
    }

    @Test
    public void testLoadRejectsMalformedDocuments() {
        final String file =
            "{" + CONTEXT + ", \"@graph\": [{\"@type\": \"observable:File\", \"core:hasFacet\": [%s]}]}";
        final String fileFacet = "{\"@type\": \"observable:FileFacet\", \"observable:filePath\": \"a.txt\"}";
        final String hashes = "{\"@type\": \"observable:ContentDataFacet\", \"observable:hash\": [%s]}";
        final Map<String, String> malformed = Map.of(
            "[]", "must be a JSON object",
            "{\"@graph\": []}", "must have an @context object",
            "{\"@context\": {\"observable\": \"https://ontology.caseontology.org/observable/\"}, \"@graph\": []}",
            "must map 'core' to",
            "{" + CONTEXT + ", \"@graph\": {}}", "must have an @graph array",
            file.formatted(""), "has no observable:FileFacet",
            file.formatted("{\"@type\": \"observable:FileFacet\"}"), "Missing observable:filePath",
            file.formatted(fileFacet + "," + hashes.formatted("{\"types:hashValue\": \"00\"}")),
            "Missing types:hashMethod for a.txt",
            file.formatted(fileFacet + "," + hashes.formatted("{\"types:hashMethod\": \"MD5\"}")),
            "Missing types:hashValue for a.txt",
            file.formatted(fileFacet + "," + hashes.formatted(
                "{\"types:hashMethod\": \"MD5\", \"types:hashValue\": \"zz\"}")),
            "Invalid MD5 hash value for a.txt",
            "{not json", "Could not parse the CASE document");

        malformed.forEach((json, message) -> assertThatExceptionOfType(PersistenceException.class).as(json)
            .isThrownBy(() -> this.provider.load(new StringReader(json), DirHasherResult.class))
            .withMessageContaining(message));
    }

    @Test
    public void testLoadRejectsHashtreeLegacyFormat() {
        final String legacy = """
            {
              "@context": {
                "core": "https://ontology.caseontology.org/core/",
                "observable": "https://ontology.caseontology.org/observable/",
                "types": "https://ontology.caseontology.org/types/"
              },
              "@graph": [{
                "@id": "case:file-1",
                "@type": "observable:File",
                "observable:filePath": "tree/alpha.txt",
                "observable:hashes": [{"types:hashMethod": "SHA-256", "types:hashValue": "00"}]
              }]
            }""";

        assertThatExceptionOfType(PersistenceException.class)
            .isThrownBy(() -> this.provider.load(new StringReader(legacy), DirHasherResult.class))
            .withMessageContaining("must map 'core' to https://ontology.unifiedcyberontology.org/uco/core/");
    }

    @Test
    public void testUnsupportedTypesAreRejected() {
        assertThatExceptionOfType(PersistenceException.class)
            .isThrownBy(() -> this.provider.persist(new ByteArrayOutputStream(), Set.of()));
        assertThatExceptionOfType(PersistenceException.class)
            .isThrownBy(() -> this.provider.load(new StringReader("{}"), DigestResult.class));
        assertThatExceptionOfType(PersistenceException.class)
            .isThrownBy(() -> this.provider.load(new StringReader("{}"), new TypeReference<DirHasherResult>()
            {
            }));
    }

    private static DirHasherResult createResult() {
        final DirHasherResult result = new DirHasherResult();
        final DigestResult digests = new DigestResult(new Digest("sha-256", SHA256_OF_ABC));
        digests.add(new Digest("md5", MD5_OF_ABC));
        result.put("dir/abc.txt", digests);
        return result;
    }

    private String persist(final DirHasherResult aResult) throws PersistenceException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.provider.persist(out, aResult);
        return out.toString(StandardCharsets.UTF_8);
    }

    private static JsonNode nodeOfType(final JsonNode aDocument, final String aType) {
        for (final JsonNode node : aDocument.path("@graph")) {
            if (aType.equals(node.path("@type").asText())) {
                return node;
            }
        }
        throw new AssertionError("No node of type " + aType);
    }
}
