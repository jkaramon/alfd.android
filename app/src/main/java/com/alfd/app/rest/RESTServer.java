package com.alfd.app.rest;

import android.app.Application;

import com.alfd.app.AlfdApplication;
import com.alfd.app.utils.Settings;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.retrofit.RetrofitObjectPersisterFactory;
import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;
import com.octo.android.robospice.retrofit.RetrofitSpiceService;

import org.joda.time.DateTime;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Date;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;

public class RESTServer extends RetrofitSpiceService {





    RequestInterceptor requestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestInterceptor.RequestFacade request) {
            request.addHeader("x-g-token", Settings.getAccessToken());
            request.addHeader("Accept",  "application/json");
        }
    };

    public RestAdapter getAdapter() {
        return createRestAdapterBuilder().build();
    }

    @Override
    protected RestAdapter.Builder createRestAdapterBuilder() {
        return new RestAdapter.Builder()
                .setEndpoint(getServerUrl())
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .setConverter(getConverter())
                .setRequestInterceptor(requestInterceptor);
    }



    @Override
    public void onCreate() {
        super.onCreate();
        addRetrofitInterface(ProductNameEndpoints.class);
    }



    @Override
    protected String getServerUrl() {
        return Settings.getServerUrl();
    }

















    @Override
    public CacheManager createCacheManager(Application application) throws CacheCreationException {
        CacheManager cacheManager = new CacheManager();
        cacheManager.addPersister(new RetrofitObjectPersisterFactory(application, getConverter(), getCacheFolder()));
        return cacheManager;
    }

    @Override
    protected Converter createConverter() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter())
                .create();
        return  new GsonConverter(gson);
    }

    public File getCacheFolder() {
        return null;
    }




    private static class DateTimeTypeConverter
            implements JsonSerializer<DateTime>, JsonDeserializer<DateTime> {
        @Override
        public JsonElement serialize(DateTime src, Type srcType, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public DateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                return new DateTime(json.getAsString());
            } catch (IllegalArgumentException e) {
                // May be it came in formatted as a java.util.Date, so try that
                Date date = context.deserialize(json, Date.class);
                return new DateTime(date);
            }
        }
    }





}
