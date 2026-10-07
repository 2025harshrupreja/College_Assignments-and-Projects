package com.example.myapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.database.DatabaseHelper;

/**
 * Register Donor screen (MCA Lab: EditText, Spinner, CheckBox, Button, Toast, SQLite).
 */
public class RegisterDonorFragment extends Fragment {

    private EditText etName, etAge, etPhone, etCity;
    private Spinner spinnerBloodGroup;
    private CheckBox cbAvailable;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register_donor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = new DatabaseHelper(requireContext());

        etName = view.findViewById(R.id.etName);
        etAge = view.findViewById(R.id.etAge);
        etPhone = view.findViewById(R.id.etPhone);
        etCity = view.findViewById(R.id.etCity);
        spinnerBloodGroup = view.findViewById(R.id.spinnerBloodGroup);
        cbAvailable = view.findViewById(R.id.cbAvailable);
        Button btnSave = view.findViewById(R.id.btnSaveDonor);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.blood_groups,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBloodGroup.setAdapter(adapter);

        btnSave.setOnClickListener(v -> saveDonor());
    }

    private void saveDonor() {
        String name = etName.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String bloodGroup = spinnerBloodGroup.getSelectedItem() != null
                ? spinnerBloodGroup.getSelectedItem().toString() : "";
        String phone = etPhone.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        boolean available = cbAvailable.isChecked();

        // Validation
        if (name.isEmpty()) {
            Toast.makeText(requireContext(),
                    "Please enter name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ageStr.isEmpty()) {
            Toast.makeText(requireContext(),
                    "Please enter age", Toast.LENGTH_SHORT).show();
            return;
        }
        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(),
                    "Invalid age", Toast.LENGTH_SHORT).show();
            return;
        }
        if (age < 18 || age > 65) {
            Toast.makeText(requireContext(),
                    "Donor age must be 18-65", Toast.LENGTH_SHORT).show();
            return;
        }
        if (bloodGroup.isEmpty() || bloodGroup.equals("Select Blood Group")) {
            Toast.makeText(requireContext(),
                    "Please select blood group", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.isEmpty() || phone.length() < 10) {
            Toast.makeText(requireContext(),
                    "Enter valid 10-digit phone", Toast.LENGTH_SHORT).show();
            return;
        }
        if (city.isEmpty()) {
            Toast.makeText(requireContext(),
                    "Please enter city", Toast.LENGTH_SHORT).show();
            return;
        }

        long id = dbHelper.insertDonor(name, age, bloodGroup, phone, city, available);
        if (id != -1) {
            Toast.makeText(requireContext(),
                    "Donor registered successfully!", Toast.LENGTH_SHORT).show();
            etName.setText("");
            etAge.setText("");
            etPhone.setText("");
            etCity.setText("");
            spinnerBloodGroup.setSelection(0);
            cbAvailable.setChecked(true);
        } else {
            Toast.makeText(requireContext(),
                    "Failed to save donor", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (dbHelper != null) dbHelper.close();
    }
}
