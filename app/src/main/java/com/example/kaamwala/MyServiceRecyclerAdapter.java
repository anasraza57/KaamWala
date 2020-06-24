package com.example.kaamwala;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;


public class MyServiceRecyclerAdapter extends FirestoreRecyclerAdapter<MyServicesModel, MyServiceRecyclerAdapter.ViewHolder> {
    Context context;
    DocumentReference personalInfoRef;
    CollectionReference serviceRef;
    FirebaseAuth auth;

    public MyServiceRecyclerAdapter(@NonNull FirestoreRecyclerOptions<MyServicesModel> options,
                                    DocumentReference documentReference, CollectionReference serviceRef, FirebaseAuth auth, Context applicationContext) {
        super(options);
        this.personalInfoRef = documentReference;
        this.serviceRef = serviceRef;
        this.auth = auth;
        this.context = applicationContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull final MyServicesModel model) {
        String documentid = getSnapshots().getSnapshot(position).getId();
        model.setMyServiceId(documentid);
        holder.serviceName.setText(model.getServiceName());
        final String optionalAddress = model.getOptionalAddress();
        final String optionalPhone = model.getOptionalPhone();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
        String date = dateFormat.format(model.getTiming());
        holder.timing.setText(date);
        holder.status.setText(model.getStatus());

        personalInfoRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    holder.name.setText(documentSnapshot.getString("fullName"));
                    if (optionalAddress.length() == 0) {
                        holder.address.setText(documentSnapshot.getString("address"));
                    } else {
                        holder.address.setText(optionalAddress);
                    }
                    if (optionalPhone.length() == 0) {
                        holder.phone.setText(auth.getCurrentUser().getPhoneNumber());
                    } else {
                        holder.phone.setText(optionalPhone);
                    }
                }
            }
        });

        holder.myServiceCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog dialog = new AlertDialog.Builder(view.getRootView().getContext()).create();
                dialog.setTitle("Do you want to cancel the request?");

                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        serviceRef.document(model.getMyServiceId()).delete();
                    }
                });

                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_rowdata_myservices, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView serviceName, name, address, phone, timing, status;
        CardView myServiceCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            serviceName = itemView.findViewById(R.id.service_name);
            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            phone = itemView.findViewById(R.id.phone);
            timing = itemView.findViewById(R.id.time);
            status = itemView.findViewById(R.id.status);
            myServiceCardView = itemView.findViewById(R.id.my_service_card);
        }
    }
}
