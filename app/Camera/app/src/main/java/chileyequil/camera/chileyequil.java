package chileyequil.camera;

import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.service.textservice.SpellCheckerService;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import android.content.Context;
import android.widget.Toast;

import cz.msebera.android.httpclient.Header;

public class chileyequil extends AppCompatActivity {
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;
    TextView textView1,porcentajes;
    ImageView image;
    Button boton;
    Uri photoURI;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chileyequil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textView1 = (TextView) findViewById(R.id.textView);
        textView1.setText("top");
        textView1.setGravity(View.TEXT_ALIGNMENT_CENTER);


        image = (ImageView) findViewById(R.id.imageView);

        porcentajes = (TextView) findViewById(R.id.textView2);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView1.setVisibility(View.INVISIBLE);
                porcentajes.setVisibility(View.INVISIBLE);
                dispatchTakePictureIntent();
            }
        });

        boton = (Button) findViewById(R.id.boton);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boton.setVisibility(View.INVISIBLE);
                porcentajes.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chileyequil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    File photoFile = null;
    private void dispatchTakePictureIntent() {
        android.content.Intent takePictureIntent = new android.content.Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);




            }
        }
    }

    HttpURLConnection urlConnection;
    private Bitmap bitmap;
    //RequestQueue queue = Volley.newRequestQueue(this);

    private void uploadImage() {
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(chileyequil.this, s, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(chileyequil.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = "bitmapString";

                //Getting Image Name
                String name = "name";

                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put("fileupload", "photoFile");
                //params.put("name", name);

                //returning parameters
                return params;
            }
        };
    };

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    String url = "http://35.202.255.134/upload";
    //String url = "http://10.12.221.36/upload";
    // String url = "http://130.211.128.2/upload";
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        image.setImageURI(photoURI);
        textView1.setVisibility(View.VISIBLE);
        boton.setVisibility(View.VISIBLE);
        final ProgressDialog loading = ProgressDialog.show(this,"Recognizing...","Please wait...",false,false);

        AsyncHttpClient client = new AsyncHttpClient();

        File myFile = new File("/path/to/file.png");
        RequestParams params = new RequestParams();
        params.put("enctype", "multipart/form-data");
        try {
            params.put("fileupload", photoFile);
        } catch(FileNotFoundException e) {}

        class FoodInfo{
            String name;
            double certainty;
            int calories;
            double carbohidrates;
            double fat;
            double protein;
            public FoodInfo(String name, double certainty, int calories, double carbohidrates, double fat, double protein){
                this.name = name; this.certainty = certainty; this.calories = calories; this.carbohidrates = carbohidrates;
                this.fat = fat; this.protein = protein;
            }
        };
        class PQsort implements Comparator<FoodInfo> {

            public int compare(FoodInfo one, FoodInfo two) {
                return (int)two.certainty - (int)one.certainty;
            }
        };

        client.post(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                try{
                    PQsort pqs = new PQsort();
                    PriorityQueue<FoodInfo> queue=new PriorityQueue<FoodInfo>(4,pqs);
                    JSONObject JSONchilaquiles = response.getJSONObject("chilaquiles");
                    JSONObject JSONhamburguesa = response.getJSONObject("hamburguesa");
                    JSONObject JSONmolletes = response.getJSONObject("molletes");
                    JSONObject JSONsandwich = response.getJSONObject("sandwich");
                    queue.add(new FoodInfo("CHILAQUILES", JSONchilaquiles.getDouble("value")*100.0, JSONchilaquiles.getInt("calories"),
                            JSONchilaquiles.getDouble("carbohidrates"), JSONchilaquiles.getDouble("fat"),
                            JSONchilaquiles.getDouble("protein")));
                    queue.add(new FoodInfo("HAMBURGUESA", JSONhamburguesa.getDouble("value")*100.0, JSONhamburguesa.getInt("calories"), JSONhamburguesa.getDouble("carbohidrates"), JSONhamburguesa.getDouble("fat"), JSONhamburguesa.getDouble("protein")));
                    queue.add(new FoodInfo("MOLLETE", JSONmolletes.getDouble("value")*100.0, JSONmolletes.getInt("calories"), JSONmolletes.getDouble("carbohidrates"), JSONmolletes.getDouble("fat"), JSONmolletes.getDouble("protein")));
                    queue.add(new FoodInfo("SANDWICH", JSONsandwich.getDouble("value") * 100.0, JSONsandwich.getInt("calories"), JSONsandwich.getDouble("carbohidrates"), JSONsandwich.getDouble("fat"), JSONsandwich.getDouble("protein")));
                    FoodInfo first = queue.poll();
                    FoodInfo second = queue.poll();
                    FoodInfo third = queue.poll();
                    FoodInfo fourth = queue.poll();
                    Log.d("response: ", "first: " + first.certainty);
                    Log.d("response: ", "second: " + second.certainty);
                    Log.d("response: ", "third: " + third.certainty);
                    Log.d("response: ", "fourth: " + fourth.certainty);
                    TextView textView2 = (TextView) findViewById(R.id.textView2);
                    textView2.setText(String.format("\n %s - %.2f \n %s - %.2f \n %s - %.2f \n %s - %.2f", first.name, first.certainty, second.name, second.certainty, third.name, third.certainty, fourth.name, fourth.certainty));
                    loading.dismiss();
                    if(first.certainty > 70){
                        textView1.setText(first.name+" -  calorias: "+first.calories+"  protein: "+first.protein+"\n  fat: "+first.fat+"  carbohidrates: "+first.carbohidrates );
                    } else {
                        textView1.setText("irreconocible");
                    }
                } catch(JSONException e){
                    Log.d("response","EXCEPTION: " + e.getMessage());
                    loading.dismiss();
                }
            }
        });
    }

}
