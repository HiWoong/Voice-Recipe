package com.penelope.acousticrecipe.ui.manual;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.penelope.acousticrecipe.data.recipe.Recipe;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ManualViewModel extends ViewModel {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final Recipe recipe;

    private final MutableLiveData<Integer> index = new MutableLiveData<>(0);
    private final LiveData<String> manual;
    private final LiveData<String> manualImage;
    private final LiveData<Integer> minutes;

    private final MutableLiveData<Boolean> isReady = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isPlaying = new MutableLiveData<>(false);
    private final LiveData<String> script;

    private final List<String> manuals;
    private final List<String> ingredients;


    @Inject
    public ManualViewModel(SavedStateHandle savedStateHandle) {

        recipe = savedStateHandle.get("recipe");
        assert recipe != null;

        manuals = recipe.getManuals();
        ingredients = recipe.getIngredients();
        List<String> manualImages = recipe.getManualImages();

        manual = Transformations.map(index, manuals::get);
        manualImage = Transformations.map(index, manualImages::get);

        script = Transformations.switchMap(isReady, ready ->
                Transformations.switchMap(isPlaying, playing ->
                        Transformations.map(manual, strManual -> {
                            if (ready && playing) {
                                return strManual;
                            } else {
                                return null;
                            }
                        })
                ));

        minutes = Transformations.map(manual, manualValue -> {
            Pattern pattern = Pattern.compile("([0-9]+)\\s*분");
            Matcher matcher = pattern.matcher(manualValue);
            if (matcher.find()) {
                String strMinutes = matcher.group(1);
                if (strMinutes != null) {
                    try {
                        return Integer.parseInt(strMinutes);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        });
    }


    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public LiveData<String> getManual() {
        return manual;
    }

    public LiveData<String> getManualImage() {
        return manualImage;
    }

    public LiveData<Integer> getIndex() {
        return index;
    }

    public int getNumOfSteps() {
        return manuals.size();
    }

    public LiveData<String> getScript() {
        return script;
    }

    public LiveData<Boolean> isPlaying() {
        return isPlaying;
    }

    public LiveData<Boolean> isReady() {
        return isReady;
    }

    public LiveData<Integer> getMinutes() {
        return minutes;
    }


    public void onPrevClick() {

        Integer indexValue = index.getValue();
        assert indexValue != null;

        if (indexValue > 0) {
            index.setValue(indexValue - 1);
        }
    }

    public void onNextClick() {

        Integer indexValue = index.getValue();
        assert indexValue != null;

        if (indexValue < manuals.size() - 1) {
            index.setValue(indexValue + 1);
        }
    }

    public void onSignClick(int position) {
        index.setValue(position);
    }

    public void onPlayStopClick() {

        Boolean isReadyValue = isReady.getValue();
        Boolean isPlayingValue = isPlaying.getValue();
        assert isPlayingValue != null && isReadyValue != null;

        if (isReadyValue) {
            isPlaying.setValue(!isPlayingValue);
        }
    }

    public void onSpeechReady() {
        isReady.setValue(true);
        isPlaying.setValue(true);
    }

    public void onSpeechNotAvailable() {
        event.setValue(new Event.ShowGeneralMessage("음성 재생이 지원되지 않습니다"));
    }

    public void onSpeechDone() {

        Integer indexValue = index.getValue();
        assert indexValue != null;

        if (indexValue < manuals.size() - 1) {
            event.postValue(new Event.InformNextManual());
            index.postValue(indexValue + 1);
        }
    }

    public void onVoiceClick() {
        event.setValue(new Event.PromptVoice());
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
        Boolean isPlayingValue = isPlaying.getValue();
        assert isPlayingValue != null;

        if (word.contains("시작")) {
            if (!isPlayingValue) {
                onPlayStopClick();
            }
        } else if (word.contains("정지")) {
            if (isPlayingValue) {
                onPlayStopClick();
            }
        } else if (word.contains("이전")) {
            onPrevClick();
            if (!isPlayingValue) {
                onPlayStopClick();
            }
        } else if (word.contains("다음")) {
            onNextClick();
            if (!isPlayingValue) {
                onPlayStopClick();
            }
        } else {
            int indexFound = searchIngredient(words.get(0));

            if (indexFound != -1) {
                index.setValue(indexFound);
                if (!isPlayingValue) {
                    onPlayStopClick();
                }
            }
        }
    }

    private int searchIngredient(String query) {

        boolean isValid = false;

        for (String ingredient: ingredients) {
            if (ingredient.contains(query)) {
                isValid = true;
                break;
            }
        }

        if (!isValid) {
            event.setValue(new Event.ShowGeneralMessage("레시피에 포함되지 않는 재료명입니다"));
            return -1;
        }

        for (int i = 0; i < manuals.size(); i++) {
            if (manuals.get(i).contains(query)) {
                return i;
            }
        }

        return -1;
    }

    public void onTimerClick() {

        Integer minutesValue = minutes.getValue();
        if (minutesValue == null || minutesValue < 1) {
            return;
        }

        event.setValue(new Event.PromptTimerDuration(minutesValue));
    }

    public void onTimerSubmit(int minutes) {
        if (minutes > 0) {
            event.setValue(new Event.StartTimer(minutes, recipe.getName(), manual.getValue()));
        } else {
            event.setValue(new Event.ShowGeneralMessage("올바른 시간을 입력하세요"));
        }
    }


    public static class Event {

        public static class InformNextManual extends Event {
            public InformNextManual() {
            }
        }

        public static class ShowGeneralMessage extends Event {
            public final String message;

            public ShowGeneralMessage(String message) {
                this.message = message;
            }
        }

        public static class PromptTimerDuration extends Event {
            public final int minutes;
            public PromptTimerDuration(int minutes) {
                this.minutes = minutes;
            }
        }

        public static class StartTimer extends Event {
            public final int minutes;
            public final String foodName;
            public final String manual;

            public StartTimer(int minutes, String foodName, String manual) {
                this.minutes = minutes;
                this.foodName = foodName;
                this.manual = manual;
            }
        }

        public static class PromptVoice extends Event {
        }
    }

}