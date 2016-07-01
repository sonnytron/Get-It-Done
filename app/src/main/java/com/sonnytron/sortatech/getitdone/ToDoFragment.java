package com.sonnytron.sortatech.getitdone;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 * Created by sonnyrodriguez on 6/22/16.
 */
public class ToDoFragment extends Fragment {
    private static final String ARG_TODO_ID = "todo_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 1;

    private Todo mTodo;
    private EditText mTitleField;
    private TextView mPriorityTextView;
    private File mPhotoFile;
    private Button mDueButton;
    private Button mStatusButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private Callbacks mCallbacks;
    private ImageButton mDeleteButton;


    public interface Callbacks {
        void onTodoUpdated(Todo todo);
        void onTodoDeleted(Todo todo);
    }

    public static ToDoFragment newInstance(UUID todoId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TODO_ID, todoId);

        ToDoFragment fragment = new ToDoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID todoId = (UUID) getArguments().getSerializable(ARG_TODO_ID);
        mTodo = TodoManager.get(getActivity()).getTodo(todoId);
        mPhotoFile = TodoManager.get(getActivity()).getPhotoFile(mTodo);
    }

    @Override
    public void onPause() {
        super.onPause();
        TodoManager.get(getActivity())
                .updateTodo(mTodo);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_todo, container, false);

        mTitleField = (EditText)v.findViewById(R.id.todo_item_title);
        mTitleField.setText(mTodo.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTodo.setTitle(s.toString());
                updateTodo();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDueButton = (Button)v.findViewById(R.id.due_date);
        updateDueDate(mTodo.dateString());
        mDueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mTodo.getDueDate());
                dialog.setTargetFragment(ToDoFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        PackageManager packageManager = getActivity().getPackageManager();

        mStatusButton = (Button)v.findViewById(R.id.status_button);
        mStatusButton.setText("Change Priority");
        mStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTodo.changePriority();
                TodoManager.get(getActivity()).updateTodo(mTodo);
                updatePriorityView();
                showToastForStatus();
            }
        });

        mPhotoButton = (ImageButton)v.findViewById(R.id.todo_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canCapturePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        if (canCapturePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView)v.findViewById(R.id.todo_photo);
        updatePhotoView();

        mPriorityTextView = (TextView)v.findViewById(R.id.todo_fragment_priority_text);
        updatePriorityView();

        mDeleteButton = (ImageButton) v.findViewById(R.id.todo_item_delete);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertForDelete();
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mTodo.setDueDate(date);
            updateDueDate("Due on: " + mTodo.getDueDate().toString());
            updateTodo();
        } else if (requestCode == REQUEST_PHOTO) {
            updateTodo();
            updatePhotoView();
        }
    }

    private void updateTodo() {
        TodoManager.get(getActivity()).updateTodo(mTodo);
        mCallbacks.onTodoUpdated(mTodo);
    }

    private void updateDueDate(String text) {
        mDueButton.setText(text);
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Picasso.with(getContext()).load(Uri.fromFile(mPhotoFile)).into(mPhotoView);
        }
    }

    private void updatePriorityView() {
        String priorityString = new String();
        switch (mTodo.getPriority()) {
            case 0:
                priorityString = "Low";
                mPriorityTextView.setTextColor(0xFF00CC00);
                break;
            case 1:
                priorityString = "Normal";
                mPriorityTextView.setTextColor(0xFFFFFF66);
                break;
            case 2:
                priorityString = "High";
                mPriorityTextView.setTextColor(0xFFFF5050);
                break;
            default:
        }
        mPriorityTextView.setText(priorityString);
    }

    private void showToastForStatus() {
        Context context = getContext();
        CharSequence text = mTodo.getStatus();
        Toast.makeText(getContext(), mTodo.getStatus(), Toast.LENGTH_SHORT).show();
    }

    private void showAlertForDelete() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setPositiveButton("For Sure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCallbacks.onTodoDeleted(mTodo);
                TodoManager.get(getActivity()).deleteTodo(mTodo);
                getActivity().onBackPressed();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCancelable(true);
        alert.setTitle("Are you sure you want to delete this Todo? There's no bringing it back!");
        alert.show();
    }
}
