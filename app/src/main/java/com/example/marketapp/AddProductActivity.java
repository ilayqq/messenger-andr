package com.example.marketapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {

    private EditText productNameInput, productPriceInput, productImageResIdInput;
    private Button saveProductButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Инициализируем Firestore
        db = FirebaseFirestore.getInstance();

        // Связываем элементы интерфейса
        productNameInput = findViewById(R.id.et_product_name);
        productPriceInput = findViewById(R.id.et_product_price);
        productImageResIdInput = findViewById(R.id.et_product_image_res_id);
        saveProductButton = findViewById(R.id.btn_save_product);

        // Устанавливаем обработчик для кнопки сохранения
        saveProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = productNameInput.getText().toString().trim();
                String productPrice = productPriceInput.getText().toString().trim();
                String productImageResId = productImageResIdInput.getText().toString().trim();

                // Проверка на заполненность полей
                if (productName.isEmpty() || productPrice.isEmpty() || productImageResId.isEmpty()) {
                    Toast.makeText(AddProductActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Сохраняем продукт в Firestore
                saveProductToFirestore(productName, productPrice, productImageResId);
            }
        });
    }

    private void saveProductToFirestore(String name, String price, String imageResId) {
        // Создаем объект продукта для сохранения
        Map<String, Object> product = new HashMap<>();
        product.put("name", name);
        product.put("price", price);
        product.put("imageResId", imageResId);

        // Сохраняем данные в коллекцию "products"
        db.collection("products")
                .add(product)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddProductActivity.this, "Product saved successfully", Toast.LENGTH_SHORT).show();
                    // Возвращаемся на главную страницу
                    setResult(RESULT_OK);  // Уведомляем главную страницу о новом продукте
                    finish(); // Закрываем текущую активность
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddProductActivity.this, "Failed to save product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
