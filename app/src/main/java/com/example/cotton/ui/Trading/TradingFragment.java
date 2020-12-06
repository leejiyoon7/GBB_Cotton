package com.example.cotton.ui.Trading;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.cotton.MainActivity;
import com.example.cotton.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class TradingFragment extends Fragment {

    ChipGroup trading_header_chip_group; // 칩 그룹
    Chip trading_hear_chip_class; //칩(강좌)
    Chip trading_hear_chip_professor; //칩(교수)
    Chip trading_hear_chip_book; //칩(책)
    SearchView trading_header_search_view; // 검색 창
    Spinner trading_title_department_spinner;//스피너
    ArrayAdapter spinnerAdapter;//스피너 어댑터

    ViewPager trading_content_view_pager;
    TradingViewPagerAdapter pagerAdapter;

    String major;//전공 value
    String bookTitle;//전공 책 제목
    String bookAuthor;//전공 책 작가

    ArrayList<TradingViewPagerItem> tradingViewPagerItems=new ArrayList<TradingViewPagerItem>();


    AppCompatButton trading_rent_button;//대여 버튼


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_trading, container, false);

        //region UI 선언 부
        trading_header_chip_group = view.findViewById(R.id.trading_header_chip_group);
        trading_header_search_view = view.findViewById(R.id.trading_header_search_view);
        trading_title_department_spinner=view.findViewById(R.id.trading_title_department_spinner);
        trading_content_view_pager=(ViewPager) view.findViewById(R.id.trading_content_view_pager);
        trading_rent_button=view.findViewById(R.id.trading_rent_button);
        trading_hear_chip_class = view.findViewById(R.id.trading_hear_chip_class);
        trading_hear_chip_professor = view.findViewById(R.id.trading_hear_chip_professor);
        trading_hear_chip_book = view.findViewById(R.id.trading_hear_chip_book);

        //endregion

        //뷰페이저 어댑터
        pagerAdapter=new TradingViewPagerAdapter(getChildFragmentManager());

        tradingViewPagerItems=new ArrayList<TradingViewPagerItem>();

        // Chip 초기 설정
        setChipOption(trading_hear_chip_class);
        setChipOption(trading_hear_chip_professor);
        setChipOption(trading_hear_chip_book);


