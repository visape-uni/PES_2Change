package pes.twochange.presentation.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;

import pes.twochange.R;
import pes.twochange.domain.model.Product;
import pes.twochange.domain.themes.AdTheme;
import pes.twochange.presentation.view.OnRecyclerViewItemLongClickListener;

import static pes.twochange.presentation.fragment.ProductFragment.ARG_PARAM1;
import static pes.twochange.presentation.fragment.ProductFragment.ARG_PARAM2;
import static pes.twochange.presentation.fragment.ProductFragment.ARG_PARAM3;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewProductFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProductFragment extends Fragment implements View.OnClickListener {
    private OnFragmentInteractionListener activity;
    private EditText nameTextView;
    private EditText descriptionTextView;
    private Spinner categorySpinner;
    private TextView postButton;
    private ProgressBar progressBar;

    private String[] categoryArray;

    private Product product;

    public EditProductFragment() {
        // Required empty public constructor
    }

    public static EditProductFragment newInstance(
            String id, String name, String description, String category
    ) {
        EditProductFragment fragment = new EditProductFragment();

        Bundle args = new Bundle();
        args.putString("id", id);
        args.putString(ARG_PARAM1, name);
        args.putString(ARG_PARAM2, description);
        args.putString(ARG_PARAM3, category);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_product, container, false);

        final ImageView closeButton = (ImageView) view.findViewById(R.id.close_button);
        nameTextView = (EditText) view.findViewById(R.id.products_name);
        descriptionTextView = (EditText) view.findViewById(R.id.products_description);
        categorySpinner = (Spinner) view.findViewById(R.id.category_spinner);
        postButton = (TextView) view.findViewById(R.id.post_product_button);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        categoryArray = getResources().getStringArray(R.array.ad_category);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, categoryArray);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        nameTextView.setText(getArguments().getString(ARG_PARAM1));
        descriptionTextView.setText(getArguments().getString(ARG_PARAM2));

        String category = getArguments().getString(ARG_PARAM2);
        int catIndex = Arrays.asList(categoryArray).indexOf(category);
        categorySpinner.setSelection(catIndex);

        closeButton.setOnClickListener(this);
        postButton.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_button:
                activity.close();
                break;

            case R.id.post_product_button:
                if (nameTextView.getText() == null || nameTextView.getText().length() == 0) {
                    alertError(R.string.product_name_error_title,
                            R.string.product_name_error_message);
                } else if (descriptionTextView.getText() == null
                        || descriptionTextView.getText().length() == 0) {
                    alertError(R.string.product_description_error_title,
                            R.string.product_description_error_message);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    postButton.setVisibility(View.GONE);
                    final String name = nameTextView.getText().toString().trim();
                    final String description = descriptionTextView.getText().toString().trim();
                    final String category = categoryArray[categorySpinner.getSelectedItemPosition()];

                    AdTheme.getInstance().getProduct(getArguments().getString("id"), new AdTheme.ProductResponse() {
                        @Override
                        public void success(Product product) {
                            product.setName(name);
                            product.setDescription(description);
                            product.setCategory(category);
                            activity.edit(product);
                            progressBar.setVisibility(View.GONE);
                            postButton.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void error(String error) {
                            Snackbar.make(
                                    postButton, "There was an error saving the product", Snackbar.LENGTH_LONG
                            ).show();
                        }
                    });


                    AdTheme.getInstance().save(product);

                    //activity.postProduct(product);
                }
        }
    }

    private void alertError(int title, int message) {
        new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(
                        R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                .show();
    }

    public interface OnFragmentInteractionListener extends OnRecyclerViewItemLongClickListener {
        void edit(Product product);
        void close();
    }
}
