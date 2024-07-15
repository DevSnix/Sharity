package com.test.sharity;

import android.widget.RadioGroup;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AccountTypeActivity  extends AppCompatActivity {

    private RadioGroup accountType;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_account_type);

            Button nextButton = findViewById(R.id.btnNext);
            accountType = findViewById(R.id.account_type);

            nextButton.setOnClickListener(view -> {
                        int accountTypeId = accountType.getCheckedRadioButtonId();
                        String accountTypeString;

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
                                Intent nextRegisterWindow = new Intent(AccountTypeActivity.this, CharityRegisterActivity.class);
                                nextRegisterWindow.putExtra("accountType", accountTypeString);
                                startActivity(nextRegisterWindow);
                            }
                        }

                    });
        }

}
