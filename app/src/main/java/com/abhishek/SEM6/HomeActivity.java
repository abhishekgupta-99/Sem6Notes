package com.abhishek.SEM6;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.abhishek.SEM6.adapters.SubjectAdapter;
import com.abhishek.SEM6.adapters.SubjectAdapter_db;
import com.abhishek.SEM6.adapters.TabAdapter;
import com.abhishek.SEM6.booksearch.BookClient;
import com.abhishek.SEM6.models.Book;
import com.abhishek.SEM6.models.Book_db;
import com.abhishek.SEM6.models.Subject;
import com.abhishek.SEM6.models.Subject_db;
import com.abhishek.SEM6.rssfeed.RssFeed;
import com.abhishek.SEM6.rssfeed.RssItem;
import com.abhishek.SEM6.rssfeed.RssReader;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.karan.churi.PermissionManager.PermissionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class HomeActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 321;
    private RecyclerView rvSubject,rv_playbooks;
    private SubjectAdapter subjectAdapter;
    Spinner type_user,content_type,subject;
    String chiptype;
    AlertDialog alertDialog;

    Toolbar toolbar;
    ImageView pl;

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private ChipGroup chipGroup;
    private Chip chip1;

    LayoutInflater inflater;
    View v;

    int filter_query=1;
    int all_books_query=0;


    boolean filter_flag=false;
    String filter_query_string="";


    private SwipeRefreshLayout swipeContainer;


    private SubjectAdapter_db subjectAdapter_db,subjectAdapter_db_dialog;
    private ArrayList<Subject> subjects;
    private static FirebaseFirestore db;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 54654;
    List<String> subject_names = new ArrayList<>();

    private FloatingActionButton floatingActionButton;


    ArrayList<Subject_db> subjects_db = new ArrayList<Subject_db>();
    private BookClient client;

    private RequestQueue mRequestQueue;

    private static  final  String BASE_URL="https://www.googleapis.com/books/v1/volumes?q=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setTheme(R.style.AppTheme);


        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        setContentView(R.layout.activity_main);

        TypefaceUtil.overrideFont(getApplicationContext(), "POPPINS", "fonts/poppins_regular.ttf");

        PermissionManager permissionManager = new PermissionManager() {
        };
        permissionManager.checkAndRequestPermissions(this);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
