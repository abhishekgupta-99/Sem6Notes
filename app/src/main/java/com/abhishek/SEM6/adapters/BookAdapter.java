package com.abhishek.SEM6.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.abhishek.SEM6.R;
import com.abhishek.SEM6.models.Book;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Created by abhishek on 3/2020.
 */

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.CustomViewHolder> {

    private ArrayList<Book> books;
    private LayoutInflater inflater;

    public BookAdapter(Context context, ArrayList<Book> books) {
        this.books = books;
        this.inflater = LayoutInflater.from(context);

    }

    @Override
    public CustomViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view;
        view = inflater.inflate(R.layout.single_book, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Book book = books.get(position);
        holder.tvChapterName.setText(book.chapterName);
        Picasso.get().load(book.imageUrl).into(holder.ivChapter);


    }

    @Override
    public int getItemCount() {
        return books.size();
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivChapter;
        public TextView tvChapterName;

        public CustomViewHolder(View itemView) {
            super(itemView);

            tvChapterName = itemView.findViewById(R.id.tvChapterName);
            ivChapter = itemView.findViewById(R.id.ivBook);
        }
    }
}
