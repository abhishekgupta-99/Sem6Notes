package com.abhishek.SEM6;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.abhishek.SEM6.adapters.SubjectAdapter;
import com.abhishek.SEM6.adapters.SubjectAdapter_db;
import com.abhishek.SEM6.models.Book;
import com.abhishek.SEM6.models.Book_db;
import com.abhishek.SEM6.models.Subject;
import com.abhishek.SEM6.models.Subject_db;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.karan.churi.PermissionManager.PermissionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 321;
    private RecyclerView rvSubject;
    private SubjectAdapter subjectAdapter;
    Spinner type_user,content_type,subject;
    String chiptype;

    private ChipGroup chipGroup;
    private Chip chip1;

    LayoutInflater inflater;
    View v;

    int filter_query=1;
            int all_books_query=0;


    boolean filter_flag=false;
    String filter_query_string="";


    private SwipeRefreshLayout swipeContainer;


    private SubjectAdapter_db subjectAdapter_db;
    private ArrayList<Subject> subjects;
    private FirebaseFirestore db;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 54654;
    List<String> subject_names = new ArrayList<>();

    private FloatingActionButton floatingActionButton;


    ArrayList<Subject_db> subjects_db = new ArrayList<Subject_db>();
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

        PermissionManager permissionManager = new PermissionManager() {
        };
        permissionManager.checkAndRequestPermissions(this);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
//            return;
//        }
//        DirectoryHelper.createDirectory(this);

        db = FirebaseFirestore.getInstance();

        firestore_cache();

        initComponents();

        subjects = prepareData();

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                subject_names.clear();
                subjectAdapter_db.clear();
                getAllSubject(filter_flag,filter_query_string);
                swipeContainer.setRefreshing(false);

            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        chipGroup = findViewById(R.id.chip_group);


        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                chiptype = group.findViewById(checkedId).toString();
                chiptype = chiptype.substring(chiptype.indexOf('/')+1,chiptype.indexOf('}'));
                //Log.d("chip",chiptype);
                filter_flag=true;
                if(!chiptype.isEmpty())
                {
                    filter_query_string = chiptype;
                }
                Toast.makeText(HomeActivity.this, chiptype, Toast.LENGTH_SHORT).show();
                subject_names.clear();
                subjectAdapter_db.clear();
                getAllSubject(filter_flag,filter_query_string);


            }
        });

        //  set_recyclerView();
        //  set_recyclerView();

