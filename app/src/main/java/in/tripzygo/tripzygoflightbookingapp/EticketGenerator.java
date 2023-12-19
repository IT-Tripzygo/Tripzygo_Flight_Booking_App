package in.tripzygo.tripzygoflightbookingapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gkemon.XMLtoPDF.PdfGenerator;
import com.gkemon.XMLtoPDF.PdfGeneratorListener;
import com.gkemon.XMLtoPDF.model.FailureResponse;
import com.gkemon.XMLtoPDF.model.SuccessResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.text.NumberFormat;
import java.util.Locale;

import in.tripzygo.tripzygoflightbookingapp.Tools.SharedPreference;

public class EticketGenerator extends AppCompatActivity {
    RecyclerView recyclerView, recyclerForReturn;
    TextView basePrice, AirlineTaxesFee, ConvenientFee, TotalPaid,IdTextView;
    private PdfGenerator.XmlToPDFLifecycleObserver xmlToPDFLifecycleObserver;
    String bookingId;
    ConstraintLayout ticketConstraintLayout;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eticket_generator);
        ticketConstraintLayout=findViewById(R.id.ticketLayout);
        IdTextView=ticketConstraintLayout.findViewById(R.id.idText);
        String data = getIntent().getStringExtra("data");
        bookingId = getIntent().getStringExtra("bookingId");
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(data, new TypeToken<JsonObject>() {
        }.getType());

        JsonArray jsonElements = jsonObject.getAsJsonObject("AIR").getAsJsonArray("tripInfos").get(0).getAsJsonObject().getAsJsonArray("sI");
        JsonArray length = jsonObject.getAsJsonObject("AIR").getAsJsonArray("tripInfos");

        JsonArray travellersInfo = jsonObject.getAsJsonObject("AIR").getAsJsonArray("travellerInfos");
        recyclerView = ticketConstraintLayout.findViewById(R.id.recycler);
        IdTextView.setText(bookingId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        AdapterSI1 adapterSI11 = new AdapterSI1(EticketGenerator.this, jsonElements, travellersInfo);
        recyclerView.setAdapter(adapterSI11);
        if (length.size() > 1) {
            JsonArray jsonSI2 = jsonObject.getAsJsonObject("AIR").getAsJsonArray("tripInfos").get(1).getAsJsonObject().getAsJsonArray("sI");
            TextView textView = ticketConstraintLayout.findViewById(R.id.textReturn);
            textView.setVisibility(View.VISIBLE);
            recyclerForReturn = ticketConstraintLayout.findViewById(R.id.recyclerForPayment);
            recyclerForReturn.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            AdapterSI2 adapterSI2 = new AdapterSI2(EticketGenerator.this, jsonSI2, travellersInfo);
            recyclerForReturn.setAdapter(adapterSI2);
        }


        // PAYMENT - STRUCTURE --------------------

        JsonObject payment = jsonObject.getAsJsonObject("AIR").getAsJsonObject("totalPriceInfo").getAsJsonObject("totalFareDetail").getAsJsonObject("fC");
        basePrice = ticketConstraintLayout.findViewById(R.id.basePrice);
        AirlineTaxesFee = ticketConstraintLayout.findViewById(R.id.AirlineTaxAndFee);
        ConvenientFee = ticketConstraintLayout.findViewById(R.id.ConvenientFee);
        TotalPaid = ticketConstraintLayout.findViewById(R.id.totalFee);
        Locale locale = new Locale.Builder().setLanguage("en").setRegion("IN").build();
        NumberFormat formatter= NumberFormat.getCurrencyInstance(locale);
        String basecurrency=formatter.format(payment.get("BF").getAsInt());
        String taxcurrency=formatter.format(payment.get("TAF").getAsInt());
        String totalcurrency=formatter.format(payment.get("TF").getAsInt());
        int basecentsIndex = basecurrency.lastIndexOf(".00");
        int taxcentsIndex = taxcurrency.lastIndexOf(".00");
        int totalcentsIndex = totalcurrency.lastIndexOf(".00");
        if (taxcentsIndex != -1) {
            taxcurrency = taxcurrency.substring(0, taxcentsIndex);
        }
        if (totalcentsIndex != -1) {
            totalcurrency = totalcurrency.substring(0, totalcentsIndex);
        }
        if (basecentsIndex != -1) {
            basecurrency = basecurrency.substring(0, basecentsIndex);
        }
        basePrice.setText(basecurrency);
        AirlineTaxesFee.setText(taxcurrency);
        TotalPaid.setText(totalcurrency);

        float bPrice = payment.get("BF").getAsFloat();


        // XML - PDF GENERATION PROCESS --------------------

        xmlToPDFLifecycleObserver = new PdfGenerator.XmlToPDFLifecycleObserver(this);
        getLifecycle().addObserver(xmlToPDFLifecycleObserver);

        findViewById(R.id.generate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.generate).setVisibility(View.GONE);
                generatePDF(ticketConstraintLayout);
            }
        });
    }

    private void generatePDF(View view) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.activity_eticket_generator,  null);

        String name = SharedPreference.loadUser(EticketGenerator.this).getUser_name();
//        View content2 = inflater.inflate(R.layout.pdf_layout2, null);
        PdfGenerator.getBuilder()
                .setContext(EticketGenerator.this)
                .fromViewSource()
                .fromView(view)
                .setFileName("Tripzygo Booking Mr. " + name + " " + bookingId)
                .savePDFSharedStorage(xmlToPDFLifecycleObserver)
                .actionAfterPDFGeneration(PdfGenerator.ActionAfterPDFGeneration.OPEN)
                .build(new PdfGeneratorListener() {
                    @Override
                    public void onFailure(FailureResponse failureResponse) {
                        super.onFailure(failureResponse);
                        Gson gson = new Gson();
                        Log.e("BTN_ERROR", gson.toJson(failureResponse));
                        Toast.makeText(EticketGenerator.this, failureResponse.toString(), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onStartPDFGeneration() {
                        Toast.makeText(EticketGenerator.this, "PDF generating...", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFinishPDFGeneration() {
                        Toast.makeText(EticketGenerator.this, "PDF Downloading Finished!", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(SuccessResponse response) {
                        super.onSuccess(response);
                        Toast.makeText(EticketGenerator.this, "PDF Downloaded Successfully!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

}