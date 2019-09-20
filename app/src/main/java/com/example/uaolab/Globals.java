package com.example.uaolab;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.util.ArrayList;

public class Globals extends Application{

    private BluetoothDevice bDevice;
    private BluetoothSocket blsocket;
    private BluetoothAdapter mBlueAdapter;
    private BluetoothDevice pairedBluetoothDevice;

    private String uniqueId;
    public  String mac;


    public BluetoothDevice getBDevice(){
        return this.bDevice;
    }

    public void setBDevice(BluetoothDevice alfa){
        this.bDevice=alfa;
    }



    public BluetoothSocket getBSoocket(){
        return this.blsocket;
    }

    public void setBSocket(BluetoothSocket alfa){
        this.blsocket=alfa;
    }



    public BluetoothAdapter getBAdapter(){
        return this.mBlueAdapter;
    }

    public void setBAdapter(BluetoothAdapter alfa){
        this.mBlueAdapter=alfa;
    }




    public BluetoothDevice getPBDevice(){
        return this.pairedBluetoothDevice;
    }

    public void setPBDevice(BluetoothDevice alfa){
        this.pairedBluetoothDevice=alfa;
    }


    public String getUID(){
        return uniqueId;
    }

    public void setUID(String alfa){
        this.uniqueId=alfa;
    }


    public String getMac(){
        return mac;
    }

    public void setMac(String alfa){
        this.mac=alfa;
    }



}
