package ams2.ivanll.photogallery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    final private ImageItem[] localDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final TextView textView;
        private final ImageButton editCommentButton;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
            textView = view.findViewById(R.id.textView);
            editCommentButton = view.findViewById(R.id.editCommentButton);
            editCommentButton.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Edita el comentario");

                LayoutInflater inflater = LayoutInflater.from(v.getContext());
                final View customLayout = inflater.inflate(R.layout.edit_comment_dialog, null);
                final EditText editText = customLayout.findViewById(R.id.commentEditText);
                editText.setText(textView.getText().toString());
                builder.setView(customLayout);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = customLayout.findViewById(R.id.commentEditText);
                        textView.setText(editText.getText().toString());
                    }
                });

                builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

                builder.show();
            });
        }

        public ImageView getImageView() {
            return imageView;
        }

        public TextView getTextView() {
            return textView;
        }

        public ImageButton getEditCommentButton() {
            return editCommentButton;
        }

    }

    public ImageAdapter(ImageItem[] dataSet) {
        localDataSet = dataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.picture_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getImageView().setImageBitmap(localDataSet[position].getBitmap());
        holder.getTextView().setText(localDataSet[position].getComment());
    }

    @Override
    public int getItemCount() {
        return localDataSet.length;
    }

}
