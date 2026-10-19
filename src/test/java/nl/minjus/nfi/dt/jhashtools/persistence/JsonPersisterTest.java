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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.minjus.nfi.dt.jhashtools.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.fail;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.*;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.minjus.nfi.dt.jhashtools.AccessTimes;
import nl.minjus.nfi.dt.jhashtools.Digest;
import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;
import nl.minjus.nfi.dt.jhashtools.exceptions.PersistenceException;

/**
 * @author eijk
 */
public class JsonPersisterTest
{

    private String testDigestResultInJson;
    private String testDirHasherResultInJson;

    @BeforeAll
    public static void setUpClass() throws Exception {
    }

    @AfterAll
    public static void tearDownClass() throws Exception {
    }

    @BeforeEach
    public void setUp() {
        this.testDigestResultInJson = "[\"sha-1:0000111122223333444455556666777788889999aaaa\"]";
        this.testDirHasherResultInJson =
            "{\"constructionInfo\":{\"constructionDate\":1273412558116,\"operatingSystem\":\"Mac OS X:10.6.3\",\"versionInformation\":\"1.0-unknown\",\"username\":\"eijk\"},\"content\":{\"myfile\":[\"sha-1:0000111122223333444455556666777788889999aaaa\"]}}";
    }

    @AfterEach
    public void tearDown() {
    }

    static class LargeArrayClass
    {

        List<Long> set;

        public LargeArrayClass()
        {
            this(0);
        }

        public LargeArrayClass(int n)
        {
            this.set = new ArrayList<Long>(n);
            for (long i = 0; i < n; i++) {
                set.add((int) i, i);
            }
        }

        public List<Long> getSet() {
            return this.set;
        }

        public void setSet(List<Long> set) {
            this.set = set;
        }
    }

    static class LargeArraySerializer extends JsonSerializer<LargeArrayClass>
    {

        @Override
        public void serialize(LargeArrayClass largeArrayClass, JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider) throws IOException, JsonProcessingException
        {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeArrayFieldStart("set");
            for (Long l : largeArrayClass.set) {
                jsonGenerator.writeNumber(l);
            }
            jsonGenerator.writeEndArray();
        }
    }

    static class LargeArrayDeserializer extends JsonDeserializer<LargeArrayClass>
    {

        @Override
        public LargeArrayClass deserialize(JsonParser jp, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException
        {
            LargeArrayClass instance = new LargeArrayClass(10);
            jp.nextToken();
            while (jp.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = jp.currentName();
                List<Long> l = new LinkedList<Long>();
                if ("set".equals(fieldName)) {
                    while (jp.nextToken() != JsonToken.END_ARRAY) {
                        l.add(jp.getLongValue());
                    }
                }
                instance.setSet(l);
            }
            return instance;
        }
    }

    @JsonSerialize(using = LargeArraySerializer.class)
    @JsonDeserialize(using = LargeArrayDeserializer.class)
    static class LargeArrayClassMixIn
    {
    }

    @Test
    public void testLargeArray() throws Exception {
        StringWriter out = new StringWriter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.addMixIn(LargeArrayClass.class, LargeArrayClassMixIn.class);

        LargeArrayClass a = new LargeArrayClass(10);
        objectMapper.writeValue(out, a);

        StringReader reader = new StringReader(out.toString());
        LargeArrayClass b = objectMapper.readValue(reader, LargeArrayClass.class);
        assertThat(b.getSet().size()).isEqualTo(a.getSet().size());
        assertThat(b.getSet()).isEqualTo(a.getSet());
    }

    @Test
    public void testSmallSets() {
        try {
            Set<Integer> mySet = new TreeSet<Integer>();
            for (int i = 0; i < 5; i++) {
                mySet.add(i);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            JsonPersistenceProvider provider = new JsonPersistenceProvider();
            provider.persist(out, mySet);

            Reader input = new StringReader(out.toString(StandardCharsets.UTF_8));
            TypeReference<Set<Integer>> setType = new TypeReference<Set<Integer>>()
            {
            };
            Set<Integer> otherSet = provider.load(input, setType);
            assertThat(otherSet.size()).isEqualTo(mySet.size());
            Iterator<Integer> it1 = mySet.iterator();
            Iterator<Integer> it2 = otherSet.iterator();
            while ((it1.hasNext() && it2.hasNext())) {
                int val1 = it1.next();
                int val2 = it2.next();
                assertThat(val2).isEqualTo(val1);
            }
            assertThat(it1.hasNext()).isFalse();
            assertThat(it2.hasNext()).isFalse();

        } catch (PersistenceException ex) {
            fail("Should not get here");
        }
    }

    @Test
    public void testLargeSets() {
        try {
            Set<Integer> mySet = new TreeSet<Integer>();
            for (Integer i = 0; i < 50000; i++) {
                mySet.add(i);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            JsonPersistenceProvider provider = new JsonPersistenceProvider();
            provider.persist(out, mySet);

            Reader reader = new StringReader(out.toString(StandardCharsets.UTF_8));
            TypeReference<TreeSet<Integer>> setType = new TypeReference<TreeSet<Integer>>()
            {
            };
            Set<Integer> otherSet = provider.load(reader, setType);
            assertThat(otherSet.size()).isEqualTo(mySet.size());
            Iterator<Integer> it1 = mySet.iterator();
            Iterator<Integer> it2 = otherSet.iterator();
            while ((it1.hasNext() && it2.hasNext())) {
                Integer val1 = it1.next();
                Integer val2 = it2.next();
                assertThat(val2).isEqualTo(val1);
            }
            assertThat(it1.hasNext()).isFalse();
            assertThat(it2.hasNext()).isFalse();
        } catch (PersistenceException ex) {
            fail("Should not get here");
        }
    }

    /**
     * Test of persist method, of class JsonPersistenceProvider.
     */
    @Test
    public void testPersist() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DigestResult obj = new DigestResult();
            Digest d = new Digest("sha-1", "0000111122223333444455556666777788889999aaaa");
            obj.add(d);
            JsonPersistenceProvider instance = new JsonPersistenceProvider();
            instance.persist(out, obj);
            String str = out.toString(StandardCharsets.UTF_8);
            assertThat(str).isEqualTo(this.testDigestResultInJson);
        } catch (PersistenceException ex) {
            fail("This should not raise an exception");
        }
    }

