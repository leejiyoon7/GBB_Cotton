package com.example.cotton.ui.Exchange;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.cotton.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;

public class ExchangeFragment extends Fragment {

    ChipGroup trading_header_chip_group; // 칩 그룹
    SearchView trading_header_search_view; // 검색 창

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_trading, container, false);


        /// UI 선업부 ///
        trading_header_chip_group = root.findViewById(R.id.trading_header_chip_group);
        trading_header_search_view = root.findViewById(R.id.trading_header_search_view);



        // 기능 부
        /**
         * 검색버을 누를 시
         * - ChipGroup에 Chip을 추가.
         * - SearchView을 초기화.
         */
        trading_header_search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Chip chip = new Chip(getContext());
                chip.setText(trading_header_search_view.getQuery());
                chip.setCheckable(false);
                // Chip 스타일 적용
                ChipDrawable drawable = ChipDrawable.createFromAttributes(getContext(), null, 0, R.style.Widget_MaterialComponents_Chip_Entry);
                chip.setChipDrawable(drawable);
                // Chip 닫기를 눌러서 ChipGroup에서 삭제.
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        trading_header_chip_group.removeView(chip);
                    }
                });
                trading_header_chip_group.addView(chip);
                trading_header_search_view.setQuery("", false);
                return false;
            }

            // 아래는 필요 없는 코드
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return root;
    }

}
