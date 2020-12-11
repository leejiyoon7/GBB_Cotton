package com.example.cotton.ui.home.allowBorrow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cotton.R;
import com.example.cotton.ui.home.MyRentedBookListItem;

import java.util.ArrayList;

public class UserRentedBookListAdapter extends RecyclerView.Adapter<UserRentedBookListAdapter.ViewHolder> {

    public AllowBorrowActivity delegate;
    public ArrayList<MyRentedBookListItem> userRentedBookList =new ArrayList<MyRentedBookListItem>();
    public ArrayList<MyRentedBookListItem> userRentedBookSelectedList =new ArrayList<MyRentedBookListItem>();

    //아이템 뷰를 저장하는 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView list_my_rented_book_title_text_view;//내가 대여한 도서 제목
        TextView list_my_rented_book_writer_text_view;//도서 작성자
        TextView list_my_rented_book_status_text_view;//도서 상태
        RadioButton list_my_rented_book_radio_btn;
        ViewHolder(View itemView){
            super(itemView);
            list_my_rented_book_title_text_view=itemView.findViewById(R.id.list_my_rented_book_title_text_view);
            list_my_rented_book_writer_text_view=itemView.findViewById(R.id.list_my_rented_book_writer_text_view);
            list_my_rented_book_status_text_view=itemView.findViewById(R.id.list_my_rented_book_status_text_view);
            list_my_rented_book_radio_btn =itemView.findViewById(R.id.list_my_rented_book_radio_btn);
            list_my_rented_book_radio_btn.setVisibility(View.VISIBLE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();
                    if (index != RecyclerView.NO_POSITION) {
                        if (userRentedBookSelectedList.contains(userRentedBookList.get(index))) {
                            userRentedBookSelectedList.remove(userRentedBookList.get(index));
                        }
                        else {
                            userRentedBookSelectedList.add(userRentedBookList.get(index));
                        }
                        notifyDataSetChanged();
                        delegate.changeSelectBtnStatus();
                    }
                }
            });
        }
    }


    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @NonNull
    @Override
    public UserRentedBookListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.my_rented_book_list_item, parent, false);
        UserRentedBookListAdapter.ViewHolder vh=new UserRentedBookListAdapter.ViewHolder(view);

        return vh;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyRentedBookListItem userRentedBookListItem= userRentedBookList.get(position);
        holder.list_my_rented_book_title_text_view.setText(userRentedBookListItem.getList_my_rented_book_title());
        holder.list_my_rented_book_writer_text_view.setText(userRentedBookListItem.getList_my_rented_book_writer());
        holder.list_my_rented_book_status_text_view.setText(userRentedBookListItem.getList_my_rented_book_status());

        if(userRentedBookSelectedList.contains(userRentedBookListItem)) {
            holder.list_my_rented_book_radio_btn.setChecked(true);
        }
        else {
            holder.list_my_rented_book_radio_btn.setChecked(false);
        }
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return userRentedBookList.size();
    }

    //아이템 데이터 추가를 위한 함수
    public void addItem(String list_my_rented_book_title,String list_my_rented_book_writer,String list_my_rented_book_status, String list_my_rented_book_barcode, String list_my_rented_book_owner_uid){
        MyRentedBookListItem item=new MyRentedBookListItem();

        item.setList_my_rented_book_title(list_my_rented_book_title);
        item.setList_my_rented_book_writer(list_my_rented_book_writer);
        item.setList_my_rented_book_status(list_my_rented_book_status);
        item.setList_my_rented_book_barcode(list_my_rented_book_barcode);
        item.setList_my_rented_book_owner_uid(list_my_rented_book_owner_uid);

        userRentedBookList.add(item);
    }

    public void selectAll() {
        userRentedBookSelectedList.clear();
        userRentedBookSelectedList.addAll(userRentedBookList);
        notifyDataSetChanged();
    }

    public void deleteAll() {
        userRentedBookSelectedList.clear();
        notifyDataSetChanged();
    }
}
