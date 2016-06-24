package com.sonnytron.sortatech.getitdone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

/**
 * Created by sonnyrodriguez on 6/22/16.
 */
public class TodoPagerActivity extends AppCompatActivity implements ToDoFragment.Callbacks {
    private static final String EXTRA_TODO_ID = "com.sonnytron.sortatech.getitdone.todo_id";

    private ViewPager mViewPager;
    private List<Todo> mTodos;

    public static Intent newIntent(Context packageContext, UUID todoId) {
        Intent intent = new Intent(packageContext, TodoPagerActivity.class);
        intent.putExtra(EXTRA_TODO_ID, todoId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_pager);

        UUID todoId = (UUID) getIntent().getSerializableExtra(EXTRA_TODO_ID);

        mViewPager = (ViewPager) findViewById(R.id.activity_todo_view_pager);
        mTodos = TodoManager.get(this).getTodos();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Todo todo = mTodos.get(position);
                return ToDoFragment.newInstance(todo.getId());
            }

            @Override
            public int getCount() {
                return mTodos.size();
            }
        });

        for (int i = 0; i < mTodos.size(); i++) {
            if (mTodos.get(i).getId().equals(todoId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
    @Override
    public void onTodoUpdated(Todo todo) {

    }
}
