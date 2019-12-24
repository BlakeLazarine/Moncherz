package com.example.moncherz.ui.BPlate;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BPlateViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public BPlateViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is BPlate fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}