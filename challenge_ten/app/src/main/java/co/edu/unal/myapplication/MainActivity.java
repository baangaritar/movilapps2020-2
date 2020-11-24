package co.edu.unal.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private Spinner departments_spinner;
    private Spinner municipality_spinner;
    private Spinner year_spinner;

    private ArrayList<String> departments = new ArrayList<>();
    private ArrayList<String> municipalities = new ArrayList<>();
    private ArrayList<String> years = new ArrayList<>();
    private ArrayList<String> coverage = new ArrayList<>();


    private ArrayAdapter<String> departments_adapter;
    private ArrayAdapter<String> municipalities_adapter;
    private ArrayAdapter<String> years_adapter;
    private ArrayAdapter<String> coverage_adapter;

    private ListView list;

    private Context context = this;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(this);
        String url = "https://www.datos.gov.co/resource/9mey-c8s8.json?$select=distinct%20a_o%2Ctrimestre&$order=a_o%20DESC";

        departments_spinner = (Spinner) findViewById(R.id.departments);
        municipality_spinner = (Spinner) findViewById(R.id.municipality);
        year_spinner = (Spinner) findViewById(R.id.year);

        list = findViewById(R.id.list);

        JsonArrayRequest yearsRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject tmp = null;
                    try {
                        tmp = response.getJSONObject(i);
                        years.add(tmp.getString("a_o") + " " + tmp.getString("trimestre"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                years_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, years);
                years_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                year_spinner.setAdapter(years_adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        url = "https://www.datos.gov.co/resource/9mey-c8s8.json?$select=distinct%20departamento&$order=departamento%20ASC";

        JsonArrayRequest departmentsRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject tmp = null;
                            try {
                                tmp = response.getJSONObject(i);
                                departments.add(tmp.getString("departamento"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        departments_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, departments);
                        departments_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        departments_spinner.setAdapter(departments_adapter);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        departments_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                municipalities.clear();
                String department = (String) parent.getItemAtPosition(pos);
                String[] year_trimester = (String[]) year_spinner.getSelectedItem().toString().split(" ");

                String url = "https://www.datos.gov.co/resource/9mey-c8s8.json?$select=distinct%20municipio&departamento="+ department + "&a_o=" + year_trimester[0] + "&trimestre=" + year_trimester[1] +  "&$order=municipio%20ASC";
                JsonArrayRequest municipalitiesRequest = new JsonArrayRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject tmp = null;
                                    try {
                                        tmp = response.getJSONObject(i);
                                        municipalities.add(tmp.getString("municipio"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                municipalities_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, municipalities);
                                municipalities_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                municipality_spinner.setAdapter(municipalities_adapter);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        });
                queue.add(municipalitiesRequest);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        year_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                departments.clear();
                String[] year_trimester = (String[]) year_spinner.getSelectedItem().toString().split(" ");

                String url = "https://www.datos.gov.co/resource/9mey-c8s8.json?$select=distinct%20departamento&a_o=" + year_trimester[0] + "&trimestre=" + year_trimester[1] +  "&$order=departamento%20ASC";
                JsonArrayRequest departmentsRequest = new JsonArrayRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject tmp = null;
                                    try {
                                        tmp = response.getJSONObject(i);
                                        departments.add(tmp.getString("departamento"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                departments_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, departments);
                                departments_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                departments_spinner.setAdapter(departments_adapter);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        });
                queue.add(departmentsRequest);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        municipality_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                coverage.clear();
                final String tmp = (String) parent.getItemAtPosition(pos);
                String[] year_trimester = (String[]) year_spinner.getSelectedItem().toString().split(" ");
                String url = "https://www.datos.gov.co/resource/9mey-c8s8.json?municipio=" + tmp + "&a_o=" + year_trimester[0] + "&trimestre=" + year_trimester[1] ;
                JsonArrayRequest codes = new JsonArrayRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject tmp = null;
                                    try {
                                        tmp = response.getJSONObject(i);
                                        String tmp2 = "Proveedor: " + tmp.getString("proveedor") + "\n";
                                        tmp2 += "Centro Poblado: " + tmp.getString("centro_poblado") + "\n";
                                        tmp2 += "Cobertura 2G: " + tmp.getString("cobertura_2g") + "\n";
                                        tmp2 += "Cobertura 3G: " + tmp.getString("cobertura_3g") + "\n";
                                        tmp2 += "Cobertura LTE: " + tmp.getString("cobertuta_lte") + "\n";
                                        coverage.add(tmp2);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                coverage_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, coverage);
                                list.setAdapter(coverage_adapter);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("REQ", "bad");
                            }
                        });
                queue.add(codes);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        queue.add(yearsRequest);

    }


}