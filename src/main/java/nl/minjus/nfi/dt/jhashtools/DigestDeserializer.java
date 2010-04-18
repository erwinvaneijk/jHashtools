/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.minjus.nfi.dt.jhashtools;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

/**
 *
 * @author kojak
 */
public class DigestDeserializer implements JsonDeserializer<Digest> {

    @Override
    public Digest deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        return new Digest(je.getAsJsonPrimitive().getAsString());
    }

}
