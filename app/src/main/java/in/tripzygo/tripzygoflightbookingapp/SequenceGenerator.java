package in.tripzygo.tripzygoflightbookingapp;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.text.DecimalFormat;

public class SequenceGenerator {

    private final DatabaseReference mDatabase;

    public SequenceGenerator() {
        mDatabase = FirebaseDatabase.getInstance("https://flightbookingapp-307f0-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
    }

    public void generateNewSequenceNumber(final SequenceCallback callback) {
        mDatabase.child("counter").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue() == null) {
                    mutableData.setValue(1);
                } else {
                    int current = mutableData.getValue(Integer.class);
                    mutableData.setValue(current + 1);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (committed) {
                    int sequenceNumber = dataSnapshot.getValue(Integer.class);
                    String formattedSequenceNumber = formatSequenceNumber(sequenceNumber);
                    String prefixedSequenceNumber = "TB2324" + formattedSequenceNumber;
                    callback.onNewSequenceNumber(prefixedSequenceNumber);
                } else {
                    callback.onError(databaseError);
                }
            }
        });
    }

    private String formatSequenceNumber(int number) {
        DecimalFormat df = new DecimalFormat("0000000");
        return df.format(number);
    }

    public interface SequenceCallback {
        void onNewSequenceNumber(String sequenceNumber);
        void onError(DatabaseError error);
    }
}

