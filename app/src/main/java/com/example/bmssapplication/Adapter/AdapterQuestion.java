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
import com.example.bmssapplication.Model.Question;
import com.example.bmssapplication.R;
import com.example.bmssapplication.databinding.DeleteMessageLayoutBinding;
import com.example.bmssapplication.databinding.GoalLayoutBinding;
import com.example.bmssapplication.databinding.QuestionLayoutBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class AdapterQuestion extends RecyclerView.Adapter<AdapterQuestion.ViewHolder> {

    Context context;
    List<Question> mQuestions;
    private OnItemClickListener mListener;

    public AdapterQuestion(Context context, List<Question> questions, OnItemClickListener mListener) {
        this.context = context;
        this.mQuestions = questions;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        QuestionLayoutBinding binding = QuestionLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Question question = mQuestions.get(position);

        holder.binding.questionTitle.setText(question.getqQuestion());
        holder.binding.questionAnswer.setText(question.getqAnswer());

        holder.binding.more.setOnClickListener(v -> {
            question.setShowAnswer(!question.isShowAnswer());
            holder.binding.more.setImageResource(question.isShowAnswer() ? R.drawable.arrow_up : R.drawable.arrow_down);
            holder.binding.questionAnswer.setVisibility(question.isShowAnswer() ? View.VISIBLE : View.GONE);
        });


    }

    @Override
    public int getItemCount() {
        return mQuestions.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItem_Click(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        QuestionLayoutBinding binding;

        public ViewHolder(QuestionLayoutBinding binding) {
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
}