//            return;
//        }
//        DirectoryHelper.createDirectory(this);

        db = FirebaseFirestore.getInstance();

        //firestore_cache();

        //initComponents();

        new RetrieveFeedTask(this).execute();
       // fetchBooks("xa");
        mRequestQueue = Volley.newRequestQueue(this);
       // search("Proakis");


        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new BookFragment(), "Bookstore");
        adapter.addFragment(new AcademicFragment(), "Academics");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


    }




    public static void firestore_cache() {

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);


    }

    /*private void set_recyclerView(ArrayList<Subject_db> subjects_db) {
        subjectAdapter_db = new SubjectAdapter_db(subjects_db, HomeActivity.this,chiptype);
        LinearLayoutManager manager = new LinearLayoutManager(HomeActivity.this);
        rvSubject.setLayoutManager(manager);
        rvSubject.setAdapter(subjectAdapter_db);

        rvSubject.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    floatingActionButton.hide();
                else if (dy < 0)
                    floatingActionButton.show();
            }
        });
    }*/


    private void set_recyclerView_dialogbox (ArrayList<Subject_db> subjects_db, View v) {
        rv_playbooks=v.findViewById(R.id.load_images_googleplay);
        subjectAdapter_db_dialog = new SubjectAdapter_db(subjects_db, HomeActivity.this,chiptype);
        LinearLayoutManager manager = new LinearLayoutManager(HomeActivity.this);
        rv_playbooks.setLayoutManager(manager);
        rv_playbooks.setAdapter(subjectAdapter_db_dialog);

        Log.d("entered","Entered");
//        rvSubject.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                if (dy > 0)
//                    floatingActionButton.hide();
//                else if (dy < 0)
//                    floatingActionButton.show();
//            }
 //       });
    }


    private void getAllBooks(boolean filter_flag,String filter_query_string) {

        final int[] count = {0};
        Query query = null;
        //

        for(String each_subject_from_db: subject_names)
        {
            final Subject_db subject=new Subject_db();
            subject.subjectName=each_subject_from_db;
            subject.books=new ArrayList<Book_db>();
            //Toast.makeText(this, each_subject_from_db, Toast.LENGTH_SHORT).show();
            Log.d("subject_curr",each_subject_from_db);

            if(filter_flag)
            {
                 query = db.collection(each_subject_from_db).whereEqualTo("content_type", filter_query_string.replace("_"," "));
            }
            else if (filter_flag == false)
            {
                query = db.collection(each_subject_from_db);
                //
            }

            // Create a query against the collection.


            //db.collection(each_subject_from_db)
                   // .whereEqualTo("content_type", "pdf")
                   query
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                count[0] += 1;

                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    Book_db book = document.toObject(Book_db.class);
                                    subject.books.add(book);
                                    //Toast.makeText(HomeActivity.this, book.name+" hh", Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(HomeActivity.this, book.uploader+" hh", Toast.LENGTH_SHORT).show();
                                //    Log.d("Books",  " => " + book.name);

                                    //Log.d("UnReached adapter",  " => " );


                                }
                                //    Log.d("All Books",  " => " + subject.books.get(0).getName());
                                subjects_db.add(subject);


                            } else {
                                Log.d("Book", "Error getting documents: ", task.getException());
                            }
                        }
                    }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    if(count[0] ==subject_names.size())
                    {
                        //set_recyclerView(subjects_db);

                    }



                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(HomeActivity.this, "Failed to retrieve Books", Toast.LENGTH_SHORT).show();

                }
            });

        }

        Log.d("Reached adapter",  " => " );



    }

    /*private void getAllSubject(final boolean filter_flag, final String filter_query_string) {
        db.collection("Subjects").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        subject_names.add(document.getId());
                    }
                  //  Log.d("subjects", subject_names.toString());
                    getAllBooks(filter_flag,filter_query_string);
                   // set_recyclerView(subjects_db);

                } else {
                  //  Log.d("subjects", "Error getting documents: ", task.getException());
                }
            }
        });

    }*/

    public void add_dialog(final GoogleSignInAccount account) {


        // googlesignin();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AppTheme_FullScreenDialog);
        // Get the layout inflater

        //  builder.setCancelable(false);

        //v = View.inflate(this,R.layout.add_book_dialog,null);
        inflater = getLayoutInflater();
        v=inflater.inflate(R.layout.add_book_dialog, null);

        toolbar = v.findViewById(R.id.toolbar);
        toolbar.setTitle("Add new");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.inflateMenu(R.menu.dialog_menu);
        toolbar.setNavigationOnClickListener(new Toolbar.OnClickListener()
        {

            @Override
            public void onClick(View view) {
                Toast.makeText(HomeActivity.this, "Dialog box closed", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(HomeActivity.this, "Save clicked", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
                return true;
            }
        });


        initialize_spinners(v);
        TextView signedin=v.findViewById(R.id.signedIn);
        signedin.setText(account.getDisplayName());

        //  builder.getContext().setTheme(R.style.AppTheme);
        final AlertDialog  alert= builder.create();
        //alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        builder.setView(v);
                // Add action buttons
               /* .setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        EditText title=v.findViewById(R.id.title);
                       // EditText url=v.findViewById(R.id.ref_url);

//                        if(!(title.getText().toString().isEmpty()) || !(url.getText().toString().isEmpty())) {
//
//                            if (URLUtil.isValidUrl(url.getText().toString())) {
//                                upload_to_firestore(account, title.getText().toString(), url.getText().toString());
//                            } else {
//                                Toast.makeText(HomeActivity.this, "Please enter a valid url ", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                        else
//                        {
//                            Toast.makeText(HomeActivity.this, "Fields can't be empty", Toast.LENGTH_SHORT).show();
//                        }






//                        password_Et= v.findViewById(R.id.password_edit);
//
//                        // Log.d("correct_password_edit", passowrd.toString());
//                        password_entered = password_Et.getText().toString();
//                        showToast("Entered Password: "+password_entered);
//                        Log.d("correct_password",password_entered);
//                        String original= "1234";
//
//                        if (password_entered.equals("bmc_car"))
//                        {
//                            // Log.d("correct_password","heyyy");
//                            dialog.cancel();
//                            check_current_user();
//
//
//                        }
//                        else {
//                            //Log.d(user_text,"string is empty");
//                            String message = "The password you have entered is incorrect." + " \n \n" + "Please try again!";
//                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
//                            builder.setTitle("Error");
//                            builder.setMessage(message);
//                            builder.setCancelable(false);
//                            // builder.setPositiveButton("Cancel", null);
//                            builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int id) {
//                                    dialog();
//                                }
//                            });
//                            builder.create().show();
//
//                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });*/

        alertDialog = builder.create();

        // show it
        alertDialog.show();
    }

    private void upload_to_firestore(GoogleSignInAccount account, String title, String url) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> book_details = new HashMap<>();
        book_details.put("uploader", account.getDisplayName());
        book_details.put("name",title);
        book_details.put("url",url);
        book_details.put("content_type",String.valueOf(content_type.getSelectedItem()));
        book_details.put("user_type",String.valueOf(type_user.getSelectedItem()));


        String subject_db= String.valueOf(subject.getSelectedItem());
        DocumentReference document = db.collection(subject_db).document(title);

        document.set(book_details)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Book Uploaded to database", Toast.LENGTH_SHORT).show();


                        //Toast.makeText(ctx, "The car is "+lab+" , with a confidence of "+  conf, Toast.LENGTH_LONG).show();
                        // get_LatLong();

                        //  Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log.w(TAG, "Error writing document", e);

                        Toast.makeText(HomeActivity.this, "Failed Writing to database", Toast.LENGTH_SHORT).show();

                    }
                });

    }

    public void googlesignin(View view) {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 321);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);

            Toast.makeText(this, "Successful Sign In", Toast.LENGTH_SHORT).show();

            add_dialog(account);

            //updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Toast.makeText(this, "Failed Sign In", Toast.LENGTH_SHORT).show();
            //Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void initialize_spinners(View v) {

      //  type_user = v.findViewById(R.id.role);
