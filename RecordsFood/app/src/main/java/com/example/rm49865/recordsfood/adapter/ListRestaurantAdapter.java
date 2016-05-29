package com.example.rm49865.recordsfood.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rm49865.recordsfood.R;
import com.example.rm49865.recordsfood.fragment.ListRestaurantsFragment;
import com.example.rm49865.recordsfood.holder.ListRestaurantHolder;
import com.example.rm49865.recordsfood.model.Restaurant;

import java.util.List;

/**
 * Created by Ricardo on 10/02/2016.
 */
public class ListRestaurantAdapter extends RecyclerView.Adapter<ListRestaurantHolder>{

    private final Context context;
    private final ListRestaurantsFragment.OnListRestaurantsInteractionListener mListener;
    private final List<Restaurant> restaurants;

    public ListRestaurantAdapter(Context context, List<Restaurant> restaurants,
                                 ListRestaurantsFragment.OnListRestaurantsInteractionListener mListener){
        this.context = context;
        this.restaurants = restaurants;
        this.mListener = mListener;
    }

    @Override
    public ListRestaurantHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ListRestaurantHolder(LayoutInflater.from(context).inflate(R.layout.row_list_restaurants, parent, false));
    }

    @Override
    public void onBindViewHolder(ListRestaurantHolder holder, final int position) {
        final Restaurant restaurant = restaurants.get(position);

        holder.getIvRest().setImageBitmap(BitmapFactory.decodeFile(restaurant.getImagePath()));
        holder.getTvName().setText(restaurant.getName());
        holder.getTvType().setText(restaurant.getType());
        holder.getTvTel().setText(restaurant.getTel());
        holder.getTvAvg().setText(restaurant.getAverageCost().toString());

        holder.getCvRest().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.detail(position);
            }
        });

        holder.getIbEdit().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.edit(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }
}
