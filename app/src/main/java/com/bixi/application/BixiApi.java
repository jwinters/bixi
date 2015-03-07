package com.bixi.application;

import android.util.Log;

import com.bixi.models.StationListResponse;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

public class BixiApi {

	private static final String BASE_URL = "http://www.bikesharetoronto.com/";

	private static final class Endpoints {
		public static final String STATIONS = BASE_URL + "stations/json";
	}

	public static StationListResponse getStationList() throws ClientProtocolException, IOException {
		final HttpGet request = new HttpGet(Endpoints.STATIONS);
		return execute(request, new Gson(), StationListResponse.class);
	}

	private static <T> T execute(final HttpUriRequest request, final Gson gson, final Type type) throws ClientProtocolException, IOException {
		Log.v("Network", "Request : " + request.getURI());
		final HttpClient client = new DefaultHttpClient();
		final HttpResponse response = client.execute(request);
		Log.v("Network", "Response : " + response.getStatusLine());
		final InputStream inputStream = response.getEntity().getContent();
		final InputStreamReader inputReader = new InputStreamReader(inputStream);
		final JsonReader jsonReader = new JsonReader(inputReader);
		final T list = gson.fromJson(jsonReader, type);
		jsonReader.close();
		return list;
	}
}