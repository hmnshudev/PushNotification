package com.push.notification.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.push.notification.R;
import com.push.notification.getter.Notifications;

import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Himanshu Sharma on 09/07/19.
 */

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    FirebaseFirestore firebaseFirestore;
    private List<Notifications> mNotifList;
    private Context context;

    public NotificationsAdapter(Context context, List<Notifications> mNotifList) {

        this.mNotifList = mNotifList;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_notification, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        firebaseFirestore = FirebaseFirestore.getInstance();

        String from_id = mNotifList.get(position).getFrom();

        holder.mNotifMessage.setText(mNotifList.get(position).getMessage());

        String nTimeStamp = mNotifList.get(position).getTime();

        final String nDate = "dd/MM/yy";
        String nTime = DateUtils.formatDateTime(context, Long.valueOf(nTimeStamp), DateUtils.FORMAT_SHOW_TIME);

        Calendar get = Calendar.getInstance();
        get.setTimeInMillis(Long.valueOf(nTimeStamp));
        Calendar now = Calendar.getInstance();

        if (now.get(Calendar.DATE) == get.get(Calendar.DATE)) {
            holder.mTime.setText(context.getString(R.string.time, nTime));
        } else if ((now.get(Calendar.DATE) - get.get(Calendar.DATE) == 1) && (now.get(Calendar.MONTH) - get.get(Calendar.MONTH) == 1)) {
            holder.mTime.setText(context.getString(R.string.date, DateFormat.format(nDate, get).toString()));
        } else if (now.get(Calendar.DATE) - get.get(Calendar.DATE) == 1) {
            holder.mTime.setText(R.string.yesterday);
        } else {
            holder.mTime.setText(context.getString(R.string.date, DateFormat.format(nDate, get).toString()));
        }


        firebaseFirestore.collection("Users").document(from_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String name = documentSnapshot.getString("name");
                String image = documentSnapshot.getString("image");

                holder.mNotifName.setText(name);

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.mipmap.default_image);

                Glide.with(context).setDefaultRequestOptions(requestOptions).load(image).into(holder.mNotifImage);

            }
        });


    }

    @Override
    public int getItemCount() {
        return mNotifList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        CircleImageView mNotifImage;
        TextView mNotifName;
        TextView mNotifMessage;
        TextView mTime;

        ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mNotifImage = mView.findViewById(R.id.notif_image);
            mNotifMessage = mView.findViewById(R.id.notif_message);
            mNotifName = mView.findViewById(R.id.notif_name);
            mTime = mView.findViewById(R.id.time);


        }
    }

}
