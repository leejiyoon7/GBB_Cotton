package com.example.cotton.ui.Trading;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.cotton.MainActivity;
import com.example.cotton.MemberInfo;
import com.example.cotton.R;
import com.example.cotton.Utils.ApiService;
import com.example.cotton.Utils.BaseUrlInterface;
import com.example.cotton.Utils.RetrofitClientJson;
import com.example.cotton.ValueObject.SetBalance.SetBalanceResultVO;
import com.example.cotton.BookSaveForm;
import com.example.cotton.FirebaseFunction;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TradingFragment extends Fragment {

    ChipGroup trading_header_chip_group; // 칩 그룹
    Chip trading_hear_chip_book; //칩(책)
    Chip trading_hear_chip_writer; //칩(교수)
    SearchView trading_header_search_view; // 검색 창
    Spinner trading_title_department_spinner;//스피너
    ArrayAdapter spinnerAdapter;//스피너 어댑터

    TextView trading_page_indicator_current_page_text_view;//현재페이지번호 텍스트뷰
    TextView trading_page_indicator_total_page_text_view;//전체페이지번호 텍스트뷰
    TextView trading_no_search_result_text_view;
    LinearLayout trading_page_indicator_wrap_layout;

    ViewPager trading_content_view_pager;

    String major;//전공 value
    String bookTitle;//전공 책 제목
    String bookAuthor;//전공 책 작가
    String userName; //현재 사용자이름
    String userWallet; //현재 사용자 지갑주소

    ArrayList<TradingViewPagerItem> tradingViewPagerItems=new ArrayList<TradingViewPagerItem>();
    List<BookSaveForm> bookSearchResultByMajor;
    List<BookSaveForm> finalFilteredList;
    List<MemberInfo> memberInfos = new ArrayList<>();;
    int currentViewPagerIndex;


    TradingChipItem tradingChipItem;//chipItem class
    TradingMajorItem tradingMajorItem;//MajorItem class
    AppCompatButton trading_rent_button;//대여 버튼
    TradingViewPagerAdapter pagerAdapter;

    TradingFragment tradingFragment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_trading, container, false);

        tradingFragment=this;

        //region UI 선언 부
        trading_header_chip_group = view.findViewById(R.id.trading_header_chip_group);
        trading_header_search_view = view.findViewById(R.id.trading_header_search_view);
        trading_title_department_spinner=view.findViewById(R.id.trading_title_department_spinner);
        trading_content_view_pager = view.findViewById(R.id.trading_content_view_pager);
        trading_rent_button=view.findViewById(R.id.trading_rent_button);
        trading_hear_chip_writer = view.findViewById(R.id.trading_hear_chip_writer);
        trading_hear_chip_book = view.findViewById(R.id.trading_hear_chip_book);
        trading_page_indicator_current_page_text_view = view.findViewById(R.id.trading_page_indicator_current_page_text_view);
        trading_page_indicator_total_page_text_view = view.findViewById(R.id.trading_page_indicator_total_page_text_view);
        trading_no_search_result_text_view = view.findViewById(R.id.trading_no_search_result_text_view);
        trading_page_indicator_wrap_layout = view.findViewById(R.id.trading_page_indicator_wrap_layout);

        //endregion

        tradingViewPagerItems=new ArrayList<TradingViewPagerItem>();
        //chip item
        tradingChipItem=new TradingChipItem();
        //MajorItem class
        tradingMajorItem=new TradingMajorItem();


        // ViewPager 초기 설정
        trading_content_view_pager.setSaveFromParentEnabled(false);
        pagerAdapter = new TradingViewPagerAdapter(getChildFragmentManager());
        trading_content_view_pager.setAdapter(pagerAdapter);
        viewPagerSetOnChangePage();

        // Chip 초기 설정
        setChipOption(trading_hear_chip_book);
        setChipOption(trading_hear_chip_writer);

        //전공 선택 스피너
        majorPickSpinner();

        FirebaseFunction firebaseUserCall = new FirebaseFunction();
        firebaseUserCall.profileGet(memberInfos, (result)->{
            userName = result.get(0).getName();
            userWallet = result.get(0).getWallet();
            return null;
        });



        tradingRentButtonClickEvent();
        return view;
    }

    // 기능 부
    /**
     * 칩에 검색내용이 비어있는지 확인하고
     * 칩이 비어있을 경우 SearchView를 숨김.
     */
    private void hideSearchViewIfChipIsEmpty() {
        if (trading_hear_chip_writer.getText().equals("교수명: ") &&
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

        /*A. Chip을 누르면 SearchView가 보여짐.*/
        /*B. 검색을 완료하면 Chip에 검색어 등록.*/
        setChipEventFunc(chip);

        /*C. Chip 닫기를 누르면 Chip에 포함된 검색어가 사라짐.*/
        setChipCloseEventFunc(chip);
    } // End of: setChipOption

    public void setChipEventFunc(Chip chip){
        chip.setOnClickListener(new View.OnClickListener() {
            /*A. Chip을 누르면 SearchView가 보여짐.*/
            @Override
            public void onClick(View v) {
                // SearchView 검색 힌트 등록.
                String queryHintWord = "";
                if (chip.equals(trading_hear_chip_book)) {
                    queryHintWord = "도서명";
                }
                else if (chip.equals(trading_hear_chip_writer)) {
                    queryHintWord = "저자명";
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
                        if (chip.equals(trading_hear_chip_book)) {
                            chip.setText("도서명: " + query);
                            updateViewPager();
                        }
                        else if (chip.equals(trading_hear_chip_writer)) {
                            chip.setText("저자명: " + query);
                            updateViewPager();
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
    }

    /**
     * Chip의 닫기 버튼을 누르게 되면
     * Chip의 검색 텍스트를 모두 삭제.
     * 키보드를 닫기.
     * 뷰페이지 업데이트.
     * @param chip : 대상 Chip 오브젝트
     */
    public void setChipCloseEventFunc(Chip chip){
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chip.equals(trading_hear_chip_book)) {
                    chip.setText("도서명: ");
                    trading_header_search_view.clearFocus();
                    updateViewPager();
                }
                else if (chip.equals(trading_hear_chip_writer)) {
                    chip.setText("저자명: ");
                    trading_header_search_view.clearFocus();
                    updateViewPager();
                }
                hideSearchViewIfChipIsEmpty();
            }
        });
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
                        major="컴퓨터공학과";
                        tradingMajorItem.setMajor(major);
                        searchBookFromFirebase();//뷰 페이저 관련 함수
                        break;
                    case 1:
                        major="전자공학과";
                        tradingMajorItem.setMajor(major);
                        searchBookFromFirebase();
                        break;
                    case 2:
                        major="전기공학과";
                        tradingMajorItem.setMajor(major);
                        searchBookFromFirebase();
                        break;
                    case 3:
                        major="AI.소프트웨어학부";
                        tradingMajorItem.setMajor(major);
                        searchBookFromFirebase();
                        break;
                    case 4:
                        major="에너지IT학과";
                        tradingMajorItem.setMajor(major);
                        searchBookFromFirebase();
                        break;
                }
//                firebaseFunction firebaseSearch = new firebaseFunction();
//                firebaseSearch.searchBook(major, (resultList) -> {
//                    Log.d("testing ", major);
//                    return null;
//                });

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getActivity(),"아무것도 선택되지 않음",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 전공을 기준으로 파이어베이스에서 책 정보를 받아옴.
     */
    public void searchBookFromFirebase(){
        FirebaseFunction firebaseSearch = new FirebaseFunction();
        firebaseSearch.searchBook(major, (resultList) -> {
            bookSearchResultByMajor = resultList;
            Log.d("result = ", String.valueOf(resultList.size()));
            updateViewPager();
            return null;
        });
    }


    /**
     * Chip의 검색어를 기준으로 List를 필터링하고 결과를 Set형태로 반환.
     * @param chip : 대상이 되는 Chip
     * @return 조건 검색 완료된 bookSaveForm타입의 Set
     */
    public Set<BookSaveForm> filterResultByChipQuery(Chip chip) {
        // 검색어가 없을 경우 학과 기준 검색결과 반환
        if(chip.getText().length() == 4) {
            return new HashSet<>(bookSearchResultByMajor);
        }

        Set<BookSaveForm> filteredSet = new HashSet<>();
        bookSearchResultByMajor.forEach(book -> {
            String chipQuery = chip.getText().toString().substring(5).replace(" ", "");
            if(chip.equals(trading_hear_chip_book)){
                if (book.getBookName().replace(" ", "").contains(chipQuery)) {
                    filteredSet.add(book);
                }
            }
            if(chip.equals(trading_hear_chip_writer)) {
                if (book.getBookWriter().replace(" ", "").contains(chipQuery)) {
                    filteredSet.add(book);
                }
            }
        });

        return filteredSet;
    }


    /**
     * 다음 세가지 검색 결과를 바탕으로 최종 검색 결과 도출
     * 1. 전공 기준 책 검색 결과
     * 2. 도서명 기준 책 검색 결과
     * 3. 저자명 기준 책 검색 결과
     *
     * 최종 검색 결과를 통해서 ViewPager 갱신.
     */
    public void updateViewPager() {
        Set<BookSaveForm> filteredBookNameSet = filterResultByChipQuery(trading_hear_chip_book);
        Set<BookSaveForm> filteredBookWriterSet = filterResultByChipQuery(trading_hear_chip_writer);
        Set<BookSaveForm> finalFilteredSet = filteredBookNameSet;
        finalFilteredSet.retainAll(filteredBookWriterSet);
        finalFilteredList = Lists.newArrayList(finalFilteredSet);

        pagerAdapter.clearFragmentList();
        if (finalFilteredList.isEmpty()) {
            pagerAdapter.addFragment("", "", "");
            trading_no_search_result_text_view.setVisibility(View.VISIBLE);
            trading_page_indicator_wrap_layout.setVisibility(View.INVISIBLE);
            trading_content_view_pager.setVisibility(View.INVISIBLE);
            trading_rent_button.setEnabled(false);
        }
        else {
            finalFilteredList.forEach(book -> {
                pagerAdapter.addFragment(book.getPictureLink(),book.getBookName(),book.getBookWriter());
            });
            trading_no_search_result_text_view.setVisibility(View.INVISIBLE);
            trading_page_indicator_wrap_layout.setVisibility(View.VISIBLE);
            trading_content_view_pager.setVisibility(View.VISIBLE);
            trading_rent_button.setEnabled(true);
        }
        pagerAdapter.notifyDataSetChanged();

        trading_page_indicator_total_page_text_view.setText(String.valueOf(finalFilteredList.size()));
    }


    /**
     * ViewPager가 넘어갈 때 ViewPager 인덱스를 갱신.
     */
    private void viewPagerSetOnChangePage() {
        trading_content_view_pager.clearOnPageChangeListeners();
        trading_content_view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                trading_page_indicator_current_page_text_view.setText(String.valueOf(position + 1));
                currentViewPagerIndex = position;
            }
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageScrollStateChanged(int state) { }
        });
    }

    //아이템 데이터 추가를 위한 함수
