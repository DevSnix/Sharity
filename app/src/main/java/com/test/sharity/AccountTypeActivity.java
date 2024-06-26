package com.test.sharity;

import android.widget.RadioGroup;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AccountTypeActivity  extends AppCompatActivity {

    private Button nextButton;
    private RadioGroup accountType;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_account_type);

            nextButton = findViewById(R.id.btnNext);
            accountType = findViewById(R.id.account_type);

            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                        public void onClick(View view) {
                            int accountTypeId = accountType.getCheckedRadioButtonId();
                            String accountTypeString = "";

                            if(accountTypeId != -1) {
                                if(accountTypeId == R.id.donorAccount) {
                                    accountTypeString = "Donor";
                                    Intent nextRegisterWindow = new Intent(AccountTypeActivity.this, RegisterActivity.class);
                                    nextRegisterWindow.putExtra("accountType", accountTypeString);
                                    startActivity(nextRegisterWindow);
                                }
                                else if(accountTypeId == R.id.doneeAccount) {
                                    accountTypeString = "Donee";
                                    Intent nextRegisterWindow = new Intent(AccountTypeActivity.this, RegisterActivity.class);
                                    nextRegisterWindow.putExtra("accountType", accountTypeString);
                                    startActivity(nextRegisterWindow);
                                }
                                else if(accountTypeId == R.id.charityAccount) {
                                    accountTypeString = "Charity";
                                    Intent nextRegisterWindow = new Intent(AccountTypeActivity.this, RegisterActivity.class);
                                    nextRegisterWindow.putExtra("accountType", accountTypeString);
                                    startActivity(nextRegisterWindow);
                                }
                            }

                        }
            });
        }


        private void nextAccountRegisterPage() {
            int accountTypeId = accountType.getCheckedRadioButtonId();
            if(accountTypeId != -1) {
                if(accountTypeId == R.id.donorAccount) {

                }
                else if(accountTypeId == R.id.doneeAccount) {

                }
                else if(accountTypeId == R.id.charityAccount) {

                }
            }
            else {
                Toast.makeText(AccountTypeActivity.this, "Please select the type of your account", Toast.LENGTH_SHORT).show();
            }

    }

}
