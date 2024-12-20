package com.example.marketapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Проверка авторизации
        if (mAuth.getCurrentUser() == null) {
            // Если пользователь не авторизован, перенаправляем на экран входа
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();  // Закрываем текущую активность
            return;
        }

        // Кнопка выхода
        Button logoutButton = findViewById(R.id.btn_logout);
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });

        // Кнопка добавления продукта
        Button addProductButton = findViewById(R.id.btn_add_product);
        addProductButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
            startActivityForResult(intent, 100); // Запрос на добавление продукта
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Загружаем список продуктов
        loadProducts();
    }

    // Метод для загрузки продуктов
    private void loadProducts() {
        db.collection("products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> productList = queryDocumentSnapshots.toObjects(Product.class);
                    productAdapter = new ProductAdapter(productList, product -> {
                        // Логика для добавления в корзину
                        if (mAuth.getCurrentUser() == null) {
                            Toast.makeText(MainActivity.this, "Please login to add products to the cart", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        } else {
                            // Добавить товар в корзину
                        }
                    });
                    recyclerView.setAdapter(productAdapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Failed to load products: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Обработка результата добавления продукта
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // После добавления продукта обновляем список
            loadProducts();
        }
    }
}
