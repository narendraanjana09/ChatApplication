package com.example.baatein.AdapterViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baatein.R;

public class StatusViewHolder extends RecyclerView.ViewHolder {
    TextView nametxt;
    ImageView imageView;
    RelativeLayout status_layout;
    public StatusViewHolder(@NonNull View itemView) {
        super(itemView);
        nametxt=itemView.findViewById(R.id.nameTextView);
        imageView=itemView.findViewById(R.id.imageview);

        status_layout=itemView.findViewById(R.id.status_item_layout);
    }

    public RelativeLayout getStatus_layout() {
        return status_layout;
    }

    public void setStatus_layout(RelativeLayout status_layout) {
        this.status_layout = status_layout;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public TextView getNametxt() {
        return nametxt;
    }

    public void setNametxt(TextView nametxt) {
        this.nametxt = nametxt;
    }
}
