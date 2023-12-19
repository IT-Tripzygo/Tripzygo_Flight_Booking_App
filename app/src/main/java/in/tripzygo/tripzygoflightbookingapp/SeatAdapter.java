package in.tripzygo.tripzygoflightbookingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.SeatViewHolder> {

    private final List<Seat> seatList;
    private final Context context;

    public SeatAdapter(List<Seat> seatList, Context context) {
        this.seatList = seatList;
        this.context = context;
    }

    @NonNull
    @Override
    public SeatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_seat, parent, false);
        return new SeatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatViewHolder holder, int position) {
        Seat seat = seatList.get(position);
        if (seat.isBooked) {
            holder.imageView.setImageDrawable(context.getDrawable(R.drawable.seat_normal_booked));
        } else {
            holder.imageView.setImageDrawable(context.getDrawable(R.drawable.seat_normal));
        }

        // Set any other seat data or status indicators as needed

        // Handle seat click event
        holder.itemView.setOnClickListener(v -> {
            holder.imageView.setImageDrawable(context.getDrawable(R.drawable.seat_normal_selected));
            // Implement seat selection logic here
            // You can change the seat's appearance or perform other actions
        });
    }

    @Override
    public int getItemCount() {
        return seatList.size();
    }

    public class SeatViewHolder extends RecyclerView.ViewHolder {
        TextView seatNumberTextView;
        ImageView imageView;

        public SeatViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_seat);
            // Initialize other seat item view elements as needed
        }
    }
}

