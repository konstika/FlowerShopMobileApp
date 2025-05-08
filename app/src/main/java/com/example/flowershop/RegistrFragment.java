package com.example.flowershop;

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

public class RegistrFragment extends Fragment {
    private AuthListener authListener;
    public RegistrFragment() {}

    public static RegistrFragment newInstance(String param1, String param2) {
        RegistrFragment fragment = new RegistrFragment();
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
        return inflater.inflate(R.layout.fragment_registr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText etPhone = view.findViewById(R.id.ET_phone);
        EditText etUsername = view.findViewById(R.id.ET_username);
        EditText etPassword = view.findViewById(R.id.ET_password);
        Button butEnter = view.findViewById(R.id.BUT_enter);
        Button butReg = view.findViewById(R.id.BUT_reg);
        butReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = etPhone.getText().toString();
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                FirestoreHandler.getInstance().registerUser(username,password,phone)
                        .observe(getViewLifecycleOwner(), result -> {
                            if(result){
                                authListener.onAuthSuccess();
                            }else{
                                Toast.makeText(getContext(), "Не удалось зарегистрироваться", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        butEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_container, new AuthFragment()).commit();
            }
        });
    }
}