package com.example.landmarknavigator;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {
    //GPS variables
    private final int LOCATION_ACCESS_CODE = 1;
    //Logging constant
    private final static String TAG = "LoginFragment";

    //Firebase variables
    private FirebaseAuth mAuth;
    // initializing view variables
    private EditText edtEmail, edtPassword;
    private TextView txtRegistration;
    private Button btnSubmit;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Assign Firebase variable
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            navigateToHomePage();
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);

        //assign view variables
        edtEmail = view.findViewById(R.id.loginEmailEdit);
        edtPassword = view.findViewById(R.id.loginPasswordEdit);
        txtRegistration = view.findViewById(R.id.loginRegistrationTextView);
        btnSubmit = view.findViewById(R.id.loginSubmitButton);

        /**
         * https://stackoverflow.com/questions/843675/how-do-i-find-out-if-the-gps-of-an-android-device-is-enabled
         */
        LocationService.manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);;

        if(!isGpsEnabled()){
            Log.i(TAG, "gpsDisabled");
            buildAlertMessageNoGps();
        }else {
            Log.i(TAG, "gpsEnabled");
            if(!checkLocationPermission()){ requestLocationPermission(); }
            else { LocationService.locationAccess = true; }
        }

        txtRegistration.setOnClickListener(navigateToRegistrationEvent);
        btnSubmit.setOnClickListener(loginEvent);
    }

    private boolean isGpsEnabled(){
        return LocationService.manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void buildAlertMessageNoGps(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("GPS is required. Do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean checkLocationPermission(){
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission(){
        requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_ACCESS_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == LOCATION_ACCESS_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.i(TAG, "onRequestPermissionResult:Granted");
                LocationService.locationAccess = true;
            }else{
                LocationService.locationAccess = false;
            }
        }
    }

    private View.OnClickListener loginEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "LoginEvent");
            if(!isGpsEnabled()){
                Log.i(TAG, "loginEvent:GpsEnabled:false");
                buildAlertMessageNoGps();
                return;
            }
            if(!LocationService.locationAccess){
                Log.i(TAG, "loginEvent:locationAccess:false");
                requestLocationPermission();
                return;
            }
            String email = edtEmail.getText().toString();
            String password = edtPassword.getText().toString();

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(getContext(), "Please complete all the fields",Toast.LENGTH_SHORT)
                        .show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(getActivity(), loginListener);
            Toast.makeText(getContext(), "Login in", Toast.LENGTH_LONG).show();
        }
    };

    private View.OnClickListener navigateToRegistrationEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!isGpsEnabled()){
                buildAlertMessageNoGps();
                return;
            }
            Navigation.findNavController(getView()).navigate(R.id.action_loginFragment_to_registrationFragment);
        }
    };

    private void navigateToHomePage(){
        Navigation.findNavController(getView()).navigate(R.id.action_loginFragment_to_homepageFragment);
    }

    private OnCompleteListener<AuthResult> loginListener = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if(task.isSuccessful()){
                Log.d(TAG, "SignInWithEmail&Password:success");
                Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                navigateToHomePage();
            }else{
                Log.w(TAG, "SignInWithEmail&Password:failure");
                Toast.makeText(getActivity(), "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        }
    };

}