//create a list of items for the spinner.
      //  String[] items = new String[]{"Student","Teacher"};
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
       // ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
//set the spinners adapter to the previously created one.
       // type_user.setAdapter(adapter);



        //content_type = v.findViewById(R.id.content_type);
//create a list of items for the spinner.
       // String[] type = new String[]{"Book Pdf","Ppt","Youtube Url","Notes","Papers"};
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
       // ArrayAdapter<String> adap = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, type);
//set the spinners adapter to the previously created one.
       // content_type.setAdapter(adap);


      //  subject = v.findViewById(R.id.subject);
//create a list of items for the spinner.
        //    String[] type = new String[]{"Book Pdf","Ppt","Youtube Url"};
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        //ArrayAdapter<String> sub_adap = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, subject_names);
//set the spinners adapter to the previously created one.
        //subject.setAdapter(sub_adap);

        String[] year = new String[] {"FE", "SE", "TE", "BE"};

        ArrayAdapter<String> adapter_year =
                new ArrayAdapter<>(
                        this,
                        R.layout.dropdown_menu_popup_item,
                        year);

        ArrayAdapter<String> adapter_sub =
                new ArrayAdapter<>(
                        this,
                        R.layout.dropdown_menu_popup_item,
                        subject_names);

        AutoCompleteTextView editTextFilledExposedDropdown1 =
                v.findViewById(R.id.filled_exposed_dropdown_year);
        editTextFilledExposedDropdown1.setAdapter(adapter_year);

        AutoCompleteTextView editTextFilledExposedDropdown2 =
                v.findViewById(R.id.filled_exposed_dropdown_sub);
        editTextFilledExposedDropdown2.setAdapter(adapter_sub);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                DirectoryHelper.createDirectory(this);
        }
    }


    // Executes an API call to the OpenLibrary search endpoint, parses the results
    // Converts them into an array of book objects and adds them to the adapter
    private void fetchBooks(String query) {
        query="Andrew sloss ";
        client = new BookClient();
        client.getBooks(query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray docs;
                    if(response != null) {
                        // Get the docs json array
                        docs = response.getJSONArray("docs");
                        // Parse json array into array of model objects
                        final ArrayList<com.abhishek.SEM6.booksearch.Book> books = com.abhishek.SEM6.booksearch.Book.fromJson(docs);
                        // Remove all books from the adapter
                        //bookAdapter.clear();
                        // Load model objects into the adapter
                        for (com.abhishek.SEM6.booksearch.Book book : books) {
                            Log.d("BOOKs url openlibrary ",book.getCoverUrl());
                           // bookAdapter.add(book); // add book through the adapter
                        }
                        //bookAdapter.notifyDataSetChanged();
                        //hideProgress();
                    }
                } catch (JSONException e) {
                    // Invalid JSON format, show appropriate error.
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }


    private void parseJson(String key) {

        ArrayList<Subject_db> fetched_books_playbooks = new ArrayList<Subject_db>();
        fetched_books_playbooks.clear();
        final Subject_db fetched_books = new Subject_db();

        fetched_books.id = 1;
        fetched_books.subjectName = "Select A Thumbnail for the required book and author";
        fetched_books.books = new ArrayList<Book_db>();


        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, key.toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String title ="";
                        String author ="";
//                        String publishedDate = "NoT Available";
//                        String description = "No Description";
//                        int pageCount = 1000;
//                        String categories = "No categories Available ";
//                        String buy ="";

  //                      String price = "NOT_FOR_SALE";
                        try {
                            JSONArray items = response.getJSONArray("items");

                            for (int i = 0 ; i< items.length() ;i++){
                                Book_db book1 = new Book_db();
                                JSONObject item = items.getJSONObject(i);
                                JSONObject volumeInfo = item.getJSONObject("volumeInfo");



                                try{
                                    title = volumeInfo.getString("title");
                                    book1.name=title;


                                    JSONArray authors = volumeInfo.getJSONArray("authors");
                                    if(authors.length() == 1){
                                        author = authors.getString(0);
                                        book1.uploader=author;
                                    }else {
                                        author = authors.getString(0) + "|" +authors.getString(1);
                                        book1.uploader=author;
                                    }


//                                    publishedDate = volumeInfo.getString("publishedDate");
//                                    pageCount = volumeInfo.getInt("pageCount");
//


                          //          JSONObject saleInfo = item.getJSONObject("saleInfo");
                            //        JSONObject listPrice = saleInfo.getJSONObject("listPrice");
//                                    price = listPrice.getString("amount") + " " +listPrice.getString("currencyCode");
//                                    description = volumeInfo.getString("description");
//                                    buy = saleInfo.getString("buyLink");
//                                    categories = volumeInfo.getJSONArray("categories").getString(0);

                                }catch (Exception e){

                                }
                                String thumbnail = volumeInfo.getJSONObject("imageLinks").getString("thumbnail");
                                book1.thumbnail=thumbnail;
                                Log.d("Thumbnail ",thumbnail);

//                                String previewLink = volumeInfo.getString("previewLink");
//                                String url = volumeInfo.getString("infoLink");


//                                mBooks.add(new Book(title , author , publishedDate , description ,categories
//                                        ,thumbnail,buy,previewLink,price,pageCount,url));
//
//
//                                mAdapter = new RecyclerViewAdapter(MainActivity.this , mBooks);
//                                mRecyclerView.setAdapter(mAdapter);
                                fetched_books.books.add(book1);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("TAG" , e.toString());

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mRequestQueue.add(request);

        fetched_books_playbooks.add(fetched_books);
        set_recyclerView_dialogbox(fetched_books_playbooks,v);
    }


    private boolean Read_network_state(Context context)
    {    boolean is_connected;
        ConnectivityManager cm =(ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        is_connected=info!=null&&info.isConnectedOrConnecting();
        return is_connected;
    }

    private void search(String search_query)
    {

        if(search_query.equals(""))
        {
            Toast.makeText(this,"Please enter your query",Toast.LENGTH_SHORT).show();
            return;
        }
        String final_query=search_query.replace(" ","+");
        Uri uri=Uri.parse(BASE_URL+final_query);
        Uri.Builder buider = uri.buildUpon();
        parseJson(buider.toString());
    }

    public void search(View view) {
        EditText search_title = v.findViewById(R.id.title);
        search(search_title.getText().toString());

    }
}