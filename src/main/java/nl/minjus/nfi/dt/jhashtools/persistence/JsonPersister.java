/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.minjus.nfi.dt.jhashtools.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import nl.minjus.nfi.dt.jhashtools.Digest;
import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;
import nl.minjus.nfi.dt.jhashtools.exceptions.PersistenceException;

import java.io.*;
import java.lang.reflect.Type;

/**
 *
 * @author eijk
 */
public class JsonPersister implements Persist {

    private GsonBuilder gsonBuilder;

    public JsonPersister() {
        // pass
        gsonBuilder = new GsonBuilder();
        this.gsonBuilder.setPrettyPrinting();
        Type digestType=new TypeToken<Digest>() {}.getType();
        this.gsonBuilder.registerTypeAdapter(digestType, new DigestSerializer());
        Type fullDigestList=new TypeToken<DirHasherResult>() {}.getType();
        Type digestResultType=new TypeToken<DigestResult>() {}.getType();
        this.gsonBuilder.registerTypeAdapter(digestResultType, new DigestResultSerializer());
        this.gsonBuilder.registerTypeAdapter(fullDigestList, new DirHasherResultSerializer());
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
        return gson.fromJson(reader, clazz);
    }
}
