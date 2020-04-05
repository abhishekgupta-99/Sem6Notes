package com.abhishek.SEM6;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.abhishek.SEM6.adapters.WebAdapter;
import com.abhishek.SEM6.models.Book;
import com.abhishek.SEM6.models.Book_db;
import com.abhishek.SEM6.models.Subject;
import com.abhishek.SEM6.models.Subject_db;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;


public class AcademicFragment extends Fragment {
    private SwipeRefreshLayout swipeContainer;
    List<String> subject_names = new ArrayList<>();
    //private SubjectAdapter_db subjectAdapter_db,subjectAdapter_db_dialog;
    private WebAdapter webAdapter;
    private ArrayList<Subject> subjects;
    private ChipGroup chipGroup;
    String chiptype;
    boolean filter_flag=false;
    String filter_query_string="";
    private FirebaseFirestore db;
    ArrayList<Subject_db> subjects_db = new ArrayList<Subject_db>();
    private RecyclerView rvSubject,rv_playbooks;
    int i = 0;
    private Chip claas_chip;
    //private ProgressBar progressBar;
    private GifImageView gifImageView;

    public AcademicFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        HomeActivity.firestore_cache();
    }

    private ArrayList<Subject> prepareData() {


        getAllSubject(filter_flag,filter_query_string);
        // getAllBooks();

        ArrayList<Subject> subjects = new ArrayList<Subject>();


        return subjects;
    }

    private void set_recyclerView(ArrayList<Book_db> subjects) {
        //subjectAdapter_db = new SubjectAdapter_db(subjects_db, getContext(),chiptype);
       // Log.d("sub11",subjects.toString());
        webAdapter = new WebAdapter( getContext(),subjects,chiptype);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rvSubject.setLayoutManager(manager);
        rvSubject.setAdapter(webAdapter);

        //progressBar.setVisibility(View.GONE);
        gifImageView.setVisibility(View.GONE);
    }

    private void getAllBooks(boolean filter_flag,String filter_query_string) {

       // progressBar.setVisibility(View.VISIBLE);
        gifImageView.setVisibility(View.VISIBLE);

        final int[] count = {0};
        Query query = null;

            final Subject_db subject=new Subject_db();
            subject.subjectName=filter_query_string;
            subject.books=new ArrayList<Book_db>();

            query = db.collection(filter_query_string+"");

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


                                }
                                subjects_db.add(subject);


                            } else {

                            }
                        }
                    }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                        set_recyclerView(subject.books);





                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getContext(), "Failed to retrieve Books", Toast.LENGTH_SHORT).show();

                }
            });



        Log.d("Reached adapter",  " => " );



    }

    private void getAllSubject(final boolean filter_flag, final String filter_query_string) {
        db.collection("Acad").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        subject_names.add(document.getId());
                    }
                    //  Log.d("acd_subjects", subject_names.toString());
                    getAllBooks(filter_flag,filter_query_string);
                    // set_recyclerView(subjects_db);

                } else {
                    //  Log.d("subjects", "Error getting documents: ", task.getException());
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_academic, container, false);

       // subjects = prepareData();

        chipGroup = view.findViewById(R.id.chip_group);
        rvSubject = view.findViewById(R.id.rvSubject);
        claas_chip=view.findViewById(R.id.Class);


       // progressBar = view.findViewById(R.id.progressBar_cyclic);
       // progressBar.setProgress(80);
       // progressBar.setIndeterminate(true);

       // Log.d("Progress ",progressBar+"");
        gifImageView = view.findViewById(R.id.book_smile);
        //default
        claas_chip.setSelected(true);
        //progressBar.setVisibility(View.VISIBLE);
        gifImageView.setVisibility(View.VISIBLE);
        getAllBooks(true,"Class");



        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {

                Log.d("Selected chip",checkedId+"");
                if((checkedId+"").equals("-1")) {
                    claas_chip.setSelected(true);
                    //subject_names.clear();
                    getAllBooks(true,"Class");

                   // filter_flag=false;

                }
                else{

                    chiptype = group.findViewById(checkedId).toString();

                    chiptype = chiptype.substring(chiptype.indexOf('/') + 1, chiptype.indexOf('}'));

                    Log.d("chip",chiptype);
                    filter_flag = true;

                }

                if(!chiptype.isEmpty())
                {
                    filter_query_string = chiptype;
                }
                //  Toast.makeText(HomeActivity.this, chiptype, Toast.LENGTH_SHORT).show();
                subject_names.clear();

                getAllBooks(filter_flag,filter_query_string);


            }
        });

        swipeContainer = view.findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                subject_names.clear();
                //subjectAdapter_db.clear();
               // subjects = prepareData();
                swipeContainer.setRefreshing(false);

            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        // Inflate the layout for this fragment
        return view;
    }
}
