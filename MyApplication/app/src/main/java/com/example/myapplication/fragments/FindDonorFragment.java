package com.example.myapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.DonorAdapter;
import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.model.Donor;

import java.util.List;

/**
 * Find Donor screen (MCA Lab: Spinner, RecyclerView, SQLite query, ACTION_DIAL).
 */
public class FindDonorFragment extends Fragment {

    private Spinner spinnerSearchGroup;
    private RecyclerView recyclerDonors;
    private TextView tvEmpty;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_donor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = new DatabaseHelper(requireContext());

        spinnerSearchGroup = view.findViewById(R.id.spinnerSearchGroup);
        Button btnSearch = view.findViewById(R.id.btnSearch);
        recyclerDonors = view.findViewById(R.id.recyclerDonors);
        tvEmpty = view.findViewById(R.id.tvEmptyDonors);

        recyclerDonors.setLayoutManager(new LinearLayoutManager(requireContext()));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.blood_groups_search,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSearchGroup.setAdapter(adapter);

        btnSearch.setOnClickListener(v -> searchDonors());
    }

    private void searchDonors() {
        String group = spinnerSearchGroup.getSelectedItem().toString();
        List<Donor> donors = dbHelper.getDonorsByBloodGroup(group);
        if (donors.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText("No donors found for " + group);
            recyclerDonors.setAdapter(null);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerDonors.setAdapter(new DonorAdapter(donors));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (dbHelper != null) dbHelper.close();
    }
}
