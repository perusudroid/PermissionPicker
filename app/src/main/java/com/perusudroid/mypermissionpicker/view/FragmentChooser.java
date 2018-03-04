package com.perusudroid.mypermissionpicker.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.perusudroid.mypermissionpicker.R;

/**
 * Created by perusu on 26/2/18.
 */

public class FragmentChooser extends BottomSheetDialogFragment implements View.OnClickListener {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.inflater_chooser, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.camLay).setOnClickListener(this);
        view.findViewById(R.id.galLay).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.camLay:
                ((MainActivity) getActivity()).cameraClicked();
                break;
            case R.id.galLay:
                ((MainActivity) getActivity()).galleryClicked();
                break;
        }
    }

}
