package com.example.chapter3.homework;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.airbnb.lottie.LottieAnimationView;


public class PlaceholderFragment extends Fragment {

    private AnimatorSet animatorSet;
    private LottieAnimationView animationView;
    private ListView listView;

    public static PlaceholderFragment newInstance(String label) {
        Bundle args = new Bundle();
        args.putString("title", label);
        PlaceholderFragment fragment = new PlaceholderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO ex3-3: 修改 fragment_placeholder，添加 loading 控件和列表视图控件
        //**must locate the views here.
        View rootView =  inflater.inflate(R.layout.fragment_placeholder, container, false);
        animationView = rootView.findViewById(R.id.lottie_view);
        listView = rootView.findViewById(R.id.list_view);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().postDelayed(new Runnable() {
            @Override
            public void run() {
                //loading anim fades
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(animationView,"alpha",1,0f);
                animator1.setInterpolator(new LinearInterpolator());
                animator1.setDuration(500);

                //generate the list of friends
                String title = getArguments().getString("title");
                String[] friends = new String[]{"People1", "People2", "People3", "People4", "People5", "People6"};
                for (int i = 0; i < friends.length; i++){
                    friends[i] = title +"-"+ friends[i];
                }
                //appear
                ListAdapter listAdapter = new ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, friends);
                listView.setAdapter(listAdapter);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(listView,"alpha",0,1f);

                animator2.setInterpolator(new LinearInterpolator());
                animator2.setDuration(500);

                animatorSet = new AnimatorSet();
                animatorSet.playTogether(animator1,animator2);
                animatorSet.start();
            }
        }, 5000);
    }
}
