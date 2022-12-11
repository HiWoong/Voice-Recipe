package com.penelope.acousticrecipe.ui.history;

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
import com.penelope.acousticrecipe.databinding.FragmentHistoryBinding;
import com.penelope.acousticrecipe.ui.home.RecipesAdapter;

import java.util.Arrays;
import java.util.Collections;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private HistoryViewModel viewModel;


    public HistoryFragment() {
        super(R.layout.fragment_history);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentHistoryBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        binding.progressBar3.setVisibility(View.VISIBLE);

        RecipesAdapter adapter = new RecipesAdapter(Glide.with(this));
        binding.recyclerRecipe.setAdapter(adapter);
        binding.recyclerRecipe.setHasFixedSize(true);

        adapter.setOnItemSelectedListener(position -> {
            Recipe recipe = adapter.getCurrentList().get(position);
            viewModel.onRecipeClick(recipe);
        });

        viewModel.getVisitedRecipes().observe(getViewLifecycleOwner(), recipes -> {
            if (recipes != null) {
                adapter.submitList(recipes);
                binding.textViewNoVisitedRecipes.setVisibility(recipes.isEmpty() ? View.VISIBLE : View.INVISIBLE);
            }
            binding.progressBar3.setVisibility(View.INVISIBLE);
        });

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof HistoryViewModel.Event.NavigateToRecipeScreen) {
                Recipe recipe = ((HistoryViewModel.Event.NavigateToRecipeScreen) event).recipe;
                NavDirections navDirections = HistoryFragmentDirections.actionGlobalRecipeFragment(recipe);
                Navigation.findNavController(requireView()).navigate(navDirections);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.onRefresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}