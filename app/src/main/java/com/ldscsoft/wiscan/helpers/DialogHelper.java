package com.ldscsoft.wiscan.helpers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ldscsoft.wiscan.activities.MainActivity;
import com.ldscsoft.wiscan.tasks.ExportDatabaseTask;

/**
 * Created by David on 22/01/2015.
 */
public class DialogHelper {

    private String fileName;
    private MainActivity mainActivity;
    private AlertDialog selectionDialog;
    private AlertDialog fileNameDialog;
    private Boolean alertReady;


    public DialogHelper(MainActivity m){
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

                    }
                }).setNegativeButton("Descartar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                }).create();

        /*Metodo para evitar que el cuadro de dialogo se cierre
        en caso de ingresar un nombre de archivo no valido*/
        alertReady = false;
        fileNameDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                if (!alertReady) {
                    Button button = fileNameDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            fileName = input.getText().toString().trim();
                            if (fileName.matches("[-_A-Za-z0-9]+")) {
                                new ExportDatabaseTask(mainActivity).execute(fileName);
                                fileNameDialog.dismiss();
                            }
                            else {
                                Toast.makeText(mainActivity, "Nombre de archivo no valido\n(Evite usar espacios)", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    alertReady = true;
                }
            }
        });
        /*Fin de metodo indicado arriba*/

    }


    private void buildSelectionDialog(){
    selectionDialog= new AlertDialog.Builder(mainActivity)
            .setMessage("Desea guardar o descartar la data?")
            .setTitle("Fin de experimento")
            .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    showFileNameDialog();
                }
            })
            .setNegativeButton("Descartar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
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
    }
}