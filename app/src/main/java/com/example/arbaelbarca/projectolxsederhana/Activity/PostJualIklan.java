package com.example.arbaelbarca.projectolxsederhana.Activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.arbaelbarca.projectolxsederhana.Model.ItemJual;
import com.example.arbaelbarca.projectolxsederhana.Model.User;
import com.example.arbaelbarca.projectolxsederhana.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class PostJualIklan extends AppCompatActivity {

    ImageView imgUpload;
    EditText txtTitle, txtDescripsi, txtHarga;
    Button btnJuall;
    private int GALLERY = 1, CAMERA = 2;
    StorageReference storageReference;
    StorageTask task;
    private static final String IMAGE_DIRECTORY = "/Image";
    Uri imageCamera, xUri;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser user;
    String mUri,  uuid;
    Bitmap thumbnail;
    DatabaseReference reference;
    static final int IMAGE_REQUST = 111;
    int PICK_IMAGE_REQUEST = 111;
    String getNama, getimageProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_jual_iklan);


        firebaseFirestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        imgUpload = findViewById(R.id.imgupload);
        txtTitle = findViewById(R.id.txtTitle);
        txtDescripsi = findViewById(R.id.txtDeskrispi);
        txtHarga = findViewById(R.id.txtHarga);
        btnJuall = findViewById(R.id.btnSimpanJual);

        getNama = getIntent().getStringExtra("namaProfile");
        getimageProfile = getIntent().getStringExtra("imageProfile");
        Log.d("responGetnama" ,"n" +getNama +getimageProfile);
        storageReference = FirebaseStorage.getInstance().getReference("UploadIklan");

        imgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageGalaery();
            }
        });
        btnJuall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });

    }

    private void takeCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }
    private void openImageGalaery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUST);
    }

//    void pilihImage()
//    {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_PICK);
//        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
//    }


    private void uploadData() {


        uuid = UUID.randomUUID().toString();
        uploadImage();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == IMAGE_REQUST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageCamera = data.getData();
            if (task != null && task.isInProgress()) {
                Toast.makeText(getApplicationContext(), "Upload Loading", Toast.LENGTH_LONG).show();

            } else {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageCamera);
                    imgUpload.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            imageCamera = data.getData();
//
//            try {
//                //getting image from gallery
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageCamera);
//
//                //Setting image to ImageView
//                imgUpload.setImageBitmap(bitmap);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

    String getFileExtention(Uri uri) {
        ContentResolver resolver = getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));
    }

    void uploadImage() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading");
        dialog.show();
        dialog.setCancelable(false);

        if (imageCamera != null) {
            final StorageReference referenceStore = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtention(imageCamera));

            task = referenceStore.putFile(imageCamera);
            task.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();


                    }

                    return referenceStore.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();
                        final ItemJual itemJual = new ItemJual();
                        itemJual.setId(user.getUid());
                        itemJual.setImageProfile(getimageProfile);
                        itemJual.setNameProfile(getNama);
                        itemJual.setTxtTitle(txtTitle.getText().toString());
                        itemJual.setTxtDeskripsi(txtDescripsi.getText().toString());
                        itemJual.setTxtHarga(txtHarga.getText().toString());
                        itemJual.setImageUpload(mUri);
                        firebaseFirestore.collection("uploadIklan").document(uuid)
                                .set(itemJual, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Success Upload", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                    finish();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Gagal Upload", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        });
                        dialog.dismiss();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Gagal Upload", Toast.LENGTH_LONG).show();

                    }
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

                }
            });


        }

        else {
            Toast.makeText(getApplicationContext(), "Tidak ada image yg di pilih", Toast.LENGTH_LONG).show();
        }
    }


    void getImageProfile(final ItemJual itemJual)
    {
        FirebaseDatabase.getInstance().getReference("Users").child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        itemJual.setImageProfile(user.getImageUrl());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
