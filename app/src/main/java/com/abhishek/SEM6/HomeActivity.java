package com.abhishek.SEM6;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abhishek.SEM6.adapters.SubjectAdapter;
import com.abhishek.SEM6.models.Book;
import com.abhishek.SEM6.models.Subject;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvSubject;
    private SubjectAdapter subjectAdapter;
    private ArrayList<Subject> subjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();

        subjects = prepareData();

        subjectAdapter = new SubjectAdapter(subjects, HomeActivity.this);
        LinearLayoutManager manager = new LinearLayoutManager(HomeActivity.this);
        rvSubject.setLayoutManager(manager);
        rvSubject.setAdapter(subjectAdapter);

    }

    private void initComponents() {
        rvSubject = findViewById(R.id.rvSubject);
    }

    private ArrayList<Subject> prepareData() {
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
}
