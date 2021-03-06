package com.example.kaamwala;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ServicesRecyclerViewAdapter extends RecyclerView.Adapter<ServicesRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<ServiceModel> servicesList;
    private FirebaseAuth auth;

    public ServicesRecyclerViewAdapter(Context context, List<ServiceModel> servicesList, FirebaseAuth auth) {
        this.context = context;
        this.servicesList = servicesList;
        this.auth = auth;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cardview_rowdata_services, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ServiceModel serviceModel = servicesList.get(position);

        holder.serviceName.setText(serviceModel.getName());
        holder.serviceImageView.setImageResource(serviceModel.getThumbnail());
        holder.serviceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (auth.getCurrentUser() != null) {
                    Intent intent = new Intent(context, RequestServiceActivity.class);
                    intent.putExtra("service", serviceModel.getName());
                    context.startActivity(intent);
                } else {
                    context.startActivity(new Intent(context, RegisterActivity.class));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return servicesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView serviceImageView;
        TextView serviceName;
        CardView serviceLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            serviceImageView = itemView.findViewById(R.id.service_logo);
            serviceName = itemView.findViewById(R.id.service_name);
            serviceLayout = itemView.findViewById(R.id.service_card);
        }
    }
}
