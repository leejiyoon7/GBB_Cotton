package com.example.cotton.ui.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cotton.FirebaseFunction;
import com.example.cotton.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;

public class MyRentedBookListAdapter extends RecyclerView.Adapter<MyRentedBookListAdapter.ViewHolder> {

    private ArrayList<MyRentedBookListItem> myRentedBookList=new ArrayList<MyRentedBookListItem>();

    //아이템 뷰를 저장하는 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView list_my_rented_book_title_text_view;//내가 대여한 도서 제목
        TextView list_my_rented_book_writer_text_view;//도서 작성자
        TextView list_my_rented_book_status_text_view;//도서 상태
        ViewHolder(View itemView){
            super(itemView);

            list_my_rented_book_title_text_view=itemView.findViewById(R.id.list_my_rented_book_title_text_view);
            list_my_rented_book_writer_text_view=itemView.findViewById(R.id.list_my_rented_book_writer_text_view);
            list_my_rented_book_status_text_view=itemView.findViewById(R.id.list_my_rented_book_status_text_view);
        }
    }

//    // 생성자에서 데이터 리스트 객체를 전달받음.
//    MyRentedBookListAdapter(){
//
//    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @NonNull
    @Override
    public MyRentedBookListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.my_rented_book_list_item, parent, false);
        MyRentedBookListAdapter.ViewHolder vh=new MyRentedBookListAdapter.ViewHolder(view);

        return vh;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyRentedBookListItem myRentedBookListItem=myRentedBookList.get(position);
        holder.list_my_rented_book_title_text_view.setText(myRentedBookListItem.getList_my_rented_book_title());
        holder.list_my_rented_book_writer_text_view.setText(myRentedBookListItem.getList_my_rented_book_writer());
        holder.list_my_rented_book_status_text_view.setText(myRentedBookListItem.getList_my_rented_book_status());
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return myRentedBookList.size();
    }

    //아이템 데이터 추가를 위한 함수
    public void addItem(String list_my_rented_book_title,String list_my_rented_book_writer,String list_my_rented_book_status, String list_my_rented_book_barcode, String list_my_rented_book_owner_uid){
        MyRentedBookListItem item=new MyRentedBookListItem();

        item.setList_my_rented_book_title(list_my_rented_book_title);
        item.setList_my_rented_book_writer(list_my_rented_book_writer);
        item.setList_my_rented_book_status(list_my_rented_book_status);
        item.setList_my_rented_book_barcode(list_my_rented_book_barcode);
        item.setList_my_rented_book_owner_uid(list_my_rented_book_owner_uid);

        myRentedBookList.add(item);
    }
}
