package pes.twochange.presentation.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;

import pes.twochange.R;
import pes.twochange.domain.model.Product;

public class ProductFirebaseListAdapter extends FirebaseListAdapter<Product> {
    public ProductFirebaseListAdapter(Activity activity, Class<Product> modelClass, int modelLayout, DatabaseReference ref) {
        super(activity, modelClass, modelLayout, ref);
    }

    @Override
    protected void populateView(View v, Product model, int position) {
        TextView productTitle, productKey;
        productTitle = (TextView) v.findViewById(R.id.product_title);
        productKey = (TextView) v.findViewById(R.id.product_key);

        productTitle.setText(model.getName());
        productKey.setText(model.getId());
    }
}
