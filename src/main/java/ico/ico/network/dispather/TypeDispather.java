package ico.ico.network.dispather;

import android.content.Context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import ico.ico.network.IHttpCallback;

/**
 * Created by ICO on 2017/3/21 0021.
 */

public abstract class TypeDispather<T> extends SimpleDispather<T> {

    TypeReference mType;

    public TypeDispather(Context context, IHttpCallback callback, TypeReference type) {
        super(context, callback);
        this.mType = type;
    }

    @Override
    public T onDataHandle(byte[] data) throws IOException {
        return (T) new ObjectMapper().readValue(data, mType);
    }

    @Override
    public abstract boolean onCallback(int statusCode, T t);
}
