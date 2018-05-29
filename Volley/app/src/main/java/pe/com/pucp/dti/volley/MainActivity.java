package pe.com.pucp.dti.volley;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pe.com.pucp.dti.volley.Model.Post;

public class MainActivity extends AppCompatActivity {

    // Constants
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";
    private static final String USERS_URL = BASE_URL + "/users";
    private static final String POSTS_URL = BASE_URL + "/posts";
    private static final String IMAGE_URL = "http://placehold.it/600/197d29";
    //private static final String HEAVY_IMAGE_URL = "https://upload.wikimedia.org/wikipedia/commons/c/c9/Tiligul_liman_5_Mb.jpg";
    //Otros ejemplos
    private static final String HEAVY_IMAGE_URL = "https://upload.wikimedia.org/wikipedia/commons/c/c5/Buachaille_Etive_Mor_%28Stob_Dearg%29.jpg";
    private static final String JSONARRAY_TAG = "JSON Array";
    private static final String JSONOBJ_TAG = "JSON Object";
    private static final String STRING_TAG = "String";
    private static final String PARSE_GSON_TAG = "Parsed GSON";
    private static final String PARSE_VOLLEY_TAG = "Parsed Volley";
    private static final String CANCEL_TAG = "CANCEL";

    // Properties
    Button donwloadImageButton;
    Button cancelButton;
    ImageView imageView;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setObjects();
        // Array request
        volleyGetCall();

        // Object request
        volleyPostCall("My Post","Post body.","1");

        // String request
        volleyStringPostCall("My Post","Post body.","1");

    }

    private void setObjects() {
        this.queue = Volley.newRequestQueue(this);
        this.donwloadImageButton =  findViewById(R.id.downloadImageButton);
        this.donwloadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                volleyImageCall();
                //picassoImageCall();
            }
        });
        this.cancelButton = findViewById(R.id.cancelButton);
        this.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queue.cancelAll(CANCEL_TAG);
            }
        });
        this.imageView =  findViewById(R.id.imageView);
    }

    private void volleyGetCall(){
        // La respuesta del servicio web es un arreglo json []
        JsonArrayRequest jsArrRequest = new JsonArrayRequest(Request.Method.GET, USERS_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i(JSONARRAY_TAG, response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        this.queue.add(jsArrRequest);
    }

    private void volleyPostCall(String title, String body, String userId) {
        Map<String, String> params = new HashMap();
        params.put("body", body);
        params.put("title", title);
        params.put("userId", userId);
        JSONObject parameters = new JSONObject(params);
        // La respuesta del servicio web es un objeto json {}
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, POSTS_URL, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(JSONOBJ_TAG, response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        this.queue.add(jsObjRequest);
    }

    private void volleyStringPostCall(final String title, final String body, final String userId) {
        StringRequest strRequest = new StringRequest(Request.Method.POST, POSTS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(STRING_TAG, response);
                parseResponseGson(response);
                parseResponseVolley(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("body", body);
                params.put("title", title);
                params.put("userId", userId);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        this.queue.add(strRequest);
    }

    private void volleyImageCall() {
        ImageRequest imgRequest = new ImageRequest(HEAVY_IMAGE_URL,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        imgRequest.setTag(CANCEL_TAG);
        this.queue.add(imgRequest);
    }

    private void parseResponseGson(String response) {
        Gson gson = new Gson();
        Post post = gson.fromJson(response, Post.class);
        // Si hay un error al parsear la cadena de caracteres GSON devolverá un objeto nulo.
        if (post != null) {
            Log.i(PARSE_GSON_TAG, post.getTitle());
        }
    }

    private void parseResponseVolley(String response){
        try {
            // La creación de un objeto JSON lanza una excepción en caso de no poder parsear la cadena de caracteres.
            JSONObject jsonObj = new JSONObject(response);
            String title = jsonObj.getString("title");
            String body = jsonObj.getString("body");
            String userId = jsonObj.getString("userId");
            int id = jsonObj.getInt("id");
            Post post = new Post(id,title, body, userId);
            Log.i(PARSE_VOLLEY_TAG, post.getTitle());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void picassoImageCall() {
        Picasso.get().load(IMAGE_URL).into(imageView);
    }

}
