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
import android.widget.Toast;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        check_content_type(book,holder);


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

    private void check_content_type(Book_db book, CustomViewHolder holder) {



        if(String.valueOf(book.content_type).equals("Youtube Url"))
        {

            Toast.makeText(context, "Youtube "+ book.content_type, Toast.LENGTH_SHORT).show();
            String ID=extractVideoIdFromUrl(book.url);

            String thumbnail_url = "https://img.youtube.com/vi/"+ID+"/0.jpg";   //maxresdefault.jpg for 720p image

            Picasso.get().load(thumbnail_url).into(holder.ivChapter);


        }
    }

    public String extractVideoIdFromUrl(String url) {


        final String[] videoIdRegex = { "\\?vi?=([^&]*)","watch\\?.*v=([^&]*)", "(?:embed|vi?)/([^/?]*)", "^([A-Za-z0-9\\-]*)"};

        String youTubeLinkWithoutProtocolAndDomain = youTubeLinkWithoutProtocolAndDomain(url);

        for(String regex : videoIdRegex) {
            Pattern compiledPattern = Pattern.compile(regex);
            Matcher matcher = compiledPattern.matcher(youTubeLinkWithoutProtocolAndDomain);

            if(matcher.find()){

                //
                return matcher.group(1);


            }
        }

        return null;
    }

    private String youTubeLinkWithoutProtocolAndDomain(String url) {

        final String youTubeUrlRegEx = "^(https?)?(://)?(www.)?(m.)?((youtube.com)|(youtu.be))/";
        Pattern compiledPattern = Pattern.compile(youTubeUrlRegEx);
        Matcher matcher = compiledPattern.matcher(url);

        if(matcher.find()){
            return url.replace(matcher.group(), "");
        }
        return url;
    }


//    public void file_download(String url, String title) {
//        File direct = new File(Environment.getExternalStorageDirectory()
//                + "/DownloadManager");
//
//        if (!direct.exists()) {
//            direct.mkdirs();
//        }
//
//        Uri uri = Uri.parse("https://cloudup.com/files/inYVmLryD4p/download");
//        DownloadManager.Request request = new DownloadManager.Request(uri);
//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);  // Tell on which network you want to download file.
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);  // This will show notification on top when downloading the file.
//        request.setTitle("Downloading a file"); // Title for notification.
//        request.setVisibleInDownloadsUi(true);
//        request.setDestinationInExternalPublicDir("/DownloadManager/", title+".mp3");  // Storage directory path
//        ((DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request); // This will start downloading
//
//    }

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
