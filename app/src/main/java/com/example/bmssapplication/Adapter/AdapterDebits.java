package com.example.bmssapplication.Adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bmssapplication.Model.Debit;
import com.example.bmssapplication.Notification.NotificationHelper;
import com.example.bmssapplication.R;
import com.example.bmssapplication.databinding.DebitLayoutBinding;
import com.example.bmssapplication.databinding.DeleteMessageLayoutBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class AdapterDebits extends RecyclerView.Adapter<AdapterDebits.ViewHolder> {


    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refDebits = database.getReference("Debits");

    Context context;
    List<Debit> mDebits;
    private OnItemClickListener mListener;

    public AdapterDebits(Context context, List<Debit> debits, OnItemClickListener mListener) {
        this.context = context;
        this.mDebits = debits;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        DebitLayoutBinding binding = DebitLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Debit debit = mDebits.get(position);

        holder.binding.result.setTextColor(context.getResources().getColor(R.color.colorRed));
        holder.binding.progressBar.setProgressDrawableColor(context.getResources().getColor(R.color.colorRed));


        holder.binding.purpose.setText(debit.getPurpose());
        holder.binding.creditorName.setText(debit.getCreditorName());
        holder.binding.progressBar.setProgressPercentage((debit.getPaid() / debit.getTotal()) * 100, true);
        holder.binding.result.setText(debit.getPaid() + " / " + debit.getTotal() + " SAR");

        if (debit.getPaid() >= debit.getTotal()) {
            holder.binding.result.setTextColor(context.getResources().getColor(R.color.colorGreen));
            holder.binding.progressBar.setProgressDrawableColor(context.getResources().getColor(R.color.colorGreen));
//            NotificationHelper.showNotification(context, (int) System.currentTimeMillis(), context.getString(R.string.debits), context.getString(R.string.congratulations_you_have_paid_off_your_debt) + " " + debit.getPurpose());
        }

        holder.binding.delete.setOnClickListener(v -> {
            showDialogDeleteMsgLayout(context.getString(R.string.are_your_sure_delete), debit);
        });
    }

    @Override
    public int getItemCount() {
        return mDebits.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItem_Click(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        DebitLayoutBinding binding;

        public ViewHolder(DebitLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItem_Click(position);
                }
            }
        }
    }

    public void showDialogDeleteMsgLayout(String msg, Debit debit) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.delete_message_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        // Initialize ViewBinding for the layout
        DeleteMessageLayoutBinding deleteMessageBinding = DeleteMessageLayoutBinding.bind(dialogView);
        deleteMessageBinding.inputMessage.setText(msg);
        deleteMessageBinding.buttonYes.setOnClickListener(v -> {
            refDebits.child(debit.getUserID()).child(debit.getID()).removeValue()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toasty.success(context, context.getString(R.string.delete), Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }
                    });
        });

        deleteMessageBinding.buttonNo.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }
}
