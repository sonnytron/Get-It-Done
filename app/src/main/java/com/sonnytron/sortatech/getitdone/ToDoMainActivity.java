package com.sonnytron.sortatech.getitdone;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ToDoMainActivity extends SingleFragmentActivity implements TodoListFragment.Callbacks, ToDoFragment.Callbacks {

    @Override
    protected Fragment createFragment() { return new TodoListFragment(); }

    @Override
    public void onTodoSelected(Todo todo) {
        Intent intent = TodoPagerActivity.newIntent(this, todo.getId());
        startActivity(intent);
    }

    @Override
    public void onTodoUpdated(Todo todo) {
        TodoListFragment listFragment = (TodoListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }

    @Override
    public void onTodoDeleted(Todo todo) {
        TodoListFragment listFragment = (TodoListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}
