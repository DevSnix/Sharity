package com.test.sharity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class SimulationPaymentActivity extends Activity {

    private EditText editTextAmount;
    private Button buttonConfirmPayment;
    private Button buttonCancelPayment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation_payment);

        editTextAmount = findViewById(R.id.editTextAmount);
        buttonConfirmPayment = findViewById(R.id.buttonConfirmPayment);
        buttonCancelPayment = findViewById(R.id.buttonCancelPayment);

        buttonConfirmPayment.setOnClickListener(v -> {
            // Simulate payment success
            double amount = Double.parseDouble(editTextAmount.getText().toString());
            Intent resultIntent = new Intent();
            resultIntent.putExtra("paymentStatus", true);
            resultIntent.putExtra("amount", amount);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        buttonCancelPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Simulate payment failure
                Intent resultIntent = new Intent();
                resultIntent.putExtra("paymentStatus", false);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}
