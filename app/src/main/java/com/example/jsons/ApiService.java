package com.example.jsons;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    /*
    Retrofit get annotation with our URL
    And our method that will return us the List of ContactList
    */
    @POST("graphql")
    Call<MetaData> getStringScalar(@Body Qrcode body);
   // Call<MetaData>getAnswers();
}
