package com.abhishek.SEM6.adapters;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.abhishek.SEM6.DirectoryHelper;
import com.abhishek.SEM6.DownloadService;
import com.abhishek.SEM6.R;
import com.abhishek.SEM6.models.Book;
import com.abhishek.SEM6.models.Book_db;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.bumptech.glide.Glide;

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
    private String subjectname;

    AlertDialog.Builder builder;

    public BookAdapter_db(Context context, ArrayList<Book_db> books, String subjectName) {
        this.context = context;
        this.books = books;
        this.inflater = LayoutInflater.from(context);
        this.subjectname=subjectName;

    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = inflater.inflate(R.layout.single_book, parent, false);
        return new CustomViewHolder(view);
    }

    @Override

    public void onBindViewHolder (final CustomViewHolder holder, final int position) {

       // Toast.makeText(context, "BINDVIEWHOLDER", Toast.LENGTH_SHORT).show();
        Log.d("BINDVIEWHOLDER","BINDVIEWHOLDER");
        final Book_db book = books.get(position);
        holder.tvChapterName.setText(book.name);
        holder.uploader.setText(book.uploader);


      if(!book.thumbnail.isEmpty()) {
         // Picasso.get().load(book.thumbnail.replace("http","https")).into(holder.ivChapter);

          Glide.with(context).load(book.thumbnail).into(holder.ivChapter);
      }
        check_content_type(book,holder);
        holder.setItemLongClickListener(new ItemLongClickListener() {
            @Override
            public void onItemLongClicked(View v, int pos) {
               // Toast.makeText(context,books.get(pos).getName(),Toast.LENGTH_SHORT).show();

                delete_alert_dialog(books.get(pos).getName());
            }
        });


       holder.download_button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(books.get(position).url)));

             //  file_download(books.get(position).url,book.name);

                  /// holder.uploader.setText("Dhruv Khandelwal");

            //   context.startService(DownloadService.getDownloadService(context, books.get(position).url, DirectoryHelper.ROOT_DIRECTORY_NAME.concat("/")));




           }
       });

    }

    private void delete_alert_dialog(final String book_name) {

        builder = new AlertDialog.Builder(context);
       // builder.setMessage("Are you sure you want to delete "+book_name) .setTitle("Delete this book ?");

        //Setting message manually and performing action on button click
        builder.setMessage("Are you sure you want to delete "+book_name) .setTitle("Delete this book ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //finish();

                        enterpassword(book_name);
//                        Toast.makeText(context,"you choose yes action for alertbox",
//                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
//                        Toast.makeText(context,"you choose no action for alertbox",
//                                Toast.LENGTH_SHORT).show();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.show();
}

    public void  enterpassword(final String bookname)
    {

        LayoutInflater inflater;
        final View v;
        final AlertDialog.Builder builder = new AlertDialog.Builder(context,android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        // Get the layout inflater

        builder.setCancelable(false);

         v = LayoutInflater.from(context).inflate(R.layout.password_dialog, null);

        //inflater = context.getLayoutInflater();
       // v=inflater.inflate(R.layout.password_dialog, null);


        //  builder.getContext().setTheme(R.style.AppTheme);
        final AlertDialog  alert= builder.create();
        //alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        builder.setView(v)
                // Add action buttons
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                       EditText password_Et= v.findViewById(R.id.password_edit);

                        // Log.d("correct_password_edit", passowrd.toString());
                      String  password_entered = password_Et.getText().toString();
                        //showToast("Entered Password: "+password_entered);
                        Log.d("correct_password",password_entered);
                        String original= "1234";

                        if (password_entered.equals("bookextc"))
                        {
                            // Log.d("correct_password","heyyy");

                            delete_from_firestore(bookname);
                            dialog.cancel();
                            //check_current_user();


                        }
                        else {
                            //Log.d(user_text,"string is empty");
                            String message = "The password you have entered is incorrect." + " \n \n" + "Please try again!";
                            AlertDialog.Builder builder = new AlertDialog.Builder(context,android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                            builder.setTitle("Error");
                            builder.setMessage(message);
                            builder.setCancelable(false);
                            // builder.setPositiveButton("Cancel", null);
                            builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    enterpassword( bookname);
                                }
                            });
                            builder.create().show();

                        }
                    }
                });

        AlertDialog alertDialog = builder.create();

        // show it
        alertDialog.show();
    }

    private void delete_from_firestore(String bookname) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(subjectname).document(bookname)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                      //  Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        Toast.makeText(context, "Book Successfully Deleted. Please Refresh.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                      //  Log.w(TAG, "Error deleting document", e);

                        Toast.makeText(context, "Could not delete. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void check_content_type(Book_db book, CustomViewHolder holder) {

        if(String.valueOf(book.content_type).equals("Youtube Url"))
        {

          //  Toast.makeText(context, "Youtube "+ book.content_type, Toast.LENGTH_SHORT).show();
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



    public class CustomViewHolder extends RecyclerView.ViewHolder implements  View.OnLongClickListener{

        public ImageView ivChapter;
        public TextView tvChapterName;
        public CardView download_button;
        public TextView uploader;
        ItemLongClickListener itemLongClickListener;

        public CustomViewHolder(View itemView) {
            super(itemView);

            tvChapterName = (TextView) itemView.findViewById(R.id.tvChapterName);
            ivChapter = (ImageView) itemView.findViewById(R.id.ivBook);
            download_button=(CardView) itemView.findViewById(R.id.download_card);
            uploader=itemView.findViewById(R.id.uploader);

            itemView.setOnLongClickListener(this);

        }

        public void setItemLongClickListener(ItemLongClickListener ic)
        {
            this.itemLongClickListener=ic;
        }


        @Override
        public boolean onLongClick(View view) {

            this.itemLongClickListener.onItemLongClicked(view,getLayoutPosition());

            Log.d("Long Clicked", "Item long-clicked at position " + getLayoutPosition());
        //    Toast.makeText(context, "Item long-clicked at position " + getLayoutPosition(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
