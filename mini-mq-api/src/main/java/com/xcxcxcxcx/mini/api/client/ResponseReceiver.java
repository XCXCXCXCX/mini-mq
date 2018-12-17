package com.xcxcxcxcx.mini.api.client;


/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface ResponseReceiver {

    void receive(Response response);

    class Response<T>{
        private int id;
        private int responseType;
        private T t;

        public int getId() {
            return id;
        }

        public Response<T> setId(int id) {
            this.id = id;
            return this;
        }

        public int getResponseType() {
            return responseType;
        }

        public Response<T> setResponseType(int responseType) {
            this.responseType = responseType;
            return this;
        }

        public T get() {
            return t;
        }

        public Response<T> set(T t) {
            this.t = t;
            return this;
        }
    }

    enum ResponseEnum{
        PUSH_RESPONSE(0),
        PUSH_ACK_RESPONSE(1),
        PULL_RESPONSE(2),
        PULL_ACK_RESPONSE(3);

        private int code;

        ResponseEnum(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
}
