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
import com.example.myapplication.model.BloodBank;

import java.util.List;

/** Simple RecyclerView adapter for blood banks (MCA Lab demo). */
public class BloodBankAdapter extends RecyclerView.Adapter<BloodBankAdapter.ViewHolder> {

    private final List<BloodBank> banks;

    public BloodBankAdapter(List<BloodBank> banks) {
        this.banks = banks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_blood_bank, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        BloodBank b = banks.get(position);
        h.tvName.setText(b.getName());
        h.tvAddress.setText(b.getAddress());
        h.tvPhone.setText("Phone: " + b.getPhone());

        // CALL -> ACTION_DIAL (implicit intent)
        h.btnCall.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + b.getPhone()));
                v.getContext().startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(v.getContext(),
                        "No dialer app found", Toast.LENGTH_SHORT).show();
            }
        });

        // VIEW LOCATION -> geo: intent (opens Google Maps / any map app)
        h.btnLocation.setOnClickListener(v -> {
            try {
                String uri = "geo:" + b.getLatitude() + "," + b.getLongitude()
                        + "?q=" + Uri.encode(b.getName());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                v.getContext().startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(v.getContext(),
                        "No map app found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return banks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress, tvPhone;
        Button btnCall, btnLocation;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvBankName);
            tvAddress = itemView.findViewById(R.id.tvBankAddress);
            tvPhone = itemView.findViewById(R.id.tvBankPhone);
            btnCall = itemView.findViewById(R.id.btnCallBank);
            btnLocation = itemView.findViewById(R.id.btnBankLocation);
        }
    }
}
