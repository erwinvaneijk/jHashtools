/*
 * Copyright (c) 2010 Erwin van Eijk <erwin.vaneijk@gmail.com>. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of <copyright holder>.
 */

package nl.minjus.nfi.dt.jhashtools.persistence;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import nl.minjus.nfi.dt.jhashtools.AccessTimes;
import nl.minjus.nfi.dt.jhashtools.ConstructionInfo;
import nl.minjus.nfi.dt.jhashtools.Digest;
import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;
import nl.minjus.nfi.dt.jhashtools.exceptions.PersistenceException;

/**
 * Persist a DirHasherResult as CASE/UCO 1.5.0 JSON-LD (https://caseontology.org/).
 * <p>
 * Every hashed file becomes an <code>observable:File</code> with an <code>observable:FileFacet</code> (path, name and
 * the last access time before hashing) and an <code>observable:ContentDataFacet</code> holding one
 * <code>types:Hash</code> per digest. The run is recorded as a <code>case-investigation:InvestigativeAction</code>
 * performed by an <code>identity:Person</code> with a <code>tool:AnalyticTool</code>.
 * <p>
 * Loading reads the documents written by this class and by hashtree: the <code>@context</code> must map the prefixes
 * used here to the UCO namespaces, and facets must be nested in their <code>observable:File</code>.
 *
 * @author Erwin van Eijk
 */
public class CasePersistenceProvider implements PersistenceProvider
{
    private static final String UCO = "https://ontology.unifiedcyberontology.org/uco/";

    private static final Map<String, String> CONTEXT = Map.of(
        "case-investigation", "https://ontology.caseontology.org/case/investigation/",
        "action", UCO + "action/",
        "core", UCO + "core/",
        "identity", UCO + "identity/",
        "observable", UCO + "observable/",
        "tool", UCO + "tool/",
        "types", UCO + "types/",
        "xsd", "http://www.w3.org/2001/XMLSchema#");

    private static final List<String> PREFIXES_READ = List.of("core", "observable", "types");

    /**
     * Algorithm names mapped to the UCO HashNameVocab names. ripemd160 is not in the vocabulary, which is open.
     */
    private static final Map<String, String> HASH_METHODS = Map.of(
        "md5", "MD5",
        "sha-1", "SHA1",
        "sha-256", "SHA256",
        "sha-384", "SHA384",
        "sha-512", "SHA512",
        "ripemd160", "RIPEMD160");

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Override
    public void persist(final OutputStream out, final Object obj) throws PersistenceException {
        if (!(obj instanceof DirHasherResult dirHasherResult)) {
            throw new PersistenceException("There is no CASE persistence method defined for " + obj);
        }
        try {
            OBJECT_MAPPER.writeValue(out, toCase(dirHasherResult));
        } catch (final IOException ex) {
            throw new PersistenceException("Could not write the CASE document", ex);
        }
    }

