/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.minjus.nfi.dt.jhashtools.persistence;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import nl.minjus.nfi.dt.jhashtools.Digest;

/**
 *
 * @author kojak
 */
public class DigestSerializer implements JsonSerializer<Digest>, JsonDeserializer<Digest> {

    @Override
    public JsonElement serialize(Digest t, Type type, JsonSerializationContext jsc) {
        JsonArray a = new JsonArray();
        a.add(new JsonPrimitive(t.getAlgorithm()));
        a.add(new JsonPrimitive(t.toHex()));
        return a;
    }

    @Override
    public Digest deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        JsonArray a = je.getAsJsonArray();
        return new Digest(a.get(0).getAsString(), a.get(1).getAsString());
    }
}
