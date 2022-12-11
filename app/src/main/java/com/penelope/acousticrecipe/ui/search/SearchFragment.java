package com.penelope.acousticrecipe.ui.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.penelope.acousticrecipe.R;
import com.penelope.acousticrecipe.data.recipe.Recipe;
import com.penelope.acousticrecipe.databinding.FragmentSearchBinding;
import com.penelope.acousticrecipe.ui.home.RecipesAdapter;

import java.util.ArrayList;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private SearchViewModel viewModel;

    private ActivityResultLauncher<Intent> voiceRecognitionLauncher;


    public SearchFragment() {
        super(R.layout.fragment_search);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        voiceRecognitionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (result.getResultCode() == Activity.RESULT_OK && data != null) {
                        ArrayList<String> words = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        viewModel.onVoiceRecognized(words);
                    }
                }
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentSearchBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        binding.fabSearch.setOnClickListener(v -> viewModel.onSearchClick());

        RecipesAdapter adapter = new RecipesAdapter(Glide.with(this));
        binding.recyclerRecipe.setAdapter(adapter);

        adapter.setOnItemSelectedListener(position -> {
            Recipe recipe = adapter.getCurrentList().get(position);
            viewModel.onRecipeClick(recipe);
        });

        viewModel.getRecipes().observe(getViewLifecycleOwner(), recipes -> {
            if (recipes != null) {
                adapter.submitList(recipes);
                String strResultNumber = String.format(Locale.getDefault(), "%d개의 레시피가 검색되었습니다", recipes.size());
                binding.textViewSearchResultNumber.setText(strResultNumber);
                binding.textViewSearchResultNumber.setVisibility(recipes.isEmpty() ? View.INVISIBLE : View.VISIBLE);
                binding.textViewNoSearchResults.setVisibility(recipes.isEmpty() ? View.VISIBLE : View.INVISIBLE);
            }
            binding.progressBar2.setVisibility(View.INVISIBLE);
        });

        viewModel.getQuery().observe(getViewLifecycleOwner(), query -> {
            String strQuery = String.format(Locale.getDefault(), "'%s' 검색 결과", query);
            binding.textViewSearchQuery.setText(strQuery);
        });

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof SearchViewModel.Event.ShowGeneralMessage) {
                String message = ((SearchViewModel.Event.ShowGeneralMessage) event).message;
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            } else if (event instanceof SearchViewModel.Event.ShowLoadingUI) {
                binding.progressBar2.setVisibility(View.VISIBLE);
            } else if (event instanceof SearchViewModel.Event.PromptVoice) {
                showVoiceRecognitionDialog();
            } else if (event instanceof SearchViewModel.Event.NavigateToRecipeScreen) {
                Recipe recipe = ((SearchViewModel.Event.NavigateToRecipeScreen) event).recipe;
                NavDirections navDirections = SearchFragmentDirections.actionGlobalRecipeFragment(recipe);
                Navigation.findNavController(requireView()).navigate(navDirections);
            }
        });

        showVoiceRecognitionDialog();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showVoiceRecognitionDialog() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "요리 이름을 말씀해주세요");

        voiceRecognitionLauncher.launch(intent);
    }
}










