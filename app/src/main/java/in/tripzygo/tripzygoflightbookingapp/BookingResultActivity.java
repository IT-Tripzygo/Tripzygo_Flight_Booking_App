package in.tripzygo.tripzygoflightbookingapp;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfDiv;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import in.tripzygo.tripzygoflightbookingapp.Modals.Booking;
import in.tripzygo.tripzygoflightbookingapp.Retroapi.ApiInterface;
import in.tripzygo.tripzygoflightbookingapp.Retroapi.Retrofitt;
import in.tripzygo.tripzygoflightbookingapp.Tools.SharedPreference;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingResultActivity extends AppCompatActivity implements Animator.AnimatorListener {
    TextView congotxt;
    Document document;
    String name, classOfT, email, Invoice, BookingDate, PNR, bookingId;

    Button backToHomeButton, ViewBooking;
    boolean booked;
    TextView oneWayDepartureCityText, oneWayArrivalCityText, roundWayText, returnDepartureCityText, returnArrivalCityText,
            airline_name_oneWay, airline_name_return, returnDepartureTimeText, oneWayDepartureTimeText, returnArrivalTimeText,
            oneWayArrivalTimeText, oneWayDepartureDateText, returnDepartureDateText,
            oneWayArrivalDateText, returnArrivalDateText;
    ImageView airlineImageOneWay, airlineImageReturn;
    StorageReference storageReference;
    boolean isChecked;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_result);
        booked = getIntent().getBooleanExtra("status", false);
        isChecked = getIntent().getBooleanExtra("gstCheck", false);
        String string = getIntent().getStringExtra("tripInfos");
        HashMap<String, Object> userMap = (HashMap<String, Object>) getIntent().getSerializableExtra("userMap");
        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(string, new TypeToken<JsonArray>() {
        }.getType());

        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
        JsonArray sI = jsonObject.getAsJsonArray("sI");
        JsonArray totalPriceList = jsonObject.getAsJsonArray("totalPriceList");
        storageReference = FirebaseStorage.getInstance().getReference().child("Invoices");

        String Image = sI.get(0).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("code").getAsString();
        oneWayDepartureCityText = findViewById(R.id.cityCodeDeparture);
        oneWayArrivalCityText = findViewById(R.id.cityCodeArrival);
        airline_name_oneWay = findViewById(R.id.airline_nameOneway);
        oneWayDepartureTimeText = findViewById(R.id.timeToDeparture);
        oneWayArrivalTimeText = findViewById(R.id.timeToArrival);
        oneWayDepartureDateText = findViewById(R.id.dateToDeparture);
        oneWayArrivalDateText = findViewById(R.id.dateToArrival);
        airlineImageOneWay = findViewById(R.id.flagImg);
        congotxt = findViewById(R.id.congotxt);
        backToHomeButton = findViewById(R.id.backToHome);
        ViewBooking = findViewById(R.id.viewBooking);
        airline_name_oneWay.setText(sI.get(0).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("name").getAsString());
        oneWayDepartureCityText.setText(sI.get(0).getAsJsonObject().getAsJsonObject("da").get("cityCode").getAsString());
        StorageReference storageReference1 = FirebaseStorage.getInstance().getReference().child("AirlineLogos").child(Image + ".png");
        storageReference1.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(BookingResultActivity.this).load(uri).into(airlineImageOneWay);
        }).addOnFailureListener(e -> {
            System.out.println("storageReference1 = " + storageReference1);
            System.out.println("e.getMessage() = " + e.getMessage());
        });
        if (sI.size() == 1) {
            oneWayArrivalCityText.setText(sI.get(0).getAsJsonObject().getAsJsonObject("aa").get("cityCode").getAsString());
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
            SimpleDateFormat df1 = new SimpleDateFormat("E, dd MMM yy", Locale.getDefault());
            SimpleDateFormat df2 = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String departure = sI.get(0).getAsJsonObject().get("dt").getAsString();
            String arrival = sI.get(0).getAsJsonObject().get("at").getAsString();
            try {
                Date dof = df.parse(departure);
                Date aol = df.parse(arrival);
                String dTime = df1.format(dof);
                String aTime = df1.format(aol);
                oneWayDepartureDateText.setText(dTime);
                oneWayArrivalDateText.setText(aTime);
                oneWayDepartureTimeText.setText(df2.format(dof));
                oneWayArrivalTimeText.setText(df2.format(aol));
                System.out.println("aTime = " + aTime + " dTime = " + dTime);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else {
            oneWayArrivalCityText.setText(sI.get(sI.size() - 1).getAsJsonObject().getAsJsonObject("aa").get("cityCode").getAsString());
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
            SimpleDateFormat df1 = new SimpleDateFormat("E, dd MMM yy", Locale.getDefault());
            SimpleDateFormat df2 = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String departureOfFirst = sI.get(0).getAsJsonObject().get("dt").getAsString();
            String arrivalOfLast = sI.get(sI.size() - 1).getAsJsonObject().get("at").getAsString();
            try {
                Date dof = df.parse(departureOfFirst);
                Date aol = df.parse(arrivalOfLast);
                String dTime = df1.format(dof);
                String aTime = df1.format(aol);
                oneWayDepartureDateText.setText(dTime);
                oneWayArrivalDateText.setText(aTime);
                oneWayDepartureTimeText.setText(df2.format(dof));
                oneWayArrivalTimeText.setText(df2.format(aol));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        if (jsonArray.size() > 1) {
            JsonObject jsonObject1 = jsonArray.get(1).getAsJsonObject();
            JsonArray returnsI = jsonObject1.getAsJsonArray("sI");
            JsonArray returnTotalPriceList = jsonObject1.getAsJsonArray("totalPriceList");
            String returnImage = returnsI.get(0).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("code").getAsString();
            returnDepartureCityText = findViewById(R.id.cityCodeDepartureRound);
            returnArrivalCityText = findViewById(R.id.cityCodeArrivalRound);
            airline_name_return = findViewById(R.id.airline_nameReturn);
            returnDepartureTimeText = findViewById(R.id.timeToDepartureRound);
            returnArrivalTimeText = findViewById(R.id.timeToArrivalRound);
            returnDepartureDateText = findViewById(R.id.dateToDepartureRound);
            returnArrivalDateText = findViewById(R.id.dateToArrivalRound);
            airlineImageReturn = findViewById(R.id.flagImgSecond);
            StorageReference storageReference2 = FirebaseStorage.getInstance().getReference().child("AirlineLogos").child(returnImage + ".png");
            storageReference2.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(BookingResultActivity.this).load(uri).into(airlineImageReturn);
            }).addOnFailureListener(e -> {
                System.out.println("storageReference1 = " + storageReference2);
                System.out.println("e.getMessage() = " + e.getMessage());
            });
            returnDepartureCityText.setText(returnsI.get(0).getAsJsonObject().getAsJsonObject("da").get("cityCode").getAsString());
            airline_name_return.setText(returnsI.get(0).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("name").getAsString());
            if (returnsI.size() == 1) {
                returnArrivalCityText.setText(returnsI.get(0).getAsJsonObject().getAsJsonObject("aa").get("cityCode").getAsString());
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
                SimpleDateFormat df1 = new SimpleDateFormat("E, dd MMM yy", Locale.getDefault());
                SimpleDateFormat df2 = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String departure = returnsI.get(0).getAsJsonObject().get("dt").getAsString();
                String arrival = returnsI.get(0).getAsJsonObject().get("at").getAsString();
                try {
                    Date dof = df.parse(departure);
                    Date aol = df.parse(arrival);
                    String dTime = df1.format(dof);
                    String aTime = df1.format(aol);
                    returnDepartureDateText.setText(dTime);
                    returnArrivalDateText.setText(aTime);
                    returnDepartureTimeText.setText(df2.format(dof));
                    returnArrivalTimeText.setText(df2.format(aol));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else {
                returnArrivalCityText.setText(returnsI.get(returnsI.size() - 1).getAsJsonObject().getAsJsonObject("aa").get("cityCode").getAsString());
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
                SimpleDateFormat df1 = new SimpleDateFormat("E, dd MMM yy", Locale.getDefault());
                SimpleDateFormat df2 = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String departureOfFirst = returnsI.get(0).getAsJsonObject().get("dt").getAsString();
                String arrivalOfLast = returnsI.get(returnsI.size() - 1).getAsJsonObject().get("at").getAsString();
                try {
                    Date dof = df.parse(departureOfFirst);
                    Date aol = df.parse(arrivalOfLast);
                    String dTime = df1.format(dof);
                    String aTime = df1.format(aol);
                    returnDepartureDateText.setText(dTime);
                    returnArrivalDateText.setText(aTime);
                    returnDepartureTimeText.setText(df2.format(dof));
                    returnArrivalTimeText.setText(df2.format(aol));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
            /*List<JsonObject> returnjsonObjects = new ArrayList<>();
            for (JsonElement jsonElement : returnTotalPriceList) {
                JsonObject jsonObject2 = jsonElement.getAsJsonObject();
                if (!jsonObject2.get("fareIdentifier").getAsString().matches("SPECIAL_RETURN")) {
                    returnjsonObjects.add(jsonObject2);
                }
            }
            returnjsonObjects.sort(new Sort());*/
        } else {
            ConstraintLayout constraintLayout = findViewById(R.id.RoundWay);
            roundWayText = findViewById(R.id.arrivalText1);
            roundWayText.setVisibility(View.GONE);
            constraintLayout.setVisibility(View.GONE);
        }
        if (booked) {
            congotxt.setText("Booking Confirmed");
        } else {
            congotxt.setText("Booking Failed");
            congotxt.setTextColor(Color.RED);
        }
//        lottieAnimationView.setRepeatCount(0);
//        lottieAnimationView.setRepeatMode(LottieDrawable.RESTART);
//        lottieAnimationView.playAnimation();
//        lottieAnimationView.addAnimatorListener(BookingResultActivity.this);
        backToHomeButton.setOnClickListener(view -> {
            startActivity(new Intent(BookingResultActivity.this, MainActivity.class));
        });
        ViewBooking.setOnClickListener(view -> {
            Booking booking = new Booking();
            System.out.println("userMap = " + userMap);
            booking.setBookingId(userMap.get("bookingId").toString());
            booking.setDeparture(userMap.get("departure").toString());
            booking.setTripInfos(userMap.get("tripInfos").toString());
            startActivity(new Intent(BookingResultActivity.this, DisplayBookingActivity.class).putExtra("booking", String.valueOf(gson.toJson(booking))));
        });
        bookingId = userMap.get("bookingId").toString();
        ApiInterface apiInterface = Retrofitt.initretro().create(ApiInterface.class);
        JsonObject jsonObject1 = new JsonObject();
        jsonObject1.addProperty("bookingId", bookingId);
        Call<JsonObject> call = apiInterface.retrieveBooking(jsonObject1);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject2 = response.body();
                    JsonObject jsonObject3 = jsonObject2.getAsJsonObject("itemInfos");
                    BookingDate = jsonObject2.getAsJsonObject("order").get("createdOn").getAsString();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
                    SimpleDateFormat outputSdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
                    Date date = null;
                    try {
                        date = df.parse(BookingDate);
                        assert date != null;
                        BookingDate = outputSdf.format(date);
                        document = new Document();
                        name = "Meet Vani";
                        classOfT = "Economy";
                        email = "meetvani6@gmail.com";
                        SequenceGenerator generator = new SequenceGenerator();
                        generator.generateNewSequenceNumber(new SequenceGenerator.SequenceCallback() {
                            @Override
                            public void onNewSequenceNumber(String sequenceNumber) {
                                Invoice = sequenceNumber;
                                generateInvoice(document, jsonObject3, Invoice);
                            }

                            @Override
                            public void onError(DatabaseError error) {

                            }
                        });

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    // Format the Date object into the desired format


                } else {
                    System.out.println("response = " + response);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println("t = " + t);
            }
        });
    }

    public void generateInvoice(Document document, JsonObject jsonObject, String Invoice) {
        try {
            File pdfFile = new File(getExternalFilesDir(null), "TripzygoBooking " + bookingId + ".pdf");
            FileOutputStream fileOutputStream = new FileOutputStream(pdfFile);
            PdfWriter.getInstance(document, fileOutputStream);
            document.open();

            // Add a title
            /*Font logoName = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph titleLogo = new Paragraph("TRIPZYGO", logoName);
            titleLogo.setAlignment(Element.ALIGN_LEFT);
            titleLogo.setSpacingBefore(10f);
            document.add(titleLogo);*/

            // Add a title
            Font titleInvoice = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph textInvoice = new Paragraph("Invoice", titleInvoice);
            textInvoice.setAlignment(Element.ALIGN_RIGHT);
            textInvoice.setSpacingAfter(10f);
            document.add(textInvoice);

            // Add invoice details
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);

            Font fontCellTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);

            table.addCell(createCell("Travbox", PdfPCell.ALIGN_LEFT, fontCellTitle));
            table.addCell(createCell(SharedPreference.loadUser(BookingResultActivity.this).getUser_name(), PdfPCell.ALIGN_RIGHT, fontCellTitle));
            table.addCell(createCell("4th floor Pratibha Tower, South City 1, Sector 41 Gurgaon, Haryana 122022", PdfPCell.ALIGN_LEFT));
            table.addCell(createCell("Mobile No: " + SharedPreference.loadUser(BookingResultActivity.this).getPhone_no(), PdfPCell.ALIGN_RIGHT));
            table.addCell(createCell("Tel: +91 89290-00444", PdfPCell.ALIGN_LEFT));
            table.addCell(createCell("Email:" + SharedPreference.loadUser(BookingResultActivity.this).getEmail_id(), PdfPCell.ALIGN_RIGHT));
            table.addCell(createCell("Email: care@travbox.in", PdfPCell.ALIGN_LEFT));
            if (isChecked) {
                table.addCell(createCell("GSTIN: "+SharedPreference.loadUser(BookingResultActivity.this).getGstNumber(), PdfPCell.ALIGN_RIGHT));
            } else {
                table.addCell(createCell("GSTIN:", PdfPCell.ALIGN_RIGHT));
            }
            table.addCell(createCell(" ", PdfPCell.ALIGN_LEFT));
            table.addCell(createCell(" ", PdfPCell.ALIGN_LEFT));
            table.addCell(createCell(" ", PdfPCell.ALIGN_RIGHT));
            table.addCell(createCell(" ", PdfPCell.ALIGN_LEFT));


            document.add(table);

            // Header separator
            LineSeparator line = new LineSeparator();
            line.setLineWidth(1);
            line.setPercentage(100);
            line.setAlignment(Element.ALIGN_CENTER);
            line.setLineColor(BaseColor.DARK_GRAY);
            document.add(line);


            // TAX details
            PdfPTable tdTable = new PdfPTable(2);
            tdTable.setWidthPercentage(100);
            tdTable.setSpacingBefore(10f);
            tdTable.setSpacingAfter(10f);

            tdTable.addCell(createCell("Invoice No. :", PdfPCell.ALIGN_LEFT));
            tdTable.addCell(createCell("Booking Date :", PdfPCell.ALIGN_LEFT));
            JsonArray travellersInfo1 = jsonObject.getAsJsonObject("AIR").getAsJsonArray("travellerInfos");
            JsonObject travellersObject1 = travellersInfo1.get(0).getAsJsonObject();
            JsonObject pnrDetail1 = travellersObject1.getAsJsonObject("pnrDetails");
            JsonArray jsonElements = jsonObject.getAsJsonObject("AIR").getAsJsonArray("tripInfos").get(0).getAsJsonObject().getAsJsonArray("sI");
            JsonObject DA = jsonElements.get(0).getAsJsonObject().getAsJsonObject("da");
            JsonObject AA = jsonElements.get(0).getAsJsonObject().getAsJsonObject("aa");
            String str = DA.get("code").getAsString() + "-" + AA.get("code").getAsString();
            PNR = pnrDetail1.get(str).getAsString();
            tdTable = ticketDetails(tdTable, Invoice, BookingDate);

            document.add(tdTable);
            document.add(line);
// ----------------------  T R A V E L L E R ' S    D E T A I L --------------------------------------------------
            String flight = jsonElements.get(0).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("name").getAsString() + " " + jsonElements.get(0).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("code").getAsString() + " " + jsonElements.get(0).getAsJsonObject().getAsJsonObject("fD").get("fN").getAsString();
            Paragraph onwards = new Paragraph("OneWords : " + DA.get("city").getAsString() + "-" + DA.get("code").getAsString() + "-" + AA.get("city").getAsString() + "-" + AA.get("code").getAsString() + ", " + flight, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
            onwards.setSpacingBefore(20f);
            onwards.setSpacingAfter(10f);
            onwards.setAlignment(Element.ALIGN_LEFT);
            document.add(onwards);
            PdfPTable travInfo = new PdfPTable(3);
            travInfo.setWidthPercentage(100);
            travInfo.setSpacingBefore(8f);
            travInfo.setSpacingAfter(2f);
            travInfo.addCell(createCell("Name", PdfPCell.ALIGN_LEFT));
            travInfo.addCell(createCell("Type", PdfPCell.ALIGN_LEFT));
            travInfo.addCell(createCell("PNR :", PdfPCell.ALIGN_LEFT));
            JsonArray travellersInfo = jsonObject.getAsJsonObject("AIR").getAsJsonArray("travellerInfos");
            for (JsonElement jsonElement : travellersInfo) {
                JsonObject travellersObject = jsonElement.getAsJsonObject();
                JsonObject pnrDetail = travellersObject.getAsJsonObject("pnrDetails");
                PNR = pnrDetail.get(str).getAsString();
                String Name = travellersObject.get("ti").getAsString() + " " + travellersObject.get("fN").getAsString() + " " + travellersObject.get("lN").getAsString();
                String TypeOfPassenger = travellersObject.get("pt").getAsString();
               /* String Basics = jsonObject.getAsJsonObject("AIR").getAsJsonObject("totalPriceInfo").getAsJsonObject("totalFareDetail").getAsJsonObject("fC").get("BF").getAsString();
                String taxes = jsonObject.getAsJsonObject("AIR").getAsJsonObject("totalPriceInfo").getAsJsonObject("totalFareDetail").getAsJsonObject("fC").get("TAF").getAsString();
                String total = jsonObject.getAsJsonObject("AIR").getAsJsonObject("totalPriceInfo").getAsJsonObject("totalFareDetail").getAsJsonObject("fC").get("TF").getAsString();*/
                travellerDetails(travInfo, Name, TypeOfPassenger, PNR);
            }

            document.add(travInfo);

// ---------------------- F O O T E R --------------------------------------------------

            // Taxes
            PdfPTable taxTable = new PdfPTable(2);
            taxTable.setWidthPercentage(33);
            taxTable.setSpacingAfter(10f);
            taxTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            String Basics = jsonObject.getAsJsonObject("AIR").getAsJsonObject("totalPriceInfo").getAsJsonObject("totalFareDetail").getAsJsonObject("fC").get("BF").getAsString();
            String taxes = jsonObject.getAsJsonObject("AIR").getAsJsonObject("totalPriceInfo").getAsJsonObject("totalFareDetail").getAsJsonObject("fC").get("TAF").getAsString();
            String total = jsonObject.getAsJsonObject("AIR").getAsJsonObject("totalPriceInfo").getAsJsonObject("totalFareDetail").getAsJsonObject("fC").get("TF").getAsString();
            taxTable.addCell(createCell(" ", PdfPCell.ALIGN_RIGHT));
            taxTable.addCell(createCell(" ", PdfPCell.ALIGN_RIGHT));
            taxTable.addCell(createCell("Basic  :", PdfPCell.ALIGN_RIGHT));
            taxTable.addCell(createCell(Basics + " INR", PdfPCell.ALIGN_RIGHT));
            taxTable.addCell(createCell("Taxes  :", PdfPCell.ALIGN_RIGHT));
            taxTable.addCell(createCell(taxes + " INR", PdfPCell.ALIGN_RIGHT));
            taxTable.addCell(createCell("Grand Total  :", PdfPCell.ALIGN_RIGHT, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13)));
            taxTable.addCell(createCell(total + " INR", PdfPCell.ALIGN_RIGHT, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13)));
            taxTable.addCell(createCell(" ", PdfPCell.ALIGN_RIGHT));
            taxTable.addCell(createCell(" ", PdfPCell.ALIGN_RIGHT));

            document.add(taxTable);

            // Terms And Conditions

            // DIV
            PdfDiv div = new PdfDiv();
//            div.setPercentageWidth(90f);
            div.setKeepTogether(true);

            PdfPTable tc = new PdfPTable(2);
            tc.setWidthPercentage(90);
            tc.setSpacingAfter(10f);
//            tc.keepRowsTogether(1, 2);
            tc.setKeepTogether(true);
            tc.setHorizontalAlignment(Element.ALIGN_LEFT);

            tc.addCell(tcCell("Terms & Conditions", PdfPCell.ALIGN_CENTER));
            tc.addCell(tcCell("For TRAVBOX ", PdfPCell.ALIGN_CENTER));


            div.addElement(tc);

            Paragraph bottomText = new Paragraph("Computer Generated Report, Require No Signature", FontFactory.getFont(FontFactory.HELVETICA, 10));
            bottomText.setSpacingBefore(20f);
            bottomText.setSpacingAfter(10f);
            bottomText.setKeepTogether(true);
            bottomText.setAlignment(Element.ALIGN_CENTER);
            div.addElement(bottomText);

            document.add(div);

            Uri pdfUri = FileProvider.getUriForFile(BookingResultActivity.this, "in.tripzygo.tripzygoflightbookingapp.provider", pdfFile);
            StorageReference filepath = storageReference.child(pdfUri.getLastPathSegment());
            final UploadTask uploadTask = filepath.putFile(pdfUri);
            uploadTask.addOnFailureListener(e -> {

            }).addOnSuccessListener(taskSnapshot -> {
                Task<Uri> uriTask = uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filepath.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("task = " + task.getResult());
                    } else {
                        System.out.println("task.getResult() = " + task.getResult());
                    }
                });
            });
            // Path to your generated PDF
            /*Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(pdfUri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);*/
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }

    private PdfPTable ticketDetails(PdfPTable tdTable, String invoice, String bookingDate) {
        tdTable.addCell(createCell(invoice, PdfPCell.ALIGN_LEFT));
        tdTable.addCell(createCell(bookingDate, PdfPCell.ALIGN_LEFT));
        return tdTable;
    }

    private PdfPTable travellerDetails(PdfPTable pdfPTable, String name, String type, String pnr) {
        pdfPTable.addCell(createCell(name, PdfPCell.ALIGN_LEFT));
        pdfPTable.addCell(createCell(type, PdfPCell.ALIGN_LEFT));
        pdfPTable.addCell(createCell(pnr, PdfPCell.ALIGN_LEFT));
        return pdfPTable;
    }

    private static PdfPCell createCell(String content, int alignment, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setExtraParagraphSpace(5f);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    private static PdfPCell createCell(String content, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(content, FontFactory.getFont(FontFactory.HELVETICA)));
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setExtraParagraphSpace(2f);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    private static PdfPCell tcCell(String content, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(content, FontFactory.getFont(FontFactory.HELVETICA)));
        cell.setExtraParagraphSpace(2f);
        cell.setPaddingTop(20f);
        cell.setPaddingBottom(20f);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    @Override
    public void onAnimationStart(@NonNull Animator animator) {

    }

    @Override
    public void onAnimationEnd(@NonNull Animator animator) {
        animator.pause();
    }

    @Override
    public void onAnimationCancel(@NonNull Animator animator) {

    }

    @Override
    public void onAnimationRepeat(@NonNull Animator animator) {

    }
}