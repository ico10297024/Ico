package ico.ico.network;

import retrofit2.Response;

/**
 * Created by ICO on 2017/1/9 0009.
 */
public class HttpResult {
    private Response<byte[]> response;
    private Exception exception;

    public HttpResult(Response<byte[]> response, Exception exception) {
        this.response = response;
        this.exception = exception;
    }

    public Response<byte[]> getResponse() {
        return response;
    }

    public HttpResult setResponse(Response<byte[]> response) {
        this.response = response;
        return this;
    }

    public Exception getException() {
        return exception;
    }

    public HttpResult setException(Exception exception) {
        this.exception = exception;
        return this;
    }
}
