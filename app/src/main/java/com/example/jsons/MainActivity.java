package com.example.jsons;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {
    private EditText editTextId;
    String dataparsed="";
    String singleparsed="";
    Adapter mAdapter;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1213;
    final String url= "http://192.168.43.153/lipaa/lipa/tkt.php";
    private SurfaceView cameraView;
    private TextView tvCode,tvInfo;
    private Button btnCheckIn, btnCancel;
    private LinearLayout lytTicketInfo;
    private ProgressBar pbTicketInfo;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private String  QR;
    private Boolean reading = true;
    Button account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }

        lytTicketInfo = (LinearLayout) findViewById(R.id.lytTicketInfo);
        tvCode = (TextView) findViewById(R.id.code_info);
        pbTicketInfo = (ProgressBar) findViewById(R.id.pbTicketInfo);
        tvInfo = (TextView) findViewById(R.id.tvTicketInfo);
        tvInfo.setMovementMethod(new ScrollingMovementMethod());
        ((NestedScrollView) findViewById(R.id.nsvTicket)).setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tvInfo.getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        tvInfo.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                tvInfo.getParent().requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reading = true;
                lytTicketInfo.setVisibility(View.GONE);
                tvCode.setText("");
            }
        });
        btnCheckIn = (Button) findViewById(R.id.btnCheckIn);
        btnCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //checkinfo();
            }
        });

        cameraView = (SurfaceView) findViewById(R.id.camera_view);


        barcodeDetector =
                new BarcodeDetector.Builder(this)
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();

        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException ie) {
                    Log.e("CAMERA SOURCE", ie.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {
                    tvCode.post(new Runnable() {    // Use the post method of the TextView
                        public void run() {
                            if (reading) {
                                reading = false;
                                QR = barcodes.valueAt(0).displayValue;
                                tvCode.setText(QR);
                                getinfo(QR);
                            }
                        }
                    });
                }
            }
        });
    }
    public void getinfo(String QR){

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://173.212.193.182:3000/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    ApiService service = retrofit.create(ApiService.class);
    Call<MetaData> call=service.getStringScalar(new Qrcode(QR));
        call.enqueue(new Callback<MetaData>()  {
        @Override
        public void onResponse(Call<MetaData> call, Response<MetaData> response) {

            if(response.isSuccessful()) {

                Toast.makeText(MainActivity.this, "successful response", Toast.LENGTH_SHORT).show();
            }else {
                int statusCode  = response.code();
                Toast.makeText(MainActivity.this, "response failed", Toast.LENGTH_SHORT).show();
                // handle request errors depending on status code
            }
        }

        @Override
        public void onFailure(Call<MetaData> call, Throwable t) {

            Toast.makeText(MainActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
        }
    });
    }

}

