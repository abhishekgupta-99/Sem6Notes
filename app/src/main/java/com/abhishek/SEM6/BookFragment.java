package com.abhishek.SEM6;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
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

import com.abhishek.SEM6.adapters.SubjectAdapter_db;
import com.abhishek.SEM6.models.Book;
import com.abhishek.SEM6.models.Book_db;
import com.abhishek.SEM6.models.Subject;
import com.abhishek.SEM6.models.Subject_db;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookFragment extends Fragment  {

    private static RecyclerView rvsubject_search;
    private SwipeRefreshLayout swipeContainer;
    List<String> subject_names = new ArrayList<>();
    private SubjectAdapter_db subjectAdapter_db,subjectAdapter_db_dialog;
    private ArrayList<Subject> subjects;
    private ChipGroup chipGroup;
    String chiptype;
    boolean filter_flag=false;
    String filter_query_string="";
    private FirebaseFirestore db;
    ArrayList<Subject_db> subjects_db = new ArrayList<Subject_db>();
    public RecyclerView rvSubject;
    public RecyclerView rv_playbooks;

    public static List<String> getSubject_names_clone() {
        return subject_names_clone;
    }

    public static void setSubject_names_clone(List<String> subject_names_clone) {
        BookFragment.subject_names_clone = subject_names_clone;
    }

    public  static List<String> subject_names_clone= new ArrayList<>();

    public static ArrayList<Subject_db> getSubjects_db_clone() {
        return subjects_db_clone;
    }

    public static void setSubjects_db_clone(ArrayList<Subject_db> subjects_db_clone) {
        BookFragment.subjects_db_clone = subjects_db_clone;
    }

    public static ArrayList<Subject_db> subjects_db_clone = new ArrayList<Subject_db>();




    public static RecyclerView getRvsubject_search() {
        return rvsubject_search;
    }


    private FloatingActionButton floatingActionButton;
    private ProgressBar progressBar;
    private SearchView searchView;

    public BookFragment() {
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

    public void set_recyclerView(ArrayList<Subject_db> subjects_db) {
        subjectAdapter_db = new SubjectAdapter_db(subjects_db, getActivity(),chiptype,0);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
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

        progressBar.setVisibility(View.GONE);
    }

    public void set_recyclerView_search(ArrayList<Subject_db> subjects_db) {

        Log.d("Search books",subjects_db.size()+"");
        SubjectAdapter_db subjectAdapter_db_search = new SubjectAdapter_db(subjects_db, HomeActivity.getAppContext(), chiptype, 0);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());

//        LayoutInflater inflater=getLayoutInflater();
//        View view = inflater.inflate(R.layout.fragment_book,null);
//        rvsubject_search=view.findViewById(R.id.rvSubject);

        getRvsubject_search().setLayoutManager(manager);
        getRvsubject_search().setAdapter(subjectAdapter_db_search);
    }

    private void getAllBooks(boolean filter_flag,String filter_query_string) {

        final int[] count = {0};
        Query query = null;
        //

        java.util.Collections.sort(subject_names, Collections.reverseOrder());

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

                        setSubjects_db_clone(subjects_db);
                        set_recyclerView(subjects_db);

                    }



                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getContext(), "Failed to retrieve Books", Toast.LENGTH_SHORT).show();

                }
            });

        }

        Log.d("Reached adapter",  " => " );



    }

    private void getAllSubject(final boolean filter_flag, final String filter_query_string) {

        progressBar.setVisibility(View.VISIBLE);
        db.collection("Subjects").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        subject_names.add(document.getId());
                    }

                    setSubject_names_clone(subject_names);
                    //  Log.d("subjects", subject_names.toString());
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book, container, false);



        chipGroup = view.findViewById(R.id.chip_group);
        rvSubject = view.findViewById(R.id.rvSubject);
        rvsubject_search=rvSubject;
        floatingActionButton = view.findViewById(R.id.imgFour);

        progressBar = view.findViewById(R.id.progressBar_cyclic);
        progressBar.setProgress(80);
        progressBar.setIndeterminate(true);

        Log.d("Progress ",progressBar+"");
        subjects = prepareData();




        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {

                Log.d("checkedid",checkedId+"");
                if((checkedId+"").equals("-1")) {

                    filter_flag=false;

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
                subjectAdapter_db.clear();
                getAllSubject(filter_flag,filter_query_string);


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
                subjectAdapter_db.clear();
                subjects = prepareData();
                swipeContainer.setRefreshing(false);

            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        return view;
    }

}
