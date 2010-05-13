/*
 * Copyright (c) 2010. Erwin van Eijk <erwin.vaneijk@gmail.com>
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
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.minjus.nfi.dt.jhashtools.persistence;

import nl.minjus.nfi.dt.jhashtools.Digest;
import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;
import nl.minjus.nfi.dt.jhashtools.exceptions.PersistenceException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;
import org.junit.*;

import java.io.*;
import java.util.*;

import static org.junit.Assert.*;

/**
 * @author eijk
 */
public class JsonPersisterTest {

    @SuppressWarnings({"FieldCanBeLocal"})
    private String testDigestInJson;
    private String testDigestResultInJson;
    private String testDirHasherResultInJson;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        this.testDigestInJson = "\"sha-1:0000111122223333444455556666777788889999aaaa\"";
        this.testDigestResultInJson = "[\"sha-1:0000111122223333444455556666777788889999aaaa\"]";
        this.testDirHasherResultInJson = "{\"constructionInfo\":{\"constructionDate\":1273412558116,\"operatingSystem\":\"Mac OS X:10.6.3\",\"versionInformation\":\"1.0-unknown\",\"username\":\"eijk\"},\"content\":{\"myfile\":[\"sha-1:0000111122223333444455556666777788889999aaaa\"]}}";
    }

    @After
    public void tearDown() {
    }

    static class LargeArrayClass {

        List<Long> set;

        public LargeArrayClass() {
            this(0);
        }
        
        public LargeArrayClass(int n) {
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

    static class LargeArraySerializer extends JsonSerializer<LargeArrayClass> {

        @Override
        public void serialize(LargeArrayClass largeArrayClass, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeArrayFieldStart("set");
            for (Long l : largeArrayClass.set) {
                jsonGenerator.writeNumber(l);
            }
            jsonGenerator.writeEndArray();
        }
    }

    static class LargeArrayDeserializer extends JsonDeserializer<LargeArrayClass> {

        @Override
        public LargeArrayClass deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            LargeArrayClass instance = new LargeArrayClass(10);
            jp.nextToken();
            while (jp.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = jp.getCurrentName();
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
    class LargeArrayClassMixIn {
    }

    @Test
    public void testLargeArray() throws Exception {
        StringWriter out = new StringWriter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.getSerializationConfig().addMixInAnnotations(LargeArrayClass.class, LargeArrayClassMixIn.class);

        LargeArrayClass a = new LargeArrayClass(10);
        objectMapper.writeValue(out, a);

        StringReader reader = new StringReader(out.toString());
        LargeArrayClass b = objectMapper.readValue(reader, LargeArrayClass.class);
        assertEquals(a.getSet().size(), b.getSet().size());
        assertEquals(a.getSet(), b.getSet());
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

            Reader input = new StringReader(out.toString());
            TypeReference<Set<Integer>> setType = new TypeReference<Set<Integer>>() {};
            Set<Integer> otherSet = provider.load(input, setType);
            assertEquals(mySet.size(), otherSet.size());
            Iterator<Integer> it1 = mySet.iterator();
            Iterator<Integer> it2 = otherSet.iterator();
            while ((it1.hasNext() && it2.hasNext())) {
                int val1 = it1.next();
                int val2 = it2.next();
                assertEquals(val1, val2);
            }
            assertTrue(!it1.hasNext());
            assertTrue(!it2.hasNext());

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

            Reader reader = new StringReader(out.toString());
            TypeReference<TreeSet<Integer>> setType = new TypeReference<TreeSet<Integer>>() {};
            Set<Integer> otherSet = provider.load(reader, setType);
            assertEquals(mySet.size(), otherSet.size());
            Iterator<Integer> it1 = mySet.iterator();
            Iterator<Integer> it2 = otherSet.iterator();
            while ((it1.hasNext() && it2.hasNext())) {
                Integer val1 = it1.next();
                Integer val2 = it2.next();
                assertEquals(val1, val2);
            }
            assertTrue(!it1.hasNext());
            assertTrue(!it2.hasNext());
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
            String str = out.toString();
            assertEquals(this.testDigestResultInJson, str);
        } catch (PersistenceException ex) {
            fail("This should not raise an exception");
        }
    }

    /**
     * Test of load method, of class JsonPersistenceProvider.
     *
     * @throws PersistenceException when problems occur with the persistence.
     */
    @Test
    public void testLoad() throws PersistenceException {
        Reader stream = new StringReader(this.testDigestResultInJson);
        Class<DigestResult> clazz = DigestResult.class;
        JsonPersistenceProvider instance = new JsonPersistenceProvider();
        DigestResult result = instance.load(stream, clazz);
        assertEquals(1, result.size());
        assertEquals("sha-1", result.digest().getAlgorithm());
        assertEquals("0000111122223333444455556666777788889999aaaa", result.digest().toHex());
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
        String str = out.toString();

        Reader reader = new StringReader(str);
        DirHasherResult res = (DirHasherResult) instance.load(reader, DirHasherResult.class);
        assertEquals(obj, res);
    }

    @Test
    public void testLoadDirHasherResult() throws PersistenceException {
        Reader stream = new StringReader(this.testDirHasherResultInJson);
        Class<DirHasherResult> clazz = DirHasherResult.class;
        JsonPersistenceProvider instance = new JsonPersistenceProvider();
        DirHasherResult result = (DirHasherResult) instance.load(stream, clazz);
        assertEquals(1, result.size());
        assertTrue("Should contain myfile", result.containsKey("myfile"));
        DigestResult digestResult = result.get("myfile");
        assertEquals("sha-1", digestResult.digest().getAlgorithm());
        assertEquals("0000111122223333444455556666777788889999aaaa", digestResult.digest().toHex());
    }
}
