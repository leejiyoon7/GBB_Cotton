package com.example.cotton.ui.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cotton.FirebaseFunction;
import com.example.cotton.R;
import com.example.cotton.ui.food.FoodListItem;
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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();
                    if (index != RecyclerView.NO_POSITION) {
                        FirebaseFunction firebaseFunction = new FirebaseFunction();

                        // QR코드 정보 준비
                        String selectedBookBarcode = myRentedBookList.get(index).getList_my_rented_book_barcode();
                        String myUID = firebaseFunction.getMyUID();
                        String qrInfoString = selectedBookBarcode + "/" + myUID;
                        String bookOwnerUID = myRentedBookList.get(index).getList_my_rented_book_owner_uid();

                        // BottomSheetDialog 초기화.
                        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(v.getContext());
                        bottomSheetDialog.setContentView(R.layout.return_book_qrcode_bottom_dialog);

                        TextView qrSubmitBtn = bottomSheetDialog.findViewById(R.id.return_book_qr_submit_btn);
                        ImageView qrImageView = bottomSheetDialog.findViewById(R.id.return_book_qr_image_view);

                        qrSubmitBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                firebaseFunction.deleteRentedBookIfReturnedSuccessfully(
                                        selectedBookBarcode,
                                        bookOwnerUID,
                                        (value) -> {
                                            Log.d("Book Return Error: ", "책이 아직 반납되지 않았습니다.");
                                            return null;
                                        },
                                        (value) -> {
                                            Toast.makeText(v.getContext(), "책 반납이 완료되었습니다.", Toast.LENGTH_SHORT);
                                            Log.d("Book Return Error: ", "책 반납이 완료되었습니다.");
                                            return null;
                                        },
                                        (value) -> {
                                            Log.d("Book Return Error: ", "책 반납에 실패했습니다.");
                                            return null;
                                        }
                                );
                                bottomSheetDialog.dismiss();
                            }
                        });

                        try {
                            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                            Bitmap bitmap = barcodeEncoder.encodeBitmap(qrInfoString, BarcodeFormat.QR_CODE, 500, 500);
                            ImageView imageViewQrCode = qrImageView;
                            imageViewQrCode.setImageBitmap(bitmap);
                        } catch(Exception e) {

                        }
                        bottomSheetDialog.show();
                    }
                }
            });

            list_my_rented_book_title_text_view=itemView.findViewById(R.id.list_my_rented_book_title_text_view);
            list_my_rented_book_writer_text_view=itemView.findViewById(R.id.list_my_rented_book_writer_text_view);
            list_my_rented_book_status_text_view=itemView.findViewById(R.id.list_my_rented_book_status_text_view);
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    MyRentedBookListAdapter(){

    }

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
