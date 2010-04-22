/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.minjus.nfi.dt.jhashtools.persistence;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;
import nl.minjus.nfi.dt.jhashtools.ConstructionInfo;
import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;

/**
 *
 * @author eijk
 */
public class DirHasherResultSerializer 
        implements JsonDeserializer<DirHasherResult>,
        JsonSerializer<DirHasherResult>,
        InstanceCreator<DirHasherResult>
{
    @Override
    public JsonElement serialize(DirHasherResult src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject map = new JsonObject();
        JsonElement constructionInfo = context.serialize(src.getConstructionInfo());
        map.add("constructionInfo", constructionInfo);
        for (Map.Entry<String, DigestResult> entry: src.entrySet()) {
            JsonElement valueElement = context.serialize(entry.getValue(), DigestResult.class);
            map.add(entry.getKey(), valueElement);
        }
        return map;
    }


    @Override
    public DirHasherResult deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        DirHasherResult dirHasherResult = new DirHasherResult();
        JsonObject object = json.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry: object.entrySet()) {
            JsonElement e = entry.getValue();
            if (entry.getKey().equals("constructionInfo")) {
                Type constructionInfoType = new TypeToken<ConstructionInfo>() {}.getType();
                ConstructionInfo info = context.deserialize(e, constructionInfoType);
                dirHasherResult.setConstructionInfo(info);
            } else {
                Type digestResultType = new TypeToken<DigestResult>() {}.getType();
                DigestResult result = context.deserialize(e, digestResultType);
                String key = entry.getKey();
                dirHasherResult.put(key, result);
            }
        }
        return dirHasherResult;
    }

    @Override
    public DirHasherResult createInstance(Type type) {
        return new DirHasherResult();
    }

}
