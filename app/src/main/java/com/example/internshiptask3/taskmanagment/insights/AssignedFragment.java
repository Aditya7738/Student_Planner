package com.example.internshiptask3.taskmanagment.insights;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.internshiptask3.R;
import com.example.internshiptask3.taskmanagment.alarmmanager.AlarmDataModel;
import com.example.internshiptask3.taskmanagment.alarmmanager.AlarmsList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AssignedFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;




    ArrayList<AssignedDataModel> assignedTaskList;

    ArrayList<String> arrayList;

    AssignedTaskAdapter assignedTaskAdapter;

    ListView assignLv;
    FirebaseAuth firebaseAuth;

    FirebaseFirestore firestore;

    Calendar currentCalendar;

    Date currentTime;



    private CollectionReference collectionReference = null;

    public AssignedFragment() {
        // Required empty public constructor
    }

    public static DoneFragment newInstance(String param1, String param2) {
        DoneFragment fragment = new DoneFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_assigned, container, false);


        assignLv = (ListView) view.findViewById(R.id.assignedList);

        firebaseAuth = FirebaseAuth.getInstance();

        firestore = FirebaseFirestore.getInstance();

        currentCalendar = Calendar.getInstance();

        currentTime = currentCalendar.getTime();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String uid = null;
        if (firebaseUser != null) {
            uid = firebaseUser.getUid();
        }


        if (uid != null) {
            collectionReference = firestore
                    .collection("users").document(uid).collection("TaskDetails");
        }


        assignedTaskList = new ArrayList<>();

        //eventChangeListener();

        assignedTaskAdapter = new AssignedTaskAdapter(getActivity(), assignedTaskList);

        assignLv.setAdapter(assignedTaskAdapter);

        assignedTaskAdapter.updateList(assignedTaskList);






        long currentTimel = System.currentTimeMillis();

        //ApiFuture<QuerySnapshot>

        collectionReference.whereGreaterThan("selectedtime", currentTime)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
//                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
//                    String documentID = documentSnapshot.getId();
//                    collectionReference.document(documentID)
//                            .get()
//                            .
//
//                    AssignedDataModel assignedDataModel = new AssignedDataModel();
//                    for (DocumentSnapshot documentSnapshot: task.getResult().getDocuments()){
//
//                        assignedDocList.add(String.valueOf(documentSnapshot.get("TaskTitle")));
//                        assignedDocList.add(String.valueOf(documentSnapshot.get("DueDate")));
//                        assignedDocList.add(String.valueOf(documentSnapshot.get("TaskSubject")));
                    //}
                    for(DocumentChange documentChange: task.getResult().getDocumentChanges()){
                        if (documentChange.getType() == DocumentChange.Type.ADDED){

                            assignedTaskList.add(documentChange.getDocument().toObject(AssignedDataModel.class)); // here is error

                        }
                    }



                }else{
                    Toast.makeText(getContext(), "Task is not assigned yet", Toast.LENGTH_SHORT).show();
                }
            }
        });

//                });
                    //List<DocumentSnapshot> documents = task.getResult().getDocuments();


                    // Inflate the layout for this fragment
        return view;

    }

    private void eventChangeListener() {
        collectionReference.whereGreaterThan("selectedtime", currentTime)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (value != null) {
                            //assignedDocList.clear();
                            for(DocumentChange documentChange: value.getDocumentChanges()){
                                if (documentChange.getType() == DocumentChange.Type.ADDED){

                                    assignedTaskList.add(documentChange.getDocument().toObject(AssignedDataModel.class)); // here is error

                                }


                            }
                        }else{
                            Toast.makeText(getActivity(), "Tasks are not assigned yet", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}