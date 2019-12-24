package com.example.moncherz.ui.Covel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CovelViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CovelViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Covel fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}