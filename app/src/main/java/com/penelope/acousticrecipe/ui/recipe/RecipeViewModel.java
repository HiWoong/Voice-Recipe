package com.penelope.acousticrecipe.ui.recipe;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.penelope.acousticrecipe.data.recipe.Recipe;
import com.penelope.acousticrecipe.utils.PrefUtils;

import java.util.HashSet;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RecipeViewModel extends ViewModel {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final Recipe recipe;


    @Inject
    public RecipeViewModel(Application application, SavedStateHandle savedStateHandle) {

        recipe = savedStateHandle.get("recipe");
        assert recipe != null;

        PrefUtils.addVisitedRecipe(application, recipe.getId());
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public Recipe getRecipe() {
        return recipe;
    }


    public void onManualClick() {
        event.setValue(new Event.NavigateToManualScreen(recipe));
    }


    public static class Event {

        public static class NavigateToManualScreen extends Event {
            public final Recipe recipe;
            public NavigateToManualScreen(Recipe recipe) {
                this.recipe = recipe;
            }
        }
    }

}