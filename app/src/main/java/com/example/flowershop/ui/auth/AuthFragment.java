package com.example.flowershop.ui.auth;

import android.content.Context;
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

import com.example.flowershop.FirestoreHandler;
import com.example.flowershop.R;

public class AuthFragment extends Fragment {
    private AuthListener authListener;

    public AuthFragment() {}

    public static AuthFragment newInstance() {
        AuthFragment fragment = new AuthFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AuthListener) {
            authListener = (AuthListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_auth, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText etPhone = view.findViewById(R.id.ET_phone);
        EditText etPassword = view.findViewById(R.id.ET_password);
        Button butEnter = view.findViewById(R.id.BUT_enter);
        Button butReg = view.findViewById(R.id.BUT_reg);
        butEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = etPhone.getText().toString();
                String password = etPassword.getText().toString();
                FirestoreHandler.getInstance().loginUser(phone, password)
                        .observe(getViewLifecycleOwner(), result -> {
                    if(result){
                        authListener.onAuthSuccess();
                    }else{
                        Toast.makeText(getContext(), "Не удалось войти", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        butReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_container, new RegistrFragment()).commit();
            }
        });
    }
}