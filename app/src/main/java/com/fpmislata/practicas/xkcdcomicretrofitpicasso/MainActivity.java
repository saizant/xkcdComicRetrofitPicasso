package com.fpmislata.practicas.xkcdcomicretrofitpicasso;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.fpmislata.practicas.xkcdcomicretrofitpicasso.Model.ComicModel;
import com.fpmislata.practicas.xkcdcomicretrofitpicasso.Presenter.ComicPresenter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.Random;

import jp.wasabeef.picasso.transformations.ColorFilterTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import jp.wasabeef.picasso.transformations.gpu.VignetteFilterTransformation;

public class MainActivity extends AppCompatActivity implements ComicPresenter.ComicPresenterListener {

    //Vistas del layout
    private Button carruselBtn;
    private ImageView imagen;

    //Objeto ComicPresenter para acceder al getComic() (contiene la interfaz Listener que implementa el método comicReady())
    private ComicPresenter comicPresenter;

    private int topComic = 0;
    private String urlString ="";
    private Boolean carruselActivado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Enganchar vistas del layout
        carruselBtn = (Button)findViewById(R.id.carruselBtn);
        imagen = (ImageView) findViewById(R.id.imagen);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);      //PANTALLA VERTICAL

        //Inicializar el ComicPresenter y llamar a su método que obtiene el Comic al arrancar la APP
        comicPresenter = new ComicPresenter(this, this);
        comicPresenter.getComic(topComic, false);
    }

    //Método implementado de la interfaz Listener del ComicPresenter
    @Override
    public void comicReady(ComicModel comicModel, Boolean aMemoria) {
        //Si va a memoria y el ComicModel no es null...
        if (aMemoria == true && comicModel != null) {
            //Usamos librería Picasso para cachear la imagen (y obtenemos su URL)
            Picasso.with(this).load(comicModel.getImg()).fetch();
            urlString = comicModel.getImg();

            if (carruselActivado) {
                //Si el carrusel se ha activado con el botón, con Handler+Runnable llamamos al método
                //que adapta la imagen a la pantalla mediante CustomTransform de Picasso
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        adaptarImagen(urlString);       //Pasamos la URL obtenida
                    }
                };
                handler.postDelayed(runnable, 7000);    //Carrusel cada 7 segundos
            }
        } else if (aMemoria == false) {
            //Si no va a memoria... (comprobamos si tenemos el ComicModel o hay ERROR)
            if (comicModel != null) {
                //Si tenemos el ComicModel cargamos la imagen
                if (topComic == 0) {
                    topComic = Integer.parseInt(comicModel.getNum());
                    adaptarImagen(comicModel.getImg());
                }
            } else {
                //Sino mostramos el ERROR
                Picasso.with(this).load(R.drawable.error).into(imagen);
                Toast.makeText(this, "Error al conectar con el servidor", Toast.LENGTH_LONG).show();
            }
        }
    }

    //Método para adaptar la imagen del Comic a la pantalla usando CustomTransform de Picasso
    private void adaptarImagen(String url) {
        Transformation transformation = new Transformation() {
            @Override
            public Bitmap transform(Bitmap source) {
                Matrix matrix = new Matrix();
                if (source.getHeight() < source.getWidth()) {
                    //Si la imagen es horizontal se gira para abarcar la pantalla
                    matrix.postRotate(90);
                }
                Bitmap resultado = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

                if (resultado != source) {
                    source.recycle();
                }
                return resultado;
            }

            @Override
            public String key() {
                return "transformation" + " desiredWidth";
            }
        };

        Picasso.with(this)
                .load(url)
                .error(R.drawable.error)    //Imagen cuando no se ha podido cargar
                .placeholder(R.drawable.animacion_progreso)      //Placeholder como ProgressBar (imagen animada en rotación)
                .transform(transformation)      //Ajuste con rotación
                .transform(new ColorFilterTransformation(Color.parseColor("#15BC5732")))      //Filtro color
                .transform(new VignetteFilterTransformation(this, new PointF(0.5f, 0.5f), new float[] { 0.1f, 0.2f }, 0.0f, 0.95f))      //Efecto viñeta sombreado ligero VERDOSO
                //.transform(new VignetteFilterTransformation(this, new PointF(0.5f, 0.5f), new float[] { 0.1f }, 0.0f, 0.95f))      //Efecto viñeta sombreado ligero ROJIZO
                //.transform(new VignetteFilterTransformation(this, new PointF(0.5f, 0.5f), new float[] { 0.1f, 0.2f, 0.3f }, 0.0f, 0.95f))      //Efecto viñeta sombreado ligero AZULADO
                //.transform(new BlurTransformation(this,1))    //Filtro pequeño desenfoque
                .transform(new RoundedCornersTransformation(10, 2))     //Esquinas redondeadas
                .into(imagen);

        Picasso.with(this).invalidate(url);
        urlString="";
        mostrarImagen();
    }

    private void mostrarImagen() {
        //Si NO tenemos URL se obtiene una imagen aleatoria en memoria
        if (urlString.equals("")) {
            Random random = new Random();
            int aleatorio = random.nextInt(topComic + 1);
            //int aleatorio = random.nextInt(topComic) + 1;
            comicPresenter.getComic(aleatorio, true);
        } else {
            //Si tenemos URL se carga la imagen de la memoria
            adaptarImagen(urlString);
        }
    }

    //OnClick del BOTÓN CARRUSEL: (cambia su texto según la situación y arranca o para el carrusel de Comics)
    public void carrusel(View view) {
        if (carruselBtn.getText().equals("MERRY-GO-ROUND")) {
            carruselBtn.setText("STOP");
            carruselBtn.setBackgroundColor(Color.parseColor("#ff0000"));
            carruselActivado = true;
            mostrarImagen();
        } else {
            carruselBtn.setText("MERRY-GO-ROUND");
            carruselBtn.setBackgroundColor(Color.parseColor("#33cc33"));
            carruselActivado = false;
        }
    }

    //OnClick de la IMAGEN: (carga nuevo Comic)
    public void nuevaImagen(View view) {
        if (carruselActivado) {
            carrusel(null);
        }
        mostrarImagen();
    }
}
