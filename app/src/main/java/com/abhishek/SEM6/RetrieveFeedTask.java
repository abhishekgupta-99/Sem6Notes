package com.abhishek.SEM6;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.abhishek.SEM6.rssfeed.RssFeed;
import com.abhishek.SEM6.rssfeed.RssItem;
import com.abhishek.SEM6.rssfeed.RssReader;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RetrieveFeedTask extends AsyncTask<String, Void, RssFeed> {


    Context ctx;
    String[] category_selected={"Time Table","Results"};
    String[] branches_neglected={"M.Tech","MCA"};

    public RetrieveFeedTask(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected RssFeed doInBackground(String... strings) {

        try {
            rss_feed();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void rss_feed() throws IOException, SAXException {

        URL url = new URL("https://www.spit.ac.in/feed/");
        RssFeed feed = RssReader.read(url);

        ArrayList<RssItem> rssItems = feed.getRssItems();
        for(RssItem rssItem : rssItems) {
            if(Arrays.asList(category_selected).contains(rssItem.getCategory()) && !rssItem.getTitle().contains("M.Tech")){
                // is present ... :)
                Log.d("RSS Reader", rssItem.getLink());

               upload_to_firestore(rssItem.getTitle(),rssItem.getLink());
            }
        }
    }


    private void upload_to_firestore( String title, String url) {


            title = title.replaceAll("[^a-zA-Z]", " ");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> book_details = new HashMap<>();
        book_details.put("uploader", "SPIT Website");
        book_details.put("name",title);
        book_details.put("url",url);
        book_details.put("content_type","Book Pdf");
        book_details.put("user_type","Website");


        String subject_db= "Academics";
        DocumentReference document = db.collection("Academic Announcements").document(title);

        document.set(book_details)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ctx, "Book Uploaded to database", Toast.LENGTH_SHORT).show();



                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log.w(TAG, "Error writing document", e);

                        Toast.makeText(ctx, "Failed Writing to database", Toast.LENGTH_SHORT).show();

                    }
                });

    }


}
