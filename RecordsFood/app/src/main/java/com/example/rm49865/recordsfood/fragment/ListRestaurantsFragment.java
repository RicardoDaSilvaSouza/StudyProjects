package com.example.rm49865.recordsfood.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rm49865.recordsfood.R;
import com.example.rm49865.recordsfood.adapter.ListRestaurantAdapter;
import com.example.rm49865.recordsfood.dao.RestaurantDAO;
import com.example.rm49865.recordsfood.model.Restaurant;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListRestaurantsFragment extends Fragment {

    private final String TAG = ListRestaurantsFragment.class.getName();
    public static final String PARAM_RESTAURANTS = "restaurants";

    private OnListRestaurantsInteractionListener mListener;
    private List<Restaurant> restaurants;
    @Bind(R.id.rvRest)
    RecyclerView rvRest;
    @Bind(R.id.tvNotResults)
    TextView tvNotResults;

    public static ListRestaurantsFragment newInstance(List<Restaurant> restaurants){
        ListRestaurantsFragment fragment = new ListRestaurantsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(PARAM_RESTAURANTS, (ArrayList<? extends Parcelable>) restaurants);
        fragment.setArguments(bundle);
        return fragment;
    }

    public ListRestaurantsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null && getArguments().containsKey(PARAM_RESTAURANTS)){
            restaurants = getArguments().getParcelableArrayList(PARAM_RESTAURANTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_restaurants, container, false);
        ButterKnife.bind(this, view);
        rvRest.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRest.setHasFixedSize(true);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(restaurants == null || restaurants.isEmpty()){
            rvRest.setVisibility(View.GONE);
            tvNotResults.setVisibility(View.VISIBLE);
        } else {
            rvRest.setVisibility(View.VISIBLE);
            tvNotResults.setVisibility(View.GONE);
            rvRest.setAdapter(new ListRestaurantAdapter(getContext(), restaurants, mListener));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnListRestaurantsInteractionListener) context;
        } catch (ClassCastException e){
            throw new RuntimeException("The activity "
                    + context.toString() + " must implements the interface "
                    + OnListRestaurantsInteractionListener.class.getName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListRestaurantsInteractionListener{
        void edit(int position);
        void detail(int position);
    }
}
