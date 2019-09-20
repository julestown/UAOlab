package com.example.uaolab;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class CaidaLibre extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private ProgressBar pgsBar;
    String readMessage;
    boolean dBluetooth = false;
    TextView textV;
    int a=0;

    BluetoothDevice bDevice;
    BluetoothSocket blsocket = null;
    BluetoothAdapter mBlueAdapter;
    BluetoothDevice pairedBluetoothDevice = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caida_libre);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Globals g = (Globals) getApplicationContext();

        bDevice = g.getBDevice();
        mBlueAdapter = g.getBAdapter();
        pgsBar = (ProgressBar) findViewById(R.id.pBar2);
        textV = findViewById(R.id.textView2);


        FloatingActionButton fab = findViewById(R.id.fab);
        FloatingActionButton parar = findViewById(R.id.parar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (pairedBluetoothDevice != null) {
                    dBluetooth = true;
                    try {
                        readData();
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                } else showToast("no hay ningun dispositivo conectado");

            }
        });


        parar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (pairedBluetoothDevice != null) {
                    dBluetooth = false;
                    sendData(0);
                } else showToast("no hay ningun dispositivo conectado");

            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.caida_libre, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            try {
                resetConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent mainActivity = new Intent(getApplicationContext(), MainAct.class);
            startActivity(mainActivity);
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            try {
                resetConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent blueActivity = new Intent(getApplicationContext(), MyBluetooth.class);
            startActivity(blueActivity);

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    void connectBlue(final BluetoothDevice device) {
        final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        mBlueAdapter.cancelDiscovery();


        try {
            blsocket = device.createRfcommSocketToServiceRecord(uuid);
            blsocket.connect();
            pairedBluetoothDevice = device;

            Toast.makeText(getApplicationContext(), "Device paired successfully!", Toast.LENGTH_LONG).show();
        } catch (IOException ioe) {
            try {
                Log.e("", "trying fallback...");

                blsocket = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(device, 1);
                blsocket.connect();

                Log.e("", "Connected");
            } catch (Exception e2) {
                Log.e("", "Couldn't establish Bluetooth connection!");
            }
            Log.e("taha>", "cannot connect to device :( " + ioe);
            Toast.makeText(getApplicationContext(), "Could not connect", Toast.LENGTH_LONG).show();
            pairedBluetoothDevice = null;

        }
    }


    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (bDevice != null) {


            mBlueAdapter.cancelDiscovery();

            if (pairedBluetoothDevice == null) {
                if (bDevice != null) {
                    while (pairedBluetoothDevice == null) {
                        connectBlue(bDevice);
                    }
                    pgsBar.setVisibility(GONE);
                    mBlueAdapter.cancelDiscovery();

                } else {
                    Toast.makeText(getApplicationContext(), "Selecciona un dispositivo Bluetooth", Toast.LENGTH_LONG).show();
                }
            } else {
                showToast("ya estas conectado");
            }
        }


    }


    void sendData(int para) {

        if (blsocket != null) {
            try {
                blsocket.getOutputStream().write(para);
                Toast.makeText(getApplicationContext(), "try", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                // TODO Send data
                Toast.makeText(getApplicationContext(), "error de envio", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "no entro   " + blsocket.toString(), Toast.LENGTH_LONG).show();
        }

    }


    String readData() throws IOException, JSONException {


        while (true) {
            try {
                byte[] buffer = new byte[32];  // buffer store for the stream
                int bytes; // bytes returned from read()
                InputStream tmpIn = null;
                tmpIn = blsocket.getInputStream();
                DataInputStream mmInStream = new DataInputStream(tmpIn);
                if (tmpIn.available() > 0) {
                    try {

                        do{
                            bytes = mmInStream.read(buffer);
                            readMessage = new String(buffer, 0, bytes);

                            insertar(readMessage);
                            textV.setText(readMessage);
                            Toast.makeText(getApplicationContext(), readMessage, Toast.LENGTH_LONG).show();

                        }while(readMessage!=null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), readMessage, Toast.LENGTH_LONG).show();
                } else {
                    SystemClock.sleep(100);
                }
            } catch (IOException e) {

                e.printStackTrace();
            }

            return readMessage;
        }
    }


    private void resetConnection() throws IOException {


        if (blsocket != null) {
            try {
                blsocket.close();
            } catch (Exception e) {
            }
            blsocket = null;
        }

    }



    private void insertar (String message) throws JSONException {
        AdminAQLiteOpenHelper admin =   new AdminAQLiteOpenHelper(this, "administracion", null, 1);
        SQLiteDatabase bd   =   admin.getWritableDatabase();

        JSONArray reader = new JSONArray(message);

            //JSONObject freeFall = (JSONObject) reader.getJSONObject();

        a=a+1;


        float  gravedad   = (float) reader.getDouble(Integer.parseInt("gravedad"));
        float  tiempo   = (float) reader.getDouble(Integer.parseInt("tiempo"));

        ContentValues registro  =   new ContentValues();
        registro.put("num",a);
        registro.put("tiempo",tiempo);
        registro.put("gravedad",gravedad);

        bd.insert("freeFall", null, registro);
        bd.close();
        Toast.makeText(this,"Datos cargados exitosamente", Toast.LENGTH_SHORT).show();
    }


     /*class leerDatos extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {

        }


        @Override
        protected String doInBackground(String... strings) {
            String myJSON1 = "";
            if (dBluetooth == true) {
                myJSON1 = readData();
            }

            return myJSON1;
        }


        @Override
        protected void onPostExecute(String myJSON) {
            try {
                JSONArray reader = new JSONArray(myJSON);


                String result = "";
                for (int i = 0; i < myJSON.length(); i++) {
                    JSONObject freeFall = reader.getJSONObject(i);
                    result += "gravedad:" + freeFall.getDouble("gravedad") + "," + "tiempo:" + freeFall.getDouble("tiempo");

                }
                showToast(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }*/
}


