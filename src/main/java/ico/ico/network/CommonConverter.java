package ico.ico.network;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by ICO on 2017/1/6 0006.
 */
public class CommonConverter implements Converter<ResponseBody, byte[]> {

    public static Factory FACTORY = new Factory() {
        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            return new CommonConverter();
        }
    };

    @Override
    public byte[] convert(ResponseBody value) throws IOException {
        return value.bytes();
    }
}