package com.example.uaolab;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MyBluetooth extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;


    private ProgressBar pgsBar;
    TextView mStatusBlueTv;
    ImageView mBlueIv;
    String uniqueId;
    String mac;
    String readMessage;
    boolean scanT=false;

    ListView scanListView;
    ArrayList arrayList,arrayList2;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<BluetoothDevice> ListDevices;




    BluetoothDevice bDevice;
    BluetoothSocket blsocket = null ;
    BluetoothAdapter mBlueAdapter;
    BluetoothDevice pairedBluetoothDevice = null;
    BluetoothDevice bDevice2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bluetooth);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pgsBar = (ProgressBar) findViewById(R.id.pBar);

        final Globals g = (Globals) getApplicationContext();

        FloatingActionButton offBtn = findViewById(R.id.off);
        FloatingActionButton scanBtn = findViewById(R.id.scan);
        FloatingActionButton conectBtn = findViewById(R.id.conectBtn);
        final FloatingActionButton sendBtn = findViewById(R.id.dataTest);

        mStatusBlueTv = findViewById(R.id.statusBluetoothTv);
        mBlueIv       = findViewById(R.id.bluetoothIv);
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
        g.setBAdapter(mBlueAdapter);

        mBlueAdapter = g.getBAdapter();

        scanListView=(ListView) findViewById(R.id.scanerListView);
        arrayList = new ArrayList();
        arrayList2 = new ArrayList();
        ListDevices = new ArrayList();
        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,arrayList);
        scanListView.setAdapter(arrayAdapter);
        uniqueId = UUID.randomUUID().toString();


        //mBlueAdapter.startDiscovery();




        ///*/////


        int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
        permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
        if (permissionCheck != 0) {

            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
        }


        offBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBlueAdapter.isEnabled()){
                    mBlueAdapter.disable();
                    Snackbar.make(view, "Apagando bluetooth", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    mBlueIv.setImageResource(R.drawable.ic_bluetooth_off);
                    mStatusBlueTv.setText("Bluetooth apagado");

                }
                else {
                    Snackbar.make(view, "Bluetooth apagado!!!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    mStatusBlueTv.setText("Bluetooth apagado");
                }

            }
        });

         scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBlueAdapter.startDiscovery();
                if (!g.getBAdapter().isEnabled()){
                    showToast("Turning On Bluetooth...");
                    //intent to on bluetooth
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_ENABLE_BT);


                }
                else {


                }

                scanT=true;
                showToast("Escaneando dispositivos");
                mBlueAdapter.cancelDiscovery();
                arrayList.clear();
                arrayList2.clear();
                ListDevices.clear();
                mBlueAdapter.startDiscovery();


                scanListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        mac= (String) arrayList2.get(position);

                        g.setMac(mac);
                        bDevice = (BluetoothDevice) ListDevices.get(position);
                        if(scanT==true) {
                            g.setBDevice(bDevice);
                            scanT=false;
                        }
                        Toast.makeText(getApplicationContext(), bDevice.toString(), Toast.LENGTH_SHORT).show();


                    }
                });


            }
        });


        conectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(pairedBluetoothDevice==null) {
                    if (g.getBDevice() != null) {

                            pgsBar.setVisibility(VISIBLE);
                            connectBlue(g.getBDevice());


                        g.setBSocket(blsocket);
                        g.setPBDevice(pairedBluetoothDevice);
                        g.setUID("00001101-0000-1000-8000-00805f9b34fb");
                        mBlueAdapter.cancelDiscovery();
                    } else {
                        Toast.makeText(getApplicationContext(), "Selecciona un dispositivo Bluetooth", Toast.LENGTH_LONG).show();
                    }
                }else{
                    showToast("ya estas conectado");
                }
                pgsBar.setVisibility(GONE);

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(pairedBluetoothDevice!=null){
                    sendData();
                    readData();
                    if(readMessage.equals("prendido")){
                        sendBtn.setImageResource(R.drawable.ic_light_test);
                    }else{
                        sendBtn.setImageResource(R.drawable.ic_light_off);
                    }
                }else showToast("no hay ningun dispositivo conectado");





            }
        });




        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);








        //check if bluetooth is available or not
        if (mBlueAdapter == null){
            mStatusBlueTv.setText("Bluetooth no disponible en este dispositivo");
            mBlueIv.setImageResource(R.drawable.ic_bluetooth_disabled);
        }
        else {
            mStatusBlueTv.setText("Bluetooth disponible");
        }

        //set image according to bluetooth status(on/off)
        if (mBlueAdapter.isEnabled()){
            mBlueIv.setImageResource(R.drawable.ic_bluetooth_on);
            mStatusBlueTv.setText("Bluetooth encendido");
        }
        else {
            mBlueIv.setImageResource(R.drawable.ic_bluetooth_off);
            mStatusBlueTv.setText("Bluetooth apagado");
        }







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
        getMenuInflater().inflate(R.menu.my_bluetooth, menu);
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
            Intent mainActivity= new Intent(getApplicationContext(), MainAct.class);
            startActivity(mainActivity);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {
            try {
                resetConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent freeFall= new Intent(getApplicationContext(), CaidaLibre.class);
            startActivity(freeFall);

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK){
                    //bluetooth is on
                    mBlueIv.setImageResource(R.drawable.ic_bluetooth_on);
                    showToast("Bluetooth encendido");
                    mStatusBlueTv.setText("Bluetooth encendido");
                }
                else {
                    //user denied to turn bluetooth on
                    showToast("no se pudo encender el bluetooth");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }





    void connectBlue(final BluetoothDevice device){
        final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb") ;
        mBlueAdapter.cancelDiscovery();


        try {
            blsocket = device.createRfcommSocketToServiceRecord(uuid);
            blsocket.connect();
            pairedBluetoothDevice = device;

            Toast.makeText(getApplicationContext(), "Device paired successfully!",Toast.LENGTH_LONG).show();
        }catch(IOException ioe)
        {try {
            Log.e("","trying fallback...");

            blsocket =(BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(device,1);
            blsocket.connect();

            Log.e("","Connected");
        }
        catch (Exception e2) {
            Log.e("", "Couldn't establish Bluetooth connection!");
        }
            Log.e("taha>", "cannot connect to device :( " +ioe);
            Toast.makeText(getApplicationContext(), "Could not connect",Toast.LENGTH_LONG).show();
            pairedBluetoothDevice = null;

        }
    }

    void sendData(){

        if (blsocket != null) {
            try {
                blsocket.getOutputStream().write(2);
                Toast.makeText(getApplicationContext(), "try",Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                // TODO Send data
                Toast.makeText(getApplicationContext(), "error de envio",Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "no entro   " +blsocket.toString(),Toast.LENGTH_LONG).show();
        }

    }


    void readData(){
        byte[] buffer = new byte[32];  // buffer store for the stream
        int bytes; // bytes returned from read()
        InputStream tmpIn = null;
        try {
            tmpIn = blsocket.getInputStream();
            DataInputStream mmInStream = new DataInputStream(tmpIn);
            bytes = mmInStream.read(buffer);
            readMessage = new String(buffer, 0, bytes);
            Toast.makeText(getApplicationContext(), readMessage,Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //return readMessage;
    }





    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action =intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                arrayList.add(device.getName()+"   MAC:  "+ device.getAddress());
                arrayList2.add(device.getAddress());
                ListDevices.add(device);
                arrayAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        mBlueAdapter.cancelDiscovery();
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadcastReceiver,intentFilter);
    }


    private void resetConnection() throws IOException {


        if (blsocket != null) {
            try {blsocket.close();} catch (Exception e) {}
            blsocket = null;
        }

    }


}
