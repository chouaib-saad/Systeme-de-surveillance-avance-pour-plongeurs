package com.example.divers.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.divers.R;
import com.example.divers.models.UserInfo;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


public class adpteruser extends FirestoreRecyclerAdapter<UserInfo, adpteruser.MyViewHolder> {
    private OnItemClickListener mListener;


    public adpteruser(@NonNull FirestoreRecyclerOptions<UserInfo> options , OnItemClickListener listener) {
        super(options);
        mListener = listener;

    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull UserInfo model) {
        holder.bind(model);
    }

  //  @Override
   // protected void onBindViewHolder(@NonNull adpteruser.MyViewHolder holder, int position, @NonNull userin model) {

   // }

    @NonNull
    @Override
    public adpteruser.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemmember, parent, false);
        return new adpteruser.MyViewHolder(view, mListener);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView nameview;
        private TextView phoneview;
        private TextView emailview;
        private String mDocumentId;


        public MyViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            nameview = itemView.findViewById(R.id.nameuser);
            emailview= itemView.findViewById(R.id.emailuser);
            phoneview = itemView.findViewById(R.id.phoneuser);
            itemView.setOnClickListener(v -> {

            });


             itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        getItem(position).setId(mDocumentId);
                        listener.onItemClick(getItem(position));
                      }
                  }
               }
              });
        }

        public void bind(UserInfo model) {
            nameview.setText(model.getFullName());
            phoneview.setText(model.getPhone());
            emailview.setText(model.getEmail());
            mDocumentId = getSnapshots().getSnapshot(getAdapterPosition()).getId();



        }
    }
    public interface OnItemClickListener {
        void onItemClick(UserInfo model);
    }


}