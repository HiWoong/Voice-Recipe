package com.penelope.acousticrecipe.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.penelope.acousticrecipe.data.recipe.Recipe;
import com.penelope.acousticrecipe.databinding.RecipeItemBinding;
import com.penelope.acousticrecipe.utils.Consts;
import com.penelope.acousticrecipe.utils.NameUtils;

public class RecipesAdapter extends ListAdapter<Recipe, RecipesAdapter.RecipeViewHolder> {

    class RecipeViewHolder extends RecyclerView.ViewHolder {

        private final RecipeItemBinding binding;

        public RecipeViewHolder(RecipeItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemSelectedListener != null) {
                    onItemSelectedListener.onItemSelected(position);
                }
            });
        }

        public void bind(Recipe model) {

            binding.textViewRecipeName.setText(model.getName());

            String strCalories = model.getCalories() + "kcal";
            binding.textViewRecipeCalories.setText(strCalories);
            binding.textViewRecipeCalories.setVisibility(model.getCalories() > 0 ? View.VISIBLE : View.INVISIBLE);

            String foodTypeName = NameUtils.getFoodTypeName(model.getFoodType());
            binding.textViewRecipeFoodType.setText(foodTypeName);

            String imageUrl = model.getImageSmall();
            if (imageUrl == null) {
                imageUrl = model.getImageBig();
            }
            if (imageUrl == null) {
                imageUrl = Consts.URL_IMAGE_DEFAULT;
            }
            glide.load(imageUrl).into(binding.imageViewRecipe);
        }
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position);
    }

    private OnItemSelectedListener onItemSelectedListener;
    private final RequestManager glide;


    public RecipesAdapter(RequestManager glide) {
        super(new DiffUtilCallback());
        this.glide = glide;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.onItemSelectedListener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecipeItemBinding binding = RecipeItemBinding.inflate(layoutInflater, parent, false);
        return new RecipeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.bind(getItem(position));
    }


    static class DiffUtilCallback extends DiffUtil.ItemCallback<Recipe> {

        @Override
        public boolean areItemsTheSame(@NonNull Recipe oldItem, @NonNull Recipe newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Recipe oldItem, @NonNull Recipe newItem) {
            return oldItem.equals(newItem);
        }
    }

}