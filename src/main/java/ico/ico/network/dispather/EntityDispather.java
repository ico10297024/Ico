package ico.ico.network.dispather;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;

import java.io.IOException;

import ico.ico.network.IHttpCallback;

/**
 * Created by ICO on 2017/3/21 0021.
 */

public abstract class EntityDispather<T> extends SimpleDispather<T> {

    Class mEntityClass;

    public EntityDispather(Context context, IHttpCallback callback, Class entityClass) {
        super(context, callback);
        this.mEntityClass = entityClass;
    }

    @Override
    public T onDataHandle(byte[] data) throws IOException, JSONException {
        return (T) new ObjectMapper().readValue(data, mEntityClass);
    }

    @Override
    public abstract boolean onCallback(int statusCode, T t);
}
