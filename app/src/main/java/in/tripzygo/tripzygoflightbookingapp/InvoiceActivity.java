package in.tripzygo.tripzygoflightbookingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.Document;

import java.util.ArrayList;
import java.util.List;

public class InvoiceActivity extends AppCompatActivity {
    Document document;
    String bookingId;
    List<JsonObject> jsonObjects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String data = getIntent().getStringExtra("data");
        bookingId = getIntent().getStringExtra("bookingId");
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(data, new TypeToken<JsonObject>() {
        }.getType());
        JsonArray jsonElements = jsonObject.getAsJsonObject("AIR").getAsJsonArray("tripInfos").get(0).getAsJsonObject().getAsJsonArray("sI");
        JsonArray length = jsonObject.getAsJsonObject("AIR").getAsJsonArray("tripInfos");
        StorageReference storageReference1 = FirebaseStorage.getInstance().getReference().child("Invoices").child("TripzygoBooking " + bookingId + ".pdf");
        storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/pdf");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            }
        });

/*        ApiInterface apiInterface = Retrofitt.initretro().create(ApiInterface.class);
        JsonObject jsonObject1 = new JsonObject();
        jsonObject1.addProperty("bookingId", bookingId);
        Call<JsonObject> call = apiInterface.retrieveBooking(jsonObject1);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject2 = response.body();
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
                        email = "meetvani60@gmail.com";
                        SequenceGenerator generator = new SequenceGenerator();
                        generator.generateNewSequenceNumber(new SequenceGenerator.SequenceCallback() {
                            @Override
                            public void onNewSequenceNumber(String sequenceNumber) {
                                Invoice=sequenceNumber;
                                generateInvoice(document, jsonObject,Invoice);
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
                    System.out.println("response = " + response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println("t = " + t);
            }
        });*/
    }

  /*  public void generateInvoice(Document document, JsonObject jsonObject, String Invoice) {
        try {
            File pdfFile = new File(getFilesDir(), "TripzygoBooking " + bookingId + ".pdf");
            FileOutputStream fileOutputStream = new FileOutputStream(pdfFile);
            PdfWriter.getInstance(document, fileOutputStream);
            document.open();

            // Add a title
            *//*Font logoName = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph titleLogo = new Paragraph("TRIPZYGO", logoName);
            titleLogo.setAlignment(Element.ALIGN_LEFT);
            titleLogo.setSpacingBefore(10f);
            document.add(titleLogo);*//*

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

            table.addCell(createCell("Tripzygo", PdfPCell.ALIGN_LEFT, fontCellTitle));
            table.addCell(createCell(SharedPreference.loadUser(InvoiceActivity.this).getUser_name(), PdfPCell.ALIGN_RIGHT, fontCellTitle));
            table.addCell(createCell("4th floor Pratibha Tower, South City 1, Sector 41 Gurgaon, Haryana 122022", PdfPCell.ALIGN_LEFT));
            table.addCell(createCell("Mobile No: " + SharedPreference.loadUser(InvoiceActivity.this).getPhone_no(), PdfPCell.ALIGN_RIGHT));
            table.addCell(createCell("Tel: +91 74940-09400", PdfPCell.ALIGN_LEFT));
            table.addCell(createCell("Email:" + SharedPreference.loadUser(InvoiceActivity.this).getEmail_id(), PdfPCell.ALIGN_RIGHT));
            table.addCell(createCell("Email: info@tripzygo.in", PdfPCell.ALIGN_LEFT));
            table.addCell(createCell("GSTIN:", PdfPCell.ALIGN_RIGHT));
            table.addCell(createCell(" ", PdfPCell.ALIGN_LEFT));
            table.addCell(createCell("Pan No: ABCDE2345R", PdfPCell.ALIGN_RIGHT));
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
               *//* String Basics = jsonObject.getAsJsonObject("AIR").getAsJsonObject("totalPriceInfo").getAsJsonObject("totalFareDetail").getAsJsonObject("fC").get("BF").getAsString();
                String taxes = jsonObject.getAsJsonObject("AIR").getAsJsonObject("totalPriceInfo").getAsJsonObject("totalFareDetail").getAsJsonObject("fC").get("TAF").getAsString();
                String total = jsonObject.getAsJsonObject("AIR").getAsJsonObject("totalPriceInfo").getAsJsonObject("totalFareDetail").getAsJsonObject("fC").get("TF").getAsString();*//*
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

            Uri pdfUri = FileProvider.getUriForFile(InvoiceActivity.this, "in.tripzygo.tripzygoflightbookingapp.provider", pdfFile);

            // Path to your generated PDF
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(pdfUri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
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

    private class LoadImageTask extends AsyncTask<String, Void, Image> {
        Document document;

        public LoadImageTask(Document document) {
            this.document = document;
        }

        @Override
        protected Image doInBackground(String... urls) {
            String imageUrl = urls[0];
            Image logo = null;
            try {
                // Load the image using Picasso or other libraries
                logo = Image.getInstance(imageUrl);
                logo.setAlignment(Element.ALIGN_LEFT);
                logo.scaleToFit(100, 100); // Resize the logo
                // Add the logo to the document

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            } catch (BadElementException e) {
                throw new RuntimeException(e);
            }
            return logo;
        }
    }*/
}