package com.example.marketapp;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirebaseHelper {

    private FirebaseFirestore db;
    private CollectionReference productsCollection;
    private CollectionReference cartCollection;

    public FirebaseHelper() {
        db = FirebaseFirestore.getInstance();
        productsCollection = db.collection("products");
        cartCollection = db.collection("cart");
    }

    // Получаем все товары из Firestore
    public void getProducts(OnProductsReceivedListener listener) {
        productsCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                List<Product> productList = new ArrayList<>();
                for (DocumentSnapshot document : querySnapshot) {
                    Product product = document.toObject(Product.class);
                    product.setId(document.getId());  // Устанавливаем id документа
                    productList.add(product);
                }
                listener.onProductsReceived(productList);
            } else {
                listener.onError(task.getException());
            }
        });
    }

    // Добавление товара в корзину
    public void addToCart(String productId, int quantity) {
        cartCollection.add(new CartItem(productId, quantity))
                .addOnSuccessListener(documentReference -> {
                    // Успешно добавили в корзину
                })
                .addOnFailureListener(e -> {
                    // Ошибка добавления в корзину
                });
    }

    // Интерфейс для получения списка продуктов
    public interface OnProductsReceivedListener {
        void onProductsReceived(List<Product> products);
        void onError(Exception e);
    }
}

