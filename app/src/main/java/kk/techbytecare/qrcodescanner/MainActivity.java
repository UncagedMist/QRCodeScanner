package kk.techbytecare.qrcodescanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.blikoon.qrcodescanner.QrCodeActivity;

public class MainActivity extends AppCompatActivity {

    public static final int REQ_CODE_QR = 101;
    public static final int CAMERA_REQ_CODE = 24;

    Button btnScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnScan = findViewById(R.id.btnScan);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanBarCode();
            }
        });
    }

    private void scanBarCode() {
        //check app have camera permission
        if (canOpenCamera())    {
            openCamera();
        }
        else    {
            //if no permission then request permission
            requestCameraPermission();
        }

    }

    private boolean canOpenCamera() {
        //get permission status
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        //if permission granted return true
        if (permission == PackageManager.PERMISSION_GRANTED)
            return true;

        //else return false
        return false;
    }

    private void openCamera() {
        //open camera
        Intent intent = new Intent(MainActivity.this, QrCodeActivity.class);
        startActivityForResult(intent,REQ_CODE_QR);
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA))   {
            Toast.makeText(this, "Permission required to Access Camera", Toast.LENGTH_SHORT).show();
        }
        ActivityCompat.requestPermissions(this,new String[] {
                Manifest.permission.CAMERA,
                Manifest.permission.VIBRATE
        },CAMERA_REQ_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //checking the user allows the permission request or denies it.

        if (requestCode == CAMERA_REQ_CODE) {
            //if permission granted

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)    {
                openCamera();
            }
            else    {
                Toast.makeText(this, "Permission Denied to Open the Camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //after scan results

        if (resultCode != Activity.RESULT_OK )   {

            if (data == null)   {
                return;
            }

            //getting the good result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if (result != null) {
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
                dialog.setTitle("Error Result");
                dialog.setMessage("QR code can't be scanned");
                dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                dialog.show();
            }
            return;
        }

        if (requestCode == REQ_CODE_QR) {

            if (data == null)   {
                return;
            }
            String scanResult = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            Log.d("RESULT : ", ""+scanResult);
            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
            dialog.setTitle("Scan Result");
            dialog.setMessage(scanResult);
            dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            dialog.show();

        }

    }
}
