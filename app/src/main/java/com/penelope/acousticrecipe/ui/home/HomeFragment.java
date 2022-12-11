package com.penelope.acousticrecipe.ui.home;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.penelope.acousticrecipe.R;
import com.penelope.acousticrecipe.data.recipe.Recipe;
import com.penelope.acousticrecipe.databinding.FragmentHomeBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    public interface HomeFragmentListener {
        void onSearchClick();
    }

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;


    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentHomeBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding.fabSearch.setOnClickListener(v -> viewModel.onSearchClick());

        RecipesAdapter adapter = new RecipesAdapter(Glide.with(this));
        binding.recyclerRecipe.setAdapter(adapter);
        binding.recyclerRecipe.setHasFixedSize(true);

        adapter.setOnItemSelectedListener(position -> {
            Recipe recipe = adapter.getCurrentList().get(position);
            viewModel.onRecipeClick(recipe);
        });

        viewModel.getRecommendedRecipes().observe(getViewLifecycleOwner(), recipes -> {
            if (recipes != null) {
                adapter.submitList(recipes);
            }
            binding.progressBar.setVisibility(View.INVISIBLE);
        });

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof HomeViewModel.Event.ShowGeneralMessage) {
                String message = ((HomeViewModel.Event.ShowGeneralMessage) event).message;
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            } else if (event instanceof HomeViewModel.Event.NavigateToSearchScreen) {
                try {
                    HomeFragmentListener host = (HomeFragmentListener) requireActivity();
                    host.onSearchClick();
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            } else if (event instanceof HomeViewModel.Event.NavigateToRecipeScreen) {
                Recipe recipe = ((HomeViewModel.Event.NavigateToRecipeScreen) event).recipe;
                NavDirections navDirections = HomeFragmentDirections.actionGlobalRecipeFragment(recipe);
                Navigation.findNavController(requireView()).navigate(navDirections);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}