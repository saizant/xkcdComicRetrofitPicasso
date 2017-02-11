package com.fpmislata.practicas.xkcdcomicretrofitpicasso.Service;

import com.fpmislata.practicas.xkcdcomicretrofitpicasso.Model.ComicModel;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Creado por Antonio Sáiz. Fecha: 03/02/2017.
 */

public class ComicService {

    private final String BASE_URL = "https://xkcd.com/";

    //INTERFAZ: contiene el método loadComic (invocado en ComicPresenter dentro del getComic() a través del objeto ComicService)
    //que devuelve el Callback<ComicModel> para actuar dependiendo del onResponse() o el onFailure()...
    //Se le pasa el número de Comic a cargar
    public interface ComicAPI {
        @GET("{numero}info.0.json")
        Call<ComicModel> loadComic(@Path("numero") String numero);
    }

    //Método para obtener la API (llamado en ComicPresenter dentro del getComic() a través del objeto ComicService)
    public ComicAPI getAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(ComicAPI.class);
    }
}
