package pes.twochange.presentation.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import pes.twochange.R;
import pes.twochange.presentation.adapter.ImagePagerAdapter;
import pes.twochange.presentation.view.ViewPagerIndicator;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProductFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    protected static final String ARG_PARAM1 = "name";
    protected static final String ARG_PARAM2 = "description";
    protected static final String ARG_PARAM3 = "category";
    protected static final String ARG_PARAM4 = "rating";
    protected static final String ARG_PARAM5 = "images";

    protected String name;
    protected String description;
    protected String category;
    protected int rating;
    protected ArrayList<String> images;

    private OnFragmentInteractionListener activity;

    public ProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param name Parameter 1.
     * @param description Parameter 2.
     * @param category Parameter 3.
     * @param rating Parameter 4.
     * @param images Parameter 5.
     * @return A new instance of fragment ProductFragment.
     */
    public static ProductFragment newInstance(String name, String description, String category,
                                              int rating, ArrayList<String> images) {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, name);
        args.putString(ARG_PARAM2, description);
        args.putString(ARG_PARAM3, category);
        args.putInt(ARG_PARAM4, rating);
        args.putStringArrayList(ARG_PARAM5, images);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(ARG_PARAM1);
            description = getArguments().getString(ARG_PARAM2);
            category = getArguments().getString(ARG_PARAM3);
            rating = getArguments().getInt(ARG_PARAM4);
            images = getArguments().getStringArrayList(ARG_PARAM5);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        ViewPager imagePager = (ViewPager) view.findViewById(R.id.image_pager);
        imagePager.setAdapter(new ImagePagerAdapter(getFragmentManager(), images));
        ViewPagerIndicator indicator = (ViewPagerIndicator) view.findViewById(R.id.indicator);
        indicator.setupWithViewPager(imagePager);
        TextView nameTextView = (TextView) view.findViewById(R.id.product_name);
        nameTextView.setText(name);
        TextView categoryTextView = (TextView) view.findViewById(R.id.product_category);
        categoryTextView.setText(category);
        TextView descriptionTextView = (TextView) view.findViewById(R.id.product_description);
        descriptionTextView.setText(description);
        TextView ratingTextView = (TextView) view.findViewById(R.id.product_rating);
        ratingTextView.setText(String.format(Locale.FRANCE, "%d", rating));
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            activity = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    public interface OnFragmentInteractionListener {
    }
}
