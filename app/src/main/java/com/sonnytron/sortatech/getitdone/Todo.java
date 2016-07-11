package com.sonnytron.sortatech.getitdone;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by sonnyrodriguez on 6/22/16.
 */
public class Todo {
    private UUID mId;
    private String mTitle;
    private Date mDueDate;
    private String mStatus;
    private int mPriority;
    private int mDone;

    public Todo() {
        this(UUID.randomUUID());
    }

    public Todo(UUID id) {
        mId = id;
        mStatus = "Normal";
        mDone = 0;
        mDueDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDueDate() {
        return mDueDate;
    }

    public void setDueDate(Date dueDate) {
        mDueDate = dueDate;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setDone(int done) {
        mDone = done;
    }

    public int getDone() {
        return mDone;
    }

    public boolean isDone() {
        return mDone == 1;
    }

    public void setStatus(String status) {
        if (status.equals("Low")) {
            mPriority = 0;
        } else if (status.equals("High")) {
            mPriority = 2;
        } else {
            mPriority = 1;
        }
        mStatus = status;
    }

    public int getPriority() {
        return mPriority;
    }

    public void changePriority() {
        switch (mPriority) {
            case 0:
                mPriority = 1;
                setStatus("Normal");
                break;
            case 1:
                mPriority = 2;
                setStatus("High");
                break;
            case 2:
                mPriority = 0;
                setStatus("Low");
                break;
            default:
                setStatus("Normal");
        }
    }

    public String getPhotoFilename() { return "IMG_" + getId().toString() + ".jpg"; }

    public String dateString() {
        if (mDueDate == null) {
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy");
        return "Complete by: " + formatter.format(mDueDate);
    }
}
