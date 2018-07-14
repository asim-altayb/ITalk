package com.hackathon.light.italk;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Emad Abbas on 7/13/2018.
 */

public class SignAdapter extends RecyclerView.Adapter<SignAdapter.SignCardHolder> {
    private final Context mContex;
    private final int mSignImageID[] = {R.drawable.a, R.drawable.b,
                                        R.drawable.c, R.drawable.d,
                                        R.drawable.e, R.drawable.f,
                                        R.drawable.g, R.drawable.h,
                                        R.drawable.i, R.drawable.j,
                                        R.drawable.k, R.drawable.l,
                                        R.drawable.m, R.drawable.n,
                                        R.drawable.o, R.drawable.p,
                                        R.drawable.q, R.drawable.r,
                                        R.drawable.s, R.drawable.t,
                                        R.drawable.u, R.drawable.v,
                                        R.drawable.w, R.drawable.x,
                                        R.drawable.y, R.drawable.z};
    private final char letters[] = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
                                    'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
                                    'q','r','s', 't', 'u', 'v', 'w', 'x',
                                    'y', 'z'};
    public SignAdapter(Context contex) {
        this.mContex = contex;
    }


    @Override
    public SignCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sign_card,parent,
                false);
        return new SignCardHolder(v);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(final SignCardHolder holder, final int position) {
        holder.imageView.setImageResource(mSignImageID[position]);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(letters != null) {
                    TabsActivity.text += (letters[position]);
                    TabsActivity.mSpokenWordTextView.setText(TabsActivity.text);
                }
            }
        });
    }

    public class SignCardHolder extends RecyclerView.ViewHolder{

        ImageView imageView ;
        public SignCardHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.sign_card_image);
        }
    }

    @Override
    public long getItemId(int position) {
        return mSignImageID[position];
    }

    @Override
    public int getItemCount() {
        return mSignImageID.length;
    }

    /*@Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ImageView signImageView = (ImageView) getItem(position);
        //optimizing memory usage by recycling cells
        if(view == null){
            final LayoutInflater layoutInflater = LayoutInflater.from(mContex);
            view = layoutInflater.inflate(R.layout.sign_card, null);
        }
        signImageView.setImageResource(mSignImageID[position]);
        return signImageView;
    }*/
}