//        subjectAdapter = new SubjectAdapter(subjects, HomeActivity.this);
//        LinearLayoutManager manager = new LinearLayoutManager(HomeActivity.this);
//        rvSubject.setLayoutManager(manager);
//        rvSubject.setAdapter(subjectAdapter);

    }

    private void firestore_cache() {

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

    }

    private void set_recyclerView(ArrayList<Subject_db> subjects_db) {
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
    }

    private void initComponents() {
        rvSubject = findViewById(R.id.rvSubject);
        floatingActionButton = findViewById(R.id.imgFour);
    }

    private ArrayList<Subject> prepareData() {


        getAllSubject(filter_flag,filter_query_string);
       // getAllBooks();

        ArrayList<Subject> subjects = new ArrayList<Subject>();

        Subject physics = new Subject();
        physics.id = 1;
        physics.subjectName = "Physics";
        physics.books = new ArrayList<Book>();

        Book book1 = new Book();
        book1.id = 1;
        book1.chapterName = "Atomic power";
        book1.imageUrl = "http://ashishkudale.com/images/phy/atoms.png";

        Book book2 = new Book();
        book2.id = 2;
        book2.chapterName = "Theory of relativity";
        book2.imageUrl = "http://ashishkudale.com/images/phy/sigma.png";

        Book book3 = new Book();
        book3.id = 3;
        book3.chapterName = "Magnetic field";
        book3.imageUrl = "http://ashishkudale.com/images/phy/magnet.png";

        Book book4 = new Book();
        book4.id = 4;
        book4.chapterName = "Practical 1";
        book4.imageUrl = "http://ashishkudale.com/images/phy/caliper.png";

        Book book5 = new Book();
        book5.id = 5;
        book5.chapterName = "Practical 2";
        book5.imageUrl = "http://ashishkudale.com/images/phy/micrometer.png";

        physics.books.add(book1);
        physics.books.add(book2);
        physics.books.add(book3);
        physics.books.add(book4);
        physics.books.add(book5);

        Subject chem = new Subject();
        chem.id = 2;
        chem.subjectName = "Chemistry";
        chem.books = new ArrayList<Book>();

        Book book6 = new Book();
        book6.id = 6;
        book6.chapterName = "Chemical bonds";
        book6.imageUrl = "http://ashishkudale.com/images/chem/bonds.png";

        Book book7 = new Book();
        book7.id = 7;
        book7.chapterName = "Sodium";
        book7.imageUrl = "http://ashishkudale.com/images/chem/sodium.png";

        Book book8 = new Book();
        book8.id = 8;
        book8.chapterName = "Periodic table";
        book8.imageUrl = "http://ashishkudale.com/images/chem/periodic_table.png";

        Book book9 = new Book();
        book9.id = 9;
        book9.chapterName = "Acid and Base";
        book9.imageUrl = "http://ashishkudale.com/images/chem/chem.png";

        chem.books.add(book6);
        chem.books.add(book7);
        chem.books.add(book8);
        chem.books.add(book9);

        Subject bio = new Subject();
        bio.id = 3;
        bio.subjectName = "Biology";
        bio.books = new ArrayList<Book>();

        Book book10 = new Book();
        book10.id = 10;
        book10.chapterName = "Bacteria";
        book10.imageUrl = "http://ashishkudale.com/images/bio/bacteria.png";

        Book book11 = new Book();
        book11.id = 11;
        book11.chapterName = "DNA and RNA";
        book11.imageUrl = "http://ashishkudale.com/images/bio/dna.png";

        Book book12 = new Book();
        book12.id = 12;
        book12.chapterName = "Study of microscope";
        book12.imageUrl = "http://ashishkudale.com/images/bio/microscope.png";

        Book book13 = new Book();
        book13.id = 13;
        book13.chapterName = "Protein and fibers";
        book13.imageUrl = "http://ashishkudale.com/images/bio/protein.png";

        bio.books.add(book10);
        bio.books.add(book11);
        bio.books.add(book12);
        bio.books.add(book13);

        Subject maths = new Subject();
        maths.id = 4;
        maths.subjectName = "Maths";
        maths.books = new ArrayList<Book>();

        Book book14 = new Book();
        book14.id = 14;
        book14.chapterName = "Circle";
        book14.imageUrl = "http://ashishkudale.com/images/maths/circle.png";

        Book book15 = new Book();
        book15.id = 15;
        book15.chapterName = "Geometry";
        book15.imageUrl = "http://ashishkudale.com/images/maths/geometry.png";

        Book book16 = new Book();
        book16.id = 16;
        book16.chapterName = "Linear equations";
        book16.imageUrl = "http://ashishkudale.com/images/maths/linear.png";

        Book book17 = new Book();
        book17.id = 17;
        book17.chapterName = "Graph";
        book17.imageUrl = "http://ashishkudale.com/images/maths/plot.png";

        Book book18 = new Book();
        book18.id = 18;
        book18.chapterName = "Trigonometry";
        book18.imageUrl = "http://ashishkudale.com/images/maths/trigonometry.png";

        maths.books.add(book14);
        maths.books.add(book18);
        maths.books.add(book15);
        maths.books.add(book16);
        maths.books.add(book17);

        subjects.add(physics);
        subjects.add(chem);
        subjects.add(maths);
        subjects.add(bio);

        return subjects;
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
                                    Log.d("Books",  " => " + book.name);

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
                        set_recyclerView(subjects_db);

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

    private void getAllSubject(final boolean filter_flag, final String filter_query_string) {
        db.collection("Subjects").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        subject_names.add(document.getId());
                    }
                    Log.d("subjects", subject_names.toString());
                    getAllBooks(filter_flag,filter_query_string);
                   // set_recyclerView(subjects_db);

                } else {
                    Log.d("subjects", "Error getting documents: ", task.getException());
                }
            }
        });

    }

    public void add_dialog(final GoogleSignInAccount account) {


       // googlesignin();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this,android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        // Get the layout inflater

      //  builder.setCancelable(false);


        inflater = getLayoutInflater();
        v=inflater.inflate(R.layout.add_book_dialog, null);


        initialize_spinners(v);
        TextView signedin=v.findViewById(R.id.signedIn);
        signedin.setText("Signed In As : "+account.getDisplayName());


        //  builder.getContext().setTheme(R.style.AppTheme);
        final AlertDialog  alert= builder.create();
        //alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        builder.setView(v)
                // Add action buttons
                .setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                         EditText title=v.findViewById(R.id.title);
                         EditText url=v.findViewById(R.id.ref_url);

                         if(!(title.getText().toString().isEmpty()) || !(url.getText().toString().isEmpty())) {

                             if (URLUtil.isValidUrl(url.getText().toString())) {
                                 upload_to_firestore(account, title.getText().toString(), url.getText().toString());
                             } else {
                                 Toast.makeText(HomeActivity.this, "Please enter a valid url ", Toast.LENGTH_SHORT).show();
                             }
                         }
                         else
                         {
                             Toast.makeText(HomeActivity.this, "Fields can't be empty", Toast.LENGTH_SHORT).show();
                         }






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
        });

        AlertDialog alertDialog = builder.create();

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

         type_user = v.findViewById(R.id.role);
//create a list of items for the spinner.
        String[] items = new String[]{"Student","Teacher"};
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
//set the spinners adapter to the previously created one.
        type_user.setAdapter(adapter);



         content_type = v.findViewById(R.id.content_type);
//create a list of items for the spinner.
        String[] type = new String[]{"Book Pdf","Ppt","Youtube Url","Notes","Papers"};
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adap = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, type);
//set the spinners adapter to the previously created one.
        content_type.setAdapter(adap);


         subject = v.findViewById(R.id.subject);
//create a list of items for the spinner.
    //    String[] type = new String[]{"Book Pdf","Ppt","Youtube Url"};
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> sub_adap = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, subject_names);
//set the spinners adapter to the previously created one.
        subject.setAdapter(sub_adap);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                DirectoryHelper.createDirectory(this);
        }
    }
}
