package com.manech.app.helper;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.manech.app.data.ManechNode;

public class QrCodeHelper {

    public static Bitmap generateQrCode(String text, int size) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size);
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File generateSingleNodePdf(ManechNode node) {
        try {
            PdfDocument document = new PdfDocument();
            int size = 400;
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(size, size, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            
            Canvas canvas = page.getCanvas();
            
            // Get config JSON from node
            JSONObject config = node.prepareConfiguration();
            String jsonString = (config != null) ? config.toString() : "";
            
            Bitmap qrBitmap = generateQrCode(jsonString, 300);
            if (qrBitmap != null) {
                canvas.drawBitmap(qrBitmap, (size - 300) / 2, 50, null);
            }
            
            Paint paint = new Paint();
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(18f);
            paint.setColor(Color.BLACK);
            canvas.drawText(node.getName(), size / 2f, 370, paint);
            
            document.finishPage(page);
            
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File filePath = new File(downloadsDir, "Manech_" + node.getName() + ".pdf");
            
            document.writeTo(new FileOutputStream(filePath));
            document.close();
            
            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File generateConfigPdf(List<ManechNode> nodes) {
        try {
            PdfDocument document = new PdfDocument();
            // A4 size approximately 595 x 842 points
            int pageWidth = 595;
            int pageHeight = 842;
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(12f);
            paint.setColor(Color.BLACK);
            
            int cols = 3;
            int qrSize = 150;
            int margin = 50;
            int spacingX = (pageWidth - (cols * qrSize)) / (cols + 1);
            int spacingY = 200; 

            for (int i = 0; i < nodes.size(); i++) {
                ManechNode node = nodes.get(i);
                int col = i % cols;
                int row = i / cols;
                
                int x = margin + col * (qrSize + spacingX);
                int y = margin + row * spacingY;
                
                JSONObject config = node.prepareConfiguration();
                String jsonString = (config != null) ? config.toString() : "";
                
                Bitmap qrBitmap = generateQrCode(jsonString, qrSize);
                if (qrBitmap != null) {
                    canvas.drawBitmap(qrBitmap, x, y, null);
                }
                canvas.drawText(node.getName(), x + qrSize / 2f, y + qrSize + 20, paint);
            }
            
            document.finishPage(page);
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File filePath = new File(downloadsDir, "ManechNodes_QR_All.pdf");
            
            document.writeTo(new FileOutputStream(filePath));
            document.close();
            
            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ManechNode parseConfigJson(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            
            ManechNode node = new ManechNode(
                0, 
                json.optString("hostname", "Unknown Node"),
                "", 
                json.optString("www_username", "admin"),
                json.optString("www_password", "admin"),
                json.optInt("min_leds", 0),
                json.optInt("max_leds", 63),
                json.optString("ap_ssid", ""),
                json.optString("ap_password", ""),
                json.optString("ap_ip", ""),
                json.optString("ap_mask", ""),
                json.optString("ap_gw", ""),
                json.optString("wifi_ssid", ""),
                json.optString("wifi_password", ""),
                json.optString("wifi_ip", ""),
                json.optString("wifi_mask", ""),
                json.optString("wifi_gw", "")
            );
            return node;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

