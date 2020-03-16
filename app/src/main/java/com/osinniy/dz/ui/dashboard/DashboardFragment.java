package com.osinniy.dz.ui.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.osinniy.dz.R;
import com.osinniy.dz.obj.dz.DZ;
import com.osinniy.dz.util.NavProvider;
import com.osinniy.dz.util.OnItemClickListener;

public class DashboardFragment extends Fragment implements OnItemClickListener<DZ> {

    private View view;
    private Navigator nav;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        nav = ((NavProvider) context).getNavigator();
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        nav = null;
    }


//TODO try to realize this

//    private void initRecyclerView() {
//        RecyclerView recyclerView = new MainActivity().findViewById(R.id.dz_recycler);
//        adapter = new DZAdapter(getLayoutInflater(), this);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(
//                new LinearLayoutManager(new MainActivity().get(), RecyclerView.VERTICAL, false));
//    }


    @Override
    public void onItemClicked(DZ item) {

    }


    public interface Navigator {

    }

}
