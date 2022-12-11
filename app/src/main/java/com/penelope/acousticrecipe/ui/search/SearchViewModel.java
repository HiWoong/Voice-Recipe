package com.penelope.acousticrecipe.ui.search;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.penelope.acousticrecipe.data.recipe.Recipe;
import com.penelope.acousticrecipe.data.recipe.RecipeRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SearchViewModel extends ViewModel {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final MutableLiveData<String> query = new MutableLiveData<>();
    private final LiveData<List<Recipe>> recipes;

    private final RecipeRepository recipeRepository;


    @Inject
    public SearchViewModel(RecipeRepository recipeRepository) {

        recipes = Transformations.switchMap(query, recipeRepository::searchRecipes);

        this.recipeRepository = recipeRepository;
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public LiveData<List<Recipe>> getRecipes() {
        return recipes;
    }

    public LiveData<String> getQuery() {
        return query;
    }


    public void onVoiceRecognized(List<String> words) {

        if (words == null) {
            event.setValue(new Event.ShowGeneralMessage("음성 인식에 실패했습니다"));
            return;
        }

        if (words.isEmpty() || words.get(0).isEmpty()) {
            event.setValue(new Event.ShowGeneralMessage("인식된 음성이 없습니다"));
            return;
        }

        String word = words.get(0);

        query.setValue(word);

        event.setValue(new Event.ShowLoadingUI());
    }

    public void onSearchClick() {
        event.setValue(new Event.PromptVoice());
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

        public static class ShowLoadingUI extends Event {
        }

        public static class PromptVoice extends Event {
        }

        public static class NavigateToRecipeScreen extends Event {
            public final Recipe recipe;
            public NavigateToRecipeScreen(Recipe recipe) {
                this.recipe = recipe;
            }
        }
    }

}