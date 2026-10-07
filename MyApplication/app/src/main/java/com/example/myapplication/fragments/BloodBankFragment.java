package com.example.myapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.BloodBankAdapter;
import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.model.BloodBank;

import java.util.List;

/**
 * Blood Banks screen (MCA Lab: RecyclerView, SQLite, ACTION_DIAL + geo intent).
 */
public class BloodBankFragment extends Fragment {

    private RecyclerView recyclerBanks;
    private TextView tvEmpty;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blood_bank, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerBanks = view.findViewById(R.id.recyclerBanks);
        tvEmpty = view.findViewById(R.id.tvEmptyBanks);
        recyclerBanks.setLayoutManager(new LinearLayoutManager(requireContext()));

        dbHelper = new DatabaseHelper(requireContext());
        List<BloodBank> banks = dbHelper.getAllBloodBanks();
        if (banks.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerBanks.setAdapter(new BloodBankAdapter(banks));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (dbHelper != null) dbHelper.close();
    }
}