    /**
     * Test of load method, of class JsonPersistenceProvider.
     * 
     * @throws PersistenceException
     *             when problems occur with the persistence.
     */
    @Test
    public void testLoad() throws PersistenceException {
        Reader stream = new StringReader(this.testDigestResultInJson);
        Class<DigestResult> clazz = DigestResult.class;
        JsonPersistenceProvider instance = new JsonPersistenceProvider();
        DigestResult result = instance.load(stream, clazz);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.digest().getAlgorithm()).isEqualTo("sha-1");
        assertThat(result.digest().toHex()).isEqualTo("0000111122223333444455556666777788889999aaaa");
    }

    @Test
    public void testPersistDirHasherResult() throws PersistenceException {
        Digest d = new Digest("sha-1", "0000111122223333444455556666777788889999aaaa");
        DigestResult digestResult = new DigestResult();
        digestResult.add(d);
        DirHasherResult obj = new DirHasherResult();
        obj.put("myfile", digestResult);

        JsonPersistenceProvider instance = new JsonPersistenceProvider();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        instance.persist(out, obj);
        String str = out.toString(StandardCharsets.UTF_8);

        Reader reader = new StringReader(str);
        DirHasherResult res = instance.load(reader, DirHasherResult.class);
        assertThat(res).isEqualTo(obj);
    }

    @Test
    public void testLoadDirHasherResult() throws PersistenceException {
        Reader stream = new StringReader(this.testDirHasherResultInJson);
        Class<DirHasherResult> clazz = DirHasherResult.class;
        JsonPersistenceProvider instance = new JsonPersistenceProvider();
        DirHasherResult result = instance.load(stream, clazz);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.containsKey("myfile")).as("Should contain myfile").isTrue();
        DigestResult digestResult = result.get("myfile");
        assertThat(digestResult.digest().getAlgorithm()).isEqualTo("sha-1");
        assertThat(digestResult.digest().toHex()).isEqualTo("0000111122223333444455556666777788889999aaaa");
    }

    @Test
    public void testPersistAndLoadAccessTimes() throws PersistenceException {
        final AccessTimes accessTimes = new AccessTimes(FileTime.from(Instant.parse("2001-02-03T04:05:06Z")),
            FileTime.from(Instant.parse("2001-02-03T04:05:06.123456789Z")));
        final DirHasherResult obj = new DirHasherResult();
        obj.put("myfile", new DigestResult(new Digest("sha-1", "0000111122223333444455556666777788889999aaaa")));
        obj.putAccessTimes(new File("myfile"), accessTimes);
        final JsonPersistenceProvider instance = new JsonPersistenceProvider();
        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        instance.persist(out, obj);
        final String json = out.toString(StandardCharsets.UTF_8);
        final DirHasherResult res = instance.load(new StringReader(json), DirHasherResult.class);

        assertThat(json).contains(
            "\"accessTimes\":{\"myfile\":{\"before\":\"2001-02-03T04:05:06Z\",\"after\":\"2001-02-03T04:05:06.123456789Z\"}}");
        assertThat(res).isEqualTo(obj);
        assertThat(res.getAccessTimes()).containsExactly(Map.entry(new File("myfile"), accessTimes));
    }

    @Test
    public void testLoadRejectsMalformedAccessTimes() {
        final JsonPersistenceProvider instance = new JsonPersistenceProvider();
        final List<String> malformed = List.of(
            "{\"content\":{},\"accessTimes\":[]}",
            "{\"content\":{},\"accessTimes\":{\"myfile\":{\"before\":\"2001-02-03T04:05:06Z\"}}}",
            "{\"content\":{},\"accessTimes\":{\"myfile\":{\"before\":1,\"after\":2}}}",
            "{\"content\":{},\"accessTimes\":{\"myfile\":{\"before\":\"yesterday\",\"after\":\"today\"}}}");

        for (final String json : malformed) {
            assertThatExceptionOfType(PersistenceException.class).as(json)
                .isThrownBy(() -> instance.load(new StringReader(json), DirHasherResult.class));
        }
    }
}
