package com.example.flowershop;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FirestoreHandler {
    private final String TAG_LOG = "TAG_LOG";
    private static FirestoreHandler instance;
    private final FirebaseFirestore db;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private User user;

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

    public LiveData<Boolean> setUser(String userId){
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        db.collection("user").document(userId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult()!=null) {
                this.user = task.getResult().toObject(User.class);
                user.setId(userId);
                result.postValue(true);
            }else{result.postValue(false);}
        });
        return result;
    }
    public String getUserId(){
        return user.getId();
    }

    public LiveData<Boolean> registerUser(String username, String password, String phone) {
        MutableLiveData<Boolean> registrationResult = new MutableLiveData<>();
        executorService.execute(() -> {
            Map<String, Object> basketDocument = new HashMap<>();
            basketDocument.put("products", new ArrayList<>());
            db.collection("basket").add(basketDocument).
                    addOnCompleteListener(basketTask -> {
                        if (basketTask.isSuccessful()) {
                            String basketId = basketTask.getResult().getId();

                            Map<String, Object> userDocument = new HashMap<>();
                            userDocument.put("username", username);
                            userDocument.put("password", password);
                            userDocument.put("phone", phone);
                            userDocument.put("basketID", basketId);
                            db.collection("user").add(userDocument).
                                    addOnCompleteListener(userTask -> {
                                        if (userTask.isSuccessful()) {
                                            setUser(basketTask.getResult().getId()).observeForever(
                                                    result ->{
                                                        registrationResult.postValue(result);
                                                    });
                                        }else{
                                            registrationResult.postValue(false);
                                            db.collection("basket").document(basketId).delete();
                                        }
                                    });
                        }else{
                            registrationResult.postValue(false);
                        }
                    });
        });
        return registrationResult;
    }

    public LiveData<Boolean> loginUser(String phone, String password) {
        MutableLiveData<Boolean> loginResult = new MutableLiveData<>();
        db.collection("user")
                .whereEqualTo("phone", phone)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String rightPassword = document.getString("password");
                                if (rightPassword.equals(password)) {
                                    setUser(document.getId()).observeForever(result -> {
                                        loginResult.postValue(result);
                                    });
                                    break;
                                }else{
                                    loginResult.postValue(false);
                                }
                            }
                        }else{
                            loginResult.postValue(false);
                        }
                    }else{
                        loginResult.postValue(false);
                    }
                });
        return loginResult;
    }


    public LiveData<List<Product>> getAllProducts() {
        MutableLiveData<List<Product>> productsLiveData = new MutableLiveData<>();
        return Transformations.switchMap(getBasketCounts(), basketCounts -> {
            executorService.execute(() -> {
                db.collection("product").get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                List<Product> products = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Product product = document.toObject(Product.class);
                                    if (product != null) {
                                        product.setId(document.getId());
                                        if(basketCounts!=null) {
                                            product.setCount(basketCounts
                                                    .getOrDefault(document.getId(),0));
                                        }else{product.setCount(0);}
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


    private LiveData<Map<String, Integer>> getBasketCounts() {
        MutableLiveData<Map<String, Integer>> basketCountsLiveData = new MutableLiveData<>();
        executorService.execute(() -> {
            db.collection("basket").document(user.getBasketID()).get()
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
                                }else{
                                    basketCountsLiveData.postValue(null);
                                }
                            }else{
                                basketCountsLiveData.postValue(null);
                            }
                        }else{
                            basketCountsLiveData.postValue(null);
                        }
                    });
        });
        return basketCountsLiveData;
    }

    public LiveData<List<Product>> getBasketProducts() {
        return Transformations.switchMap(getBasketCounts(), basketCounts -> {
            MutableLiveData<List<Product>> basketProductsLiveData = new MutableLiveData<>();
            if (basketCounts == null) {
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
    public void updateBasket(String productId, int count){
        executorService.execute(() -> {
            DocumentReference basketRef = db.collection("basket").document(user.getBasketID());
            basketRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<Map<String, Object>> products =
                            (List<Map<String, Object>>) task.getResult().get("products");
                    if (products == null) {
                        products = new java.util.ArrayList<>();
                    }

                    boolean productExists = false;
                    for (Map<String, Object> product : products) {
                        if (product != null && productId.equals(product.get("productID"))) {
                            productExists = true;
                            if (count > 0) {
                                product.put("count", count);
                            } else {
                                products.remove(product);
                            }
                            break;
                        }
                    }
                    if (!productExists && count > 0) {
                        Map<String, Object> productMap = new HashMap<>();
                        productMap.put("productID", productId);
                        productMap.put("count", count);
                        products.add(productMap);
                    }
                    basketRef.update("products", products);
                }
            });
        });
    }

}