//    public void addItem(String _major, int _bookImg, String _bookTitle, String _bookAuthor){
//        TradingViewPagerItem item=new TradingViewPagerItem();
//
//        item.setMajor(_major);
//        item.setRegisteredBookImage(_bookImg);
//        item.setRegisteredBookTitle(_bookTitle);
//        item.setRegisteredBookAuthor(_bookAuthor);
//
//        tradingViewPagerItems.add(item);
//    }


    //대여하기 버튼 클릭 이벤트
    public void tradingRentButtonClickEvent(){
        //구매 버튼 누를 시 MainActivity로 이동
        trading_rent_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BookSaveForm selectedBookInfo = new BookSaveForm();
                selectedBookInfo = finalFilteredList.get(currentViewPagerIndex);
                String barcode = selectedBookInfo.getBarcode();
                String selectedBookName = selectedBookInfo.getBookName();
                String selectedBookWriter = selectedBookInfo.getBookWriter();

                if (selectedBookInfo != null) {
                    FirebaseFunction firebaseFunction = new FirebaseFunction();
                    firebaseFunction.updateRentMember(barcode, userName);
                }

                FirebaseFunction firebaseFunction = new FirebaseFunction();

                firebaseFunction.getUuid(barcode, (result) -> {

                    firebaseFunction.returnUuid(result,(wallet) -> {

                        ApiService call = RetrofitClientJson.getApiService(BaseUrlInterface.LUNIVERSE);

                        HashMap<String, String> bodyMap2 = new HashMap<String, String>();
                        bodyMap2.put("valueAmount", "4500000000000000000000"); //가격
                        bodyMap2.put("receiverAddress", wallet);

                        HashMap<String, Object> bodyMap = new HashMap<String, Object>();
                        bodyMap.put("from", userWallet); //보내는사람 지갑주소
                        bodyMap.put("inputs", new HashMap<String, String>(bodyMap2)); //bodyMap2

                        Log.d("성공 : ", "result : " + bodyMap.toString());

                        //대여 성공시 user개인정보 빌린도서 목록에 추가됩니다.
                        //상태는 대여성공시 "대여중"으로 초기화됩니다.
                        firebaseFunction.insertRentedBookInfoToUser(barcode,selectedBookName, selectedBookWriter, "대여중");

                        call.buyFood(bodyMap).enqueue(new Callback<SetBalanceResultVO>() {
                            @Override
                            public void onResponse(Call<SetBalanceResultVO> call, Response<SetBalanceResultVO> response) {

                                Log.d("성공 : ", "result : " + response.raw());
                                Log.d("성공 : ", "result : " + response.body().getResult());
                                Log.d("성공 : ", "TxId : " + response.body().getDataFoodBuy().getTxId());
                                Log.d("성공 : ", "ReqTs : " + response.body().getDataFoodBuy().getReqTs());
                            }

                            @Override
                            public void onFailure(Call<SetBalanceResultVO> call, Throwable t) {
                                Log.d("실패 : ", t.toString());
                            }

                        });

                        HashMap<String, String> bodyMap4 = new HashMap<String, String>();
                        bodyMap4.put("valueAmount", "500000000000000000000"); //가격
                        bodyMap4.put("receiverAddress", "0xfb8e77f5808121c3ecf19d92ffb56b2e3d8db57b");

                        HashMap<String, Object> bodyMap3 = new HashMap<String, Object>();
                        bodyMap3.put("from", userWallet); //보내는사람 지갑주소
                        bodyMap3.put("inputs", new HashMap<String, String>(bodyMap4)); //bodyMap2


                        call.buyFood(bodyMap3).enqueue(new Callback<SetBalanceResultVO>() {
                            @Override
                            public void onResponse(Call<SetBalanceResultVO> call, Response<SetBalanceResultVO> response) {
                                Log.d("성공 : ", "result : " + response.raw());
                                Log.d("성공 : ", "result : " + response.body().getResult());
                                Log.d("성공 : ", "TxId : " + response.body().getDataFoodBuy().getTxId());
                                Log.d("성공 : ", "ReqTs : " + response.body().getDataFoodBuy().getReqTs());
                            }

                            @Override
                            public void onFailure(Call<SetBalanceResultVO> call, Throwable t) {
                                Log.d("실패 : ", t.toString());
                            }

                        });

                        return null;
                    });

                    return null;
                });

                FragmentTransaction ft;
                if(tradingFragment.getFragmentManager()!=null){
                    ft = tradingFragment.getFragmentManager().beginTransaction();
                    ft.detach(tradingFragment).attach(tradingFragment).commit();
                    Toast.makeText(getActivity(),"대여가 완료되었습니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
