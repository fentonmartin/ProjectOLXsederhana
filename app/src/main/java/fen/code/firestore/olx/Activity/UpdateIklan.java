package fen.code.firestore.olx.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import fen.code.firestore.olx.Model.ItemJual;
import fen.code.firestore.olx.Model.User;
import fen.code.firestore.olx.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateIklan extends AppCompatActivity {
    String getIdsnapshot, getImage, getTitle, getDeskripsi, getHarga, getid, getIdSendiri;

    ImageView imgUpload;
    EditText txtTitle, txtDescripsi, txtHarga;
    Button btnJuall ,btnUpdate,btnDelete;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser user;
    LinearLayout llUpdateDelte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_iklan);


        firebaseFirestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        initialize();

    }


    private void initialize() {
        getIdSendiri = getIntent().getStringExtra("getid");
        getid = getIntent().getStringExtra("userid");
        getIdsnapshot = getIntent().getStringExtra("idsnapshot");
        getImage = getIntent().getStringExtra("image");
        getTitle = getIntent().getStringExtra("title");
        getDeskripsi = getIntent().getStringExtra("deskripsi");
        getHarga = getIntent().getStringExtra("harga");

        llUpdateDelte = findViewById(R.id.linearUpdatedelte);
        imgUpload = findViewById(R.id.imgupload);
        txtTitle = findViewById(R.id.txtTitle);
        txtDescripsi = findViewById(R.id.txtDeskrispi);
        txtHarga = findViewById(R.id.txtHarga);
        btnJuall = findViewById(R.id.btnSimpanJual);
        btnDelete = findViewById(R.id.btnDelete);
        btnUpdate = findViewById(R.id.btnUpdate);

        Glide.with(UpdateIklan.this)
                .load(getImage)
                .into(imgUpload);

        txtTitle.setText(getTitle);
        txtDescripsi.setText(getDeskripsi);
        txtHarga.setText(getHarga);
        txtTitle.setEnabled(false);
        txtDescripsi.setEnabled(false);
        txtHarga.setEnabled(false);
        Log.d("respongetImage", "i " + getIdSendiri);


        user = FirebaseAuth.getInstance().getCurrentUser();
        if (getIdSendiri.equals(user.getUid())) {
            llUpdateDelte.setVisibility(View.VISIBLE);

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtTitle.setEnabled(true);
                    txtDescripsi.setEnabled(true);
                    txtHarga.setEnabled(true);

                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UpdateIklan.this);
                    builder.setMessage("Apakah anda yakin ingin hapus !!");
                    builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteIklan();
                        }
                    }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
            });
        }


        btnJuall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase.getInstance().getReference("Users")
                        .child(user.getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                UpdateData(user.getImageUrl(), user.getUsername());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }
        });
    }


    void UpdateData(String imageProfile, String namaProfile) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Update loading");
        dialog.show();
        ItemJual itemJual = new ItemJual();


        itemJual.setImageProfile(imageProfile);
        itemJual.setNameProfile(namaProfile);
        itemJual.setId(user.getUid());
        itemJual.setImageUpload(getImage);
        itemJual.setTxtTitle(txtTitle.getText().toString());
        itemJual.setTxtDeskripsi(txtDescripsi.getText().toString());
        itemJual.setTxtHarga(txtHarga.getText().toString());

        firebaseFirestore.collection("uploadIklan")
                .document(getIdsnapshot)
                .set(itemJual)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialog.show();
                            finish();
                            Toast.makeText(getApplicationContext(), "Succes Update", Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Gagal Update", Toast.LENGTH_LONG).show();
            }
        });

    }

    void deleteIklan() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Delete Loading");
        dialog.show();
        DocumentReference reference = firebaseFirestore.collection("uploadIklan")
                .document(getIdsnapshot);

        reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dialog.dismiss();
                finish();
            }
        });
    }
}
