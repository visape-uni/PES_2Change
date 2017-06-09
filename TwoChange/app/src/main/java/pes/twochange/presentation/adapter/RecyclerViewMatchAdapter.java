package pes.twochange.presentation.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import pes.twochange.R;
import pes.twochange.domain.model.Match;
import pes.twochange.presentation.view.OnRecyclerViewItemClickListener;

public class RecyclerViewMatchAdapter extends RecyclerView.Adapter<RecyclerViewMatchAdapter.ProductHolder> {

    private Context context;
    private ArrayList<Match> matchMap;
    private OnRecyclerViewItemClickListener listener;

    public RecyclerViewMatchAdapter(Context context, Map<String, Match> matchMap,
                                    OnRecyclerViewItemClickListener listener) {
        this.context = context;
        this.matchMap = new ArrayList<>(matchMap.values());
        this.listener = listener;
    }

    public void setMatchMap(Map<String, Match> matchMap) {
        this.matchMap = new ArrayList<>(matchMap.values());
    }

    @Override
    public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_item_view,
                null);
        return new ProductHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProductHolder holder, int position) {
        final int finalPosition = position;
        Match match = matchMap.get(position);
        holder.textView.setText("You have a match with " + match.getUsernameReceiver().toUpperCase()
                + " for " + match.getCategoryProductReceiver().toUpperCase());
        holder.itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onRecyclerViewItemClickListener(finalPosition);
                    }
                }
        );
        int color = 0xff0000ff;
        switch (match.getStatusInt()) {
            case Match.ACCEPTED_INT:
                color = 0xff00ff00;
                break;
            case Match.DENIED_INT:
                color = 0xffff0000;
                break;
        }
        holder.divider.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return matchMap.size();
    }

    class ProductHolder extends RecyclerView.ViewHolder {

        LinearLayout divider;
        View itemView;
        TextView textView;

        public ProductHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.textView = (TextView) itemView.findViewById(R.id.text);
            this.divider = (LinearLayout) itemView.findViewById(R.id.status_divider);
        }
    }
}
