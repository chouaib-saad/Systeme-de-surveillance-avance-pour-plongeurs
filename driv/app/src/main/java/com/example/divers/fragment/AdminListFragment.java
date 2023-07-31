package com.example.divers.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.divers.R;
import com.example.divers.adapter.adpteruser;
import com.example.divers.models.UserInfo;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AdminListFragment extends Fragment implements adpteruser.OnItemClickListener {

    RecyclerView recyclerView;
    adpteruser adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference myCollection = db.collection("users");
    UserInfo mymodal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_list, container, false);

        recyclerView = view.findViewById(R.id.subrecycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        myCollection = db.collection("users");
        Query query = myCollection.orderBy("fullName", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<UserInfo> options = new FirestoreRecyclerOptions.Builder<UserInfo>()
                .setQuery(query, UserInfo.class)
                .build();

        adapter = new adpteruser(options, this::onItemClick);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        return view;
    }

    @Override
    public void onItemClick(UserInfo model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Show data of user")
                .setPositiveButton("Oximeter", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mooveToFragment(new HeartBeat(), model.getId());
                    }
                })
                .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteUser(model);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteUser(UserInfo model) {
        myCollection.document(model.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // L'utilisateur a été supprimé avec succès
                        // Ajoutez ici toute logique supplémentaire après la suppression de l'utilisateur
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // La suppression de l'utilisateur a échoué
                        // Traitez ici l'erreur de suppression de l'utilisateur
                    }
                });
    }

    void mooveToFragment(Fragment fragment, String path) {
        Bundle args = new Bundle();
        args.putString("path", path);
        fragment.setArguments(args);

        // Replace the current fragment with the ItemDetailsFragment
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.flContent, fragment)
                .addToBackStack(null)
                .commit();
    }
}