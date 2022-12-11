package com.penelope.acousticrecipe.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.function.BiConsumer;

public class BaseRepository<T> {

    public LiveData<T> getLiveData(BiConsumer<OnSuccessListener<T>, OnFailureListener> consumer) {
        MutableLiveData<T> liveData = new MutableLiveData<>();
        consumer.accept(liveData::setValue, e -> liveData.setValue(null));
        return liveData;
    }
    


}
