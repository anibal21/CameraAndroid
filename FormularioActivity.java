package autumn.dispatcherapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import autumn.dispatcherapp.UploadImage.Base64;
import autumn.dispatcherapp.UploadImage.RequestPackage;

import autumn.dispatcherapp.Modelo.DoFileUpload;


public class FormularioActivity extends AppCompatActivity implements View.OnClickListener{

    Button photoButton,siguiente;
    private static final int CAMERA_REQUEST = 1888;

    //Camera variables
    private ConnectionDetector cd;
    private static final int CAMERA_PIC_REQUEST = 1111;
    private static final int MY_REQUEST_CODE = 1;
    private ImageView ivImage;
    private static final String IMAGE_CAPTURE_FOLDER = "/uploads";
    private static File file;
    private static Uri _imagefileUri;
    private static String _bytes64Sting, _imageFileName;
    public static String URL = "http://www.autumnideas.com/Dispatcher/uploadImage.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.formulario_activity);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        _imageFileName = String.valueOf("img-"+date.toString());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_REQUEST_CODE);
                }
            }
        }
        //        Object initialization
        cd = new ConnectionDetector(FormularioActivity.this);

        //Camara Formulario
        photoButton = (Button) this.findViewById(R.id.button_photo);
        siguiente = (Button) this.findViewById(R.id.button_siguiente1);  //boton para ir a la siguiente activity
        ivImage = (ImageView) findViewById(R.id.ivImage);
        ivImage.setVisibility(View.VISIBLE);
        cd = new ConnectionDetector(getApplicationContext());

        photoButton.setOnClickListener(this);
        siguiente.setOnClickListener(this);

    }

    //Metodos Camara

    private void captureImage() {
        Intent intent = new Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        _imagefileUri = Uri.fromFile(getFile());

        intent.putExtra(MediaStore.EXTRA_OUTPUT, _imagefileUri);
        startActivityForResult(intent, CAMERA_PIC_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_PIC_REQUEST) {
		//Aqui no hace nada, hasta que se suba con el boton siguiente
            }
        } else if (resultCode == RESULT_CANCELED) {
            // user cancelled Image capture
            Toast.makeText(getApplicationContext(),
                    "User cancelled image capture", Toast.LENGTH_SHORT).show();
        } else {
            // failed to capture image
            Toast.makeText(getApplicationContext(),
                    "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void uploadImage(String picturePath) {
        Bitmap bm = BitmapFactory.decodeFile(picturePath);

        // Pequeña transformación luego de capturar la imagen para que se redimencione a una resolucion de 960 pixeles
        final int maxSize = 960;
        int outWidth;
        int outHeight;
        int inWidth = bm.getWidth();
        int inHeight = bm.getHeight();
        if(inWidth > inHeight){
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, outWidth, outHeight, false);

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] byteArray = bao.toByteArray();
        _bytes64Sting = Base64.encodeBytes(byteArray);
        RequestPackage rp = new RequestPackage();
        rp.setMethod("POST");
        rp.setUri(URL);
        rp.setSingleParam("base64", _bytes64Sting);
        rp.setSingleParam("ImageName", _imageFileName + ".jpg");

        new DoFileUpload(FormularioActivity.this).execute(rp);
    }


    private File getFile() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        file = new File(filepath, IMAGE_CAPTURE_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }

        return new File(file + File.separator + _imageFileName
                + ".jpg");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_photo:
                photoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        captureImage();
                    }
                });
                break;

            case R.id.button_siguiente1:

                    if ((!s_nombre_r.equals(""))&&(!s_descripcion_r.equals(""))&&(!s_cargo_r.equals(""))&&(!s_rut_r.equals(""))&&(!valor_c.equals(""))) {

                        if (validarRut(s_rut_r) == true) {  // el metodo validarRut está al final del código

                            try {              // se comprueba que se ingresa un entero en valor_c
                                int num = Integer.parseInt(valor_c);
                            } catch (NumberFormatException e) {
                                valor.setError("Ha ingresado un monto inválido");
                                valor.requestFocus();
                                break;
                            }
                            if (cd.isConnectingToInternet()) {
                                    if (cd.isConnectingToInternet()) {
                                        uploadImage(_imagefileUri.getPath());
                                    } else {
                                        Toast.makeText(FormularioActivity.this, "NO hay conexión a internet..", Toast.LENGTH_LONG).show();
                                    }
                            } else {
                                Toast.makeText(FormularioActivity.this, "No hay conexión a internet !", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            rut_r.setError("Ha ingresado un RUN inválido");
                            rut_r.requestFocus();
                        }
                    }else {
                        Toast.makeText(FormularioActivity.this, "Formulario incompleto!", Toast.LENGTH_LONG).show();
                    }

                break;
        }
    }

}
