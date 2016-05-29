package com.example.rm49865.recordsfood.holder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rm49865.recordsfood.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Ricardo on 10/02/2016.
 */
public class ListRestaurantHolder extends RecyclerView.ViewHolder{

    @Bind(R.id.cvRest)
    CardView cvRest;
    @Bind(R.id.ivRest)
    ImageView ivRest;
    @Bind(R.id.tvName)
    TextView tvName;
    @Bind(R.id.tvType)
    TextView tvType;
    @Bind(R.id.tvTel)
    TextView tvTel;
    @Bind(R.id.tvAvg)
    TextView tvAvg;
    @Bind(R.id.ibEdit)
    ImageButton ibEdit;

    public ListRestaurantHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

    public CardView getCvRest() {
        return cvRest;
    }

    public ImageView getIvRest() {
        return ivRest;
    }

    public TextView getTvName() {
        return tvName;
    }

    public TextView getTvType() {
        return tvType;
    }

    public TextView getTvTel() {
        return tvTel;
    }

    public TextView getTvAvg() {
        return tvAvg;
    }

    public ImageButton getIbEdit() {
        return ibEdit;
    }
}
