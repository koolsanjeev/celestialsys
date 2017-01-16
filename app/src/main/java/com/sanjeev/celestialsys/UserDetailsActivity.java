package com.sanjeev.celestialsys;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.login.LoginManager;

import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class UserDetailsActivity extends AppCompatActivity {

    private User mUser;
    private EditText mEmailView;
    private EditText mUsernameView;
    private EditText mPasswordView;
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mAddressView;
    private Button mSubmitButton;
    private View mProgressView;
    private View mUserDetailsFormView;

    private void launchLoginActivity() {
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mUser.getId() != 0) {
            MenuItem signOut = menu.add("Sign out");
            signOut.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
            signOut.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    launchLoginActivity();
                    return false;
                }
            });
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        mUserDetailsFormView = findViewById(R.id.user_details_form);
        mProgressView = findViewById(R.id.progress);

        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mFirstNameView = (EditText) findViewById(R.id.firstname);
        mLastNameView = (EditText) findViewById(R.id.lastname);
        mEmailView = (EditText) findViewById(R.id.email);
        mAddressView = (EditText) findViewById(R.id.address);
        mSubmitButton = (Button) findViewById(R.id.submit_details_button);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Reset errors.
                mEmailView.setError(null);
                mPasswordView.setError(null);

                String email = mEmailView.getText().toString();
                String password = mPasswordView.getText().toString();

                boolean cancel = false;
                View focusView = null;

                // Check for a valid email address.
                if (TextUtils.isEmpty(email)) {
                    mEmailView.setError(getString(R.string.error_field_required));
                    focusView = mEmailView;
                    cancel = true;
                } else if (!ValidationUtils.isEmailValid(email)) {
                    mEmailView.setError(getString(R.string.error_invalid_email));
                    focusView = mEmailView;
                    cancel = true;
                }

                // Check for a valid password, if the user entered one.
                if (!TextUtils.isEmpty(password)) {
                    if (!ValidationUtils.isPasswordValid(password)) {
                        mPasswordView.setError(getString(R.string.error_invalid_password));
                        focusView = mPasswordView;
                        cancel = true;
                    }
                }

                if (cancel) {
                    // There was an error; don't attempt any action and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    // Show a progress spinner, and kick off a background task to
                    // perform the action.
                    showProgress(true);

                    mUser.setUsername(mUsernameView.getText().toString());
                    mUser.setPassword(mPasswordView.getText().toString());
                    mUser.setFirstName(mFirstNameView.getText().toString());
                    mUser.setLastName(mLastNameView.getText().toString());
                    mUser.setEmail(mEmailView.getText().toString());
                    mUser.setAddress(mAddressView.getText().toString());

                    if (mUser.getId() == 0) {
                        new UserRegistrationTask(mUser).execute();
                    } else {
                        new UserUpdateTask(mUser).execute();
                    }
                }
            }
        });

        mUser = (User) getIntent().getSerializableExtra("user");
        if (mUser.getId() == 0) {
            mUsernameView.setEnabled(true);
        }

        mUsernameView.setText(mUser.getUsername());
        mPasswordView.setText(mUser.getPassword());
        mFirstNameView.setText(mUser.getFirstName());
        mLastNameView.setText(mUser.getLastName());
        mEmailView.setText(mUser.getEmail());
        mAddressView.setText(mUser.getAddress());
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mUserDetailsFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mUserDetailsFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mUserDetailsFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mUserDetailsFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        launchLoginActivity();
    }

    /**
     * Represents an asynchronous user registration task used to authenticate
     * the User.
     */
    public class UserRegistrationTask extends AsyncTask<Void, Void, User> {

        private User mUser;

        UserRegistrationTask(User user) {
            mUser = user;
        }

        @Override
        protected User doInBackground(Void... params) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
            try {
                return restTemplate.postForObject(Constants.USERS_URL, mUser, User.class);
            } catch (Exception ex) {
                Snackbar.make(UserDetailsActivity.this.getCurrentFocus(), "Exception : " + ex.getMessage(), Snackbar.LENGTH_SHORT).show();
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(final User user) {
            showProgress(false);

            if (user == null) {
                Snackbar.make(UserDetailsActivity.this.getCurrentFocus(), "Failed to register record", Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(UserDetailsActivity.this.getCurrentFocus(), "Registered successfully", Snackbar.LENGTH_SHORT).show();
                mUsernameView.setEnabled(false);
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }

    /**
     * Represents an asynchronous user details update task used to authenticate
     * the User.
     */
    public class UserUpdateTask extends AsyncTask<Void, Void, Boolean> {

        private User mUser;

        UserUpdateTask(User user) {
            mUser = user;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
            try {
                restTemplate.put(Constants.USERS_URL, mUser);
            } catch (Exception ex) {
                Snackbar.make(UserDetailsActivity.this.getCurrentFocus(), "Exception : " + ex.getMessage(), Snackbar.LENGTH_LONG).show();
                cancel(true);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            showProgress(false);

            if (result) {
                Snackbar.make(UserDetailsActivity.this.getCurrentFocus(), "Record updated successfully", Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(UserDetailsActivity.this.getCurrentFocus(), "Failed to updated record", Snackbar.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }
}
