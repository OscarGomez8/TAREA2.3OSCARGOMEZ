package com.example.tarea23oscargomez.configuracion;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tarea23oscargomez.R;
import com.example.tarea23oscargomez.configuracion.Fotos;
import com.example.tarea23oscargomez.configuracion.SQLiteConexion;

public class ListAdapter extends ArrayAdapter<Fotos> implements View.OnClickListener {
    private final List<Fotos> fotos;
    private Context context;

    SQLiteConexion conexion;

    public ListAdapter(@NonNull Context context, List<Fotos> fotos) {
        super(context, R.layout.item_list, fotos);
        this.fotos = fotos;
        this.context = context;
    }

    @Override
    public void onClick(View v) {

    }

    public static class ViewHolder{
        ImageView imageProfile;
        TextView txtDescription;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Fotos fotoToma = fotos.get(position);
        ViewHolder viewHolder;
        View view = convertView;

        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.item_list,null);
        }
        ImageView imagen = view.findViewById(R.id.profile);
        TextView description = view.findViewById(R.id.itemDescription);

        imagen.setImageBitmap(obtenerImagen(fotoToma.getId()));
        description.setText(fotoToma.getDescripcion());

        return view;
    }


    private Bitmap obtenerImagen(String id) {
        conexion = new SQLiteConexion(context, null);
        SQLiteDatabase db = conexion.getReadableDatabase();
        Bitmap bitmap;
        String selectQuery = "SELECT " + SQLiteConexion.foto +" FROM " + SQLiteConexion.tableName + " WHERE id = ?";
// Ejecuta la consulta
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});
        // Verifica si se encontraron resultados
        if (cursor.moveToFirst()) {
            // Obtiene los datos de la imagen en forma de arreglo de bytes
            byte[] imageData = Base64.decode(cursor.getBlob(cursor.getColumnIndexOrThrow("foto")), Base64.DEFAULT);

            // Convierte los datos de la imagen en un objeto Bitmap
            bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        }
        else{
            bitmap = BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.profiledefault);
        }
        // Cierra el cursor y la conexión a la base de datos
        cursor.close();
        db.close();
        return bitmap;
    }
}
