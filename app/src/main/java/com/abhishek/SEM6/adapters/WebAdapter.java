package com.abhishek.SEM6.adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abhishek.SEM6.R;
import com.abhishek.SEM6.models.Book_db;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class WebAdapter extends RecyclerView.Adapter<WebAdapter.ViewHolder> {


    String [] names,links,pubs,contents;
    FragmentManager fragmentManager;

    private Context context;
    private ArrayList<Book_db> books;
    private LayoutInflater inflater;
    private String subjectname;

    class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView web_image;
        public TextView webtext;
        public CardView web_card;
        public TextView uploader;
        ItemLongClickListener itemLongClickListener;

        ViewHolder(View view)
        {
            super(view);
            web_card = (CardView) view.findViewById(R.id.web_card_view);
            webtext = (TextView) view.findViewById(R.id.web_text);
            uploader = (TextView) view.findViewById(R.id.pub_date);
            web_image = (ImageView) view.findViewById(R.id.web_image);

            //Log.d("myactivitysee",activity.toString()+"...");
            //Log.d("myactivitysee2","hehe "+activity.toString());
        }
    }

    public  WebAdapter(Context context, ArrayList<Book_db> books, String subjectName)
    {

        this.context = context;
        this.books = books;
        this.inflater = LayoutInflater.from(context);
        this.subjectname=subjectName;
    }

    @NonNull
    @Override
    public WebAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.web_card,viewGroup,false);
        //Log.d("myactivitysee",names[2]+" "+activity.toString());
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WebAdapter.ViewHolder viewHolder, int i) {

        final Book_db book = books.get(i);
        viewHolder.webtext.setText(book.name);
        viewHolder.uploader.setText(book.uploader);

        if(!book.thumbnail.isEmpty()) {
            // Picasso.get().load(book.thumbnail.replace("http","https")).into(holder.ivChapter);

            Glide.with(context).load(book.thumbnail).into(viewHolder.web_image);
        }
    }

    @Override
    public int getItemCount() {
        return books.size();
    }
}
