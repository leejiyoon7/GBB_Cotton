package com.example.cotton.ui.Trading;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.cotton.MainActivity;
import com.example.cotton.R;
import com.example.cotton.ui.home.register.RegisterBookActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;

public class TradingFragment extends Fragment {

    ChipGroup trading_header_chip_group; // 칩 그룹
    SearchView trading_header_search_view; // 검색 창
    Spinner trading_title_department_spinner;//스피너
    ArrayAdapter spinnerAdapter;//스피너 어댑터

    ViewPager trading_content_view_pager;
    TradingViewPagerAdapter pagerAdapter;

    String major;//전공 value

    AppCompatButton trading_rent_button;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_trading, container, false);

        //region UI 선언 부
        trading_header_chip_group = view.findViewById(R.id.trading_header_chip_group);
        trading_header_search_view = view.findViewById(R.id.trading_header_search_view);
        trading_title_department_spinner=view.findViewById(R.id.trading_title_department_spinner);
        trading_content_view_pager=(ViewPager) view.findViewById(R.id.trading_content_view_pager);
        trading_rent_button=view.findViewById(R.id.trading_rent_button);
        //endregion

        pagerAdapter=new TradingViewPagerAdapter(getChildFragmentManager());

        searchBook();//search 기능

        majorPickSpinner();//전공 선택 스피너

        TradingViewPagerFunc();//뷰 페이저 관련 함수

        tradingRentButtonClickEvent();//대여하기 버튼 클릭 이벤트

        return view;
    }
    
    // 기능 부
    /**
     * 검색버튼을 누를 시
     * - Chip중에서 검색어와 같은 Chip이 있는지 확인
     * A. 검색어를 포함한 Chip이 없을 경우
     * - ChipGroup에 Chip을 추가.
     * - SearchView을 초기화.
     * B. 검색어를 포함한 Chip이 있을 경우
     * - SearchView을 초기화.
     */
    public void searchBook(){

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

    //spinner 구현 method
    public void majorPickSpinner(){
        String[] items=getResources().getStringArray(R.array.major);
        spinnerAdapter= new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_dropdown_item,items);
        trading_title_department_spinner.setAdapter(spinnerAdapter);

        trading_title_department_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                switch(position){
                    case 0:
                        Toast.makeText(getActivity(),"컴퓨터공학과 선택",Toast.LENGTH_SHORT).show();
                        major="컴퓨터공학과";
                        break;
                    case 1:
                        Toast.makeText(getActivity(),"전자공학과 선택",Toast.LENGTH_SHORT).show();
                        major="전자공학과";
                        break;
                    case 2:
                        Toast.makeText(getActivity(),"전기공학과 선택",Toast.LENGTH_SHORT).show();
                        major="전기공학과";
                        break;
                    case 3:
                        Toast.makeText(getActivity(),"AI.소프트웨어학부 선택",Toast.LENGTH_SHORT).show();
                        major="AI.소프트웨어학부";
                        break;
                    case 4:
                        Toast.makeText(getActivity(),"에너지IT학과",Toast.LENGTH_SHORT).show();
                        major="에너지IT학과";
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getActivity(),"아무것도 선택되지 않음",Toast.LENGTH_SHORT).show();
            }
        });
    }

    //뷰 페이저 관련 함수
    public void TradingViewPagerFunc(){

        //추후 파이어베이스 참조해서 구현할때 여기서 책의 개수만큼 프래그먼트를 반복문으로 제작, 필요 시 대여자 정보, 및 각종 정보를 method를 변형해서 추가 가능
        // add your fragments
        pagerAdapter.addFragment(R.drawable.book_imsi,"종이 여자1","기욤 뮈소1");

        pagerAdapter.addFragment(R.drawable.book_imsi,"종이 여자2","기욤 뮈소2");

        pagerAdapter.addFragment(R.drawable.book_imsi,"종이 여자3","기욤 뮈소3");

        pagerAdapter.addFragment(R.drawable.book_imsi,"종이 여자4","기욤 뮈소4");

        trading_content_view_pager.setAdapter(pagerAdapter);

        pagerAdapter.notifyDataSetChanged();

        trading_content_view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                //fragment의 개수에 따라 메소드 라이브러리 사용해야합니다.
                Toast.makeText(getActivity(),"onPageSelected 현재 페이지: "+position, Toast.LENGTH_SHORT).show();
                
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //대여하기 버튼 클릭 이벤트
    public void tradingRentButtonClickEvent(){
        //구매 버튼 누를 시 MainActivity로 이동
        trading_rent_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getActivity(),"대여하기 버튼 클릭됨",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

}
