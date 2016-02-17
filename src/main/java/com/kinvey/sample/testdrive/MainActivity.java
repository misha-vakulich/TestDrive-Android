package com.kinvey.sample.testdrive;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyDeleteCallback;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.Query;
import com.kinvey.java.core.KinveyClientCallback;
import com.kinvey.java.dto.User;
import com.kinvey.java.model.KinveyDeleteResponse;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = "TestDrive";

    private ProgressBar bar;


    private String appKey="your_app_key";
    private String appSecret="your_app_secret";

    private Client kinveyClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bar = (ProgressBar) findViewById(R.id.refresh_progress);
        bar.setIndeterminate(true);

        kinveyClient = new Client.Builder(appKey, appSecret, this).build();
        if (!kinveyClient.userStore().isUserLoggedIn()) {
            bar.setVisibility(View.VISIBLE);
            kinveyClient.userStore().login(new KinveyUserCallback() {
                @Override
                public void onSuccess(User result) {
                    bar.setVisibility(View.GONE);
                    Log.i(TAG, "Logged in successfully as " + result.getId());
                    Toast.makeText(MainActivity.this, "New implicit user logged in successfully as " + result.getId(),
                            Toast.LENGTH_LONG).show();
                }
                @Override
                public void onFailure(Throwable error) {
                    bar.setVisibility(View.GONE);
                    Log.e(TAG, "Login Failure", error);
                    Toast.makeText(MainActivity.this, "Login error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }   else {
            Toast.makeText(this, "Using cached implicit user " + kinveyClient.userStore().getCurrentUser().getId(), Toast.LENGTH_LONG).show();
        }
    }

    public void onLoadClick(View view) {
        bar.setVisibility(View.VISIBLE);
        kinveyClient.dataStore("entityCollection", Entity.class).find("myEntity", new KinveyClientCallback<Entity>() {
            @Override
            public void onSuccess(Entity result) {
                bar.setVisibility(View.GONE);

                if (result == null){
                    Toast.makeText(MainActivity.this,"Load Worked!\nNo data found", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this,"Load Worked!\nTitle: " + result.getTitle()
                            + "\nDescription: " + result.get("Description"), Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onFailure(Throwable error) {
                bar.setVisibility(View.GONE);
                Log.e(TAG, "AppData.getEntity Failure", error);
                Toast.makeText(MainActivity.this, "Save Failed!\n: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onQueryClick(View view) {
        bar.setVisibility(View.VISIBLE);
        Query myQuery = kinveyClient.query();
        myQuery.equals("_id", "myEntity");
        kinveyClient.dataStore("entityCollection", Entity.class).find(myQuery, new KinveyListCallback<Entity>() {
            @Override
            public void onSuccess(List<Entity> result) {
                bar.setVisibility(View.GONE);

                Toast.makeText(MainActivity.this, "query Worked!\n Got: " + result.size(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(Throwable error) {
                bar.setVisibility(View.GONE);
                Log.e(TAG, "AppData.get by Query Failure", error);
                Toast.makeText(MainActivity.this, "query Failed!\n " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onLoadAllClick(View view) {
        bar.setVisibility(View.VISIBLE);
        kinveyClient.dataStore("entityCollection", Entity.class).find(new KinveyListCallback<Entity>() {
            @Override
            public void onSuccess(List<Entity> result) {
                bar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Get All Worked!\n Got: " + result.size(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(Throwable error) {
                bar.setVisibility(View.GONE);
                Log.e(TAG, "AppData.get all Failure", error);
                Toast.makeText(MainActivity.this, "Get All error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onSaveClick(View view) {
        bar.setVisibility(View.VISIBLE);
        Entity entity = new Entity("myEntity");
        entity.put("Description","This is a description of a dynamically-added Entity property.");
        kinveyClient.dataStore("entityCollection", Entity.class).save(entity, new KinveyClientCallback<Entity>() {
            @Override
            public void onSuccess(Entity result) {
                bar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this,"Entity Saved\nTitle: " + result.getTitle()
                        + "\nDescription: " + result.get("Description"), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Throwable error) {
                bar.setVisibility(View.GONE);
                Log.e(TAG, "AppData.save Failure", error);
                Toast.makeText(MainActivity.this, "Save error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onDeleteClick(View view) {
        bar.setVisibility(View.VISIBLE);
        kinveyClient.dataStore("entityCollection", Entity.class).delete("myEntity", new KinveyDeleteCallback() {
            @Override
            public void onSuccess(Integer result) {
                bar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Number of Entities Deleted: " + result, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Throwable error) {
                bar.setVisibility(View.GONE);
                Log.e(TAG, "AppData.delete Failure", error);
                Toast.makeText(MainActivity.this, "Delete error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
