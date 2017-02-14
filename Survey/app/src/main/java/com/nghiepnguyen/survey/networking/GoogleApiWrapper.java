package com.nghiepnguyen.survey.networking;

import android.content.Context;
import android.util.Log;

import com.nghiepnguyen.survey.Interface.ICallBack;
import com.nghiepnguyen.survey.R;
import com.nghiepnguyen.survey.model.CommonErrorModel;
import com.nghiepnguyen.survey.model.GoogleAPI.GeoLocation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by W10-PRO on 14-Feb-17.
 */

public class GoogleApiWrapper {
    private static final String TAG = "GoogleApiWrapper";
    private static final String MAP_GEOCODE_ADDRESS_SEARCH = "https://maps.googleapis.com/maps/api/geocode/json?";

    /**
     * Geocoding get address from latlng
     * Api key Premium account
     *
     * @param context
     * @param location
     * @param onGeoCallBack
     */
    public static void getGeocodeAddressTextSearch(Context context, String location, final ICallBack onGeoCallBack) {
        StringBuilder sbUrl = new StringBuilder(MAP_GEOCODE_ADDRESS_SEARCH);
        try {
            sbUrl.append("key=").append(context.getResources().getString(R.string.google_api_browser_key));
            sbUrl.append("&latlng=").append(location);
        } catch (Exception e) {
            e.printStackTrace();
        }

        new DataAsyncTask(context, new ICallBack() {

            @Override
            public void onCompleted() {
                onGeoCallBack.onCompleted();
            }

            @Override
            public void onSuccess(Object data) {
                List<GeoLocation> geoLocationList = new ArrayList<>();
                try {
                    JSONObject jsonResult = new JSONObject(data.toString());
                    JSONArray jsonArray = jsonResult.getJSONArray("results");
                    int resultsArrayLength = jsonArray.length();
                    if (resultsArrayLength > 0) {
                        // Always use the first address
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String address = jsonObject.getString("formatted_address");
                        JSONObject jsonLocation = jsonObject.getJSONObject("geometry").getJSONObject("location");

                        GeoLocation newGeoLocation = new GeoLocation();
                        newGeoLocation.setLat(jsonLocation.getString("lat"));
                        newGeoLocation.setLng(jsonLocation.getString("lng"));
                        newGeoLocation.setPrimaryText(address);
                        newGeoLocation.setSecondaryText("");
                        newGeoLocation.setPlace_id(jsonObject.getString("place_id"));

                        JSONArray jsonAddressComponentArray = jsonObject.getJSONArray("address_components");
                        for (int j = 0; j < jsonAddressComponentArray.length(); j++) {
                            JSONArray jsonTypeArray = jsonAddressComponentArray.getJSONObject(j).getJSONArray("types");
                            for (int k = 0; k < jsonTypeArray.length(); k++) {
                                String type = jsonTypeArray.getString(k);
                                if (type.equals("country")) {
                                    String countryCode = jsonAddressComponentArray.getJSONObject(j).getString("short_name");
                                    newGeoLocation.setCountry_code(countryCode);
                                }
                            }
                        }

                        geoLocationList.add(newGeoLocation);
                        onGeoCallBack.onSuccess(geoLocationList);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onGeoCallBack.onFailure(new CommonErrorModel(0, e.getMessage()));
                }

            }

            @Override
            public void onFailure(CommonErrorModel error) {
                Log.d(TAG, "text search has failure");
                onGeoCallBack.onFailure(error);
            }
        }).execute(sbUrl.toString());
    }
}
