package com.penelope.acousticrecipe.data.recipe;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Recipe implements Serializable {

    private String id;
    private String name;
    private String imageBig;
    private String imageSmall;
    private List<String> ingredients;
    private List<String> manuals;
    private List<String> manualImages;
    private FoodType foodType;
    private CookingMethod cookingMethod;
    private String hashtag;
    private int calories;
    private int carbohydrate;
    private int protein;
    private int fat;
    private int sodium;

    public Recipe() {
    }

    public Recipe(String id, String name, String imageBig, String imageSmall, List<String> ingredients, List<String> manuals, List<String> manualImages, FoodType foodType, CookingMethod cookingMethod, String hashtag, int calories, int carbohydrate, int protein, int fat, int sodium) {
        this.id = id;
        this.name = name;
        this.imageBig = imageBig;
        this.imageSmall = imageSmall;
        this.ingredients = ingredients;
        this.manuals = manuals;
        this.manualImages = manualImages;
        this.foodType = foodType;
        this.cookingMethod = cookingMethod;
        this.hashtag = hashtag;
        this.calories = calories;
        this.carbohydrate = carbohydrate;
        this.protein = protein;
        this.fat = fat;
        this.sodium = sodium;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageBig() {
        return imageBig;
    }

    public String getImageSmall() {
        return imageSmall;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public List<String> getManuals() {
        return manuals;
    }

    public List<String> getManualImages() {
        return manualImages;
    }

    public FoodType getFoodType() {
        return foodType;
    }

    public CookingMethod getCookingMethod() {
        return cookingMethod;
    }

    public String getHashtag() {
        return hashtag;
    }

    public int getCalories() {
        return calories;
    }

    public int getCarbohydrate() {
        return carbohydrate;
    }

    public int getProtein() {
        return protein;
    }

    public int getFat() {
        return fat;
    }

    public int getSodium() {
        return sodium;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageBig(String imageBig) {
        this.imageBig = imageBig;
    }

    public void setImageSmall(String imageSmall) {
        this.imageSmall = imageSmall;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public void setManuals(List<String> manuals) {
        this.manuals = manuals;
    }

    public void setManualImages(List<String> manualImages) {
        this.manualImages = manualImages;
    }

    public void setFoodType(FoodType foodType) {
        this.foodType = foodType;
    }

    public void setCookingMethod(CookingMethod cookingMethod) {
        this.cookingMethod = cookingMethod;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setCarbohydrate(int carbohydrate) {
        this.carbohydrate = carbohydrate;
    }

    public void setProtein(int protein) {
        this.protein = protein;
    }

    public void setFat(int fat) {
        this.fat = fat;
    }

    public void setSodium(int sodium) {
        this.sodium = sodium;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", imageBig='" + imageBig + '\'' +
                ", imageSmall='" + imageSmall + '\'' +
                ", ingredients=" + ingredients +
                ", manuals=" + manuals +
                ", manualImages=" + manualImages +
                ", foodType=" + foodType +
                ", cookingMethod=" + cookingMethod +
                ", hashtag='" + hashtag + '\'' +
                ", calories=" + calories +
                ", carbohydrate=" + carbohydrate +
                ", protein=" + protein +
                ", fat=" + fat +
                ", sodium=" + sodium +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return calories == recipe.calories && carbohydrate == recipe.carbohydrate && protein == recipe.protein && fat == recipe.fat && sodium == recipe.sodium && id.equals(recipe.id) && name.equals(recipe.name) && Objects.equals(imageBig, recipe.imageBig) && Objects.equals(imageSmall, recipe.imageSmall) && ingredients.equals(recipe.ingredients) && manuals.equals(recipe.manuals) && manualImages.equals(recipe.manualImages) && foodType == recipe.foodType && cookingMethod == recipe.cookingMethod && Objects.equals(hashtag, recipe.hashtag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, imageBig, imageSmall, ingredients, manuals, manualImages, foodType, cookingMethod, hashtag, calories, carbohydrate, protein, fat, sodium);
    }
}
