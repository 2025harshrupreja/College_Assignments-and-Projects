package com.example.myapplication.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Donor;

import java.util.List;

/** Simple RecyclerView adapter for donor search results (MCA Lab demo). */
public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.ViewHolder> {

    private final List<Donor> donors;

    public DonorAdapter(List<Donor> donors) {
        this.donors = donors;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_donor, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Donor d = donors.get(position);
        h.tvName.setText(d.getName());
        h.tvDetails.setText(d.getBloodGroup() + "  |  Age: " + d.getAge()
                + "  |  " + d.getCity());
        h.tvPhone.setText("Phone: " + d.getPhone());

        h.btnCall.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + d.getPhone()));
                v.getContext().startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(v.getContext(),
                        "No dialer app found", Toast.LENGTH_SHORT).show();
            }
        });

        h.itemView.setOnClickListener(v -> {
            // Donor details dialog (simple, viva-friendly)
            new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                    .setTitle(d.getName())
                    .setMessage("Blood Group: " + d.getBloodGroup()
                            + "\nAge: " + d.getAge()
                            + "\nPhone: " + d.getPhone()
                            + "\nCity: " + d.getCity()
                            + "\nAvailable: " + (d.isAvailable() ? "Yes" : "No"))
                    .setPositiveButton("CALL DONOR", (dialog, which) -> {
                        try {
                            Intent intent = new Intent(Intent.ACTION_DIAL,
                                    Uri.parse("tel:" + d.getPhone()));
                            v.getContext().startActivity(intent);
                        } catch (Exception ex) {
                            Toast.makeText(v.getContext(),
                                    "No dialer app found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Close", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return donors.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails, tvPhone;
        Button btnCall;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvDonorName);
            tvDetails = itemView.findViewById(R.id.tvDonorDetails);
            tvPhone = itemView.findViewById(R.id.tvDonorPhone);
            btnCall = itemView.findViewById(R.id.btnCallDonor);
        }
    }
}
