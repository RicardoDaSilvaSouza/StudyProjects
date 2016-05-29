package com.example.rm49865.recordsfood;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.google.android.gms.auth.api.Auth;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = LoginActivity.class.getName();
    private final String STAYING_CONNECTED = "stayingConnected";
    @Bind(R.id.tlUser)
    TextInputLayout tlUser;
    @Bind(R.id.tlPassword)
    TextInputLayout tlPassword;
    @Bind(R.id.cbConnected)
    CheckBox cbConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        if(isConnected()){
            initApp();
        }
    }

    @OnClick(R.id.btLogin)
    public void doLogin(View view){
        if(validateCommonUser()){
            stayConnected();
            initApp();
        } else {
            tlPassword.setError(getString(R.string.message_error_login));
        }
    }

    private void stayConnected(){
        SharedPreferences preferences = getSharedPreferences(MainActivity.PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(STAYING_CONNECTED, cbConnected.isChecked());
        editor.commit();
    }

    private void initApp(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private boolean isConnected(){
        return getSharedPreferences(MainActivity.PREFERENCES_NAME, MODE_PRIVATE).getBoolean(STAYING_CONNECTED, false);
    }

    private boolean validateCommonUser(){
        boolean result = false;
        if((!tlUser.getEditText().getText().toString().isEmpty()
                && tlUser.getEditText().getText().toString().equalsIgnoreCase("admin"))
                && (!tlPassword.getEditText().getText().toString().isEmpty()
                && tlPassword.getEditText().getText().toString().equalsIgnoreCase("admin"))){
            result = true;
        }
        return result;
    }
}
