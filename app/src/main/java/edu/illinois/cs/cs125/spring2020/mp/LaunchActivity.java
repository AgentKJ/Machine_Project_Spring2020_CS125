package edu.illinois.cs.cs125.spring2020.mp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.List;

/**
 * LaunchActivity.class.
 */
public class LaunchActivity extends AppCompatActivity {

    /**
     * Request code for sign in.
     */
    private static final int RC_SIGN_IN = 0;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // Launch MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            // start login activity for result
            createSignInIntent();
        }
    }

    /**
     * Sign the user in.
     */
    public void createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Collections.singletonList(new AuthUI.IdpConfig.EmailBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_create_intent]
    }

    // [START auth_fui_result]

    /**
     *
     * @param requestCode the request code
     * @param resultCode the result code
     * @param data intent
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // Launch MainActivity
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                // Sign in failed. If response is null the user canceled the sign-in flow using the back button.
                // Otherwise check response.getError().getErrorCode() and handle the error.
                setContentView(R.layout.activity_launch);
                Button loginButton = findViewById(R.id.goLogin);
                loginButton.setOnClickListener((final View view) -> createSignInIntent());
            }
        }
    }
    // [END auth_fui_result]

}
