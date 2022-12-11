package com.penelope.acousticrecipe.ui.history;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.penelope.acousticrecipe.data.recipe.Recipe;
import com.penelope.acousticrecipe.data.recipe.RecipeRepository;
import com.penelope.acousticrecipe.utils.PrefUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HistoryViewModel extends ViewModel {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final MutableLiveData<List<String>> idsOfVisitedRecipes = new MutableLiveData<>();
    private final LiveData<List<Recipe>> visitedRecipes;

    private final Application application;


    @Inject
    public HistoryViewModel(Application application, RecipeRepository recipeRepository) {

        visitedRecipes = Transformations.switchMap(idsOfVisitedRecipes, recipeRepository::getRecipes);

        this.application = application;
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public LiveData<List<Recipe>> getVisitedRecipes() {
        return visitedRecipes;
    }


    public void onRefresh() {
        idsOfVisitedRecipes.setValue(PrefUtils.getVisitedRecipes(application));
    }

    public void onRecipeClick(Recipe recipe) {
        event.setValue(new Event.NavigateToRecipeScreen(recipe));
    }


    public static class Event {

        public static class NavigateToRecipeScreen extends Event {
            public final Recipe recipe;

            public NavigateToRecipeScreen(Recipe recipe) {
                this.recipe = recipe;
            }
        }
    }

}