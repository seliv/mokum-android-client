package seliv.mokum;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        String authToken = AuthManager.getAuthToken(this);
        if (authToken != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("token", authToken);
            startActivity(intent);
        }
    }

    public void signIn(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
