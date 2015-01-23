package com.android.wiscan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by David on 22/01/2015.
 */
public class DialogHelper {

    String fileName;
    private MainActivity mainActivity;
    private AlertDialog selectionDialog;
    private AlertDialog fileNameDialog;

    DialogHelper(MainActivity m){
        mainActivity = m;
        buildSelectionDialog();
        buildFilenameDialog();
    }

    private void buildFilenameDialog() {
        final EditText input = new EditText(mainActivity);
        fileNameDialog = new AlertDialog.Builder(mainActivity)
                .setTitle("Indique el nombre del archivo")
                .setView(input)
                .setPositiveButton("Exportar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        fileName = input.getText().toString();
                        /*Snippet para escribir a CSV
                        CSVWriter writer = null;
                        try {
                            writer = new CSVWriter(new FileWriter(fileName+".csv"), '\t');
                            // feed in your array (or convert your data to an array)
                            String[] entries = "first#second#third".split("#");
                            writer.writeNext(entries);
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                            */
/*Snippet para exportar la BD*/
/*
                        private void exportDB(){
                            File sd = Environment.getExternalStorageDirectory();
                            File data = Environment.getDataDirectory();
                            FileChannel source=null;
                            FileChannel destination=null;
                            String currentDBPath = "/data/"+ "com.authorwjf.sqliteexport" +"/databases/"+SAMPLE_DB_NAME;
                            String backupDBPath = SAMPLE_DB_NAME;
                            File currentDB = new File(data, currentDBPath);
                            File backupDB = new File(sd, backupDBPath);
                            try {
                                source = new FileInputStream(currentDB).getChannel();
                                destination = new FileOutputStream(backupDB).getChannel();
                                destination.transferFrom(source, 0, source.size());
                                source.close();
                                destination.close();
                                Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
                            } catch(IOException e) {
                                e.printStackTrace();
                            }
                        }*/
                        Log.v("PRUEBA DIALOG", "NOMBRE DE ARCHIVO: " + fileName);
                        mainActivity.imprimirDBenLog();
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                }).create();
    }


    private void buildSelectionDialog(){
    selectionDialog= new AlertDialog.Builder(mainActivity)
            .setMessage("Desea guardar o descartar la data?")
            .setTitle("Fin de experimento")
            .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Toast.makeText(mainActivity, "SE PRESIONO GUARDAR", Toast.LENGTH_LONG).show();
                    //TODO Llamar al metodo para solicitar nombre de archivo y guardarlo
                    dialog.dismiss();
                    showFileNameDialog();
                }
            })
            .setNegativeButton("Descartar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Toast.makeText(mainActivity, "SE PRESIONO DESCARTAR", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }).create();
}
    public void showSelectionDialog() {
        if(!selectionDialog.isShowing())
            selectionDialog.show();
    }

    private void showFileNameDialog() {
        if(!fileNameDialog.isShowing())
            fileNameDialog.show();
        //CSVWriter writer = new CSVWriter()
    }
}