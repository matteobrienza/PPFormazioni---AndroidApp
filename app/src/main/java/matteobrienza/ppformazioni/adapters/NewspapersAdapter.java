package matteobrienza.ppformazioni.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import matteobrienza.ppformazioni.interfaces.INewspaperAdapterClicks;
import matteobrienza.ppformazioni.R;
import matteobrienza.ppformazioni.models.Newspaper;

/**
 * Created by Matteo on 03/01/2017.
 */

public class NewspapersAdapter extends RecyclerView.Adapter<NewspapersAdapter.NewspaperViewHolder> {

    private List<Newspaper> newspapers;
    private INewspaperAdapterClicks interfaceClick;
    private Context context;

    private Button buttonSelected;

    public class NewspaperViewHolder extends RecyclerView.ViewHolder {

        public Button Newspaper;

        public NewspaperViewHolder(View v) {
            super(v);
            Newspaper = (Button)v.findViewById(R.id.newspaper);
        }
    }

    public NewspapersAdapter(List<Newspaper> ns, INewspaperAdapterClicks ic, Context c) {
        newspapers = ns;
        interfaceClick = ic;
        context = c;
    }


    @Override
    public NewspaperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_newspaper, parent, false);
        return new NewspaperViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NewspaperViewHolder holder, final int position) {

        if(position==0){
            buttonSelected = holder.Newspaper;
            buttonSelected.setBackground(context.getResources().getDrawable(R.drawable.newspaper_shape_active));
        }

        holder.Newspaper.setText(newspapers.get(position).getName());
        holder.Newspaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSelected.setBackground(context.getResources().getDrawable(R.drawable.newspaper_shape_inactive));
                buttonSelected = (Button)v;
                buttonSelected.setBackground(context.getResources().getDrawable(R.drawable.newspaper_shape_active));
                interfaceClick.onNewspaperItemClick(newspapers.get(position).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return newspapers.size();
    }
}