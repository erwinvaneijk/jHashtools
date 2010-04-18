/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.minjus.nfi.dt.jhashtools;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

/**
 *
 * @author kojak
 */
public class DigestSerializer implements JsonSerializer<Digest> {

    @Override
    public JsonElement serialize(Digest t, Type type, JsonSerializationContext jsc) {
        return new JsonPrimitive(t.toHex());
    }
}
