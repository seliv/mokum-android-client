package seliv.mokum;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView textView = (TextView) findViewById(R.id.TokenTextView);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void loginWithToken(View view) {
        EditText tokenEditText = (EditText) findViewById(R.id.TokenEditText);

        Intent intent = new Intent(view.getContext(), MainActivity.class);
        String value = tokenEditText.getText().toString();
        intent.putExtra("token", value);
        AuthManager.saveAuthToken(this, value);
        view.getContext().startActivity(intent);
    }
}
