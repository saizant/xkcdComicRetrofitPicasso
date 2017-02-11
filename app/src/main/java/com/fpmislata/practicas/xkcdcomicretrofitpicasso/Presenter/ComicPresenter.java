package com.fpmislata.practicas.xkcdcomicretrofitpicasso.Presenter;

import android.content.Context;

import com.fpmislata.practicas.xkcdcomicretrofitpicasso.Model.ComicModel;
import com.fpmislata.practicas.xkcdcomicretrofitpicasso.Service.ComicService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Creado por Antonio Sáiz. Fecha: 03/02/2017.
 */

public class ComicPresenter {

    private final Context context;
    private final ComicPresenterListener mListener;
    private final ComicService comicService;

    //CONSTRUCTOR (recibe parámetros interfaz y contexto, e inicializa el objeto ComicService):
    public ComicPresenter(ComicPresenterListener comicPresenterListener, Context context) {
        this.context = context;
        this.mListener = comicPresenterListener;
        this.comicService = new ComicService();
    }

    //INTERFAZ: contiene el método comicReady (invocado aquí dentro del getComic() a través del objeto ComicService).
    //Se le pasa la instancia del ComicModel (y el boolean)
    public interface ComicPresenterListener {
        void comicReady(ComicModel comicModel, Boolean aMemoria);
    }

    //MÉTODO para obtener el Comic (llamado en el Main al arrancar la Activity o pinchar el ImageView)
    public void getComic(int numero, final Boolean aMemoria) {

        String numComic = "";
        //Concatenar para la URL con el número recibido para descargar
        if(numero != 0) {
            numComic = "" + numero + "/";
        }

        //Usando la API a través del método del ComicService para cargar el Comic
        comicService.getAPI()
                .loadComic(numComic)
                .enqueue(new Callback<ComicModel>() {
                    @Override
                    public void onResponse(Call<ComicModel> call, Response<ComicModel> response) {
                        ComicModel resultado = response.body();
                        if (resultado != null) {
                            mListener.comicReady(resultado, aMemoria);
                        }
                    }

                    @Override
                    public void onFailure(Call<ComicModel> call, Throwable t) {
                        mListener.comicReady(null, aMemoria);
                    }
                });
    }
}
