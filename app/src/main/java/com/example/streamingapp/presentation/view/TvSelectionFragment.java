package com.example.streamingapp.presentation.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.streamingapp.R;
import com.example.streamingapp.data.model.Programme;
import com.example.streamingapp.data.model.TvChannel;
import com.example.streamingapp.data.model.TvChannelUiItem;
import com.example.streamingapp.databinding.FragmentTvSelectionBinding;
import com.example.streamingapp.presentation.adapter.TvSelectionRecItemAdapter;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.List;


public class TvSelectionFragment extends Fragment {
    private FragmentTvSelectionBinding binding;
    private StreamingViewModel vm;
    private TvSelectionRecItemAdapter tvSelectionRecItemAdapter;
    private List<TvChannel> tvChannelList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentTvSelectionBinding.inflate(inflater, container, false);

        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory())
                .get(StreamingViewModel.class);

        binding.recVTvNames.setLayoutManager(new LinearLayoutManager(requireContext()));

        tvSelectionRecItemAdapter =  new TvSelectionRecItemAdapter(
                requireContext(),
                item -> {
                    TvProgramFragment frag = new TvProgramFragment();
                    Bundle b = new Bundle();
                    b.putString("tvName", item.getChannelName());
                    frag.setArguments(b);

                    getParentFragmentManager()
                            .beginTransaction()
                            .replace(R.id.tvFrameLayout, frag)
                            .addToBackStack(null)
                            .commit();
                }
        );

        binding.recVTvNames.setAdapter(tvSelectionRecItemAdapter);
        vm.loadTvItems();
        vm.getTvLiveData().observe(getViewLifecycleOwner(), items ->{
            tvChannelList = items;
            tvSelectionRecItemAdapter.submitList(mapChannelsToUi(items));
        });

        binding.recVTvNames.setHasFixedSize(true);

        return binding.getRoot();
    }

    public List<TvChannelUiItem> mapChannelsToUi(List<TvChannel> channels) {
        List<TvChannelUiItem> uiList = new ArrayList<>();

        for (TvChannel channel : channels) {
            if (channel.getProgrammes() != null && !channel.getProgrammes().isEmpty()) {
                Programme current = null;
                for (Programme p : channel.getProgrammes()) {
                    if ("live".equalsIgnoreCase(p.getStatus())) {
                        current = p;
                        break;
                    }
                }
                if (current == null) current = channel.getProgrammes().get(0); // fallback

                uiList.add(new TvChannelUiItem(
                        channel.getChannelLogo(),
                        channel.getChannelName(),
                        current.getName(),
                        current.getTiming(),
                        current.getUrl(),
                        current.getStatus()
                ));
            }
        }

        return uiList;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // avoid memory leaks
    }
}