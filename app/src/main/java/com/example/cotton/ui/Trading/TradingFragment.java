package com.example.cotton.ui.Trading;

import android.os.Bundle;
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

public class TradingFragment extends Fragment {

    ChipGroup trading_header_chip_group; // 칩 그룹
    SearchView trading_header_search_view; // 검색 창

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_trading, container, false);


        //region UI 선언 부
        trading_header_chip_group = root.findViewById(R.id.trading_header_chip_group);
        trading_header_search_view = root.findViewById(R.id.trading_header_search_view);
        //endregion



        // 기능 부
        /**
         * 검색버을 누를 시
         * - Chip중에서 검색어와 같은 Chip이 있는지 확인
         * A. 검색어를 포함한 Chip이 없을 경우
         * - ChipGroup에 Chip을 추가.
         * - SearchView을 초기화.
         * B. 검색어를 포함한 Chip이 있을 경우
         * - SearchView을 초기화.
         */
        trading_header_search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // ChipGroup에서 Chip을 하나하나씩 참조한다.
                int numberOfChip = trading_header_chip_group.getChildCount();
                for(int i = 0; i < numberOfChip; i++) {
                    Chip chipChild = (Chip)trading_header_chip_group.getChildAt(i);

                    // 하나라도 겹치면 break
                    if(chipChild.getText().toString().equals(query)) {
                        trading_header_search_view.setQuery("", false);
                        break;
                    }

                    // 하나도 겹치지 않을 경우 Chip 추가.
                    else if (i == numberOfChip - 1){
                        // 칩 추가 & 검색창 초기화.
                        addChipToChipGroup(query);
                        trading_header_search_view.setQuery("", false);
                    }
                }

                // ChipGroup에 Chip이 하나도 없을 경우에는 그냥 추가.
                if(trading_header_chip_group.getChildCount() == 0) {
                    // 칩 추가 & 검색창 초기화.
                    addChipToChipGroup(query);
                    trading_header_search_view.setQuery("", false);
                }
                return false;
            }

            // 아래는 필요 없는 코드
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        // End of: trading_header_search_view.setOnQueryTextListener

        return root;
    }


    /**
     * ChipGroup에 Chip을 추가합니다.
     * @param chipText : Chip에 들어갈 텍스트.
     */
    private void addChipToChipGroup(String chipText) {
        Chip chip = new Chip(getContext());
        chip.setText(chipText);
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
    }

}
