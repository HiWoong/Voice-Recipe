package com.penelope.acousticrecipe.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.penelope.acousticrecipe.api.recipe.RecipeApi;
import com.penelope.acousticrecipe.data.recipe.Recipe;
import com.penelope.acousticrecipe.data.recipe.RecipeRepository;
import com.penelope.acousticrecipe.data.recommended.Recommended;
import com.penelope.acousticrecipe.data.recommended.RecommendedRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HomeViewModel extends ViewModel {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final LiveData<List<Recipe>> recommendedRecipes;

    private final RecipeRepository recipeRepository;


    @Inject
    public HomeViewModel(RecipeRepository recipeRepository, RecommendedRepository recommendedRepository) {

        this.recipeRepository = recipeRepository;

        fetchRecipes();

        recommendedRepository.updateRecommended();

        LiveData<Recommended> recommended = recommendedRepository.getRecommended();
        recommendedRecipes = Transformations.switchMap(recommended, rec ->
                recipeRepository.getRecipes(rec.getItems())
        );
    }

    private void fetchRecipes() {

        recipeRepository.getAllRecipes(
                recipes -> {
                    if (recipes.size() < 100) {
                        new Thread(() -> {
                            List<Recipe> newRecipes = RecipeApi.get();
                            if (newRecipes != null) {
                                for (Recipe recipe : newRecipes) {
                                    recipeRepository.addRecipe(recipe, unused -> {
                                    }, Throwable::printStackTrace);
                                }
                            }
                        }).start();
                    }
                },
                Throwable::printStackTrace
        );
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public LiveData<List<Recipe>> getRecommendedRecipes() {
        return recommendedRecipes;
    }


    public void onSearchClick() {
        event.setValue(new Event.NavigateToSearchScreen());
    }

    public void onRecipeClick(Recipe recipe) {
        event.setValue(new Event.NavigateToRecipeScreen(recipe));
    }


    public static class Event {

        public static class ShowGeneralMessage extends Event {
            public final String message;

            public ShowGeneralMessage(String message) {
                this.message = message;
            }
        }

        public static class NavigateToSearchScreen extends Event {
        }

        public static class NavigateToRecipeScreen extends Event {
            public final Recipe recipe;

            public NavigateToRecipeScreen(Recipe recipe) {
                this.recipe = recipe;
            }
        }

    }

}