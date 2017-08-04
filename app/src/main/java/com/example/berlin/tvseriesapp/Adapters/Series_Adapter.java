package com.example.berlin.tvseriesapp.Adapters;

import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.berlin.tvseriesapp.Models.Series_Model;
import com.example.berlin.tvseriesapp.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;


public class Series_Adapter extends RecyclerView.Adapter<Series_Adapter.MyViewHolder>  {
    ArrayList<Series_Model> series_models;
    Context context;
    int  LastPosition=-1;
    RecyclerViewClickListener recyclerViewClickListener ;
    public Series_Adapter(){}
    public Series_Adapter(ArrayList<Series_Model>series_models, Context context){
        this.series_models=new ArrayList<>();
        this.series_models=series_models;
        this.context=context;
    }

    public void setClickListener(RecyclerViewClickListener clickListener){
       this.recyclerViewClickListener= clickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.poster_movie,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Picasso.with(context).load(holder.imgString+series_models.get(position).getPoster_ImageUrl())
                .placeholder(R.drawable.loadingicon).error(R.drawable.error).into(holder.PosterImg);
        setAnimation(holder.PosterContainer,position);
    }

    @Override
    public int getItemCount() {
        return series_models.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final public  String imgString= "http://image.tmdb.org/t/p/w185/";
        ImageView PosterImg;
        RelativeLayout PosterContainer;
        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            PosterImg=(ImageView)itemView.findViewById(R.id.Poster_Image2);
            PosterContainer=(RelativeLayout)itemView.findViewById(R.id.PosterContainer);
        }

        @Override
        public void onClick(View view) {
            if(recyclerViewClickListener!=null)
                recyclerViewClickListener.ItemClicked(view ,getAdapterPosition());
        }

        public void clearAnimation() {
            PosterContainer.clearAnimation();
        }
    }

    public interface RecyclerViewClickListener {
        public void ItemClicked(View v, int position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > LastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            LastPosition = position;
        }
    }

    private ArrayList<Series_Model> models;

    @Override
    public void onViewDetachedFromWindow(MyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
             holder.clearAnimation();
    }


    public ArrayList<Series_Model> getMovies() {
        return models;
    }
}