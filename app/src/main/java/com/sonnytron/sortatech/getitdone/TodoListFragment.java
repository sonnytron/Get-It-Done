package com.sonnytron.sortatech.getitdone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.util.List;

/**
 * Created by sonnyrodriguez on 6/22/16.
 */
public class TodoListFragment extends Fragment {

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private RecyclerView mTodoRecyclerView;
    private TodoAdapter mAdapter;
    private boolean mSubtitleShowing;
    private Callbacks mCallbacks;

    public interface Callbacks {
        void onTodoSelected(Todo todo);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo_list, container, false);
        mTodoRecyclerView = (RecyclerView) view.findViewById(R.id.todo_recycler_view);
        mTodoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null) {
            mSubtitleShowing = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleShowing);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_todo_list, menu);

        MenuItem subItem = menu.findItem(R.id.menu_item_show_subtitles);
        if (mSubtitleShowing) {
            subItem.setTitle(R.string.hide_subtitle);
        } else {
            subItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_todo:
                Todo todo = new Todo();
                TodoManager.get(getActivity()).addTodo(todo);
                updateUI();
                mCallbacks.onTodoSelected(todo);
                return true;
            case R.id.menu_item_show_subtitles:
                mSubtitleShowing = !mSubtitleShowing;
                getActivity().invalidateOptionsMenu();
                updateSubtitleCount();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitleCount() {
        TodoManager todoManager = TodoManager.get(getActivity());
        int todoCount = todoManager.getTodos().size();
        String subtitle = getString(R.string.subtitle_format, todoCount);

        if (!mSubtitleShowing) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }
    
    public void updateUI() {
        TodoManager todoManager = TodoManager.get(getActivity());
        List<Todo> todos = todoManager.getTodos();

        if (mAdapter == null) {
            mAdapter = new TodoAdapter(todos);
            mTodoRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setTodos(todos);
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitleCount();
    }

    private class TodoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mStatusTextView;
        private ImageView mPhotoView;
        private Todo mTodo;
        private File mPhotoFile;

        public void bindTodo(Todo todo) {
            mTodo = todo;
            mTitleTextView.setText(mTodo.getTitle());
            mStatusTextView.setText(mTodo.getStatus());
            mPhotoFile = TodoManager.get(getActivity()).getPhotoFile(mTodo);
            updatePhotoView();
            updatePriorityStatus();
        }

        public TodoHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_todo_title_text_view);
            mStatusTextView = (TextView) itemView.findViewById(R.id.list_item_todo_status_text_view);
            mPhotoView = (ImageView) itemView.findViewById(R.id.list_item_todo_photo_view);
        }

        private void updatePhotoView() {
            if (mPhotoFile == null || !mPhotoFile.exists()) {
                mPhotoView.setImageDrawable(null);
            } else {
                Bitmap bitmap = PhotoUtilities.getScaledBitmap(mPhotoFile.getPath(), getActivity());
                mPhotoView.setImageBitmap(bitmap);
            }
        }

        private void updatePriorityStatus() {
            switch (mTodo.getPriority()) {
                case 0:
                    mStatusTextView.setTextColor(0xFF00CC00);
                    break;
                case 1:
                    mStatusTextView.setTextColor(0xFFFFFF66);
                    break;
                case 2:
                    mStatusTextView.setTextColor(0xFFFF5050);
                    break;
                default:
            }
        }

        @Override
        public void onClick(View v) {
            mCallbacks.onTodoSelected(mTodo);
        }
    }

    private class TodoAdapter extends RecyclerView.Adapter<TodoHolder> {
        private List<Todo> mTodos;

        public TodoAdapter(List<Todo> todos) {
            mTodos = todos;
        }

        @Override
        public TodoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_todo, parent, false);
            return new TodoHolder(view);
        }

        @Override
        public void onBindViewHolder(TodoHolder holder, int position) {
            Todo todo = mTodos.get(position);
            holder.bindTodo(todo);
        }

        @Override
        public int getItemCount() {
            return mTodos.size();
        }

        public void setTodos(List<Todo> todos) {
            mTodos = todos;
        }
    }
}
