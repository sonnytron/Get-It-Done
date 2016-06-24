package com.sonnytron.sortatech.getitdone;

import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by sonnyrodriguez on 6/22/16.
 */
public class TodoListActivity extends SingleFragmentActivity implements TodoListFragment.Callbacks, ToDoFragment.Callbacks {
    @Override
    protected Fragment createFragment() {
        return new TodoListFragment();
    }

    @Override
    public void onTodoSelected(Todo todo) {
        Intent intent = TodoPagerActivity.newIntent(this, todo.getId());
        startActivity(intent);
    }

    @Override
    public void onTodoUpdated(Todo todo) {
        TodoListFragment listFragment = (TodoListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }

    @Override
    public void onTodoDeleted(Todo todo) {
        TodoListFragment listFragment = (TodoListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }

}
