package com.penelope.acousticrecipe.data.recipe;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class RecipeRepository {

    private final CollectionReference recipeCollection;

    @Inject
    public RecipeRepository(FirebaseFirestore firestore) {
        recipeCollection = firestore.collection("recipes");
    }

    public void addRecipe(Recipe recipe, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {

        recipeCollection.document(recipe.getId())
                .set(recipe)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void getAllRecipes(OnSuccessListener<List<Recipe>> onSuccessListener, OnFailureListener onFailureListener) {

        recipeCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    List<Recipe> recipes = new ArrayList<>();

                    if (queryDocumentSnapshots == null || queryDocumentSnapshots.isEmpty()) {
                        onSuccessListener.onSuccess(recipes);
                        return;
                    }

                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Recipe recipe = snapshot.toObject(Recipe.class);
                        if (recipe != null) {
                            recipes.add(recipe);
                        }
                    }
                    onSuccessListener.onSuccess(recipes);
                })
                .addOnFailureListener(onFailureListener);
    }

    public void searchRecipes(String query, OnSuccessListener<List<Recipe>> onSuccessListeners, OnFailureListener onFailureListener) {

        getAllRecipes(
                recipes -> {
                    List<Recipe> filtered = new ArrayList<>();
                    for (Recipe recipe : recipes) {
                        if (recipe.getName().contains(query)) {
                            filtered.add(recipe);
                        }
                    }
                    onSuccessListeners.onSuccess(filtered);
                },
                onFailureListener
        );
    }

    public LiveData<List<Recipe>> searchRecipes(String query) {

        MutableLiveData<List<Recipe>> recipes = new MutableLiveData<>();
        searchRecipes(query,
                recipes::setValue,
                e -> {
                    e.printStackTrace();
                    recipes.setValue(null);
                });
        return recipes;
    }

    public void getRecipeMap(OnSuccessListener<Map<String, Recipe>> onSuccessListener, OnFailureListener onFailureListener) {

        getAllRecipes(
                recipes -> {
                    Map<String, Recipe> map = new HashMap<>();
                    for (Recipe recipe : recipes) {
                        map.put(recipe.getId(), recipe);
                    }
                    onSuccessListener.onSuccess(map);
                },
                onFailureListener
        );
    }

    public void getRecipes(List<String> ids, OnSuccessListener<List<Recipe>> onSuccessListener, OnFailureListener onFailureListener) {

        getRecipeMap(
                map -> {
                    List<Recipe> recipeList = new ArrayList<>();
                    for (String id : ids) {
                        Recipe recipe = map.get(id);
                        if (recipe != null) {
                            recipeList.add(recipe);
                        }
                    }
                    onSuccessListener.onSuccess(recipeList);
                },
                onFailureListener
        );
    }

    public LiveData<List<Recipe>> getRecipes(List<String> ids) {

        MutableLiveData<List<Recipe>> recipes = new MutableLiveData<>();
        getRecipes(ids, recipes::setValue, e -> recipes.setValue(null));
        return recipes;
    }

}
