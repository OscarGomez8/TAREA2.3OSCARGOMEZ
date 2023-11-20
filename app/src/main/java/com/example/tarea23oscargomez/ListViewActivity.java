package com.example.tarea23oscargomez;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tarea23oscargomez.configuracion.Fotos;
import com.example.tarea23oscargomez.configuracion.SQLiteConexion;
import com.example.tarea23oscargomez.configuracion.ListAdapter;
import java.util.ArrayList;
import java.util.List;


public class ListViewActivity extends AppCompatActivity {

    ListView listFotos;
    List<Fotos> fotos = new ArrayList<>();
    ListAdapter fotosAdapter;
    Button backButton;
    SQLiteConexion conexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        conexion = new SQLiteConexion(this, null);
        backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed(); // Vuelve a la actividad anterior al presionar el botón
            }
        });

        listFotos = (ListView) findViewById(R.id.listFotos);
        obtenerTabla();
        fotosAdapter =new ListAdapter(ListViewActivity.this, fotos);
        listFotos.setAdapter(fotosAdapter);
    }

    private void obtenerTabla(){
        SQLiteDatabase db = conexion.getReadableDatabase();
        Fotos fotoTabla = null;
        //Cursor de base de datos
        Cursor cursor = db.rawQuery(SQLiteConexion.SelectTablePhotos,null);

        //Recorremos el cursor
        while (cursor.moveToNext()){
            fotoTabla = new Fotos();
            fotoTabla.setId(cursor.getString(0));
            fotoTabla.setDescripcion(cursor.getString(2));
            fotos.add(fotoTabla);
        }
        cursor.close();
    }
}

