package com.abhishek.SEM6.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abhishek.SEM6.R;
import com.abhishek.SEM6.models.Book_db;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class WebAdapter extends RecyclerView.Adapter<WebAdapter.ViewHolder> {


    String [] names,links,pubs,contents;
    FragmentManager fragmentManager;

    private Context context;
    private ArrayList<Book_db> books;
    private LayoutInflater inflater;
    private String subjectname;

    class ViewHolder extends RecyclerView.ViewHolder implements  View.OnLongClickListener{

        public ImageView web_image;
        public TextView webtext;
        public CardView web_card;
        public TextView uploader;
        ItemLongClickListener itemLongClickListener;

        ViewHolder(View view)
        {
            super(view);
            web_card = view.findViewById(R.id.web_card_view);
            webtext = view.findViewById(R.id.web_text);
            uploader = view.findViewById(R.id.pub_date);
            web_image = view.findViewById(R.id.web_image);
            itemView.setOnLongClickListener(this);
            //Log.d("myactivitysee",activity.toString()+"...");
            //Log.d("myactivitysee2","hehe "+activity.toString());
        }

        @Override
        public boolean onLongClick(View view) {

            this.itemLongClickListener.onItemLongClicked(view,getLayoutPosition());
            return false;
        }

        public void setItemLongClickListener(ItemLongClickListener ic)
        {
            this.itemLongClickListener=ic;
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

    private void delete_alert_dialog(final String book_name) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // builder.setMessage("Are you sure you want to delete "+book_name) .setTitle("Delete this book ?");

        //Setting message manually and performing action on button click
        builder.setMessage("Are you sure you want to delete "+book_name) .setTitle("Delete this Announcement ?")
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
        db.collection("Class").document(bookname)
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


    @Override
    public void onBindViewHolder(@NonNull WebAdapter.ViewHolder viewHolder, final int i) {

        final Book_db book = books.get(i);
        viewHolder.webtext.setText(book.name);
        viewHolder.uploader.setText(book.uploader);

        if(!book.thumbnail.isEmpty() || !book.thumbnail.equals("null")) {
            // Picasso.get().load(book.thumbnail.replace("http","https")).into(holder.ivChapter);

            Glide.with(context).load(book.thumbnail).into(viewHolder.web_image);
        }


        viewHolder.setItemLongClickListener(new ItemLongClickListener() {
            @Override
            public void onItemLongClicked(View v, int pos) {

                delete_alert_dialog(books.get(pos).getName());
            }
        });

        viewHolder.web_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(books.get(i).url)));

            }
        });

    }




    @Override
    public int getItemCount() {
        return books.size();
    }
}
