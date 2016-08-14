package com.gimelgimel.data.access.http;

import com.gimelgimel.domain.logging.Logger;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import adapters.MessageJsonAdapter;
import adapters.MessageListJsonAdapter;
import config.Constants;
import httpModels.Message;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestAPI {

    Logger mLogger;
    GGMessagingAPI mMessagingAPI;

    public RestAPI(Logger logger) {
        mLogger = logger;

        initializeAPIs();
    }

    private void initializeAPIs() {
        initializeMessagingAPI();
    }

    private void initializeMessagingAPI() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message -> mLogger.v(message));

        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        Gson gson = createMessagingGson();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.MESSAGING_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        mMessagingAPI = retrofit.create(GGMessagingAPI.class);
    }

    private Gson createMessagingGson() {
        // The following code creates a new Gson instance that will convert all fields from lower
        // case with underscores to camel case and vice versa. It also registers a type adapter for
        // the Message class. This DateTypeAdapter will be used anytime Gson encounters a Date field.
        // The gson instance is passed as a parameter to GsonConverter, which is a wrapper
        // class for converting types.
        MessageJsonAdapter messageJsonAdapter = new MessageJsonAdapter();
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Message.class, messageJsonAdapter)
                .registerTypeAdapter(List.class,
                        new MessageListJsonAdapter(messageJsonAdapter, mLogger))
                .create();
    }


    public GGMessagingAPI getMessagingAPI() {
        return mMessagingAPI;
    }
}