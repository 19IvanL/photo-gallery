package ams2.ivanll.photogallery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    final private List<ImageItem> localDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private String name;
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
                        String comment = editText.getText().toString();
                        textView.setText(comment);
                        InternalDataAccess.saveComment(v.getContext(), name, comment);
                    }
                });

                builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

                builder.show();
            });
        }

        public String getName() {
            return name;
        }

        public void setName (String name) {
            this.name = name;
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

    public ImageAdapter(List<ImageItem> dataSet) {
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
        holder.setName(localDataSet.get(position).getName());
        holder.getImageView().setImageBitmap(localDataSet.get(position).getBitmap());
        holder.getTextView().setText(localDataSet.get(position).getComment());
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

}
