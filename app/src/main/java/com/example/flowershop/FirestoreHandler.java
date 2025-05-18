package com.example.flowershop;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.example.flowershop.entity.Order;
import com.example.flowershop.entity.Product;
import com.example.flowershop.entity.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
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
                Log.d("LOGIN","GET USER");
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

    public String getUsername(){
        return user.getUsername();
    }

    public LiveData<Boolean> registerUser(String username, String password, String phone){
        MediatorLiveData<Boolean> registrationResult = new MediatorLiveData<>();
        executorService.execute(() -> {
            db.collection("user").whereEqualTo("username", username).get()
                    .addOnCompleteListener(usernameTask -> {
                        if (usernameTask.isSuccessful()) {
                            QuerySnapshot usernameQuerySnapshot = usernameTask.getResult();
                            if (!usernameQuerySnapshot.isEmpty()) {
                                registrationResult.postValue(false);
                                return;
                            }
                            db.collection("user").whereEqualTo("phone", phone).get()
                                    .addOnCompleteListener(phoneTask -> {
                                        if (phoneTask.isSuccessful()) {
                                            QuerySnapshot phoneQuerySnapshot = phoneTask.getResult();
                                            if (!phoneQuerySnapshot.isEmpty()) {
                                                registrationResult.postValue(false);
                                                return;
                                            }
                                            else{
                                                registrationResult.addSource(createUser(username, password, phone),new Observer<Boolean>() {
                                                    @Override
                                                    public void onChanged(Boolean result) {
                                                        registrationResult.postValue(result);
                                                    }
                                                });
                                            }
                                        }
                                    });
                        }
                    });
        });
        return registrationResult;
    }

    public LiveData<Boolean> createUser(String username, String password, String phone) {
        MediatorLiveData<Boolean> registrationResult = new MediatorLiveData<>();
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
                                            LiveData<Boolean> setUserLiveData = setUser(userTask.getResult().getId());
                                            registrationResult.addSource(setUserLiveData, new Observer<Boolean>() {
                                                @Override
                                                public void onChanged(Boolean result) {
                                                    registrationResult.postValue(result);
                                                }
                                            });
                                        }else{
                                            registrationResult.postValue(false);
                                            db.collection("basket").document(basketId).delete();
                                        }
                                    });
                        }else{registrationResult.postValue(false);}
                    });
        });
        return registrationResult;
    }

    public LiveData<Boolean> loginUser(String phone, String password) {
        MediatorLiveData<Boolean> loginResult = new MediatorLiveData<>();
        db.collection("user")
                .whereEqualTo("phone", phone)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            Log.d("LOGIN","NO EMPTY");
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            String rightPassword = document.getString("password");
                            if (rightPassword.equals(password)) {
                                Log.d("LOGIN","PASSWORD");
                                LiveData<Boolean> setUserLiveData = setUser(document.getId());
                                loginResult.addSource(setUserLiveData, result -> {
                                    Log.d("LOGIN","SET USER");
                                    loginResult.postValue(result);
                                });
                            }else{loginResult.postValue(false);}
                        }else{loginResult.postValue(false);}
                    }else{loginResult.postValue(false);}
                });
        return loginResult;
    }

    public void exit(){
        user = null;
    }

    public LiveData<List<Product>> getAllProducts() {
        MediatorLiveData<List<Product>> mediatorLiveData = new MediatorLiveData<>();

        mediatorLiveData.addSource(getBasketCounts(), basketCounts -> {
            executorService.execute(() -> {
                db.collection("product").get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                List<Product> products = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Product product = document.toObject(Product.class);
                                    if (product != null) {
                                        product.setId(document.getId());
                                        if (basketCounts != null) {
                                            product.setCount(basketCounts
                                                    .getOrDefault(document.getId(), 0));
                                        } else {
                                            product.setCount(0);
                                        }
                                        products.add(product);
                                    }
                                }
                                mediatorLiveData.postValue(products);
                            } else {
                                mediatorLiveData.postValue(null);
                            }
                        });
            });
        });
        return mediatorLiveData;
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
        MediatorLiveData<List<Product>> mediatorLiveData = new MediatorLiveData<>();
        LiveData<Map<String, Integer>> basketCountsLiveData = getBasketCounts();
        mediatorLiveData.addSource(basketCountsLiveData, basketCounts -> {
            if (basketCounts == null) {
                mediatorLiveData.setValue(null);
                return;
            }
            Map<String, LiveData<Product>> productLiveDataMap = new HashMap<>();
            Map<String, Integer> productCountsMap = new HashMap<>(basketCounts);
            List<Product> basketProducts = new ArrayList<>();

            if(!basketCounts.isEmpty()) {
                for (Map.Entry<String, Integer> entry : basketCounts.entrySet()) {
                    String productId = entry.getKey();
                    LiveData<Product> productLiveData = getProduct(productId);
                    productLiveDataMap.put(productId, productLiveData);
                }
                if(!productLiveDataMap.isEmpty()){
                    for (Map.Entry<String, LiveData<Product>> entry : productLiveDataMap.entrySet()) {
                        String productId = entry.getKey();
                        mediatorLiveData.addSource(entry.getValue(), new Observer<Product>() {
                            @Override
                            public void onChanged(Product product) {
                                if (product != null) {
                                    Integer count = productCountsMap.get(productId);
                                    product.setCount(count);
                                    basketProducts.add(product);
                                    if (basketProducts.size() == productLiveDataMap.size()) {
                                        mediatorLiveData.setValue(basketProducts);
                                    }
                                }
                            }
                        });
                    }
                } else{mediatorLiveData.setValue(basketProducts);}

            } else{mediatorLiveData.setValue(basketProducts);}
        });
        return mediatorLiveData;
    }

    public LiveData<Product> getProduct(String productId) {
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
                                } else {productLiveData.postValue(null);}
                            } else {productLiveData.postValue(null);}
                        } else {productLiveData.postValue(null);}
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

    public LiveData<List<Order>> getOrders() {
        MediatorLiveData<List<Order>> ordersLiveData = new MediatorLiveData<>();
        executorService.execute(() -> {
            db.collection("order").whereEqualTo("userID", user.getId()).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<Order> orders = new ArrayList<>();
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            if (documents.isEmpty()) {
                                ordersLiveData.postValue(new ArrayList<>());
                                return;
                            }
                            List<LiveData<Order>> orderLiveDataList = new ArrayList<>();
                            for (DocumentSnapshot document : documents) {
                                MediatorLiveData<Order> orderLiveData = new MediatorLiveData<>();
                                Order order = document.toObject(Order.class);
                                if (order != null) {
                                    order.setId(document.getId());
                                    List<Map<String, Object>> productMaps =
                                            (List<Map<String, Object>>) document.get("products");
                                    if (productMaps != null) {
                                        List<Product> products = new ArrayList<>();
                                        List<LiveData<Product>> productLiveDataList = new ArrayList<>();
                                        for (Map<String, Object> entry : productMaps) {
                                            String productId = entry.get("productID").toString();
                                            MutableLiveData<Product> productLiveData = new MutableLiveData<>();
                                            productLiveDataList.add(productLiveData);
                                            db.collection("product").document(productId).get()
                                                    .addOnCompleteListener(productTask -> {
                                                        if (productTask.isSuccessful() && productTask.getResult() != null) {
                                                            Product product = productTask.getResult().toObject(Product.class);
                                                            if(product != null){
                                                                product.setCount(((Long) entry.get("count")).intValue());
                                                                product.setPrice(((Long) entry.get("price")).intValue());
                                                                products.add(product);
                                                                productLiveData.postValue(product);
                                                            }

                                                        } else {productLiveData.postValue(null);}
                                                        if (products.size() == productLiveDataList.size()) {
                                                            order.setProducts(products);
                                                            orderLiveData.postValue(order);
                                                        }
                                                    });
                                        }

                                        if (productMaps.isEmpty()) {
                                            order.setProducts(new ArrayList<>());
                                            orderLiveData.postValue(order);
                                        }
                                    }else {
                                        order.setProducts(new ArrayList<>());
                                        orderLiveData.postValue(order);
                                    }
                                } else {orderLiveData.postValue(null);}
                                orderLiveDataList.add(orderLiveData);

                                ordersLiveData.addSource(orderLiveData, new Observer<Order>() {
                                    @Override
                                    public void onChanged(Order orderValue) {
                                        if (orderValue != null) {
                                            orders.add(orderValue);
                                        }
                                        if (orders.size() == orderLiveDataList.size()) {
                                            ordersLiveData.postValue(orders);
                                        }
                                    }
                                });
                            }
                        } else {ordersLiveData.postValue(new ArrayList<>());}
                    });
        });
        return ordersLiveData;
    }


    public void updateOrder(String id, String status) {
        executorService.execute(() -> {
            db.collection("order").document(id).update("status", status);
        });
    }

    public void addOrder(Order order){
        executorService.execute(() -> {
            List<Product> products = order.getProducts();
            List<Map<String, Object>> productsInBasket = new ArrayList<>();
            for (Product product: products) {
                Map<String, Object> productInBasket = new HashMap<>();
                productInBasket.put("count", product.getCount());
                productInBasket.put("price", product.getPrice());
                productInBasket.put("productID", product.getId());
                productsInBasket.add(productInBasket);
            }
            Map<String, Object> orderDocument = new HashMap<>();
            orderDocument.put("products", productsInBasket);
            orderDocument.put("userID", order.getUserID());
            orderDocument.put("date_order", order.getDate_order());
            orderDocument.put("date", order.getDate());
            orderDocument.put("time", order.getTime());
            orderDocument.put("address", order.getAddress());
            orderDocument.put("status", order.getStatus());
            db.collection("order").add(orderDocument);
        });
    }
    public void clearBasket(){
        executorService.execute(() -> {
            db.collection("basket").document(user.getBasketID()).
                    update("products", new ArrayList<>());
        });
    }

}
