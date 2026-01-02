package com.example.streamingapp.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AvRecomPagerViewModel extends ViewModel {

    private final MutableLiveData<Integer> nextPage = new MutableLiveData<>();
    private final MutableLiveData<boolean[]> stepValidity =
            new MutableLiveData<>(new boolean[]{false, false, false});

    public LiveData<Integer> getNextPage() {
        return nextPage;
    }

    public LiveData<boolean[]> getStepValidity() {
        return stepValidity;
    }

    public void moveToPage(int page) {
        nextPage.setValue(page);
    }

    public void setStepValid(int step, boolean valid) {
        boolean[] states = stepValidity.getValue();
        if (states == null || step >= states.length) return;

        states[step] = valid;
        stepValidity.setValue(states);
    }
}

