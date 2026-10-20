package com.example.smartbussystem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.OutputStream;

public class TicketViewActivity
        extends AppCompatActivity {

    TextView tvTicketInfo;
    ImageView ivQR;
    Button btnDownloadPDF;

    FirebaseFirestore db;

    Bitmap qrBitmap;

    String passengerName,
            route,
            from,
            to,
            date,
            time,
            busNumber,
            ticketId;

    long passengers,
            total;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        AppCompatDelegate
                .setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO
                );

        setContentView(
                R.layout.activity_ticket_view
        );

        tvTicketInfo =
                findViewById(
                        R.id.tvTicketInfo
                );

        ivQR =
                findViewById(
                        R.id.ivQR
                );

        btnDownloadPDF =
                findViewById(
                        R.id.btnDownloadPDF
                );

        db =
                FirebaseFirestore
                        .getInstance();

        ticketId =
                getIntent()
                        .getStringExtra(
                                "ticketId"
                        );

        loadTicket(
                ticketId
        );

        btnDownloadPDF
                .setOnClickListener(v -> {

                    createPDF();

                });
    }

    private void loadTicket(
            String ticketId
    ) {

        db.collection("tickets")
                .document(ticketId)
                .get()
                .addOnSuccessListener(doc -> {

                    if(doc.exists()) {

                        passengerName =
                                doc.getString(
                                        "passengerName"
                                );

                        route =
                                doc.getString(
                                        "route"
                                );

                        from =
                                doc.getString(
                                        "from"
                                );

                        to =
                                doc.getString(
                                        "to"
                                );

                        date =
                                doc.getString(
                                        "date"
                                );

                        time =
                                doc.getString(
                                        "time"
                                );

                        busNumber =
                                doc.getString(
                                        "busNumber"
                                );

                        passengers =
                                doc.getLong(
                                        "passengers"
                                );

                        total =
                                doc.getLong(
                                        "total"
                                );

                        String info =

                                "🎫 Ticket ID: "
                                        + ticketId

                                        + "\n\n👤 Name: "
                                        + passengerName

                                        + "\n\n🚌 Route: "
                                        + route

                                        + "\n\n🚌 Bus No: "
                                        + busNumber

                                        + "\n\n📍 From: "
                                        + from

                                        + "\n📍 To: "
                                        + to

                                        + "\n\n📅 Date: "
                                        + date

                                        + "\n🕒 Time: "
                                        + time

                                        + "\n\n👥 Passengers: "
                                        + passengers

                                        + "\n\n💰 Total: ₹"
                                        + total;

                        tvTicketInfo
                                .setText(
                                        info
                                );

                        generateQR(
                                ticketId
                        );
                    }

                });
    }

    private void generateQR(
            String ticketId
    ) {

        try {

            String baseURL =
                    "https://smartbus-system.netlify.app/ticket.html";

            String qrURL =
                    baseURL
                            + "?id="
                            + ticketId;

            QRCodeWriter writer =
                    new QRCodeWriter();

            var bitMatrix =
                    writer.encode(
                            qrURL,
                            BarcodeFormat.QR_CODE,
                            700,
                            700
                    );

            qrBitmap =
                    Bitmap.createBitmap(
                            700,
                            700,
                            Bitmap.Config.RGB_565
                    );

            for(int x = 0;
                x < 700;
                x++)
            {

                for(int y = 0;
                    y < 700;
                    y++)
                {

                    qrBitmap.setPixel(
                            x,
                            y,
                            bitMatrix.get(x, y)
                                    ?
                                    android.graphics.Color.BLACK
                                    :
                                    android.graphics.Color.WHITE
                    );
                }
            }

            ivQR.setImageBitmap(
                    qrBitmap
            );

        }

        catch(Exception e){

            e.printStackTrace();
        }
    }

    private void createPDF() {

        Intent intent =
                new Intent(
                        Intent.ACTION_CREATE_DOCUMENT
                );

        intent.addCategory(
                Intent.CATEGORY_OPENABLE
        );

        intent.setType(
                "application/pdf"
        );

        intent.putExtra(
                Intent.EXTRA_TITLE,
                "SmartBus_"
                        + ticketId
                        + ".pdf"
        );

        startActivityForResult(
                intent,
                101
        );
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if(requestCode == 101
                &&
                resultCode == RESULT_OK
                &&
                data != null)
        {

            Uri uri =
                    data.getData();

            try {

                PdfDocument pdfDocument =
                        new PdfDocument();

                PdfDocument.PageInfo pageInfo =
                        new PdfDocument
                                .PageInfo.Builder(
                                1400,
                                900,
                                1
                        )
                                .create();

                PdfDocument.Page page =
                        pdfDocument
                                .startPage(
                                        pageInfo
                                );

                Canvas canvas =
                        page.getCanvas();

                Paint titlePaint =
                        new Paint();

                Paint textPaint =
                        new Paint();

                Paint borderPaint =
                        new Paint();

                // Background
                canvas.drawRGB(
                        244,
                        247,
                        251
                );

                // White Card
                Paint boxPaint =
                        new Paint();

                boxPaint.setColor(
                        android.graphics.Color.WHITE
                );

                canvas.drawRoundRect(
                        50,
                        50,
                        1350,
                        850,
                        20,
                        20,
                        boxPaint
                );

                // Border
                borderPaint.setStyle(
                        Paint.Style.STROKE
                );

                borderPaint.setStrokeWidth(
                        4
                );

                canvas.drawRoundRect(
                        50,
                        50,
                        1350,
                        850,
                        20,
                        20,
                        borderPaint
                );

                // Title
                titlePaint.setTextSize(
                        50
                );

                titlePaint
                        .setFakeBoldText(
                                true
                        );

                canvas.drawText(
                        "🎫 SmartBus Ticket",
                        470,
                        120,
                        titlePaint
                );

                // Left Data
                textPaint.setTextSize(
                        30
                );

                int y = 220;

                canvas.drawText(
                        "Ticket ID: "
                                + ticketId,
                        100,
                        y,
                        textPaint
                );

                y += 60;

                canvas.drawText(
                        "Passenger: "
                                + passengerName,
                        100,
                        y,
                        textPaint
                );

                y += 60;

                canvas.drawText(
                        "Route: "
                                + route,
                        100,
                        y,
                        textPaint
                );

                y += 60;

                canvas.drawText(
                        "Bus Number: "
                                + busNumber,
                        100,
                        y,
                        textPaint
                );

                y += 60;

                canvas.drawText(
                        "From: "
                                + from,
                        100,
                        y,
                        textPaint
                );

                y += 60;

                canvas.drawText(
                        "To: "
                                + to,
                        100,
                        y,
                        textPaint
                );

                y += 60;

                canvas.drawText(
                        "Passengers: "
                                + passengers,
                        100,
                        y,
                        textPaint
                );

                y += 60;

                canvas.drawText(
                        "Date: "
                                + date,
                        100,
                        y,
                        textPaint
                );

                y += 60;

                canvas.drawText(
                        "Time: "
                                + time,
                        100,
                        y,
                        textPaint
                );

                y += 60;

                canvas.drawText(
                        "Total Fare: ₹"
                                + total,
                        100,
                        y,
                        textPaint
                );

                // QR Title
                titlePaint.setTextSize(
                        36
                );

                canvas.drawText(
                        "Scan QR",
                        980,
                        180,
                        titlePaint
                );

                // QR Image
                canvas.drawBitmap(
                        qrBitmap,
                        null,
                        new Rect(
                                900,
                                220,
                                1250,
                                570
                        ),
                        null
                );

                textPaint.setTextSize(
                        22
                );

                canvas.drawText(
                        "Scan QR to validate ticket",
                        900,
                        620,
                        textPaint
                );

                canvas.drawText(
                        "Powered by SmartBus",
                        520,
                        790,
                        textPaint
                );

                pdfDocument.finishPage(
                        page
                );

                OutputStream outputStream =
                        getContentResolver()
                                .openOutputStream(
                                        uri
                                );

                pdfDocument.writeTo(
                        outputStream
                );

                outputStream.close();

                pdfDocument.close();

                Toast.makeText(
                        this,
                        "Ticket PDF Saved Successfully",
                        Toast.LENGTH_LONG
                ).show();

            }

            catch(Exception e){

                e.printStackTrace();

                Toast.makeText(
                        this,
                        e.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }
}