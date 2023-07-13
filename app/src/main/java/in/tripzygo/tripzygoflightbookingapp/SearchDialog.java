package in.tripzygo.tripzygoflightbookingapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import in.tripzygo.tripzygoflightbookingapp.Modals.AirportCode;

public class SearchDialog extends BottomSheetDialogFragment {
    Context context;
    List<AirportCode> airportCodes = new ArrayList<>();

    public SearchDialog() {
    }

    public SearchDialog(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        BottomSheetBehavior bottomSheetBehavior = dialog.getBehavior();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        // Do something with your dialog like setContentView() or whatever
        return dialog;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.search, null);
        dialog.setContentView(view);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.search, container, false);
        ImageView imgToolBack = view.findViewById(R.id.img_tool_back);
        final EditText edtToolSearch = view.findViewById(R.id.edt_tool_search);
        ImageView imgToolMic = view.findViewById(R.id.img_tool_mic);
        final ListView listSearch = (ListView) view.findViewById(R.id.list_search);
        BottomSheetBehavior bottomSheetBehavior = new BottomSheetBehavior();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        imgToolMic.setOnClickListener(view1 -> {
            getDialog().dismiss();
        });
        edtToolSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                listSearch.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String json = null;
                try {
                    InputStream is = context.getAssets().open("airportcodes.json");
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();
                    json = new String(buffer, "UTF-8");
                    airportCodes = gson.fromJson(json, new TypeToken<List<AirportCode>>() {
                    }.getType());
                    final SearchAdapter searchAdapter = new SearchAdapter(getContext(), airportCodes, false);
                    listSearch.setAdapter(searchAdapter);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Gson gson = new Gson();
                String json = null;
                try {
                    InputStream is = context.getAssets().open("airportcodes.json");
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();
                    json = new String(buffer, "UTF-8");
                    airportCodes = gson.fromJson(json, new TypeToken<List<AirportCode>>() {
                    }.getType());
                    List<AirportCode> airportCodeList = new ArrayList<>();
                    for (AirportCode airportCode : airportCodes) {
                        if (airportCode.getCity().equalsIgnoreCase(String.valueOf(charSequence)) || airportCode.getCode().equalsIgnoreCase(String.valueOf(charSequence))) {
                            System.out.println("airportCode = " + airportCode.getCode());
                            System.out.println("airportCode.getCity() = " + airportCode.getCity());
                            System.out.println("airportCode.getName() = " + airportCode.getName());
                            airportCodeList.add(airportCode);
                            final SearchAdapter searchAdapter = new SearchAdapter(getContext(), airportCodeList, false);
                            listSearch.setAdapter(searchAdapter);
                        } else {
//                            System.out.println("airportCode = " + airportCode.getCode());
//                            System.out.println("airportCode.getCity() = " + airportCode.getCity());
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;

    }
}
