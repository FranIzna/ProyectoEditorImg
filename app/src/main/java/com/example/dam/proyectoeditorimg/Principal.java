package com.example.dam.proyectoeditorimg;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class Principal extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        inicio();
    }
    private Bitmap bm;
    private ImageView img;
    private LinearLayout prin, sec;
    private ImageView ivBn,ivSepia;
    public void inicio() {
        img = (ImageView) findViewById(R.id.imagen);
        bm = ((BitmapDrawable) img.getDrawable()).getBitmap();
        prin = (LinearLayout) findViewById(R.id.lyEditorPrincipal);
        sec = (LinearLayout) findViewById(R.id.lyEditorSecundario);
        sec.setVisibility(View.GONE);
        /****/
            Intent i=getIntent();
            Uri uri=i.getData();
            img.setImageURI(uri);
        /****/
        ivBn=(ImageView) findViewById(R.id.edBn);
        ivSepia=(ImageView) findViewById(R.id.edSepia);
            actualizaBotones();
    }
    public static final int foto = 1;
    private static final int GUARDA=2;

    String miDirectorio= "/proyectoEditor/imagenes/";
    public void foto(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(intent, foto);
    }

    private boolean guardaImg(Bitmap imageData, String filename) {
        String iconsStoragePath =
                Environment.getExternalStorageDirectory() + miDirectorio;
        File sdIconStorageDir = new File(iconsStoragePath);

        sdIconStorageDir.mkdirs();
        try {
            String filePath = sdIconStorageDir.toString() + filename;
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

            imageData.compress(Bitmap.CompressFormat.JPEG, 10, bos);
            bos.flush();
            bos.close();
        } catch (Exception e){}
        return true;
    }
    public String nombreAleatorio(int longitud){
            String s = "";
            long milis = new java.util.GregorianCalendar().getTimeInMillis();
            Random r = new Random(milis);
            int i = 0;
            while ( i < longitud){
                char c = (char)r.nextInt(255);
                if ( (c >= '0' && c <='9') || (c >='A' && c <='Z') ){
                    s += c;
                    i ++;
                }
            }
            return s;
        }
    String ruta =
            Environment.getExternalStorageDirectory() + miDirectorio;
    private String guardarImagen (Bitmap imagen){
        String nom=nombreAleatorio(7)+".png";
        Log.v("AA ","--_ "+nom);
//        ContextWrapper cw = new ContextWrapper(this);
//        File dirImages = cw.getDir("Imagenes", this.MODE_PRIVATE);
//        Log.v("BB ","--_ "+dirImages.getAbsolutePath());
        File myPath = new File(ruta, nom);
        Log.v("CC ","--_ "+myPath);
        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(myPath);
            imagen.compress(Bitmap.CompressFormat.JPEG, 10, fos);
            fos.flush();
        }catch (FileNotFoundException ex){
            ex.printStackTrace();
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return myPath.getAbsolutePath();
    }

    public void guarda(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.guardar));
        alert.setPositiveButton(getString(R.string.aceptar),
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                
                BitmapDrawable b=(BitmapDrawable)img.getDrawable();
                Bitmap bm=(Bitmap) b.getBitmap();

                guardarImagen(bm);
                Toast.makeText(getApplicationContext(),
                        getString(R.string.salidaGuarda)+" "+miDirectorio,
                        Toast.LENGTH_LONG).show();
                }
            });
         alert.setNegativeButton(getString(R.string.cancelar),null);

        alert.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == foto) {
//            Bitmap thumbnail = data.getParcelableExtra("data");
            Uri uri = data.getData();
            if (uri != null)
                img.setImageURI(uri);
            actualizaBotones();
        }
    }
    public Bitmap rotarImg(Bitmap bmpOriginal, float angulo) {
        Matrix matriz = new Matrix();
        matriz.postRotate(angulo);
        return Bitmap.createBitmap(bmpOriginal, 0, 0,
                bmpOriginal.getWidth(), bmpOriginal.getHeight(), matriz, true);
    }
    public void rotar(View v) {
//        tostada(getString(R.string.rotando));

        BitmapDrawable bmpDraw = (BitmapDrawable) img.getDrawable();
        Bitmap bitmap = bmpDraw.getBitmap();
        img.setImageBitmap(rotarImg(bitmap, 90));
    }
    public void espejo(View v) {
//        tostada(getString(R.string.espejo));

        bm = ((BitmapDrawable) img.getDrawable()).getBitmap();
        Bitmap bmp = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), bm.getConfig());
        int pixel;
        for (int i = 0; i < bm.getWidth(); i++) {
            for (int j = 0; j < bm.getHeight(); j++) {
                pixel = bm.getPixel(i, j);
                bmp.setPixel(bm.getWidth() - i - 1, j,
                        Color.argb( Color.alpha(pixel),
                                    Color.red(pixel),
                                    Color.green(pixel),
                                    Color.blue(pixel)));
            }
        }
        img.setImageBitmap(bmp);
    }
    public void efectos(View v) {
        prin.setVisibility(View.GONE);
        sec.setVisibility(View.VISIBLE);
    }
    public Bitmap cambiaGris(Bitmap bmpOriginal) {
        Bitmap bmpGris = Bitmap.createBitmap(bmpOriginal.getWidth(),
                bmpOriginal.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas lienzo = new Canvas(bmpGris);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter cmcf = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(cmcf);
        lienzo.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGris;
    }
    public void blancoNegro(View v) {
//        tostada(getString(R.string.bN));

        BitmapDrawable bmpDraw = (BitmapDrawable) img.getDrawable();
        Bitmap bitmap = bmpDraw.getBitmap();

        Bitmap bmpGris = cambiaGris(bitmap);//Se pasa a gris
        img.setImageBitmap(bmpGris);
    }
    public Bitmap toSepia(Bitmap bmpOriginal) {
        int width, height, r, g, b, c, gry;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();
        int depth = 20;

        Bitmap bmpSephia = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmpSephia);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setScale(.3f, .3f, .3f, 1.0f);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        canvas.drawBitmap(bmpOriginal, 0, 0, paint);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                c = bmpOriginal.getPixel(x, y);

                r = Color.red(c);
                g = Color.green(c);
                b = Color.blue(c);

                gry = (r + g + b) / 3;
                r = g = b = gry;

                r = r + (depth * 2);
                g = g + depth;

                if (r > 255) r = 255;

                if (g > 255) g = 255;

                bmpSephia.setPixel(x, y, Color.rgb(r, g, b));
            }
        }
        return bmpSephia;
    }
    public void sepia(View v) {
//        tostada(getString(R.string.sepia));

        Bitmap bmpSepia = toSepia(
                ((BitmapDrawable) img.getDrawable()).getBitmap());
        img.setImageBitmap(bmpSepia);
    }
    public void atras(View v) {
        sec.setVisibility(View.GONE);
        prin.setVisibility(View.VISIBLE);
    }
    public void actualizaBotones(){
        BitmapDrawable bmN=(BitmapDrawable) img.getDrawable();
            Bitmap bm=bmN.getBitmap();
            bm=cambiaGris(bm);
        ivBn.setImageBitmap(bm);
        /****/
        BitmapDrawable bmS=(BitmapDrawable) img.getDrawable();
            Bitmap bm2=bmS.getBitmap();
            bm2=toSepia(bm2);
        ivSepia.setImageBitmap(bm2);
    }
    public void tostada(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

}