//        searchBook();//search 기능

        majorPickSpinner();//전공 선택 스피너

        return view;
    }
    
    // 기능 부
    /**
     * 칩에 검색내용이 비어있는지 확인하고
     * 칩이 비어있을 경우 SearchView를 숨김.
     */
    private void hideSearchViewIfChipIsEmpty() {
        if (trading_hear_chip_class.getText().equals("강좌명: ") &&
                trading_hear_chip_professor.getText().equals("교수명: ") &&
                trading_hear_chip_book.getText().equals("도서명: ")) {
            trading_header_search_view.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 칩 초기설정을 추가.
     * A. Chip을 누르면 SearchView가 보여짐.
     * B. 검색을 완료하면 Chip에 검색어 등록.
     * C. Chip 닫기를 누르면 Chip에 포함된 검색어가 사라짐.
     * @param chip: ChipGroup에 등록 된 칩.
     */
    private void setChipOption(Chip chip) {
        chip.setOnClickListener(new View.OnClickListener() {
            /*A. Chip을 누르면 SearchView가 보여짐.*/
            @Override
            public void onClick(View v) {
                // SearchView 검색 힌트 등록.
                String queryHintWord = "";
                if (chip.equals(trading_hear_chip_class)) {
                    queryHintWord = "강좌명";
                }
                else if (chip.equals(trading_hear_chip_professor)) {
                    queryHintWord = "교수명";
                }
                else if (chip.equals(trading_hear_chip_book)) {
                    queryHintWord = "도서명";
                }
                trading_header_search_view.setQueryHint(queryHintWord + "을 입력해주세요.");
                trading_header_search_view.setVisibility(View.VISIBLE);
                trading_header_search_view.requestFocus();
                // 키보드 열기
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                /*B. 검색을 완료하면 Chip에 검색어 등록.*/
                trading_header_search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        if (chip.equals(trading_hear_chip_class)) {
                            chip.setText("강좌명: " + query);
                        }
                        else if (chip.equals(trading_hear_chip_professor)) {
                            chip.setText("교수명: " + query);
                        }
                        else if (chip.equals(trading_hear_chip_book)) {
                            chip.setText("도서명: " + query);
                        }
                        trading_header_search_view.setQuery("", false);
                        trading_header_search_view.setVisibility(View.INVISIBLE);
                        return false;
                    }
                    // 아래는 필요 없는 코드
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });
            }
        }); //End of: chip.setOnClickListener

        /*C. Chip 닫기를 누르면 Chip에 포함된 검색어가 사라짐.*/
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chip.equals(trading_hear_chip_class)) {
                    chip.setText("강좌명: ");
                }
                else if (chip.equals(trading_hear_chip_professor)) {
                    chip.setText("교수명: ");
                }
                else if (chip.equals(trading_hear_chip_book)) {
                    chip.setText("도서명: ");
                }
                hideSearchViewIfChipIsEmpty();
            }
        });
    } // End of: setChipOption


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
                        major="컴퓨터공학과";
                        TradingViewPagerFunc(major);//뷰 페이저 관련 함수
                        break;
                    case 1:
                        major="전자공학과";
                        TradingViewPagerFunc(major);
                        break;
                    case 2:
                        major="전기공학과";
                        TradingViewPagerFunc(major);
                        break;
                    case 3:
                        major="AI.소프트웨어학부";
                        TradingViewPagerFunc(major);
                        break;
                    case 4:
                        major="에너지IT학과";
                        TradingViewPagerFunc(major);
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
    public void TradingViewPagerFunc(String _major){

        //추후 파이어베이스 참조해서 구현할때 여기서 책의 개수만큼 프래그먼트를 반복문으로 제작, 필요 시 대여자 정보, 및 각종 정보를 method를 변형해서 추가 가능
        // add your fragments
        pagerAdapter.addFragment(R.drawable.book_imsi,"종이 여자1","기욤 뮈소1");
        addItem(_major, R.drawable.book_imsi,"종이 여자1","기욤 뮈소1");

        pagerAdapter.addFragment(R.drawable.book_imsi,"종이 여자2","기욤 뮈소2");
        addItem(_major, R.drawable.book_imsi,"종이 여자2","기욤 뮈소2");

        pagerAdapter.addFragment(R.drawable.book_imsi,"종이 여자3","기욤 뮈소3");
        addItem(_major, R.drawable.book_imsi,"종이 여자3","기욤 뮈소3");

        pagerAdapter.addFragment(R.drawable.book_imsi,"종이 여자4","기욤 뮈소4");
        addItem(_major, R.drawable.book_imsi,"종이 여자4","기욤 뮈소4");

        trading_content_view_pager.setAdapter(pagerAdapter);

        pagerAdapter.notifyDataSetChanged();

        //뷰페이저 스크롤을 하지 않아도 대여하기 버튼 이벤트가 활성화되도록 초기화
        tradingRentButtonClickEvent(_major,"종이 여자1","기욤 뮈소1");

        //뷰페이저 스크롤 이벤트, 여기서 position에 따라 가져올 정보가 각각 다름
        trading_content_view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                TradingViewPagerItem tradingViewPagerItem=tradingViewPagerItems.get(position);

                String bookTitle=tradingViewPagerItem.getRegisteredBookTitle();
                String bookAuthor=tradingViewPagerItem.getRegisteredBookAuthor();

                tradingRentButtonClickEvent(_major,bookTitle,bookAuthor);//대여하기 버튼 클릭 이벤트

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    //아이템 데이터 추가를 위한 함수
    public void addItem(String _major, int _bookImg, String _bookTitle, String _bookAuthor){
        TradingViewPagerItem item=new TradingViewPagerItem();

        item.setMajor(_major);
        item.setRegisteredBookImage(_bookImg);
        item.setRegisteredBookTitle(_bookTitle);
        item.setRegisteredBookAuthor(_bookAuthor);

        tradingViewPagerItems.add(item);
    }

    //대여하기 버튼 클릭 이벤트
    public void tradingRentButtonClickEvent(String _major, String _bookTitle, String _bookAuthor){
        //구매 버튼 누를 시 MainActivity로 이동
        trading_rent_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getActivity(),"전공: "+_major+" 책 제목: "+_bookTitle+" 저자: "+_bookAuthor,Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

}
