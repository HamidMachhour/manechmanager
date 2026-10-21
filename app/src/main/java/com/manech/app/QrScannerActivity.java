package com.manech.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.common.InputImage;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QrScannerActivity extends AppCompatActivity {
    private PreviewView previewView;
    private ExecutorService cameraExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);
        previewView = findViewById(R.id.previewView);
        cameraExecutor = Executors.newSingleThreadExecutor();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
        startCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA}, 101);
    }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_SHORT).show();
            finish();
    }
}

    private void startCamera() {
        final com.google.common.util.concurrent.ListenableFuture<ProcessCameraProvider> cameraProviderFuture = 
            ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
                bindAnalysis(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e("QrScanner", "Camera provider failed", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreview(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        try {
            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview);
        } catch (Exception e) {
            Log.e("QrScanner", "Failed to bind preview", e);
        }
    }

    private void bindAnalysis(ProcessCameraProvider cameraProvider) {
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, image -> {
            // FIX: Use the correct ImageProxy class from androidx.camera.core
            androidx.camera.core.ImageProxy imageProxy = image;
            
            InputImage inputImage = InputImage.fromMediaImage(
                imageProxy.getImage(), 
                imageProxy.getImageInfo().getRotationDegrees()
            );

            BarcodeScanner scanner = BarcodeScanning.getClient();
            scanner.process(inputImage)
                .addOnSuccessListener(barcodes -> {
                    // Use the base type for barcodes to avoid explicit Barcode class import if it's problematic
                    for (Object barcodeObj : barcodes) {
                        // Cast to the internal ML Kit barcode type via reflection or generic access if needed
                        // But let's try the most common path first
                        try {
                            java.lang.reflect.Method getRawValueMethod = barcodeObj.getClass().getMethod("getRawValue");
                            String value = (String) getRawValueMethod.invoke(barcodeObj);
                            if (value != null) {
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("SCAN_RESULT", value);
                                setResult(RESULT_OK, resultIntent);
                                finish();
                            }
                        } catch (Exception e) {
                            Log.e("QrScanner", "Error extracting value", e);
                        }
                    }
                })
                .addOnCompleteListener(task -> imageProxy.close());
        });

        try {
            // Note: we don't unbindAll here because we want to keep the preview
            cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, imageAnalysis);
        } catch (Exception e) {
            Log.e("QrScanner", "Failed to bind analysis", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}

