package autumn.dispatcherapp.Modelo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import autumn.dispatcherapp.BotonesActivity;
import autumn.dispatcherapp.FormularioActivity;
import autumn.dispatcherapp.R;
import autumn.dispatcherapp.UploadImage.MyHttpURLConnection;
import autumn.dispatcherapp.UploadImage.RequestPackage;

public class DoFileUpload extends AsyncTask<RequestPackage, Void, String> {

    private ProgressDialog pDialog;
    public static final String SERVER_URL_REQUEST_FORMULARIO = "https://www.autumnideas.com/Dispatcher/sendData.php";
    String fname;
    String s_nombre_r,s_descripcion_r,s_cargo_r,s_rut_r,valor_c;
    Context activity;

    private final String USER_AGENT = "Mozilla/5.0";
    private final String rut;
    private final String nombre;
    private final String cargo;
    private final String descripcion;
    private final String n_guia;
    private String id_orden_trabajo;
    private String json_decoded = "";
    private String nombre_c;
    private String direccion_c;
    private String telefono_c;
    private String hora_c;

    public DoFileUpload(String s, String rut, String nombre, String cargo, String descripcion, String n_guia, String id_orden_trabajo, Context activity, String nombre_c, String direccion_c, String telefono_c, String hora_c) {
        this.rut = rut;
        this.nombre = nombre;
        this.cargo = cargo;
        this.descripcion = descripcion;
        this.id_orden_trabajo = id_orden_trabajo;
        this.n_guia = n_guia;
        this.activity = activity;
        this.nombre_c = nombre_c;
        this.direccion_c = direccion_c;
        this.telefono_c = telefono_c;
        this.hora_c = hora_c;
    }


    @Override
    protected void onPreExecute() {

        pDialog = new ProgressDialog(activity);
        pDialog.setMessage("Subiendo cambios..");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    @Override
    protected String doInBackground(RequestPackage... params) {
        //Subida de datos
        try {
            //Cambiar php para agregar web service
            this.json_decoded = POST(SERVER_URL_REQUEST_FORMULARIO, "rut="+this.rut+"&nombre="+this.nombre
                    +"&cargo="+this.cargo+"&descripcion="+this.descripcion+"&id_orden="+this.id_orden_trabajo
                    +"&image_url="+fname+"&n_guia="+this.n_guia);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("paraaams culiao", String.valueOf(params[0]));
        String content = MyHttpURLConnection.getData(params[0]);
        return content;
    }

    private String POST(String url, String urlParameters) throws IOException {
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        // String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        return (response.toString());
    }

    @Override
    protected void onPostExecute(String file_url) {
        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }

        EditText tipo_orden = (EditText) ((Activity) activity).findViewById(R.id.tipo);
        EditText n_documento = (EditText) ((Activity) activity).findViewById(R.id.n_documento);
        EditText id_cliente = (EditText) ((Activity) activity).findViewById(R.id.cliente);
        EditText fecha = (EditText) ((Activity) activity).findViewById(R.id.fecha_hora);

        //Envío de datos a siguiente activity
        Intent intent = new Intent(activity, BotonesActivity.class);
        intent.putExtra("nombre_recepcionista", s_nombre_r);
        intent.putExtra("tipo_orden", tipo_orden.getText().toString());
        intent.putExtra("rut_recepcionista", s_rut_r);
        intent.putExtra("numero_documento", n_documento.getText().toString());
        intent.putExtra("id_cliente", id_cliente.getText().toString());
        intent.putExtra("fecha", fecha.getText().toString());
        intent.putExtra("cargo_recepcionista", s_cargo_r);
        intent.putExtra("descripcion_recepcionista", s_descripcion_r);
        intent.putExtra("id_orden", id_orden_trabajo);


        intent.putExtra("nombre_c", nombre_c);
        intent.putExtra("direccion_c", direccion_c);
        intent.putExtra("telefono_c", telefono_c);
        intent.putExtra("valor",valor_c);
        intent.putExtra("hora_c", hora_c);
        intent.putExtra("descripcion",descripcion);

        ((Activity) activity).finish(); //CASTING
        //finish();

        activity.startActivity(intent);
    }
}