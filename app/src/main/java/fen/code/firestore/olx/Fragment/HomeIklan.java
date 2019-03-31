package fen.code.firestore.olx.Fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fen.code.firestore.olx.Activity.PostJualIklan;
import fen.code.firestore.olx.Activity.UpdateIklan;
import fen.code.firestore.olx.Adapter.AdapterHomeIklan;
import fen.code.firestore.olx.Model.ItemJual;
import fen.code.firestore.olx.Model.User;
import fen.code.firestore.olx.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeIklan extends Fragment {

    Button button;
    RecyclerView rvIklanHome;
    AdapterHomeIklan adapterHomeIklan;
    List<ItemJual> itemJualList;
    FirebaseFirestore firestore;

    ProgressDialog dialog;
    FirebaseUser user;
    String snapshotId;

    public HomeIklan() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home_iklan, container, false);

        button = v.findViewById(R.id.btnBuatIklan);
        rvIklanHome = v.findViewById(R.id.rvIklanHome);
        firestore = FirebaseFirestore.getInstance();
        itemJualList = new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        dialog = new ProgressDialog(getActivity());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.getUid();
                FirebaseDatabase.getInstance().getReference("Users").child(user.getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                Intent intent = new Intent(getContext(), PostJualIklan.class);
                                intent.putExtra("namaProfile", user.getUsername());
                                intent.putExtra("imageProfile", user.getImageUrl());
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        });
        getData();
        return v;
    }

    void getData() {

        dialog.setMessage("Loading");
        dialog.show();
        firestore.collection("uploadIklan").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            ItemJual itemJual = snapshot.toObject(ItemJual.class);
                            itemJualList.add(itemJual);
                            snapshotId = snapshot.getId();
                        }
                        adapterHomeIklan = new AdapterHomeIklan(getContext(), itemJualList);
                        rvIklanHome.setLayoutManager(new GridLayoutManager(getContext(), 2));
                        rvIklanHome.setAdapter(adapterHomeIklan);
                        dialog.dismiss();

                        adapterHomeIklan.ActionClick(new AdapterHomeIklan.onAction() {
                            @Override
                            public void onActionClik(View view, int position) {
                                ItemJual itemJual = itemJualList.get(position);
                                Intent intent = new Intent(getContext(), UpdateIklan.class);
                                intent.putExtra("idsnapshot", snapshotId);
                                intent.putExtra("getid", itemJual.getId());
                                intent.putExtra("image", itemJual.getImageUpload());
                                intent.putExtra("title", itemJual.getTxtTitle());
                                intent.putExtra("harga", itemJual.getTxtHarga());
                                intent.putExtra("deskripsi", itemJual.getTxtDeskripsi());
                                startActivity(intent);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}