package pes.twochange.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import pes.twochange.R;
import pes.twochange.domain.model.Profile;
import pes.twochange.services.ImageManager;

/**
 * Created by Visape on 07/06/2017.
 */

public class ProfileAdapter extends ArrayAdapter<Profile> {
    public ProfileAdapter (Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }
    public ProfileAdapter (Context context, int resource, List<Profile> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.profile, null);
        }

        Profile p = getItem(position);

        if (p != null) {
            TextView usernameTextView = (TextView) v.findViewById(R.id.profileUsername);
            TextView nameTextView = (TextView) v.findViewById(R.id.profileName);
            CircleImageView image = (CircleImageView) v.findViewById(R.id.profilePhoto);
            String imagePath = String.format("profiles/%s.jpg", p.getUsername());
            ImageManager.getInstance().putImageIntoView(imagePath, getContext(), image);

            if (usernameTextView != null) {
                usernameTextView.setText(p.getUsername());
            }

            if (nameTextView != null) {
                nameTextView.setText(p.fullName());
            }

        }

        return v;
    }
}
