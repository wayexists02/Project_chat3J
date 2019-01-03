package com.chat3j.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chat3j.R;

import java.util.ArrayList;

public class TextMessageAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ChatItem> ChatItemList;

    // ListViewAdapter의 생성자
    public TextMessageAdapter() {
        ChatItemList = new ArrayList<>();
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return ChatItemList.size();
    }

    public ChatItem.Sender getSenderType(int position) {
        return ChatItemList.get(position).getSender();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        ChatItem.Sender type = getSenderType(pos);
        int res = 0;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {

            switch (type) {
                case OTHER:
                    res = R.layout.chat_item;
                    break;
                case ME:
                    res = R.layout.chat_item_me;
                    break;
            }
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(res, parent, false);
        }
        TextView tv_User, tv_Message, tv_Date;
        switch (type) {
            case OTHER:
                tv_User = (TextView) convertView.findViewById(R.id.tv_user);
                tv_User.setText(((ChatItem) getItem(pos)).getUserName());
                tv_Message = (TextView) convertView.findViewById(R.id.tv_message);
                tv_Message.setText(((ChatItem) getItem(pos)).getMessage());
                tv_Date = (TextView) convertView.findViewById(R.id.tv_date);
                tv_Date.setText(((ChatItem) getItem(pos)).getDate().toString());
                break;
            case ME:
                tv_Message = (TextView) convertView.findViewById(R.id.tv_message);
                tv_Message.setText(((ChatItem) getItem(pos)).getMessage());
                tv_Date = (TextView) convertView.findViewById(R.id.tv_date);
                tv_Date.setText(((ChatItem) getItem(pos)).getDate().toString());
                break;
        }

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return ChatItemList.get(position);
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(ChatItem item) {
        ChatItemList.add(item);
    }
}
