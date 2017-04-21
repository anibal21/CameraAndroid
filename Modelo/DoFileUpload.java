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
    public static final String SERVER_URL_REQUEST_FORMULARIO = "https://www.url.cl";
    String fname;
    Context activity;

    private final String USER_AGENT = "Mozilla/5.0";
    private final String rut;
    private String json_decoded = "";


    public DoFileUpload( Context activity) {
        this.activity = activity;
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
            this.json_decoded = POST(SERVER_URL_REQUEST_FORMULARIO, "rut="+this.rut);
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
   
        //EditText tipo_orden = (EditText) ((Activity) activity).findViewById(R.id.tipo);

        //Envío de datos a siguiente activity
        Intent intent = new Intent(activity, BotonesActivity.class);

        ((Activity) activity).finish(); //CASTING
        //finish();

        activity.startActivity(intent);
    }
}
