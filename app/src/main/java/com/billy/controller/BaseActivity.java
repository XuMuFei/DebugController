package com.billy.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * @author billy.qi
 * @since 17/5/31 13:11
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBackBtn();
    }

    protected void initBackBtn() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return onOptionsItemSelected(itemId) || super.onOptionsItemSelected(item);
    }

    protected boolean onOptionsItemSelected(int itemId) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int menuResId = getMenuResId();
        if (menuResId != 0) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(menuResId, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    protected int getMenuResId() {
        return 0;
    }
}
