package com.example.flowershop;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FirestoreHandler {
    private final String TAG_LOG = "TAG_LOG";
    private static FirestoreHandler instance;
    private final FirebaseFirestore db;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final String userId = "V6BABtafBBEo3Ojs2Jeu";

    private FirestoreHandler() {
        this.db = FirebaseFirestore.getInstance();
    }
    //singleton pattern
    public static synchronized FirestoreHandler getInstance() {
        if (instance == null) {
            instance = new FirestoreHandler();
        }
        return instance;
    }

    public LiveData<List<Product>> getAllProducts() {
        MutableLiveData<List<Product>> productsLiveData = new MutableLiveData<>();
        return Transformations.switchMap(getBasketCounts(), basketCounts -> {
            if (basketCounts == null) {
                Log.w(TAG_LOG, "Error getting basket products");
            }
            executorService.execute(() -> {
                db.collection("product").get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                List<Product> products = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Product product = document.toObject(Product.class);
                                    if (product != null) {
                                        product.setId(document.getId());
                                        product.setCount(basketCounts.getOrDefault(document.getId(), 0));
                                        products.add(product);
                                    } else {
                                        Log.w(TAG_LOG, "Couldn't convert document to Product");
                                    }
                                }
                                productsLiveData.postValue(products);
                            } else {
                                Log.e(TAG_LOG, "Error getting documents.", task.getException());
                                productsLiveData.postValue(null);
                            }
                        });
            });
            return productsLiveData;
        });
    }


    public LiveData<Map<String, Integer>> getBasketCounts() {
        MutableLiveData<Map<String, Integer>> basketCountsLiveData = new MutableLiveData<>();
        executorService.execute(() -> {
            db.collection("basket").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                List<Map<String, Object>> productsInBasket = (List<Map<String, Object>>) document.get("products");
                                if (productsInBasket != null) {
                                    Map<String, Integer> basketProducts = new HashMap<>();
                                    for (Map<String, Object> productMap : productsInBasket) {
                                        String productId = (String) productMap.get("productID");
                                        Long countLong = (Long) productMap.get("count");
                                        Integer count = (countLong != null) ? countLong.intValue() : 0;
                                        if (productId != null) {
                                            basketProducts.put(productId, count);
                                        }
                                    }
                                    basketCountsLiveData.postValue(basketProducts);
                                }
                            }
                        }
                    });
        });
        return basketCountsLiveData;
    }

    public LiveData<List<Product>> getBasketProducts() {
        return Transformations.switchMap(getBasketCounts(), basketCounts -> {
            MutableLiveData<List<Product>> basketProductsLiveData = new MutableLiveData<>();
            if (basketCounts == null) {
                Log.w(TAG_LOG, "Error getting basket counts for getBasketProducts");
                basketProductsLiveData.postValue(null);
                return basketProductsLiveData;
            }
            List<Product> basketProducts = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : basketCounts.entrySet()) {
                String productId = entry.getKey();
                Integer count = entry.getValue();
                getProduct(productId).observeForever(product -> {
                    if (product != null) {
                        product.setCount(count);
                        basketProducts.add(product);
                    }
                    basketProductsLiveData.postValue(basketProducts);
                });
            }
            return basketProductsLiveData;
        });
    }

    private LiveData<Product> getProduct(String productId) {
        MutableLiveData<Product> productLiveData = new MutableLiveData<>();
        executorService.execute(() -> {
            db.collection("product").document(productId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                Product product = document.toObject(Product.class);
                                if (product != null) {
                                    product.setId(document.getId());
                                    productLiveData.postValue(product);
                                } else {
                                    Log.w(TAG_LOG, "Couldn't convert document to Product class");
                                    productLiveData.postValue(null);
                                }
                            } else {
                                Log.w(TAG_LOG, "Product with id: " + productId + " not found");
                                productLiveData.postValue(null);
                            }
                        } else {
                            Log.e(TAG_LOG, "Error getting product with id: " + productId, task.getException());
                            productLiveData.postValue(null);
                        }
                    });
        });
        return productLiveData;
    }

}
