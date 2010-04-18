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
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import nl.minjus.nfi.dt.jhashtools.Digest;
import nl.minjus.nfi.dt.jhashtools.DigestResult;

/**
 *
 * @author eijk
 */
class DigestResultSerializer
        implements JsonSerializer<DigestResult>, JsonDeserializer<DigestResult>
{

    public DigestResultSerializer() {
    }

    @Override
    public JsonElement serialize(DigestResult src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray arr =  new JsonArray();
        for (Digest d : src) {
            JsonElement valueElement = context.serialize(d, d.getClass());
            arr.add(valueElement);
        }
        return arr;
    }

    @Override
    public DigestResult deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        DigestResult digestResult = new DigestResult();
        JsonArray array = json.getAsJsonArray();
        for (int i=0; i<array.size(); ++i) {
            JsonElement a = array.get(i);
            Type digestType = new TypeToken<Digest>() {}.getType();
            Digest digest = context.deserialize(a, digestType);
            digestResult.add(digest);
        }
        return digestResult;
    }
}
