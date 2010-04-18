/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.minjus.nfi.dt.jhashtools.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.minjus.nfi.dt.jhashtools.Digest;
import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;

/**
 *
 * @author eijk
 */
public class JsonPersister implements Persist {

    private GsonBuilder gsonBuilder;
    private final Type digestType;
    private final Type fullDigestList;
    private final Type digestResultType;

    public JsonPersister() {
        // pass
        gsonBuilder = new GsonBuilder();
        this.gsonBuilder.setPrettyPrinting();
        this.digestType = new TypeToken<Digest>() {}.getType();
        this.gsonBuilder.registerTypeAdapter(digestType, new DigestSerializer());
        this.fullDigestList = new TypeToken<DirHasherResult>() {}.getType();
        this.digestResultType = new TypeToken<DigestResult>() {}.getType();
        this.gsonBuilder.registerTypeAdapter(this.digestResultType, new DigestResultSerializer());
        this.gsonBuilder.registerTypeAdapter(this.fullDigestList, new DirHasherResultSerializer());
    }

    @Override
    public void persist(OutputStream out, Object obj) throws PersistenceException {
        try {
            Gson gson = this.gsonBuilder.create();
            String result = gson.toJson(obj);
            out.write(result.getBytes());
        } catch (IOException ex) {
            throw new PersistenceException(ex);
        }
    }

    @Override
    public Object load(InputStream stream, Class clazz) {
        Gson gson = this.gsonBuilder.create();
        Reader reader = new InputStreamReader(stream);
        Object obj = gson.fromJson(reader, clazz);
        return obj;
    }
}
