package com.penelope.acousticrecipe.ui.recipe;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.penelope.acousticrecipe.R;
import com.penelope.acousticrecipe.data.recipe.Recipe;
import com.penelope.acousticrecipe.databinding.FragmentRecipeBinding;
import com.penelope.acousticrecipe.utils.NameUtils;
import com.penelope.acousticrecipe.utils.StringUtils;

import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RecipeFragment extends Fragment {

    private FragmentRecipeBinding binding;
    private RecipeViewModel viewModel;


    public RecipeFragment() {
        super(R.layout.fragment_recipe);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentRecipeBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(RecipeViewModel.class);

        showRecipeImage();

        binding.textViewRecipeName.setText(viewModel.getRecipe().getName());
        binding.textViewRecipeName2.setText(viewModel.getRecipe().getName());

        String strCookingType = String.format(Locale.getDefault(), "%s 요리",
                NameUtils.getCookingMethodName(viewModel.getRecipe().getCookingMethod())
        );
        binding.textViewCookingType.setText(strCookingType);

        binding.textViewHashTag.setText(viewModel.getRecipe().getHashtag());

        String strIngredients = StringUtils.join(viewModel.getRecipe().getIngredients(), ", ");
        binding.textViewIngredients.setText(strIngredients);

        String strNutrients = NameUtils.getNutrients(viewModel.getRecipe());
        binding.textViewNutrients.setText(!strNutrients.isEmpty() ? strNutrients : "미제공");

        String strButton = String.format(Locale.getDefault(), "레시피 확인 (총 %d 단계)",
                viewModel.getRecipe().getManuals().size()
        );
        binding.buttonManual.setText(strButton);

        binding.buttonManual.setOnClickListener(v -> viewModel.onManualClick());

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof RecipeViewModel.Event.NavigateToManualScreen) {
                Recipe recipe = ((RecipeViewModel.Event.NavigateToManualScreen) event).recipe;
                NavDirections navDirections = RecipeFragmentDirections.actionRecipeFragmentToManualFragment(recipe);
                Navigation.findNavController(requireView()).navigate(navDirections);
            }
        });
    }

    private void showRecipeImage() {

        String imageUrl = viewModel.getRecipe().getImageSmall();
        if (imageUrl == null) {
            imageUrl = viewModel.getRecipe().getImageBig();
        }
        if (imageUrl == null) {
            imageUrl = "https://img.joomcdn.net/a66ee2497f45e4258f4b181d40c52391e3f8cea9_original.jpeg";
        }
        Glide.with(this).load(imageUrl).into(binding.imageViewRecipe);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}