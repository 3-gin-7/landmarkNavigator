package com.example.landmarknavigator;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrationFragment extends Fragment {
    // initializing view variables
    private EditText edtEmail, edtPassword, edtRepeat;
    private Button btnSubmit;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    public static RegistrationFragment newInstance(String param1, String param2) {
        RegistrationFragment fragment = new RegistrationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edtEmail = view.findViewById(R.id.registrationEmailEdit);
        edtPassword = view.findViewById(R.id.registrationPasswordEdit);
        edtRepeat = view.findViewById(R.id.registrationPasswordRepeatEdit);
        btnSubmit = view.findViewById(R.id.registrationSubmitButton);

        btnSubmit.setOnClickListener(registrationEvent);
    }

    private View.OnClickListener registrationEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String email = edtEmail.getText().toString();
            String password = edtPassword.getText().toString();
            String repeat = edtRepeat.getText().toString();

            if(email.isEmpty() || password.isEmpty() || repeat.isEmpty()){
                Toast.makeText(getContext(), "Please complete all fields", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            if(!password.equals(repeat)){
                Toast.makeText(getContext(), "Passwords do no match", Toast.LENGTH_SHORT)
                        .show();
                return;
            }

            //TODO: add progress spinner and add user to firebase
            Toast.makeText(getContext(), "Registering", Toast.LENGTH_SHORT).show();
        }
    };
}