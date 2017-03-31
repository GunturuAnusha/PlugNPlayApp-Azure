package com.example.plugnplay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableQueryCallback;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncContext;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.MobileServiceLocalStoreException;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore;
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;

/**
 * Created by Anusha on 3/22/2017.
 */

public class CreateAccountActivityCopy extends Activity {

    private MobileServiceClient mClient;

    private MobileServiceTable<user> mUser;
    private ProgressBar mProgressBar;

    private EditText mEditTextUsernameCreate, mEditTextEmailCreate, mEditTextPasswordCreate, mEditTextPhone, mEditTextAge, mEditTextSubscribed;
    private String mUserName, mUserEmail, mPassword, mPhone, mAge, mSubscribed;
    private Button mSignupButton;

    private TextView mSigninLink;


    private Button mSigninButton;
    private static final String TAG = CreateAccountActivityCopy.class.getSimpleName();

    public static final int GOOGLE_LOGIN_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_account);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        try {
            // Create the Mobile Service Client instance, using the provided

            // Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://carreserve.azurewebsites.net",
                    this).withFilter(new ProgressFilter());

            // Extend timeout from default of 10s to 20s
            mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(20, TimeUnit.SECONDS);
                    client.setWriteTimeout(20, TimeUnit.SECONDS);
                    return client;
                }
            });

            // Get the Mobile Service Table instance to use

            mUser = mClient.getTable(user.class);




            initLocalStore().get();

            mEditTextUsernameCreate = (EditText) findViewById(R.id.userName);
            mEditTextEmailCreate = (EditText) findViewById(R.id.email);
            mEditTextPasswordCreate = (EditText) findViewById(R.id.password);
            mEditTextAge = (EditText) findViewById(R.id.age);
            mEditTextPhone = (EditText) findViewById(R.id.phone);
            mEditTextSubscribed = (EditText) findViewById(R.id.signUpSubscribed);

            mSigninLink = (TextView) findViewById(R.id.signinhere);

            //mEmail = (EditText) findViewById(R.id.email);
            // mPassword = (EditText) findViewById(R.id.password);

            mSigninButton = (Button) findViewById(R.id.signin);

            mSignupButton = (Button) findViewById(R.id.signup);

            mSignupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUserName = mEditTextUsernameCreate.getText().toString();
                    mUserEmail = mEditTextEmailCreate.getText().toString();
                    mPassword = mEditTextPasswordCreate.getText().toString();
                    mAge = mEditTextAge.getText().toString();
                    mPhone = mEditTextPhone.getText().toString();
                    mSubscribed = mEditTextSubscribed.getText().toString();
                    mUserEmail = mUserEmail.trim();
                    mPassword = mPassword.trim();

                    if (mPassword.isEmpty() || mUserEmail.isEmpty()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccountActivityCopy.this);
                        builder.setMessage(R.string.signup_error_message)
                                .setTitle(R.string.signup_error_title)
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        final String userName = mUserName;
                        final String emailAddress = mUserEmail;
                        final String password = mPassword;
                        final String phone = mPhone;
                        final String age = mAge;
                        final String subscribed = "";

                        final user user1 = new user();

                        final String email = emailAddress;
                        final String passwd = password;
                        final String username = userName;

                        //Login with an email/password combination
                        final String finalEmail = email;
                        final String pwd = passwd;

                        // mClient.login(userName, passwd);
                        //authenticate();
                        addItem(v);
                        Intent i = new Intent(CreateAccountActivityCopy.this, HomePage.class);
                        i.putExtra("userName", finalEmail);
                        i.putExtra("password", pwd);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);

                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccountActivityCopy.this);
                    addItem(v);
                    Intent intent = new Intent(CreateAccountActivityCopy.this, HomePage.class);

                    startActivity(intent);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            });

            mSigninLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(CreateAccountActivityCopy.this, LoginActivity.class);
                    startActivity(i);
                }
            });

            //  authenticate();


            // Load the items from the Mobile Service
            refreshItemsFromTable();

        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        } catch (Exception e) {
            createAndShowDialog(e, "Error");
        }

        //  setSupportActionBar(toolbar);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        // client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void addItem(View view) {
        if (mClient == null) {
            return;
        }

        // Create a new item
        final user item = new user();

        item.setUserName(mUserName.toString());
        item.setPassword(mPassword.toString());
        item.setEmailAddress(mUserEmail.toString());
        item.setPhone(mPhone.toString());
        item.setAge(mAge.toString());
        item.setmComplete(false);


        // Insert the new item
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final user entity = addItemInTable(item);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            /*if(!entity.isComplete()){
                                mAdapter.add(entity);
                            }*/
                        }
                    });
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };

        runAsyncTask(task);


        //mUserName.setText("");
        //  mPassword.setText("");

    }

    public user addItemInTable(user item) throws ExecutionException, InterruptedException {
        user entity = mUser.insert(item).get();
        return entity;
    }


    private AsyncTask<Void, Void, Void> initLocalStore() throws MobileServiceLocalStoreException, ExecutionException, InterruptedException {

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    MobileServiceSyncContext syncContext = mClient.getSyncContext();

                    if (syncContext.isInitialized())
                        return null;

                    SQLiteLocalStore localStore = new SQLiteLocalStore(mClient.getContext(), "OfflineStore", null, 1);

                    Map<String, ColumnDataType> tableDefinition = new HashMap<String, ColumnDataType>();
                    tableDefinition.put("id", ColumnDataType.String);
                    tableDefinition.put("username", ColumnDataType.String);
                    tableDefinition.put("password", ColumnDataType.String);
                    tableDefinition.put("complete", ColumnDataType.Boolean);

                    localStore.defineTable("User", tableDefinition);

                    SimpleSyncHandler handler = new SimpleSyncHandler();

                    syncContext.initialize(localStore, handler).get();

                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        return runAsyncTask(task);
    }

    private void refreshItemsFromTable() {

        // Get the items that weren't marked as completed and add them in the
        // adapter

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    final List<user> results = refreshItemsFromMobileServiceTable();

                    //Offline Sync
                    //final List<ToDoItem> results = refreshItemsFromMobileServiceTableSyncTable();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                          /*  mAdapter.clear();

                            for (ToDoItem item : results) {
                                mAdapter.add(item);
                            }*/
                        }
                    });
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        runAsyncTask(task);
    }


    private List<user> refreshItemsFromMobileServiceTable() throws ExecutionException, InterruptedException {
        return mUser.where().field("complete").
                eq(val(false)).execute().get();
    }


    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }


    private void createAndShowDialogFromTask(final Exception exception, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, "Error");
            }
        });
    }


    /**
     * Creates a dialog and shows it
     *
     * @param exception The exception to show in the dialog
     * @param title     The dialog title
     */
    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if (exception.getCause() != null) {
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    /**
     * Creates a dialog and shows it
     *
     * @param message The dialog message
     * @param title   The dialog title
     */
    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }


    private class ProgressFilter implements ServiceFilter {

        @Override
        public ListenableFuture<ServiceFilterResponse> handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback) {

            final SettableFuture<ServiceFilterResponse> resultFuture = SettableFuture.create();


            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.VISIBLE);
                }
            });

            ListenableFuture<ServiceFilterResponse> future = nextServiceFilterCallback.onNext(request);

            Futures.addCallback(future, new FutureCallback<ServiceFilterResponse>() {
                @Override
                public void onFailure(Throwable e) {
                    resultFuture.setException(e);
                }

                @Override
                public void onSuccess(ServiceFilterResponse response) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.GONE);
                        }
                    });

                    resultFuture.set(response);
                }
            });

            return resultFuture;
        }
    }
}

