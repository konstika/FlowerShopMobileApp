package com.example.flowershop.ui.basket;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.flowershop.R;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Geometry;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.search.Response;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;
import com.yandex.mapkit.search.SearchOptions;
import com.yandex.mapkit.search.Session;
import com.yandex.mapkit.search.ToponymObjectMetadata;
import com.yandex.runtime.Error;

public class MapFragment extends Fragment {
    private MapView mapView;
    private TextView addressTextView;
    private SearchManager searchManager;
    private String currentAddress;
    private String currentApart;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;

    public MapFragment() {}

    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.initialize(requireContext());
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = view.findViewById(R.id.mapview);
        addressTextView = view.findViewById(R.id.TV_address);
        EditText addressEditText = view.findViewById(R.id.ET_address);
        EditText apartEditText = view.findViewById(R.id.ET_apart_number);
        addressEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchPlace(v.getText().toString());
                    handled = true;
                }
                return handled;
            }
        });
        apartEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    currentApart = v.getText().toString();
                    if(currentAddress!= null && !currentAddress.isEmpty()) {
                        addressTextView.setText(currentAddress + ", кв. " + currentApart);
                    }
                    handled = true;
                }
                return handled;
            }
        });

        mapView.getMap().move(
                new CameraPosition(new Point(55.7558, 37.6173), 11.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        Button butInputAddress = view.findViewById(R.id.BUT_input_address);
        butInputAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = addressTextView.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("address", address);
                OrderFragment orderFragment = (OrderFragment)requireActivity()
                        .getSupportFragmentManager().findFragmentByTag("ORDER");
                orderFragment.setAddress(address);
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Location permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Location permission required for map functionality", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
    }

    private void searchPlace(String query) {
        if (query == null || query.trim().isEmpty()) {
            Toast.makeText(getContext(), "Введите корректный запрос", Toast.LENGTH_SHORT).show();
            return;
        }
        SearchOptions searchOptions = new SearchOptions();
        Geometry geometry = Geometry.fromPoint(new Point(55.7558, 37.6173));
        searchManager.submit(query, geometry, searchOptions, new Session.SearchListener() {
            @Override
            public void onSearchResponse(@NonNull Response response) {
                Log.d("AAA","ПОЛУЧЕН ОТВЕТ");
                if (!response.getCollection().getChildren().isEmpty()) {
                    Point point = response.getCollection().getChildren().get(0).getObj().getGeometry().get(0).getPoint();
                    ToponymObjectMetadata metadata = response.getCollection().getChildren().get(0).getObj()
                            .getMetadataContainer().getItem(ToponymObjectMetadata.class);
                    if (metadata != null  && metadata.getAddress() != null) {
                        currentAddress = metadata.getAddress().getFormattedAddress();
                        if(currentApart!=null && !(currentApart.isEmpty())) {
                            addressTextView.setText(currentAddress + ", кв. " + currentApart);
                        }else{
                            addressTextView.setText(currentAddress);
                        }
                    } else {
                        addressTextView.setText("Адрес не найден");
                    }

                    mapView.getMap().move(
                            new CameraPosition(point, 15.0f, 0.0f, 0.0f),
                            new Animation(Animation.Type.SMOOTH, 0),
                            null
                    );

                    mapView.getMap().getMapObjects().addPlacemark(point);
                } else {
                    Toast.makeText(getContext(), "Место не найдено", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSearchError(@NonNull Error error) {
                Toast.makeText(getContext(), "Ошибка", Toast.LENGTH_SHORT).show();
                Log.e("SearchError", "Ошибка поиска: " + error.toString());
            }
        });
    }

}