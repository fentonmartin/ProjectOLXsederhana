package fen.code.firestore.olx.Fragment;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fen.code.firestore.olx.Activity.UpdateIklan;
import fen.code.firestore.olx.Adapter.AdapterProfileIklan;
import fen.code.firestore.olx.Model.ItemJual;
import fen.code.firestore.olx.Model.User;
import fen.code.firestore.olx.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {

    Button button;
    RecyclerView rvIklanHome;
    AdapterProfileIklan adapterHomeIklan;
    List<ItemJual> itemJualList;
    FirebaseFirestore firestore;

    ProgressDialog dialog;
    FirebaseUser user;
    ImageView imgProfile;
    TextView txtNameProfile, txtEmailProfile;
    Uri imageCamera, xUri;
    DatabaseReference reference;

    int PICK_IMAGE_REQUEST = 111;

    StorageReference storageReference;
    StorageTask task;
    String snapshotid;

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        button = v.findViewById(R.id.btnBuatIklan);
        rvIklanHome = v.findViewById(R.id.rvIklansaya);
        imgProfile = v.findViewById(R.id.imgProfile);
        txtEmailProfile = v.findViewById(R.id.txtEmailProfile);
        txtNameProfile = v.findViewById(R.id.txtNamePorfile);
        firestore = FirebaseFirestore.getInstance();
        itemJualList = new ArrayList<>();
        dialog = new ProgressDialog(getActivity());
        user = FirebaseAuth.getInstance().getCurrentUser();

        getData();
        getDataProfile();
        storageReference = FirebaseStorage.getInstance().getReference("UploadProfile");
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalery();
            }
        });
        return v;
    }

    private void openGalery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void getDataProfile() {
        FirebaseDatabase.getInstance().getReference("Users").child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        txtEmailProfile.setText(user.getEmail());
                        txtNameProfile.setText(user.getUsername());

                        if (user.getImageUrl().equals("default")) {
                            imgProfile.setImageResource(R.drawable.add_user);
                        } else {
                            Glide.with(getContext())
                                    .load(user.getImageUrl())
                                    .into(imgProfile);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    void getData() {
        dialog.setMessage("Loading");
        dialog.show();

        firestore.collection("uploadIklan").whereEqualTo("id", user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            ItemJual itemJual = snapshot.toObject(ItemJual.class);
                            itemJualList.add(itemJual);
                            snapshotid = snapshot.getId();
                        }

                        adapterHomeIklan = new AdapterProfileIklan(getContext(), itemJualList);
                        rvIklanHome.setLayoutManager(new LinearLayoutManager(getActivity()));
                        rvIklanHome.setAdapter(adapterHomeIklan);
                        dialog.dismiss();

                        adapterHomeIklan.ActionClick(new AdapterProfileIklan.onAction() {
                            @Override
                            public void onActionClik(View view, int position) {
                                ItemJual itemJual = itemJualList.get(position);
                                Intent intent = new Intent(getContext(), UpdateIklan.class);
                                intent.putExtra("idsnapshot", snapshotid);
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

    String getFileExtention(Uri uri) {
        ContentResolver resolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));
    }

    void uploadimageProfile() {
        final ProgressDialog dialog = new ProgressDialog(getContext());
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
                        final String mUri = downloadUri.toString();
                        reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("imageUrl", mUri);
                        reference.updateChildren(hashMap);
                        dialog.dismiss();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "Gagal Upload", Toast.LENGTH_LONG).show();
                    }
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();

                }
            });
        } else {
            Toast.makeText(getActivity(), "Tidak ada image yg di pilih", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageCamera = data.getData();
            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageCamera);
                //Setting image to ImageView
                imgProfile.setImageBitmap(bitmap);
                uploadimageProfile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
