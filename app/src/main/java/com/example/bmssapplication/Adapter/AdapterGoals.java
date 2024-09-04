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

import com.example.bmssapplication.Model.Goal;
import com.example.bmssapplication.Notification.NotificationHelper;
import com.example.bmssapplication.R;
import com.example.bmssapplication.databinding.DeleteMessageLayoutBinding;
import com.example.bmssapplication.databinding.GoalLayoutBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class AdapterGoals extends RecyclerView.Adapter<AdapterGoals.ViewHolder> {


    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refGoals = database.getReference("Goals");

    Context context;
    List<Goal> mGoals;
    private OnItemClickListener mListener;

    public AdapterGoals(Context context, List<Goal> goals, OnItemClickListener mListener) {
        this.context = context;
        this.mGoals = goals;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        GoalLayoutBinding binding = GoalLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Goal goal = mGoals.get(position);


        holder.binding.result.setTextColor(context.getResources().getColor(R.color.colorRed));
        holder.binding.progressBar.setProgressDrawableColor(context.getResources().getColor(R.color.colorRed));


        holder.binding.goalName.setText(goal.getGoalName());
        holder.binding.progressBar.setProgressPercentage((goal.getSaving() / goal.getTarget()) * 100, true);
        holder.binding.result.setText(goal.getSaving() + " / " + goal.getTarget());

        if (goal.getSaving() >= goal.getTarget()) {
            holder.binding.result.setTextColor(context.getResources().getColor(R.color.colorGreen));
            holder.binding.progressBar.setProgressDrawableColor(context.getResources().getColor(R.color.colorGreen));
          //  NotificationHelper.showNotification(context, (int) System.currentTimeMillis(), context.getString(R.string.goals), context.getString(R.string.congratulations_you_have_achieved_your_goal) + " " + goal.getGoalName());
        }

        holder.binding.delete.setOnClickListener(v -> {
            showDialogDeleteMsgLayout(context.getString(R.string.are_your_sure_delete), goal);
        });
    }

    @Override
    public int getItemCount() {
        return mGoals.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItem_Click(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        GoalLayoutBinding binding;

        public ViewHolder(GoalLayoutBinding binding) {
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

    public void showDialogDeleteMsgLayout(String msg, Goal goal) {
        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.delete_message_layout, null);
        dialogBuilder.setView(dialogView);
        androidx.appcompat.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));


        // Initialize ViewBinding for the layout
        DeleteMessageLayoutBinding deleteMessageBinding = DeleteMessageLayoutBinding.bind(dialogView);
        deleteMessageBinding.inputMessage.setText(msg);
        deleteMessageBinding.buttonYes.setOnClickListener(v -> {
            refGoals.child(goal.getUserID()).child(goal.getID()).removeValue().addOnCompleteListener(task -> {
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
