package org.redhelp.network;

import android.util.Log;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.spi.service.ServiceFinder;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

/*

  Created by harshis on 5/19/14.

*/

public  class RestClientCall {

    private static final String TAG = "RestClientCall";
    private static ClientConfig config;
    private static Client client;

    private static URI getBaseURI() {
        return UriBuilder.fromUri("http://redhelp.redhelp.cloudbees.net/").build();
        //return UriBuilder.fromUri("http://10.0.2.2:8080/RedHelpServer").build();

    }

    static{
        ServiceFinder.setIteratorProvider(new AndroidServiceIteratorProvider());
        config = new DefaultClientConfig();
        client = Client.create(config);
    }

    public static String postCall(String path, String json_request)
    {
        String msg = String.format("Json Request: %s,  and path: (%s)", json_request, path);
        Log.e(TAG, msg);

        WebResource service = client.resource(getBaseURI());
        ClientResponse response = service.path("rest").path(path)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, json_request);
        Log.e(TAG,response.toString());
        String json_response_string = null;
        try {
            json_response_string = response.getEntity(String.class);
        }catch (Exception e) {
            Log.e(TAG, "Exception while deserializing :"+e.toString());
        }

        msg = String.format("Json Response: %s", json_response_string);
        Log.e(TAG, msg);
        return json_response_string;
    }

    public static String getCall(String path)
    {
        String msg = String.format("getCall, and path: (%s)", path);
        Log.e(TAG, msg);

        WebResource service = client.resource(getBaseURI());
        ClientResponse response = service.path("rest").path(path).
                accept(MediaType.APPLICATION_JSON_TYPE).
                get(ClientResponse.class);

        Log.e(TAG,response.toString());
        String json_response_string = null;
        try {
            json_response_string = response.getEntity(String.class);
        }catch (Exception e) {
            Log.e(TAG, "Exception while getting string :" + e.toString());
        }

        msg = String.format("Json Response: %s", json_response_string);
        Log.e(TAG, msg);
        return json_response_string;

    }

}
