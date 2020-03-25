package com.abhishek.SEM6.adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.abhishek.SEM6.DirectoryHelper;
import com.abhishek.SEM6.DownloadService;
import com.abhishek.SEM6.R;
import com.abhishek.SEM6.models.Book;
import com.abhishek.SEM6.models.Book_db;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * * Created by abhishek on 3/2020.
 */

public class BookAdapter_db extends RecyclerView.Adapter<BookAdapter_db.CustomViewHolder> {

    private Context context;
    private ArrayList<Book_db> books;
    private LayoutInflater inflater;

    public BookAdapter_db(Context context, ArrayList<Book_db> books) {
        this.context = context;
        this.books = books;
        this.inflater = LayoutInflater.from(context);

    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = inflater.inflate(R.layout.single_book, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int position) {
        final Book_db book = books.get(position);
        holder.tvChapterName.setText(book.name);
        holder.uploader.setText(book.uploader);
       holder.download_button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(books.get(position).url)));

             //  file_download(books.get(position).url,book.name);

                  /// holder.uploader.setText("Dhruv Khandelwal");

            //   context.startService(DownloadService.getDownloadService(context, books.get(position).url, DirectoryHelper.ROOT_DIRECTORY_NAME.concat("/")));




           }
       });
       // Picasso.get().load(book.url).into(holder.ivChapter);
    }


    public void file_download(String url, String title) {
        File direct = new File(Environment.getExternalStorageDirectory()
                + "/DownloadManager");

        if (!direct.exists()) {
            direct.mkdirs();
        }

     //   DownloadManager mgr = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

//        Uri downloadUri = Uri.parse(url);
//        DownloadManager.Request request = new DownloadManager.Request(
//                downloadUri);
//
//        request.setAllowedNetworkTypes(
//                DownloadManager.Request.NETWORK_WIFI
//                        | DownloadManager.Request.NETWORK_MOBILE)
//                .setAllowedOverRoaming(false).setTitle("Demo")
//                .setDescription("Something useful. No, really.")
//                .setDestinationInExternalPublicDir("/book_downloads", title);
//
//        mgr.enqueue(request);

        Uri uri = Uri.parse("https://cloudup.com/files/inYVmLryD4p/download");
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);  // Tell on which network you want to download file.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);  // This will show notification on top when downloading the file.
        request.setTitle("Downloading a file"); // Title for notification.
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir("/DownloadManager/", title+".mp3");  // Storage directory path
        ((DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request); // This will start downloading

    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void clear() {
        books.clear();
        notifyDataSetChanged();
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivChapter;
        public TextView tvChapterName;
        public CardView download_button;
        public TextView uploader;

        public CustomViewHolder(View itemView) {
            super(itemView);

            tvChapterName = (TextView) itemView.findViewById(R.id.tvChapterName);
            ivChapter = (ImageView) itemView.findViewById(R.id.ivBook);
            download_button=(CardView) itemView.findViewById(R.id.download_card);
            uploader=itemView.findViewById(R.id.uploader);
        }
    }
}
