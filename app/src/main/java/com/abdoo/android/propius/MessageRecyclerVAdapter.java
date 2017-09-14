package com.abdoo.android.propius;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;

public class MessageRecyclerVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    FirebaseAuth mAuth;
    RecyclerView.ViewHolder viewHolder;
    final int COMING = 0;
    final int GOING = 1;

    // The items to display in your RecyclerView
    private List<Message> messagesList;

    public MessageRecyclerVAdapter(List<Message> messagesList) {
        this.messagesList = messagesList;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.messagesList.size();
    }

    @Override
    public int getItemViewType(int i) {
        String fromUserId = messagesList.get(i).getFrom();
        String crrUserId = mAuth.getInstance().getCurrentUser().getUid();
        if(fromUserId.equals(crrUserId))
            return GOING;
        else
            return COMING;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType){
            case COMING:
                View v1 = inflater.inflate(R.layout.message_single_layout_coming, viewGroup, false);
                viewHolder = new ComingVHolder(v1);
                break;
            case GOING:
                View v2 = inflater.inflate(R.layout.message_single_layout_going, viewGroup, false);
                viewHolder = new GoingVHolder(v2);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        switch (viewHolder.getItemViewType()){
            case COMING:
                ComingVHolder vh1 = (ComingVHolder) viewHolder;
                vh1.comingMsg.setText(messagesList.get(i).getMessage());
                break;

            case GOING:
                GoingVHolder vh2 = (GoingVHolder) viewHolder;
                vh2.goingMsg.setText(messagesList.get(i).getMessage());
                break;
        }
    }

    private class ComingVHolder extends RecyclerView.ViewHolder {
        private TextView comingMsg;
        public ComingVHolder(View v1) {
            super(v1);
            comingMsg = (TextView) v1.findViewById(R.id.message_single_text_c);
        }
    }

    private class GoingVHolder extends RecyclerView.ViewHolder {
        private TextView goingMsg;
        public GoingVHolder(View v2) {
            super(v2);
            goingMsg = (TextView) v2.findViewById(R.id.message_single_text_g);
        }
    }
}