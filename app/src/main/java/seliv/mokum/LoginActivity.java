package seliv.mokum;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void loginWithToken(View view) {
        EditText tokenEditText = (EditText) findViewById(R.id.TokenEditText);

        Intent intent = new Intent(view.getContext(), MainActivity.class);
        String value = tokenEditText.getText().toString();
        intent.putExtra("token", value);
        view.getContext().startActivity(intent);
    }
}
