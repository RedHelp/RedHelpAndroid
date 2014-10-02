package org.redhelp.network;

import android.util.Log;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.spi.service.ServiceFinder;

import org.redhelp.util.ProfileHelper;

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

        if(ProfileHelper.profileType.equals(ProfileHelper.ProfileType.TEST)) {
            //Test server
            return UriBuilder.fromUri("http://redhelp.redhelp.cloudbees.net/").build();
        } else if(ProfileHelper.profileType.equals(ProfileHelper.ProfileType.PROD)) {
            //Prod server
            return UriBuilder.fromUri("http://default-environment-dw9xerpppq.elasticbeanstalk.com//").build();
        }

        return null;
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

        long networkStartProfiler = System.nanoTime();

        WebResource service = client.resource(getBaseURI());
        ClientResponse response = service.path("rest").path(path)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, json_request);

        long networkElapsedTime = System.nanoTime() - networkStartProfiler;
        double networkElapsedTimeSec = (double)networkElapsedTime/1000000000.0;
        // Log.i(TAG, "Network elapsed time:" +networkElapsedTimeSec);

        long parsingStartProfiler = System.nanoTime();
        String json_response_string = null;
        try {
            json_response_string = response.getEntity(String.class);
        }catch (Exception e) {
            Log.e(TAG, "Exception while deserializing :"+e.toString());
        }

        long parsingElapsedTime = System.nanoTime() - parsingStartProfiler;
        double parsingElapsedTimeSec = (double)parsingElapsedTime/1000000000.0;
        // Log.i(TAG, "Parsing elapsed time:" + parsingElapsedTimeSec);

        //msg = String.format("Json Response: %s", json_response_string);
        Log.e(TAG, msg);
        return json_response_string;
    }

    public static String getCall(String path)
    {
        String msg = String.format("getCall, and path: (%s)", path);
        Log.e(TAG, msg);

        long networkStartProfiler = System.nanoTime();
        WebResource service = client.resource(getBaseURI());
        ClientResponse response = service.path("rest").path(path).
                accept(MediaType.APPLICATION_JSON_TYPE).
                get(ClientResponse.class);

        long networkElapsedTime = System.nanoTime() - networkStartProfiler;
        double networkElapsedTimeSec = (double)networkElapsedTime/1000000000.0;
        // Log.i(TAG, "Network elapsed time:" +networkElapsedTimeSec);


        long parsingStartProfiler = System.nanoTime();
        String json_response_string = null;
        try {
            json_response_string = response.getEntity(String.class);
        }catch (Exception e) {
            Log.e(TAG, "Exception while getting string :" + e.toString());
        }

        long parsingElapsedTime = System.nanoTime() - parsingStartProfiler;
        double parsingElapsedTimeSec = (double)parsingElapsedTime/1000000000.0;
        // Log.i(TAG, "Parsing elapsed time:" + parsingElapsedTimeSec);

        // msg = String.format("Json Response: %s", json_response_string);
        Log.e(TAG, msg);
        return json_response_string;

    }

}
