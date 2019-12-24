package com.example.moncherz.ui.DeNeve;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DeNeveViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DeNeveViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is tools fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}