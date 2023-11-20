package com.example.tarea23oscargomez;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.tarea23oscargomez.configuracion.Fotos;
import com.example.tarea23oscargomez.configuracion.SQLiteConexion;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    ImageView fotoTomada;
    ImageButton btnTomarFoto;
    Button btnSalvarFoto, btnVerLista;
    EditText txtDescripcion;

    SQLiteConexion conexion;

    static final  int REQUEST_IMAGE = 101;
    static final  int PETICION_ACCESS_CAM = 201;
    String ruta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fotoTomada = (ImageView) findViewById(R.id.fotoTomada);
        txtDescripcion = (EditText) findViewById(R.id.txtDescripcion);
        btnSalvarFoto = (Button) findViewById(R.id.btnSalvar);
        btnTomarFoto = (ImageButton) findViewById(R.id.btntomarfoto);
        btnVerLista = (Button) findViewById(R.id.btnVerLista);

        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permisosPedidos();
            }
        });

        btnSalvarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String descripcion = txtDescripcion.getText().toString();
                if (descripcion.isEmpty()) {
                    txtDescripcion.setError("Es necesario darle una descripción a la Imagen");
                } else {
                    SalvarFoto();
                }
            }
        });

        btnVerLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentVerLista = new Intent(getApplicationContext(), ListViewActivity.class);
                startActivity(intentVerLista);
            }
        });
    }

    private void permisosPedidos(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA},PETICION_ACCESS_CAM);
        }
        else
        {
            despachoImagen();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode ==  PETICION_ACCESS_CAM){
            if (grantResults.length>0&&grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }
            {
                Toast.makeText(getApplicationContext(),"Se necesita brindar permiso de la camara",Toast.LENGTH_LONG);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_IMAGE){
//            Bundle extras = data.getExtras();
//            Bitmap imagen = (Bitmap) extras.get("data");
//            Objetoimagen.setImageBitmap(imagen);
            try {
                File foto = new File(ruta);
                fotoTomada.setImageURI(Uri.fromFile(foto));
            }
            catch (Exception ex)
            {
                ex.toString();
            }
        }
    }

    private void despachoImagen() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File foto = null;
            try {
                foto = crearImagen();
            } catch (IOException ex) {
                ex.toString();
            }
            // Continue only if the File was successfully created
            if (foto != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.tarea23oscargomez.fileprovider",
                        foto);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE);
            }
        }
    }

    private File crearImagen() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,    /* prefix */
                ".jpg",         /* suffix */
               storageDir      /* directory */
      );

        // Save a file: path for use  with ACTION_VIEW intents

        ruta = image.getAbsolutePath();
        return image;
    }

    private String convertirImagen64(String path){
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] imagearray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imagearray,Base64.DEFAULT);
    }



    public void SalvarFoto() {
        conexion = new SQLiteConexion(this,null);
        SQLiteDatabase db = conexion.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLiteConexion.foto, convertirImagen64(ruta));
        values.put(SQLiteConexion.descripcion, txtDescripcion.getText().toString());
        db.insert(SQLiteConexion.tableName, null, values);
        Toast.makeText(getApplicationContext(), "Registro ingresado ",Toast.LENGTH_LONG).show();
        Limpiar();
        db.close();
    }
    private void Limpiar() {
        fotoTomada.setImageResource(R.drawable.profiledefault);
        txtDescripcion.setText("");
    }
}