    @Override
    public <T> T load(final Reader reader, final TypeReference<T> type) throws PersistenceException {
        throw new PersistenceException("Currently the use of TypeReference is not supported for CASE files");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T load(final Reader reader, final Class<T> clazz) throws PersistenceException {
        if (!DirHasherResult.class.equals(clazz)) {
            throw new PersistenceException("There is no CASE persistence method defined for " + clazz);
        }
        final JsonNode document;
        try {
            document = OBJECT_MAPPER.readTree(reader);
        } catch (final IOException ex) {
            throw new PersistenceException("Could not parse the CASE document", ex);
        }
        return (T) fromCase(document);
    }

    private static ObjectNode toCase(final DirHasherResult aResult) {
        final ObjectNode document = OBJECT_MAPPER.createObjectNode();
        final ObjectNode context = document.putObject("@context");
        CONTEXT.entrySet().stream().sorted(Map.Entry.comparingByKey())
            .forEach(entry -> context.put(entry.getKey(), entry.getValue()));
        final ArrayNode graph = document.putArray("@graph");
        final ConstructionInfo info = aResult.getConstructionInfo();

        final ObjectNode tool = addNode(graph, "tool:AnalyticTool");
        tool.put("core:name", "jHashtools");
        tool.put("tool:version", info.getVersionInformation());
        tool.put("tool:toolType", "hash-calculator");

        final ObjectNode performer = addNode(graph, "identity:Person");
        performer.put("core:name", info.getUsername());

        final List<String> fileIds = new ArrayList<>();
        for (final Map.Entry<File, DigestResult> entry : aResult) {
            final Optional<AccessTimes> accessTimes = Optional
                .ofNullable(aResult.getAccessTimes().get(entry.getKey()));
            fileIds.add(addFile(graph, entry.getKey(), entry.getValue(), accessTimes));
        }

        final ObjectNode action = addNode(graph, "case-investigation:InvestigativeAction");
        action.put("core:name", "Compute digests");
        putTypedLiteral(action, "action:startTime", "xsd:dateTime", info.getConstructionDate().toInstant().toString());
        action.putObject("action:performer").put("@id", performer.get("@id").asText());
        action.putObject("action:instrument").put("@id", tool.get("@id").asText());
        final ArrayNode results = action.putArray("action:result");
        fileIds.forEach(fileId -> results.addObject().put("@id", fileId));
        return document;
    }

    private static String addFile(final ArrayNode aGraph, final File aFile, final DigestResult theDigests,
        final Optional<AccessTimes> theAccessTimes)
    {
        final ObjectNode file = addNode(aGraph, "observable:File");
        final ArrayNode facets = file.putArray("core:hasFacet");

        final ObjectNode fileFacet = addNode(facets, "observable:FileFacet");
        fileFacet.put("observable:fileName", aFile.getName());
        fileFacet.put("observable:filePath", aFile.getPath());
        // The access time before hashing is the evidential one; reading the file may have changed it.
        theAccessTimes.ifPresent(times -> putTypedLiteral(fileFacet, "observable:accessedTime", "xsd:dateTime",
            times.before().toInstant().toString()));

        final ObjectNode contentDataFacet = addNode(facets, "observable:ContentDataFacet");
        final ArrayNode hashes = contentDataFacet.putArray("observable:hash");
        for (final Digest digest : theDigests) {
            final ObjectNode hash = addNode(hashes, "types:Hash");
            hash.put("types:hashMethod", toHashMethod(digest.getAlgorithm()));
            putTypedLiteral(hash, "types:hashValue", "xsd:hexBinary", digest.toHex());
        }
        return file.get("@id").asText();
    }

    private static ObjectNode addNode(final ArrayNode aParent, final String aType) {
        final ObjectNode node = aParent.addObject();
        node.put("@id", "urn:uuid:" + UUID.randomUUID());
        node.put("@type", aType);
        return node;
    }

    private static void putTypedLiteral(final ObjectNode aNode, final String aField, final String aType,
        final String aValue)
    {
        aNode.putObject(aField).put("@type", aType).put("@value", aValue);
    }

    private static String toHashMethod(final String anAlgorithm) {
        return HASH_METHODS.getOrDefault(anAlgorithm, anAlgorithm.toUpperCase(Locale.ROOT));
    }

    private static String fromHashMethod(final String aHashMethod) {
        return HASH_METHODS.entrySet().stream()
            .filter(entry -> entry.getValue().equalsIgnoreCase(aHashMethod))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse(aHashMethod.toLowerCase(Locale.ROOT));
    }

    private static DirHasherResult fromCase(final JsonNode aDocument) throws PersistenceException {
        if (aDocument == null || !aDocument.isObject()) {
            throw new PersistenceException("A CASE document must be a JSON object");
        }
        checkContext(aDocument.get("@context"));
        final JsonNode graph = aDocument.get("@graph");
        if (graph == null || !graph.isArray()) {
            throw new PersistenceException("A CASE document must have an @graph array");
        }
        final DirHasherResult result = new DirHasherResult();
        for (final JsonNode node : graph) {
            if (hasType(node, "observable:File")) {
                readFile(node, result);
            }
        }
        return result;
    }

    private static void checkContext(final JsonNode aContext) throws PersistenceException {
        if (aContext == null || !aContext.isObject()) {
            throw new PersistenceException("A CASE document must have an @context object");
        }
        for (final String prefix : PREFIXES_READ) {
            final JsonNode namespace = aContext.get(prefix);
            if (namespace == null || !CONTEXT.get(prefix).equals(namespace.asText())) {
                throw new PersistenceException("The @context of the CASE document must map '" + prefix + "' to "
                    + CONTEXT.get(prefix));
            }
        }
    }

    private static boolean hasType(final JsonNode aNode, final String aType) {
        final JsonNode type = aNode.get("@type");
        if (type == null) {
            return false;
        }
        if (type.isArray()) {
            for (final JsonNode element : type) {
                if (aType.equals(element.asText())) {
                    return true;
                }
            }
            return false;
        }
        return aType.equals(type.asText());
    }

    private static void readFile(final JsonNode aFile, final DirHasherResult aResult) throws PersistenceException {
        final String fileId = aFile.path("@id").asText("(no @id)");
        final List<JsonNode> facets = elements(aFile.path("core:hasFacet"));
        String filePath = null;
        for (final JsonNode facet : facets) {
            if (hasType(facet, "observable:FileFacet")) {
                filePath = requireText(facet, "observable:filePath", fileId);
            }
        }
        if (filePath == null) {
            throw new PersistenceException("observable:File " + fileId + " has no observable:FileFacet");
        }
        final DigestResult digests = new DigestResult();
        for (final JsonNode facet : facets) {
            if (hasType(facet, "observable:ContentDataFacet")) {
                for (final JsonNode hash : elements(facet.path("observable:hash"))) {
                    digests.add(readHash(hash, filePath));
                }
            }
        }
        if (!digests.isEmpty()) {
            aResult.put(filePath, digests);
        }
    }

    /**
     * JSON-LD allows a single value instead of an array with one element.
     */
    private static List<JsonNode> elements(final JsonNode aNode) {
        if (aNode.isArray()) {
            final List<JsonNode> elements = new ArrayList<>();
            aNode.forEach(elements::add);
            return elements;
        }
        return aNode.isObject() ? List.of(aNode) : List.of();
    }

    private static Digest readHash(final JsonNode aHash, final String aFilePath) throws PersistenceException {
        final String hashMethod = requireText(aHash, "types:hashMethod", aFilePath);
        final JsonNode value = aHash.path("types:hashValue");
        final JsonNode hexValue = value.isObject() ? value.path("@value") : value;
        if (!hexValue.isTextual()) {
            throw new PersistenceException("Missing types:hashValue for " + aFilePath);
        }
        try {
            return new Digest(fromHashMethod(hashMethod), HexFormat.of().parseHex(hexValue.asText()));
        } catch (final IllegalArgumentException ex) {
            throw new PersistenceException("Invalid " + hashMethod + " hash value for " + aFilePath, ex);
        }
    }

    private static String requireText(final JsonNode aNode, final String aField, final String aContext)
        throws PersistenceException
    {
        final JsonNode value = aNode.get(aField);
        if (value == null || !value.isTextual() || value.asText().isEmpty()) {
            throw new PersistenceException("Missing " + aField + " for " + aContext);
        }
        return value.asText();
    }
}
