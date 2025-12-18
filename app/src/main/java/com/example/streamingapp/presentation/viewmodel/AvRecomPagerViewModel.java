package com.example.streamingapp.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AvRecomPagerViewModel extends ViewModel {

    private final MutableLiveData<Integer> nextPage = new MutableLiveData<>();
    private final MutableLiveData<int[]> stepValidity =
            new MutableLiveData<>(new int[]{0, 0, 0}); // 0 = invalid, 1 = valid

    public void moveToPage(int page) {
        nextPage.setValue(page);
    }

    public LiveData<Integer> getNextPage() {
        return nextPage;
    }

    public void setStepValid(int step, boolean valid) {
        int[] states = stepValidity.getValue();
        if (states == null) return;
        states[step] = valid ? 1 : 0;
        stepValidity.setValue(states);
    }

    public LiveData<Boolean> isStepValid(int step) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        int[] states = stepValidity.getValue();
        result.setValue(states != null && states[step] == 1);
        return result;
    }
}
